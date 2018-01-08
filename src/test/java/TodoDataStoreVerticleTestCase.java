import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.mokabyte.datastore.DataStoreVerticle;
import io.vertx.mokabyte.model.TodoModel;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

@RunWith(VertxUnitRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TodoDataStoreVerticleTestCase {

    private static Vertx vertx;

    private TodoModel todoModel;

    @BeforeClass
    public static void init(final TestContext context) {
        vertx = Vertx.vertx();
        final DeploymentOptions options = new DeploymentOptions()
                .setConfig(new JsonObject().put("datasource.driver", "org.h2.Driver"))
                .setConfig(new JsonObject().put("datasource.url", "jdbc:h2:file:c:/development/var/storage/h2/todoDb;DB_CLOSE_ON_EXIT=FALSE"))
                .setConfig(new JsonObject().put("datasource.user", "sa"))
                .setConfig(new JsonObject().put("datasource.password", ""));

        // We pass the options as the second parameter of the deployVerticle method.
        vertx.deployVerticle(DataStoreVerticle.class.getName(), options, context.asyncAssertSuccess());
    }

    @Before
    public void setUp() {
        todoModel = TestUtil.createTestModel();
    }

    @AfterClass
    public static void tearDown(TestContext context) {
        vertx.close(context.asyncAssertSuccess());
    }
}
