package com.example.vertx.sandbox.verticles;

import com.example.vertx.sandbox.models.Juice;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;

import java.util.LinkedHashMap;
import java.util.Map;

public class WebVerticle extends AbstractVerticle {

    private Map<Integer, Juice> products = new LinkedHashMap<>();

    @Override
    public void start(Future<Void> voidFuture) {
        int port = config().getInteger("http.port", 8080);
        System.out.println(port);
        Router router = Router.router(vertx);
        createSomeData();
        deployWebVerticle(port, router, voidFuture);
    }

    private void deployWebVerticle(int port, Router router, Future<Void> voidFuture) {
        // Serve static resources from the /assets directory
        router.route("/").handler(routingContext -> {
            HttpServerResponse httpServerResponse = routingContext.response();
            httpServerResponse.sendFile("assets/index.html");
        });

        router.route("/").handler(StaticHandler.create().setIndexPage("assets/index.html"));

        router.route("/").handler(StaticHandler.create("assets/index.html"));

        router.route("/test").handler(routingContext -> {
            HttpServerResponse httpServerResponse = routingContext.response();
            httpServerResponse.putHeader("content-type", "text/html").end("<h1>Test Vert.x 3 application</h1>");
        });

        router.route("/api/juices*").handler(BodyHandler.create());

        router.get("/api/juices").handler(this::getAllJuices);

        router.post("/api/juices").handler(this::addOne);

        router.delete("/api/juices/:id").handler(this::deleteOne);

        router.get("/api/juices/:id").handler(this::getOne);

        router.put("/api/juices/:id").handler(this::updateOne);

        // Create the HTTP server and pass the "accept" method to the request handler.
        // Retrieve the port from the configuration, default to 8080.
        vertx.createHttpServer()
                .requestHandler(router::accept).listen(port, result -> {
                    if (result.succeeded()) {
                        voidFuture.complete();
                    } else {
                        voidFuture.fail(result.cause());
                    }
                }
        );
    }

    private void createSomeData() {
        Juice apple = new Juice("Apple Refresh - Carbonated", "Scotland, Islay");
        products.put(apple.getId(), apple);
        Juice orange = new Juice("Orange Splash - Pulpy", "Scotland, Island");
        products.put(orange.getId(), orange);
    }

    private void getAllJuices(RoutingContext routingContext) {
        routingContext.response()
                .putHeader("content-type", "application/json; charset=utf-8")
                .end(Json.encodePrettily(products.values()));
    }

    private void addOne(RoutingContext routingContext) {
        final Juice juice = Json.decodeValue(routingContext.getBodyAsString(),
                Juice.class);
        products.put(juice.getId(), juice);
        routingContext.response().setStatusCode(201).putHeader("content-type", "application/json; charset=utf-8").end(Json.encodePrettily(juice));
    }

    private void deleteOne(RoutingContext routingContext) {
        String id = routingContext.request().getParam("id");
        if (id == null) {
            routingContext.response().setStatusCode(400).end();
        } else {
            Integer idAsInteger = Integer.valueOf(id);
            products.remove(idAsInteger);
        }
        routingContext.response().setStatusCode(204).end();
    }

    private void getOne(RoutingContext routingContext) {
        final String id = routingContext.request().getParam("id");
        if (id == null) {
            routingContext.response().setStatusCode(400).end();
        } else {
            final Integer idAsInteger = Integer.valueOf(id);
            Juice juice = products.get(idAsInteger);
            if (juice == null) {
                routingContext.response().setStatusCode(404).end();
            } else {
                routingContext.response()
                        .putHeader("content-type", "application/json; charset=utf-8")
                        .end(Json.encodePrettily(juice));
            }
        }
    }

    private void updateOne(RoutingContext routingContext) {
        final String id = routingContext.request().getParam("id");
        JsonObject json = routingContext.getBodyAsJson();
        if (id == null || json == null) {
            routingContext.response().setStatusCode(400).end();
        } else {
            final Integer idAsInteger = Integer.valueOf(id);
            Juice juice = products.get(idAsInteger);
            if (juice == null) {
                routingContext.response().setStatusCode(404).end();
            } else {
                juice.setName(json.getString("name"));
                juice.setOrigin(json.getString("origin"));
                routingContext.response()
                        .putHeader("content-type", "application/json; charset=utf-8")
                        .end(Json.encodePrettily(juice));
            }
        }
    }
}
