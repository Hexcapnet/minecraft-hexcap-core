package net.hexcap.minecraft.core.api.controller.authme;


import io.javalin.http.Handler;
import net.hexcap.minecraft.module.authme.service.authme.AuthMeService;
import net.hexcap.minecraft.module.authme.service.authme.impl.IAuthMeService;

public class AuthMeController {

    private final AuthMeService authMeService;

    public AuthMeController() {
        authMeService = new IAuthMeService();
    }

    public Handler register() {
        return ctx -> {
            String username = ctx.queryParam("username");
            String password = ctx.queryParam("password");
            if(username == null || username.isBlank() || password == null || password.isBlank()) {
                ctx.status(400);
                ctx.result("Bad request");
                return;
            }
            authMeService.register(username, password);
            ctx.result("Register: " + username);
        };
    }

    public Handler unregister() {
        return ctx -> {
            String username = ctx.queryParam("username");
            if(username == null || username.isBlank()) {
                ctx.status(400);
                ctx.result("Bad request");
                return;
            }
            authMeService.unRegister(username);
            ctx.result("Unregister: " + username);
        };
    }

    public Handler updatePassword() {
        return ctx -> {
            String username = ctx.queryParam("username");
            String password = ctx.queryParam("password");
            if(username == null || username.isBlank() || password == null || password.isBlank()) {
                ctx.status(400);
                ctx.result("Bad request");
                return;
            }
            ctx.result("Update password: " + username + " " + password);
        };
    }
}
