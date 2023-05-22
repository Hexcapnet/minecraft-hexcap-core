package net.hexcap.minecraft.core.config.task;

import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;
import net.hexcap.minecraft.core.Core;
import net.hexcap.minecraft.core.model.config.Config;
import net.hexcap.minecraft.core.service.logger.Logger;

import java.util.HashMap;
import java.util.Map;

public class TaskRequestContext implements ClientRequestFilter {
    private final Core core = Core.getInstance();
    private final Logger logger = core.getHexLogger();

    @Override
    public void filter(ClientRequestContext requestContext) {
        Config config = new Config();
        Map<String, String> headers = new HashMap<>();
        String apiKey = config.getYaml().getString("backend.security.api-key");
        String secretKey = config.getYaml().getString("backend.security.api-secret");
        headers.put("X-API-KEY", apiKey);
        headers.put("X-API-SECRET", secretKey);
        headers.forEach(requestContext.getHeaders()::add);
        logger.info("Listening for tasks...");
    }
}
