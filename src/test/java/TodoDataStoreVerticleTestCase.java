import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.mokabyte.datastore.DataSourceConfig;
import io.vertx.mokabyte.datastore.DataStoreVerticle;
import io.vertx.mokabyte.model.TodoModel;
import org.flywaydb.core.Flyway;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import javax.sql.DataSource;

@RunWith(VertxUnitRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TodoDataStoreVerticleTestCase {

    private static Vertx vertx;

    private TodoModel todoModel;

    private DataSource dataSource = null;

    @BeforeClass
    public static void init(final TestContext context) {
        final JsonObject properties = new JsonObject();
        properties.put("datasource.driver", "org.h2.Driver");
        properties.put("datasource.url", "jdbc:h2:mem:todoDb");
        properties.put("datasource.user", "sa");
        properties.put("datasource.password", "");

        final DeploymentOptions options = new DeploymentOptions().setConfig(properties);
        initDB(properties);

        vertx = Vertx.vertx();

        // We pass the options as the second parameter of the deployVerticle method.
        vertx.deployVerticle(DataStoreVerticle.class.getName(), options, context.asyncAssertSuccess());
    }

    private static void initDB(JsonObject properties) {
        final Flyway flyway = new Flyway();
        flyway.setDataSource(DataSourceConfig.initDataSource(properties));
        flyway.migrate();
    }

    @Before
    public void setUp() {
        todoModel = TestUtil.createTestModel();
    }

    @Test
    public void createNewContent() {

    }

    @Test
    public void readContent() {

    }

    @Test
    public void readAllContent() {

    }

    @Test
    public void updateContent() {

    }

    @Test
    public void whenIsLastCallDelete() {

    }

    @AfterClass
    public static void tearDown(TestContext context) {
        vertx.close(context.asyncAssertSuccess());
    }
}
