package net.hexcap.minecraft.core.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.hexcap.minecraft.core.Core;
import net.hexcap.minecraft.core.dto.auth.RegisterDTO;
import net.hexcap.minecraft.core.dto.auth.UpdatePasswordDTO;
import net.hexcap.minecraft.core.dto.command.CommandDTO;
import net.hexcap.minecraft.core.model.config.Config;
import net.hexcap.minecraft.core.model.task.Task;
import net.hexcap.minecraft.core.service.logger.Logger;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static net.hexcap.minecraft.core.config.task.TaskConfig.sslContext;

public class ServerTaskHandler {
    private final Core core = Core.instance;
    private final Logger logger = core.getHexLogger();
    private final HttpClient httpClient = HttpClient.newBuilder()
            .sslContext(sslContext())
            .build();
    private final ObjectMapper mapper = new ObjectMapper();


    public void handle(Task task) {
        switch (task.getType()) {
            case "REGISTER":
                handleRegister(task);
                break;
            case "UNREGISTER":
                handleUnregister(task);
                break;
            case "UPDATE_PASSWORD":
                handleUpdatePassword(task);
                break;
            case "COMMAND":
                handleCommand(task);
                break;
        }
    }

    private void handleRegister(Task task) {
        RegisterDTO registerDTO = mapper.convertValue(task.getData(), RegisterDTO.class);
        String username = registerDTO.getUsername();
        String password = registerDTO.getPassword();
        Class<?> authMeClass = authMeClass();
        try {
            if (authMeClass == null) {
                logger.error("Hexauth not found.");
                return;
            }
            Object authMeService = authMeClass.getDeclaredConstructor().newInstance();
            Method registerMethod = authMeClass.getMethod("register", String.class, String.class);
            registerMethod.invoke(authMeService, username, password);
            logger.info("Register " + username + " to AuthMe.");
            completeTask(task.getId());
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException |
                 NoSuchMethodException e) {
            logger.error("Failed to register " + username);
        }
    }

    private void handleUnregister(Task task) {
        String username = mapper.convertValue(task.getData(), String.class);
        Class<?> authMeClass = authMeClass();
        try {
            if (authMeClass == null) {
                logger.error("Hexauth not found.");
                return;
            }
            Object authMeService = authMeClass.getDeclaredConstructor().newInstance();
            Method unRegisterMethod = authMeClass.getMethod("unRegister", String.class);
            unRegisterMethod.invoke(authMeService, username);
            logger.info("Unregister " + username + " from AuthMe.");
            completeTask(task.getId());
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException |
                 NoSuchMethodException e) {
            logger.error("Failed to unregister " + username);
        }
    }

    private void handleUpdatePassword(Task task) {
        UpdatePasswordDTO updatePasswordDTO = mapper.convertValue(task.getData(), UpdatePasswordDTO.class);
        String username = updatePasswordDTO.getUsername();
        String password = updatePasswordDTO.getPassword();
        Class<?> authMeClass = authMeClass();
        try {
            if (authMeClass == null) {
                logger.error("Hexauth not found.");
                return;
            }
            Object authMeService = authMeClass.getDeclaredConstructor().newInstance();
            Method unRegisterMethod = authMeClass.getMethod("updatePassword", String.class, String.class);
            unRegisterMethod.invoke(authMeService, username, password);
            logger.info("Unregister " + username + " from AuthMe.");
            completeTask(task.getId());
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException |
                 NoSuchMethodException e) {
            logger.error("Failed to update password.");
        }
    }

    private void handleCommand(Task task) {
        try {
            CommandDTO commandDTO = mapper.convertValue(task.getData(), CommandDTO.class);
            Bukkit.getScheduler().runTask(Core.instance, () -> {
                ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
                commandDTO.getCommands().forEach(cmd -> {
                    logger.info("Sending command: " + cmd);
                    Bukkit.dispatchCommand(console, cmd);
                });
            });
            completeTask(task.getId());
        } catch (Exception e) {
            logger.error("Failed to execute command.");
        }
    }

    private Class<?> authMeClass() {
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            return classLoader.loadClass("net.hexcap.minecraft.module.authme.service.authme.impl.IAuthMeService");
        } catch (ClassNotFoundException e) {
            logger.error("Hexauth not found.");
            return null;
        }
    }

    private void completeTask(String id) {
        try {
            Config config = new Config();
            String domain = config.getYaml().getString("backend.domain");
            String apiKey = config.getYaml().getString("backend.security.api-key");
            String secretKey = config.getYaml().getString("backend.security.api-secret");
            HttpRequest request = HttpRequest.newBuilder()
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .header("Content-Type", "application/json")
                    .headers("X-API-KEY", apiKey, "X-API-SECRET", secretKey)
                    .uri(URI.create("https://" + domain + "/api/v1/tasks/" + id + "/complete"))
                    .build();
            HttpResponse<Void> response = httpClient.send(request, HttpResponse.BodyHandlers.discarding());
            if (response.statusCode() != 200) {
                logger.error("Failed to complete task: " + id);
                return;
            }
            logger.info("Completed task: " + id);
        } catch (IOException | InterruptedException e) {
            logger.error("Failed to complete task: " + id);
        }
    }
}
