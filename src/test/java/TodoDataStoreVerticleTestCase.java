import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.mokabyte.datastore.DataSourceConfig;
import io.vertx.mokabyte.datastore.DataStoreVerticle;
import io.vertx.mokabyte.model.TodoModel;
import org.flywaydb.core.Flyway;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

@RunWith(VertxUnitRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TodoDataStoreVerticleTestCase {

    private static Vertx vertx;

    private TodoModel todoModel;

    @BeforeClass
    public static void init(final TestContext context) {
        final JsonObject properties = new JsonObject();
        properties.put("datasource.driver", "org.h2.Driver");
        properties.put("datasource.url", "jdbc:h2:mem:todoDb;DB_CLOSE_DELAY=-1");
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
    public void createNewContent(final TestContext context) {
        final Async async = context.async();
        vertx.eventBus().send("todo.create", Json.encode(todoModel), response -> {
            if (response.succeeded()) {
                context.assertNotNull(response.result());
                context.assertNotNull(response.result().body());
                final Message<Object> returnMessage = response.result();
                context.assertTrue(returnMessage.body() instanceof Long);
                context.assertEquals(1L, returnMessage.body());
                async.complete();
            } else {
                context.fail(response.cause());
            }
        });
    }

    @Test
    public void readContent(final TestContext context) {
        final Async async = context.async();
        vertx.eventBus().send("todo.find.todo", 1L, response -> {
            if (response.succeeded()) {
                context.assertNotNull(response.result());
                context.assertNotNull(response.result().body());
                final TodoModel foundTodo = Json.decodeValue(response.result().body().toString(), TodoModel.class);
                context.assertEquals(1L, foundTodo.getId());
                async.complete();
            } else {
                context.fail(response.cause());
            }
        });
    }

    @Test
    public void readAllContent(final TestContext context) {
        final Async async = context.async();
        vertx.eventBus().send("todo.find.all", "_ALL_", response -> {
            if (response.succeeded()) {
                context.assertNotNull(response.result());
                context.assertNotNull(response.result().body());
                final TodoModel[] founds = Json.decodeValue(response.result().body().toString(), TodoModel[].class);
                context.assertNotNull(founds);
                context.assertTrue(founds.length > 0);
                async.complete();
            } else {
                context.fail(response.cause());
            }
        });
    }

    @Test
    public void updateContent(final TestContext context) {
        final Async async = context.async();
        final TodoModel updateTodo = TestUtil.createTestModel();
        context.assertEquals(todoModel.getTodoText(), updateTodo.getTodoText());
        updateTodo.setId(1L);
        updateTodo.setTodoText("Change Text Todo");

        vertx.eventBus().send("todo.update", Json.encode(updateTodo), response -> {
            if (response.succeeded()) {
                context.assertNotNull(response.result());
                context.assertNotNull(response.result().body());
                context.assertEquals(1, response.result().body());

                vertx.eventBus().send("todo.find.todo", 1L, loadResponse -> {
                    if (loadResponse.succeeded()) {
                        context.assertNotNull(loadResponse.result());
                        context.assertNotNull(loadResponse.result().body());
                        final TodoModel foundTodo = Json.decodeValue(loadResponse.result().body().toString(), TodoModel.class);
                        context.assertEquals(updateTodo.getTodoText(), foundTodo.getTodoText());
                        async.complete();
                    } else {
                        context.fail(loadResponse.cause());
                    }
                });
            } else {
                context.fail(response.cause());
            }
        });
    }

    @Test
    public void whenIsLastCallDelete(final TestContext context) {
        final Async async = context.async();

        vertx.eventBus().send("todo.delete", 1L, response -> {
            if (response.succeeded()) {
                context.assertNotNull(response.result());
                context.assertNotNull(response.result().body());
                context.assertEquals(true, response.result().body());

                vertx.eventBus().send("todo.find.all", "_ALL_", loadResponse -> {
                    if (loadResponse.succeeded()) {
                        context.assertNotNull(loadResponse.result());
                        context.assertNotNull(loadResponse.result().body());
                        final TodoModel[] founds = Json.decodeValue(loadResponse.result().body().toString(), TodoModel[].class);
                        context.assertNotNull(founds);
                        context.assertTrue(founds.length == 0);
                        async.complete();
                    } else {
                        context.fail(loadResponse.cause());
                    }
                });
            } else {
                context.fail(response.cause());
            }
        });
    }

    @AfterClass
    public static void tearDown(TestContext context) {
        vertx.close(context.asyncAssertSuccess());
    }
}
