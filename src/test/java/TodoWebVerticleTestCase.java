import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.mokabyte.model.TodoModel;
import io.vertx.mokabyte.model.UserModel;
import io.vertx.mokabyte.web.WebVerticle;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class TodoWebVerticleTestCase {
    private static final int HTTP_PORT = 9000;

    private Vertx vertx;

    private TodoModel todoModel;

    @Before
    public void setUp(final TestContext context) {
        vertx = Vertx.vertx();

        final DeploymentOptions options = new DeploymentOptions()
                .setConfig(new JsonObject().put("http.port", HTTP_PORT));
        // We pass the options as the second parameter of the deployVerticle method.
        vertx.deployVerticle(WebVerticle.class.getName(), options, context.asyncAssertSuccess());

        final UserModel userModel = new UserModel();
        userModel.setName("Marco");
        userModel.setSurname("Rotondi");
        userModel.setEmail("email@email.it");
        userModel.setUsername("mrc");
        userModel.setPassword("secret");

        todoModel = new TodoModel();
        todoModel.setTodoText("Appointment with All");
        todoModel.setUser(userModel);
    }

    @After
    public void tearDown(TestContext context) {
        vertx.close(context.asyncAssertSuccess());
    }

    @Test
    public void whenRequestRootReturnIndexPage(final TestContext context) {
        final Async async = context.async();

        vertx.createHttpClient().getNow(HTTP_PORT, "localhost", "/", response -> response.handler(body -> {
            context.assertTrue(body.toString().contains("<title>Todo Vert.X App</title>"));
            async.complete();
        }));
    }

    @Test
    public void createNewTodo(final TestContext context) {
        final Async async = context.async();

        final String bodyData = Json.encodePrettily(todoModel);
        final String bodyLength = Integer.toString(bodyData.length());

        vertx.createHttpClient().post(HTTP_PORT, "localhost", "/api/todo")
                .putHeader("content-type", "application/json")
                .putHeader("content-length", bodyLength)
                .handler(response -> {
                    context.assertEquals(response.statusCode(), 201);
                    context.assertTrue(response.headers().get("content-type").contains("application/json"));
                    response.bodyHandler(body -> {
                        final TodoModel todo = Json.decodeValue(body.toString(), TodoModel.class);
                        context.assertEquals(todo.getTodoText(), "Appointment with All");
                        context.assertNotNull(todoModel.getId());
                        async.complete();
                    });
                })
                .write(bodyData)
                .end();
    }




}
