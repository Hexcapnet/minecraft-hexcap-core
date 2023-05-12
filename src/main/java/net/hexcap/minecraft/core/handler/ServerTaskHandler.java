package net.hexcap.minecraft.core.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.hexcap.minecraft.core.Core;
import net.hexcap.minecraft.core.dto.auth.RegisterDTO;
import net.hexcap.minecraft.core.dto.command.CommandDTO;
import net.hexcap.minecraft.core.model.task.Task;
import net.hexcap.minecraft.core.service.logger.Logger;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ServerTaskHandler {
    private final Core core = Core.instance;
    private final Logger logger = core.getHexLogger();
    private final ObjectMapper mapper = new ObjectMapper();

    public void handle(Task task) {
        switch (task.getType()) {
            case "REGISTER":
                handleRegister(task);
                break;
            case "UNREGISTER":
                handleUnregister(task);
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
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
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
            Method unRegisterMethod = authMeClass.getMethod("unRegister");
            unRegisterMethod.invoke(authMeService);
            logger.info("Unregister " + username + " from AuthMe.");
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
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
        } catch (Exception e) {
            logger.error(e.getMessage());
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
}
