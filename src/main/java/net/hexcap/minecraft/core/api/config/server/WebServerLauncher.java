package net.hexcap.minecraft.core.api.config.server;

import io.javalin.Javalin;
import net.hexcap.minecraft.core.Core;
import net.hexcap.minecraft.core.api.config.security.SecurityConfig;
import net.hexcap.minecraft.core.api.controller.authme.AuthMeController;
import net.hexcap.minecraft.core.api.controller.command.CommandController;
import net.hexcap.minecraft.core.service.logger.Logger;

import java.util.concurrent.CompletableFuture;


public class WebServerLauncher {

    private final Logger logger = Core.instance.getHexLogger();

    public CompletableFuture<Javalin> run() {
        AuthMeController authMeController = new AuthMeController();
        SecurityConfig securityConfig = new SecurityConfig();
        return CompletableFuture.supplyAsync(() -> {
            Javalin app = Javalin.create(config -> config.showJavalinBanner = false);

            app.before("/api/*", securityConfig);
            app.get("/api/ping", ctx -> ctx.result("pong"));
            app.post("/api/authme/register", authMeController.register());
            app.put("/api/authme/password", authMeController.updatePassword());
            app.delete("/api/authme/unregister", authMeController.unregister());
            app.post("/api/command/send", new CommandController().commandHandler());

            app.events(event -> {
                event.serverStarted(() -> {
                    logger.info("Web server is running. on port 1881");
                });
            });
            return app.start("0.0.0.0", 1881);
        });
    }
}
