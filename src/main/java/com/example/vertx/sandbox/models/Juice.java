package com.example.vertx.sandbox.models;

import java.util.concurrent.atomic.AtomicInteger;

public class Juice {

    private static final AtomicInteger COUNTER = new AtomicInteger(1);

    private final int id;

    private String name;

    private String origin;

    public Juice(String name, String origin) {
        this.id = COUNTER.getAndIncrement();
        this.name = name;
        this.origin = origin;
    }

    public Juice() {
        this.id = COUNTER.getAndIncrement();
    }

    public static AtomicInteger getCOUNTER() {
        return COUNTER;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }
}
