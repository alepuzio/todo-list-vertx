package net.alepuzio.todolistvertx;

import io.vertx.core.*;
import io.vertx.core.json.JsonObject;
import net.alepuzio.todolistvertx.context.CurrentContext;
import net.alepuzio.todolistvertx.context.Status;
import net.alepuzio.todolistvertx.datastore.DataSourceConfig;
import net.alepuzio.todolistvertx.datastore.DataStoreVerticle;
import net.alepuzio.todolistvertx.web.WebVerticle;

import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Todo extends AbstractVerticle {
	private static final Logger logger = LoggerFactory.getLogger(Todo.class);

	/*
	 * verifica che le tre operazioni che restituiscono una Future siano completate
	 * correttamente
	 */
	@Override
	public void start(Future<Void> startFuture) {
		final JsonObject config = config();
		logger.info("Start Vertx Todo List");
		logger.info("with config:\n {}", config.encodePrettily());
		CompositeFuture.all(initDb(config), deploy(WebVerticle.class, config), deploy(DataStoreVerticle.class, config))
				.setHandler(result -> {
					if (result.succeeded()) {
						// Here we are waiting for uncaught exception
						vertx.exceptionHandler(e -> {
							logger.error("An error found: {}", e.getMessage(), e);
						});
						startFuture.complete();
					} else {
						startFuture.fail(result.cause());
						getVertx().close();
					}
				});
		logger.info(new Status(new CurrentContext(vertx.getOrCreateContext())).start());
	}

	@Override
	public void stop(Future<Void> stopFuture) {
		logger.info(new Status(new CurrentContext(vertx.getOrCreateContext())).stop());
		stopFuture.complete();
	}

	/**
	 * @effects deploy the verticle using the config
	 * @param verticle
	 * @param config:  external configuration in json
	 */
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
		logger.info(new Status(new CurrentContext(vertx.getOrCreateContext())).deploy());
		return done;
	}

	/**
	 * @effects build th database using Flyway
	 */
	private Future<Void> initDb(final JsonObject config) {
		final Future<Void> done = Future.future();
		getVertx().executeBlocking(// Safely execute some blocking code.
				initDbFeature -> {
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

	public static void main(String[] args) {// TODO move in specific class or using Spring Boot
		if (null != args && 2 == args.length) {
			Launcher.executeCommand("run", Todo.class.getName(), args[0], args[1]);
		} else {
			Launcher.executeCommand("run");
		}
	}

}
