package com.example.vertx.sandbox.applications;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;

public class VertxApplication {

    private VertxOptions vertxOptions;
    private Vertx vertx;

    public VertxApplication() {
        this.vertxOptions = new VertxOptions();
    }
}
