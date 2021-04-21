import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import net.alepuzio.todolistvertx.datastore.DataSourceConfig;
import net.alepuzio.todolistvertx.datastore.DataStoreVerticle;
import net.alepuzio.todolistvertx.model.TodoModel;
import net.alepuzio.todolistvertx.web.WebVerticle;

import org.flywaydb.core.Flyway;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.util.Map;

@RunWith(VertxUnitRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TodoWebVerticleTestCase {
    private static final int HTTP_PORT = 9000;

    private static Vertx vertx;

    private TodoModel todoModel;

    @BeforeClass
    public static void init(final TestContext context) {
        vertx = Vertx.vertx();

        final JsonObject properties = new JsonObject();
        properties.put("datasource.driver", "org.h2.Driver");
        properties.put("datasource.url", "jdbc:h2:mem:todoWebDb;DB_CLOSE_DELAY=-1");
        properties.put("datasource.user", "sa");
        properties.put("datasource.password", "");
        properties.put("http.port", HTTP_PORT);

        final DeploymentOptions options = new DeploymentOptions()
                .setConfig(properties);

        initDB(properties);

        // We pass the options as the second parameter of the deployVerticle method.
        vertx.deployVerticle(WebVerticle.class.getName(), options, context.asyncAssertSuccess());
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
    public void a_whenRequestRootReturnIndexPage(final TestContext context) {
        final Async async = context.async();

        vertx.createHttpClient().getNow(HTTP_PORT, "localhost", "/", response -> response.handler(body -> {
            context.assertTrue(body.toString().contains("<title>Todo Vert.x App</title>"));
            async.complete();
        }));
    }

    @Test
    public void b_createNewTodo(final TestContext context) {
        final Async async = context.async();

        final String bodyData = Json.encodePrettily(todoModel);
        final String bodyLength = Integer.toString(bodyData.length());

        vertx.createHttpClient().post(HTTP_PORT, "localhost", "/api/todo")
                .putHeader("content-type", "application/json")
                .putHeader("content-length", bodyLength)
                .handler(response -> {
                    context.assertEquals(response.statusCode(), 200);
                    context.assertTrue(response.headers().get("content-type").contains("application/json"));
                    response.bodyHandler(body -> {
                        final TodoModel todo = Json.decodeValue(body.toString(), TodoModel.class);
                        context.assertEquals(todo.getTodoText(), "Appointment with All");
                        context.assertNotNull(todo.getId());
                        async.complete();
                    });
                })
                .write(bodyData)
                .end();
    }

    @Test
    public void c_loadAllTodo(final TestContext context) {
        final Async async = context.async();

        vertx.createHttpClient().get(HTTP_PORT, "localhost", "/api/todo")
                .handler(response -> {
                    context.assertEquals(response.statusCode(), 200);
                    context.assertTrue(response.headers().get("content-type").contains("application/json"));
                    response.bodyHandler(body -> {
                        final TodoModel[] todoArray = Json.decodeValue(body.toString(), TodoModel[].class);
                        context.assertEquals(todoArray.length, 1);
                        context.assertTrue(todoArray[0].getTodoText().equalsIgnoreCase(todoModel.getTodoText()));
                        async.complete();
                    });
                }).end();
    }

    @Test
    public void d_loadTodoById(final TestContext context) {
        final Async async = context.async();

        vertx.createHttpClient().get(HTTP_PORT, "localhost", "/api/todo/1")
                .handler(response -> {
                    context.assertEquals(response.statusCode(), 200);
                    context.assertTrue(response.headers().get("content-type").contains("application/json"));
                    response.bodyHandler(body -> {
                        final TodoModel todo = Json.decodeValue(body.toString(), TodoModel.class);
                        context.assertNotNull(todo);
                        context.assertEquals(todo.getId(), 1L);
                        async.complete();
                    });
                }).end();
    }

    @Test
    public void e_updateTodoById(final TestContext context) {
        final Async async = context.async();

        final String bodyData = Json.encodePrettily(Map.of("todoText", "Change Appointment"));
        final String bodyLength = Integer.toString(bodyData.length());

        vertx.createHttpClient().put(HTTP_PORT, "localhost", "/api/todo/1")
                .putHeader("content-type", "application/json")
                .putHeader("content-length", bodyLength)
                .handler(response -> {
                    context.assertEquals(response.statusCode(), 200);
                    context.assertTrue(response.headers().get("content-type").contains("application/json"));
                    response.bodyHandler(body -> {
                        final TodoModel todo = Json.decodeValue(body.toString(), TodoModel.class);
                        context.assertNotNull(todo);
                        context.assertEquals(todo.getTodoText(), "Change Appointment");
                        context.assertEquals(todo.getId(), 1L);
                        async.complete();
                    });
                })
                .write(bodyData)
                .end();
    }

    @Test
    public void f_deleteTodoById(final TestContext context) {
        final Async async = context.async();

        vertx.createHttpClient().delete(HTTP_PORT, "localhost", "/api/todo/1")
                .handler(response -> {
                    context.assertEquals(response.statusCode(), 200);
                    async.complete();
                })
                .end();
    }

    @Test
    public void g_emptyTodos(final TestContext context) {
        final Async async = context.async();

        vertx.createHttpClient().get(HTTP_PORT, "localhost", "/api/todo")
                .handler(response -> {
                    context.assertEquals(response.statusCode(), 200);
                    context.assertTrue(response.headers().get("content-type").contains("application/json"));
                    response.bodyHandler(body -> {
                        final TodoModel[] todoArray = Json.decodeValue(body.toString(), TodoModel[].class);
                        context.assertEquals(todoArray.length, 0);
                        async.complete();
                    });
                }).end();
    }

    @AfterClass
    public static void tearDown(TestContext context) {
        org.h2.store.fs.FileUtils.deleteRecursive("mem:todoWebDb", true);
        vertx.close(context.asyncAssertSuccess());
    }
}
