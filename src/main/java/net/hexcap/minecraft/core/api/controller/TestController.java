package net.hexcap.minecraft.core.api.controller;

import io.javalin.http.Handler;

public class TestController {
    public Handler test() {
        return ctx -> ctx.result("Hello World!");
    }
}
