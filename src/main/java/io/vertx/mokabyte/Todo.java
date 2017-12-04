package io.vertx.mokabyte;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Launcher;
import io.vertx.mokabyte.web.WebVerticle;

public class Todo extends AbstractVerticle {

    @Override
    public void start(Future<Void> startFuture) {
        getVertx().deployVerticle(new WebVerticle(), result -> {
            if (result.succeeded()) {
                startFuture.complete();
            } else {
                startFuture.fail(result.cause());
            }
        });
    }

    public static void main(String[] args) {
        Launcher.executeCommand("run", Todo.class.getName());
    }

}
