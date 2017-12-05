package io.vertx.mokabyte.web;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.mokabyte.model.TodoModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class WebVerticle extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger(WebVerticle.class);
    private static final int HTTP_PORT = 9000;

    private final Map<Long, TodoModel> todoModelList = new HashMap<>();

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
                        webFuture.succeeded();
                    } else {
                        webFuture.fail(accepted.cause());
                    }
                });
        logger.info("Successful start WebServer on port: {}", HTTP_PORT);
    }

    @Override
    public void stop(Future<Void> stopFuture) throws Exception {
        super.stop(stopFuture);
    }

    private void getAll(final RoutingContext routingContext) {
        routingContext.response()
                .putHeader("content-type", "application/json; charset=utf-8")
                .end(Json.encodePrettily(todoModelList.values()));
    }

    private void getTodoItem(final RoutingContext routingContext) {
        final String id = routingContext.request().getParam("id");
        if (Objects.isNull(id)) {
            routingContext.response().setStatusCode(400).end();
        } else {
            final Long todoId = Long.valueOf(id);
            final TodoModel todo = todoModelList.get(todoId);

            if (Objects.isNull(todo)) {
                routingContext.response().setStatusCode(404).end();
            } else {
                routingContext.response()
                        .putHeader("content-type", "application/json; charset=utf-8")
                        .end(Json.encodePrettily(todo));
            }
        }
    }

    private void createTodoItem(final RoutingContext routingContext) {
        // Read the request's content and create an instance of Whisky.
        final TodoModel todo = Json.decodeValue(routingContext.getBodyAsString(), TodoModel.class);
        // Add it to the backend map
        todoModelList.put(todo.getId(), todo);

        // Return the created whisky as JSON
        routingContext.response()
                .setStatusCode(201)
                .putHeader("content-type", "application/json; charset=utf-8")
                .end(Json.encodePrettily(todo));
    }

    private void updateTodoItem(final RoutingContext routingContext) {
        final String id = routingContext.request().getParam("id");
        final JsonObject json = routingContext.getBodyAsJson();

        if (Objects.isNull(id) || Objects.isNull(json)) {
            routingContext.response().setStatusCode(400).end();
        } else {
            final Long todoId = Long.valueOf(id);
            final TodoModel todo = todoModelList.get(todoId);

            if (Objects.isNull(todo)) {
                routingContext.response().setStatusCode(404).end();
            } else {
                todo.setTodoText(json.getString("todoText"));
                routingContext.response()
                        .putHeader("content-type", "application/json; charset=utf-8")
                        .end(Json.encodePrettily(todo));
            }
        }
    }

    private void deleteTodoItem(final RoutingContext routingContext) {
        final String id = routingContext.request().getParam("id");
        if (Objects.isNull(id)) {
            routingContext.response().setStatusCode(400).end();
        } else {
            final Long todoId = Long.valueOf(id);
            todoModelList.remove(todoId);
        }

        routingContext.response().setStatusCode(204).end();
    }

}
