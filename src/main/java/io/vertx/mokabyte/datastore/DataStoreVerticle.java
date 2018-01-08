package io.vertx.mokabyte.datastore;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.SQLClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataStoreVerticle extends AbstractVerticle {
    private static Logger logger = LoggerFactory.getLogger(DataStoreVerticle.class);

    @Override
    public void start(Future<Void> storeFeature) throws Exception {
        final SQLClient sqlClient = JDBCClient.create(getVertx(), DataSourceConfig.initDataSource(config()));


        logger.info("Successful start DataStore");
    }

    @Override
    public void stop(Future<Void> stopFeature) throws Exception {
        super.stop(stopFeature);
    }
}
