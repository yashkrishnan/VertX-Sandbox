package com.example.vertx.sandbox.unittests;

import com.example.vertx.sandbox.models.Juice;
import com.example.vertx.sandbox.verticles.WebVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * This is JUnit test for WebVerticle. The test uses vertx-unit, so we declare a custom runner.
 */
@RunWith(VertxUnitRunner.class)
public class WebVerticleTest {

    private Vertx vertx;
    private Integer port;

    /**
     * Before executing our test, it will deploy verticle.
     * This method instantiates a new Vertx and deploy the verticle. Then, it waits in the verticle has successfully
     * completed its start sequence (testContext.asyncAssertSuccess).
     *
     * @param testContext the test testContext.
     */
    @Before
    public void setUp(TestContext testContext) {
        // Let's configure the verticle to listen on the 'test' port (randomly picked).
        // We create deployment options and set the _configuration_ json object:
        vertx = Vertx.vertx();
        try {
            ServerSocket socket = new ServerSocket(0);
            port = socket.getLocalPort();
            socket.close();
            DeploymentOptions deploymentOptions = new DeploymentOptions().setConfig(new JsonObject().put("http.port", port));
            // Pass the options as the second parameter of the deployVerticle method.
            vertx.deployVerticle(WebVerticle.class.getName(), deploymentOptions, testContext.asyncAssertSuccess());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method, called after our test, just cleanup everything by closing the vert.x instance
     *
     * @param context the test context
     */
    @After
    public void tearDown(TestContext context) {
        vertx.close(context.asyncAssertSuccess());
    }

    /**
     * Let's ensure that our application behaves correctly.
     *
     * @param testContext the test context
     */
    @Test
    public void testVerticle(TestContext testContext) {
        // This test is asynchronous, so get an async handler to inform the test when we are done.
        final Async async = testContext.async();

        // We create a HTTP client and query our application. When we get the response we check it contains the 'Hello'
        // message. Then, we call the `complete` method on the async handler to declare this async (and here the test) done.
        // Notice that the assertions are made on the 'context' object and are not Junit assert. This ways it manage the
        // async aspect of the test the right way.
        vertx.createHttpClient().getNow(port, "localhost", "/",
                httpClientResponse -> httpClientResponse.handler(buffer -> {
                    String bodyContent = buffer.toString();
                    testContext.assertTrue(bodyContent.contains(""));
                    async.complete();
                }));
    }

    @Test
    public void checkIndexPageIsServed(TestContext context) {
        Async async = context.async();
        vertx.createHttpClient().getNow(port, "localhost", "/", response -> {
            context.assertEquals(response.statusCode(), 200);
            context.assertEquals(response.headers().get("content-type"), "text/html");
            response.bodyHandler(body -> {
                context.assertTrue(body.toString().contains("<title>My Juice Collection</title>"));
                async.complete();
            });
        });
    }

    @Test
    public void checkAdd(TestContext testContext) {
        Async async = testContext.async();
        final String json = Json.encodePrettily(new Juice("Citrus Current", "Ireland"));
        final String length = Integer.toString(json.length());
        vertx.createHttpClient().post(port, "localhost", "/api/juices")
                .putHeader("content-type", "application/json")
                .putHeader("content-length", length)
                .handler(response -> {
                    testContext.assertEquals(response.statusCode(), 201);
                    testContext.assertTrue(response.headers().get("content-type").contains("application/json"));
                    response.bodyHandler(body -> {
                        final Juice juice = Json.decodeValue(body.toString(), Juice.class);
                        testContext.assertEquals(juice.getName(), "Citrus Current");
                        testContext.assertEquals(juice.getOrigin(), "Ireland");
                        testContext.assertNotNull(juice.getId());
                        async.complete();
                    });
                })
                .write(json)
                .end();
    }
}