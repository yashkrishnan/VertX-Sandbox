package com.example.vertx.sandbox.tests;

import com.example.vertx.sandbox.verticles.WebVerticle;
import io.vertx.core.Vertx;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class WebVerticleTest {

    private Vertx vertx;

    @Before
    public void setUp(TestContext context) {
        vertx = Vertx.vertx();
        vertx.deployVerticle(WebVerticle.class.getName(),
                context.asyncAssertSuccess());
    }

    @After
    public void tearDown(TestContext context) {
        vertx.close(context.asyncAssertSuccess());
    }

    @Test
    public void testMyApplication(TestContext context) {
        final Async async = context.async();

        vertx.createHttpClient().getNow(8080, "localhost", "/",
                httpClientResponse -> httpClientResponse.handler(buffer -> {
                    String bodyContent = buffer.toString();
                    System.out.println(bodyContent);
                    TestContext testContext = context.assertTrue(bodyContent.contains("Juice"));
                    System.out.println(testContext.toString());
                    async.complete();
                }));
    }
}