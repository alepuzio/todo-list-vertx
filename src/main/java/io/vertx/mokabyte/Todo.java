package io.vertx.mokabyte;

import io.vertx.core.*;
import io.vertx.core.json.JsonObject;
import io.vertx.mokabyte.datastore.DataSourceConfig;
import io.vertx.mokabyte.datastore.DataStoreVerticle;
import io.vertx.mokabyte.web.WebVerticle;
import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Todo extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger(Todo.class);

    @Override
    public void start(Future<Void> startFuture) {
        final JsonObject config = config();
        logger.info("Start Vertx Todo List");
        logger.info("with config:\n {}", config.encodePrettily());

        CompositeFuture.all(
                initDb(config),
                deploy(WebVerticle.class, config),
                deploy(DataStoreVerticle.class, config))
                .setHandler(result -> {
                    if (result.succeeded()) {
                        startFuture.complete();
                    } else {
                        startFuture.fail(result.cause());
                        getVertx().close();
                    }
                });

    }

    @Override
    public void stop(Future<Void> stopFuture) {
        stopFuture.complete();
    }

    private Future<Void> deploy(final Class<? extends Verticle> verticle, final JsonObject config) {
        final Future<Void> done = Future.future();
        getVertx().deployVerticle(verticle, new DeploymentOptions().setConfig(config), result -> {
            if (result.succeeded()) {
                done.complete();
            } else {
                logger.error("Error to deploy Verticle: {}", verticle.getClass().getName());
                done.fail(result.cause());
            }
        });

        return done;
    }

    private Future<Void> initDb(final JsonObject config) {
        final Future<Void> done = Future.future();
        getVertx().executeBlocking(initDbFeature -> {
            final Flyway flyway = new Flyway();
            flyway.setDataSource(DataSourceConfig.initDataSource(config));
            flyway.migrate();

            initDbFeature.complete();
        }, initRes -> {
            if (initRes.succeeded()) {
                logger.info("Db Init Successfully");
                done.complete();
            } else {
                done.fail(initRes.cause());
            }
        });

        return done;
    }

    public static void main(String[] args) {
        Launcher.executeCommand("run", Todo.class.getName(), args[0], args[1]);
    }

}
