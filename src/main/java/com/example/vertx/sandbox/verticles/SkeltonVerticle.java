package com.example.vertx.sandbox.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;

public class SkeltonVerticle extends AbstractVerticle {

    @Override
    public void start(Future<Void> voidFuture) {
        int port = config().getInteger("http.port", 8080);
        System.out.println(port);
        vertx.createHttpServer().requestHandler(request -> {
            request.response().end("Test verticle serve");
        }).listen(port, result -> {
            if (result.succeeded()) {
                System.out.println("Success");
                voidFuture.succeeded();
            } else {
                System.out.println("Failure");
                voidFuture.fail(result.cause());
            }
        });
    }
}
