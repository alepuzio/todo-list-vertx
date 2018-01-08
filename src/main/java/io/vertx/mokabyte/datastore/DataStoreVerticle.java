package io.vertx.mokabyte.datastore;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.SQLClient;
import io.vertx.ext.sql.SQLConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    }

    private void findTodo(final Message<Object> message, final SQLConnection connection) {

    }

    private void createTodo(final Message<Object> message, final SQLConnection connection) {

    }

    private void updateTodo(final Message<Object> message, final SQLConnection connection) {

    }

    private void deleteTodo(final Message<Object> message, final SQLConnection connection) {

    }
}
