import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.mokabyte.web.WebVerticle;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class TodoWebVerticleTestCase {
    private static final int HTTP_PORT = 9000;
    private Vertx vertx;

    @Before
    public void setUp(final TestContext context) {
        vertx = Vertx.vertx();

        final DeploymentOptions options = new DeploymentOptions()
                .setConfig(new JsonObject().put("http.port", HTTP_PORT));
        // We pass the options as the second parameter of the deployVerticle method.
        vertx.deployVerticle(WebVerticle.class.getName(), options, context.asyncAssertSuccess());
    }

    @After
    public void tearDown(TestContext context) {
        vertx.close(context.asyncAssertSuccess());
    }

    @Test
    public void whenRequestRootReturnIndexPage(final TestContext context) {
        final Async async = context.async();

        vertx.createHttpClient().getNow(HTTP_PORT, "localhost", "/", response -> response.handler(body -> {
            context.assertTrue(body.toString().contains("Todo Vert.X App"));
            async.complete();
        }));
    }


}
