package io.vertx.mokabyte.web;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.mokabyte.model.TodoModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class WebVerticle extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger(WebVerticle.class);
    private static final int HTTP_PORT = 9000;

    @Override
    public void start(final Future<Void> webFuture) {
        final Router router = Router.router(getVertx());
        router.route(HttpMethod.GET,"/").handler(StaticHandler.create("web"));

        //Define API REST Routing
        router.get("/api/todo").handler(this::getAll);
        router.get("/api/todo/:id").handler(this::getTodoItem);

        router.route("/api/todo*").handler(BodyHandler.create());
        router.post("/api/todo").handler(this::createTodoItem);
        router.put("/api/todo/:id").handler(this::updateTodoItem);
        router.delete("/api/todo/:id").handler(this::deleteTodoItem);

        logger.info("Try to start WebServer on port: {}", HTTP_PORT);
        getVertx().createHttpServer()
                .requestHandler(router::accept)
                .listen(HTTP_PORT, accepted -> {
                    if (accepted.succeeded()) {
                        webFuture.complete();
                        logger.info("Successful start WebServer on port: {}", HTTP_PORT);
                    } else {
                        webFuture.fail(accepted.cause());
                    }
                });
    }

    @Override
    public void stop(Future<Void> stopFuture) throws Exception {
        super.stop(stopFuture);
    }

    private void getAll(final RoutingContext routingContext) {
        vertx.eventBus().send("todo.find.all", "_ALL_", response -> {
            if (response.succeeded()) {
                final TodoModel[] founds = Json.decodeValue(response.result().body().toString(), TodoModel[].class);
                routingContext.response()
                        .putHeader("content-type", "application/json; charset=utf-8")
                        .end(Json.encodePrettily(founds));
            } else {
                routingContext.response().setStatusCode(500).end();
            }
        });
    }

    private void getTodoItem(final RoutingContext routingContext) {
        final String id = routingContext.request().getParam("id");
        if (Objects.isNull(id)) {
            routingContext.response().setStatusCode(400).end();
        } else {
            vertx.eventBus().send("todo.find.todo", Long.valueOf(id), response -> {
                if (response.succeeded()) {
                    final TodoModel foundTodo = Json.decodeValue(response.result().body().toString(), TodoModel.class);
                    if (Objects.isNull(foundTodo)) {
                        routingContext.response().setStatusCode(404).end();
                    } else {
                        routingContext.response()
                                .putHeader("content-type", "application/json; charset=utf-8")
                                .end(Json.encodePrettily(foundTodo));
                    }
                } else {
                    routingContext.response().setStatusCode(500).end();
                }
            });
        }
    }

    private void createTodoItem(final RoutingContext routingContext) {
        // Read the request's content and create an instance of Whisky.
        final TodoModel todo = Json.decodeValue(routingContext.getBodyAsString(), TodoModel.class);

        vertx.eventBus().send("todo.create", Json.encode(todo), response -> {
            if (response.succeeded()) {
                final Message<Object> returnMessage = response.result();
                if (returnMessage.body() instanceof Long) {
                    todo.setId((Long) returnMessage.body());
                    routingContext.response()
                            .setStatusCode(201)
                            .putHeader("content-type", "application/json; charset=utf-8")
                            .end(Json.encodePrettily(todo));
                } else {
                    routingContext.response().setStatusCode(500).end();
                }
            } else {
                routingContext.response().setStatusCode(500).end();
            }
        });
    }

    private void updateTodoItem(final RoutingContext routingContext) {
        final String id = routingContext.request().getParam("id");
        final TodoModel updateTodo = Json.decodeValue(routingContext.getBodyAsString(), TodoModel.class);
        if (Objects.isNull(updateTodo.getId())) {
            updateTodo.setId(Long.valueOf(id));
        }

        if (Objects.isNull(id) || Objects.isNull(updateTodo)) {
            routingContext.response().setStatusCode(400).end();
        } else {
            vertx.eventBus().send("todo.update", Json.encode(updateTodo), response -> {
                if (response.succeeded()) {
                    vertx.eventBus().send("todo.find.todo", Long.valueOf(id), loadResponse -> {
                        if (loadResponse.succeeded()) {
                            final TodoModel foundTodo = Json.decodeValue(loadResponse.result().body().toString(), TodoModel.class);
                            routingContext.response()
                                    .putHeader("content-type", "application/json; charset=utf-8")
                                    .end(Json.encodePrettily(foundTodo));
                        } else {
                            routingContext.response().setStatusCode(500).end();
                        }
                    });
                } else {
                    routingContext.response().setStatusCode(500).end();
                }
            });
        }
    }

    private void deleteTodoItem(final RoutingContext routingContext) {
        final String id = routingContext.request().getParam("id");
        if (Objects.isNull(id)) {
            routingContext.response().setStatusCode(400).end();
        } else {
            vertx.eventBus().send("todo.delete", 1L, response -> {
                if (response.succeeded()) {
                    routingContext.response().setStatusCode(204).end();
                } else {
                    routingContext.response().setStatusCode(500).end();
                }
            });
        }
    }

}
