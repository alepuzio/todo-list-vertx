package net.alepuzio.todolistvertx.datastore;

import io.vertx.core.json.JsonObject;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

public class DataSourceConfig {

    public static DataSource initDataSource(final JsonObject config) {
        final DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(config.getString("datasource.driver"));
        dataSource.setUrl(config.getString("datasource.url"));
        dataSource.setUsername(config.getString("datasource.user"));
        dataSource.setPassword(config.getString("datasource.password"));

        return dataSource;
    }
}
