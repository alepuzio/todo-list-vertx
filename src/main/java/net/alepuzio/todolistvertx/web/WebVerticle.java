package net.alepuzio.todolistvertx.web;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;
import net.alepuzio.todolistvertx.model.Error;
import net.alepuzio.todolistvertx.model.TodoModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * @overview: class about the routing of the HTTP request
 * */
public class WebVerticle extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger(WebVerticle.class);
    private static final int HTTP_PORT = 9000;

    @Override
    public void start(final Future<Void> webFuture/*future called when the startup is complete*/) {
        final Router router = Router.router(getVertx());
        router.route(HttpMethod.GET,"/*").handler(StaticHandler.create("web"));//define the static part

        //Define API REST Routing
        router.get("/api/todo").handler(this::getAll);//define the REST GET
        router.get("/api/todo/:id").handler(this::getTodoItem);//define the REST GET

        router.route("/api/todo*").handler(BodyHandler.create());//accept body request for request in /api/todo*
        router.post("/api/todo").handler(this::createTodoItem);//define the REST POST
        router.put("/api/todo/:id").handler(this::updateTodoItem);//define the REST PUT
        router.delete("/api/todo/:id").handler(this::deleteTodoItem);//define the REST DELETE

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
        vertx.eventBus().send(
        		"todo.find.all", //address
        		"_ALL_", ///TODO define?
        		response -> {	
		            if (response.succeeded()) {
		                try {
		                    final TodoModel[] founds = Json.decodeValue(response.result().body().toString(), TodoModel[].class);//deseralize the json in Pojo
		                    routingContext.response()
		                            .putHeader("content-type", "application/json; charset=utf-8")
		                            .end(Json.encodePrettily(founds));//write the pojo in JSon
		                } catch (DecodeException de) {
		                	logger.error("Exception in Json decoding: {}", response.cause());
		                    final Error error = Json.decodeValue(response.result().body().toString(), Error.class);
		                    routingContext.response().setStatusCode(500).end(Json.encodePrettily(error));
		                }
		            } else {
		                logger.error("Load Response error: {}", response.cause());
		                routingContext.response().setStatusCode(500).end();
		            }
        		});
    }

    private void getTodoItem(final RoutingContext routingContext) {
        final String id = routingContext.request().getParam("id");///read ID param
        if (Objects.isNull(id)) {
            routingContext.response().setStatusCode(400).end();
        } else {
            vertx.eventBus().send("todo.find.todo",//channel
            		Long.valueOf(id),
            		loadResponse -> parseSearchResult(routingContext, loadResponse)
            		);
        }
    }

    private void createTodoItem(final RoutingContext routingContext) {
        // Read the request's content and create an instance of Whisky.
        final TodoModel todo = Json.decodeValue(routingContext.getBodyAsString(), TodoModel.class);//deseralize the json in Pojo

        vertx.eventBus().send("todo.create", Json.encode(todo), response -> {
            if (response.succeeded()) {
                final Message<Object> returnMessage = response.result();
                if (returnMessage.body() instanceof Long) {
                    vertx.eventBus().send("todo.find.todo", returnMessage.body(), loadResponse -> parseSearchResult(routingContext, loadResponse));
                } else {
                    final Error error = Json.decodeValue(response.result().body().toString(), Error.class);
                    routingContext.response().setStatusCode(500).end(Json.encodePrettily(error));
                }
            } else {
                logger.error("Load Response error: {}", response.cause());
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

        if (Objects.isNull(id)) {
            routingContext.response().setStatusCode(400).end();
        } else {
            vertx.eventBus().send("todo.update", Json.encode(updateTodo), response -> {
                if (response.succeeded()) {
                    vertx.eventBus().send("todo.find.todo", Long.valueOf(id), loadResponse -> parseSearchResult(routingContext, loadResponse));
                } else {
                    logger.error("Load Response error: {}", response.cause());
                    routingContext.response().setStatusCode(500).end();
                }
            });
        }
    }

    private void parseSearchResult(RoutingContext routingContext, AsyncResult<Message<Object>> loadResponse) {
        if (loadResponse.succeeded()) {
            try {
                final TodoModel foundTodo = Json.decodeValue(loadResponse.result().body().toString(), TodoModel.class);//deseralize the json in Pojo
                routingContext.response()
                        .putHeader("content-type", "application/json; charset=utf-8")
                        .end(Json.encodePrettily(foundTodo));//write the pojo in the body
            } catch (DecodeException de) {
                logger.error("Exception in decoding: {}", loadResponse.cause());
                final Error error = Json.decodeValue(loadResponse.result().body().toString(), Error.class);
                routingContext.response().setStatusCode(500).end(Json.encodePrettily(error));
            }
        } else {
            logger.error("Load Response error: {}", loadResponse.cause());
            routingContext.response().setStatusCode(500).end();
        }
    }

    private void deleteTodoItem(final RoutingContext routingContext) {
        final String id = routingContext.request().getParam("id");
        if (Objects.isNull(id)) {
            routingContext.response().setStatusCode(400).end();
        } else {
            vertx.eventBus().send("todo.delete",//channel
            		Long.valueOf(id),
            		response -> {
		                if (response.succeeded()) {
		                    routingContext.response().setStatusCode(200).end();
		                } else {
		                    final Error error = Json.decodeValue(response.result().body().toString(), Error.class);
		                    routingContext.response().setStatusCode(500).end(Json.encodePrettily(error));
		                }
            		});
        }
    }

}
