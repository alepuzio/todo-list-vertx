package io.vertx.mokabyte.datastore;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;

public class DataStoreVerticle extends AbstractVerticle {

    @Override
    public void start(Future<Void> storeFeature) throws Exception {

    }

    @Override
    public void stop(Future<Void> stopFeature) throws Exception {
        super.stop(stopFeature);
    }
}
