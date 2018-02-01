package io.vertx.mokabyte.datastore;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLClient;
import io.vertx.ext.sql.SQLConnection;
import io.vertx.ext.sql.SQLOptions;
import io.vertx.mokabyte.model.Error;
import io.vertx.mokabyte.model.TodoModel;
import io.vertx.mokabyte.model.UserModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class DataStoreVerticle extends AbstractVerticle {
    private static Logger logger = LoggerFactory.getLogger(DataStoreVerticle.class);

    @Override
    public void start(Future<Void> storeFeature) {
        final EventBus eventBus = getVertx().eventBus();
        final SQLClient sqlClient = JDBCClient.create(getVertx(), DataSourceConfig.initDataSource(config()));

        sqlClient.getConnection(conn -> {
            if (conn.succeeded()) {
                logger.info("Successful start DataStore");
                storeFeature.complete();
                conn.result().setOptions(new SQLOptions().setAutoGeneratedKeys(Boolean.TRUE));

                eventBus.consumer("todo.find.all", message -> findAll(message, conn.result()));

                eventBus.consumer("todo.find.todo", message -> findTodo(message, conn.result()));

                eventBus.consumer("todo.create", message -> createTodo(message, conn.result()));

                eventBus.consumer("todo.update", message -> updateTodo(message, conn.result()));

                eventBus.consumer("todo.delete", message -> deleteTodo(message, conn.result()));
            } else {
                storeFeature.fail(conn.cause());
            }
        });
    }

    @Override
    public void stop(Future<Void> stopFeature) throws Exception {
        super.stop(stopFeature);
    }

    private void findAll(final Message<Object> message, final SQLConnection connection) {
        if (Objects.nonNull(message)) {
            connection.query("SELECT t.id as T_ID, t.content as T_CONTENT, t.creation_date AS T_CREATION, " +
                    "u.id as U_ID, u.username as U_USER, u.password as U_PWD, u.name as U_NAME, u.surname as U_SNAME, " +
                    "u.email as U_EMAIL, u.creation_date as U_CREATION from todo t join user u" +
                    " on (u.id = t.id) order by t.id desc", sqlResult -> {

                if (sqlResult.succeeded()) {
                    message.reply(Json.encode(fillTodoModel(sqlResult)));
                } else {
                    logger.error("Error to execute query findAll: {}", sqlResult.cause().getMessage());
                    final Error error = new Error("Error on query findAll", sqlResult.cause().getMessage());

                    message.reply(Json.encode(error));
                }
            });
        }
    }

    private void findTodo(final Message<Object> message, final SQLConnection connection) {
        if (Objects.nonNull(message) && Objects.nonNull(message.body())) {
            final JsonArray jsonParam = new JsonArray().add((Long) message.body());

            connection.queryWithParams( "SELECT t.id as T_ID, t.content as T_CONTENT, t.creation_date AS T_CREATION, " +
                    "u.id as U_ID, u.username as U_USER, u.password as U_PWD, u.name as U_NAME, u.surname as U_SNAME, " +
                    "u.email as U_EMAIL, u.creation_date as U_CREATION from todo t join user u" +
                    " on (u.id = t.id) WHERE t.id = ? order by t.id desc", jsonParam,  sqlResult -> {

                if (sqlResult.succeeded()) {
                    final List<TodoModel> todoList = fillTodoModel(sqlResult);
                    if (!todoList.isEmpty()) {
                        message.reply(Json.encode(todoList.get(0)));
                    } else {
                        message.reply(Json.encode(new Error("Item Not Found", "Item not Found on DB")));
                    }
                } else {
                    logger.error("Error to execute query findAll: {}", sqlResult.cause().getMessage());
                    final Error error = new Error("Error on query findAll", sqlResult.cause().getMessage());

                    message.reply(Json.encode(error));
                }
            });
        }
    }

    private void createTodo(final Message<Object> message, final SQLConnection connection) {
        if (Objects.nonNull(message) && Objects.nonNull(message.body())) {
            final TodoModel todo = Json.decodeValue(message.body().toString(), TodoModel.class);
            final JsonArray todoParam = new JsonArray()
                .add(todo.getTodoText())
                .add( 1L );
            connection.updateWithParams("INSERT INTO todo (content, user_id) VALUES (?, ?)",
                todoParam, insTodoResult -> {
                    if (insTodoResult.succeeded()) {
                        message.reply(insTodoResult.result().getKeys().getLong(0));
                    } else {
                        logger.error("Error to execute query createTodo: {}", insTodoResult.cause().getMessage());
                        final Error error = new Error("Error on query createTodo", insTodoResult.cause().getMessage());

                        message.reply(Json.encodePrettily(error));
                    }
                });
        }
    }

    private void updateTodo(final Message<Object> message, final SQLConnection connection) {
        if (Objects.nonNull(message) && Objects.nonNull(message.body())) {
            final TodoModel todo = Json.decodeValue(message.body().toString(), TodoModel.class);
            final JsonArray todoParam = new JsonArray()
                .add(todo.getTodoText())
                .add( todo.getId() );

                connection.updateWithParams("UPDATE todo set content = ? WHERE user_id = ?",
                    todoParam, updTodoResult -> {
                        if (updTodoResult.succeeded()) {
                            message.reply(updTodoResult.result().getUpdated());
                        } else {
                            logger.error("Error to execute query updateTodo Todo: {}", updTodoResult.cause().getMessage());
                            final Error error = new Error("Error on query updateTodo Todo", updTodoResult.cause().getMessage());

                            message.reply(Json.encodePrettily(error));
                        }
                    });
        }
    }

    private void deleteTodo(final Message<Object> message, final SQLConnection connection) {
        if (Objects.nonNull(message) && Objects.nonNull(message.body())) {
            final Long todoId = (Long) message.body();
            final JsonArray deleteParam = new JsonArray().add(todoId);

            connection.updateWithParams("DELETE FROM todo WHERE id = ?",
                    deleteParam, delResult -> {
                        if (delResult.succeeded()) {
                            message.reply(delResult.succeeded());
                        } else {
                            logger.error("Error to execute query deleteTodo: {}", delResult.cause().getMessage());
                            final Error error = new Error("Error on query deleteTodo", delResult.cause().getMessage());

                            message.reply(Json.encodePrettily(error));
                        }
                    });
        }
    }

    private static List<TodoModel> fillTodoModel(AsyncResult<ResultSet> sqlResult) {
        final ResultSet resultSet = sqlResult.result();
        final List<TodoModel> todos = new LinkedList<>();

        for (JsonObject row : resultSet.getRows()) {
            final TodoModel todo = new TodoModel(row.getLong("T_ID"));
            todo.setTodoText(row.getString("T_CONTENT"));
            todo.setCreationDate(LocalDateTime.ofInstant(row.getInstant("T_CREATION"), ZoneOffset.UTC));

            todo.setUser(new UserModel(row.getLong("U_ID")));
            todo.getUser().setUsername(row.getString("U_USER"));
            todo.getUser().setPassword(row.getString("U_PWD"));
            todo.getUser().setName(row.getString("U_NAME"));
            todo.getUser().setSurname(row.getString("U_SNAME"));
            todo.getUser().setEmail(row.getString("U_EMAIL"));
            todo.getUser().setCreationDate(LocalDateTime.ofInstant(row.getInstant("U_CREATION"), ZoneOffset.UTC));

            todos.add(todo);
        }

        return todos;
    }
}