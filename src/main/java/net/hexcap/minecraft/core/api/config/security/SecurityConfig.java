package net.hexcap.minecraft.core.api.config.security;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.http.servlet.JavalinServletContext;
import net.hexcap.minecraft.core.Core;
import net.hexcap.minecraft.core.model.config.Config;
import net.hexcap.minecraft.core.service.logger.Logger;
import org.jetbrains.annotations.NotNull;

public class SecurityConfig implements Handler {

    private final Logger logger = Core.instance.getHexLogger();

    @Override
    public void handle(@NotNull Context ctx) {
        String token = ctx.header("X-MC-TOKEN");
        if (token == null || token.isEmpty()) {
            JavalinServletContext context = (JavalinServletContext) ctx;
            context.getTasks().clear();
            ctx.status(401).result("Unauthorized");
            logger.error("Unauthorized request from " + ctx.req().getRemoteAddr() + " with empty token.");
            return;
        }
        String privateToken = new Config().getYaml().getString("token");
        if (!token.equals(privateToken)) {
            JavalinServletContext context = (JavalinServletContext) ctx;
            context.getTasks().clear();
            ctx.status(401).result("Unauthorized");
            logger.error("Unauthorized request from " + ctx.req().getRemoteAddr() + " with invalid token.");
        }
    }
}
