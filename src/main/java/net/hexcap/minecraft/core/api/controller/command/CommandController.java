package net.hexcap.minecraft.core.api.controller.command;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import io.javalin.http.Handler;
import net.hexcap.minecraft.core.Core;
import net.hexcap.minecraft.core.service.logger.Logger;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public class CommandController {
    private final Logger logger = Core.instance.getHexLogger();

    public Handler commandHandler() {
        return ctx -> {
            String body = ctx.body();
            ObjectMapper mapper = new ObjectMapper();
            JsonNode json = mapper.readTree(body);
            ArrayNode arrayNode = (ArrayNode) json.get("commands");
            Iterator<JsonNode> itr = arrayNode.elements();
            itr.forEachRemaining(node -> {
                String command = node.asText();
                logger.info("Command sent: " + command);
                sendCommand(command);
            });
            Map<String, Object> response = new TreeMap<>();
            response.put("status", "success");
            response.put("message", "Command sent");
            response.put("commands", arrayNode);
            ctx.json(response);
        };
    }

    private void sendCommand(String command) {
        Bukkit.getScheduler().runTask(Core.instance, () -> {
            ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
            Bukkit.getServer().dispatchCommand(console, command);
        });
    }
}
