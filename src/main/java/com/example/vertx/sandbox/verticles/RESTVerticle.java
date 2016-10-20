package com.example.vertx.sandbox.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;

public class RESTVerticle extends AbstractVerticle {

    @Override
    public void start(Future<Void> voidFuture) {
        int port = config().getInteger("http.port", 8080);
        System.out.println(port);
        Router router = Router.router(vertx);
        router.route("/rest").handler(routingContext -> {
            HttpServerResponse httpServerResponse = routingContext.response();
            httpServerResponse.putHeader("content-type", "text/html").end("<h1>Test Vert.x 3 application - REST</h1>");
        });

        // Create the HTTP server and pass the "accept" method to the request handler.
        // Retrieve the port from the configuration, default to 8080.
        vertx.createHttpServer()
                .requestHandler(router::accept).listen(config().getInteger("http.port", 8080), result -> {
                    if (result.succeeded()) {
                        voidFuture.complete();
                    } else {
                        voidFuture.fail(result.cause());
                    }
                }
        );
    }
}
