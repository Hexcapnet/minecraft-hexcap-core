package net.hexcap.minecraft.core.config.task;

import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;
import net.hexcap.minecraft.core.model.config.Config;

import java.util.HashMap;
import java.util.Map;

public class TaskRequestContext implements ClientRequestFilter {

    @Override
    public void filter(ClientRequestContext requestContext) {
        Config config = new Config();
        Map<String, String> headers = new HashMap<>();
        String apiKey = config.getYaml().getString("backend.security.api-key");
        String secretKey = config.getYaml().getString("backend.security.api-secret");
        headers.put("X-API-KEY", apiKey);
        headers.put("X-API-SECRET", secretKey);
        headers.forEach(requestContext.getHeaders()::add);
    }
}
