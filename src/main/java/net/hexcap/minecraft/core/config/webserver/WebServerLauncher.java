package net.hexcap.minecraft.core.config.webserver;

import io.javalin.Javalin;
import net.hexcap.minecraft.core.Core;
import net.hexcap.minecraft.core.api.controller.TestController;
import net.hexcap.minecraft.core.service.logger.Logger;

import java.util.concurrent.CompletableFuture;


public class WebServerLauncher {

    private final Logger logger = Core.instance.getHexLogger();

    public CompletableFuture<Javalin> run() {
        return CompletableFuture.supplyAsync(() -> {
            logger.info("Web server is running.");
            Javalin app = Javalin.create(config -> config.showJavalinBanner = false);
            app.routes(() -> {
                app.get("/test", new TestController().test());
            });
            return app.start("0.0.0.0", 1881);
        });
    }
}
