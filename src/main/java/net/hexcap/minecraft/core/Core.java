package net.hexcap.minecraft.core;

import lombok.Getter;
import lombok.Setter;
import net.hexcap.minecraft.core.config.file.FileManager;
import net.hexcap.minecraft.core.config.task.TaskConfig;
import net.hexcap.minecraft.core.config.ws.WSConfig;
import net.hexcap.minecraft.core.event.ServerLoadEventHandler;
import net.hexcap.minecraft.core.handler.ModuleHandler;
import net.hexcap.minecraft.core.service.logger.Logger;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

@Getter
@Setter
public final class Core extends JavaPlugin {
    @Getter
    public static Core instance;
    public static WSConfig wsConfig;


    @Override
    public void onLoad() {
        getHexLogger().info("Hexcore is loading...");
    }

    @Override
    public void onEnable() {
        _registerEvents();
        _init();
    }

    @Override
    public void onDisable() {
        /*StompSession session = WsConfig.getSession();
        if (session != null && session.isConnected()) session.disconnect();*/
        getHexLogger().info("Plugin disabled.");

    }

    private void _registerEvents() {
        getServer().getPluginManager().registerEvents(new ServerLoadEventHandler(), this);
    }

    public Logger getHexLogger() {
        return new Logger();
    }

    private void _init() {
        try {
            instance = this;
            FileManager fileManager = new FileManager();
            fileManager._init();
            listenTasks();
            ModuleHandler moduleHandler = new ModuleHandler();
            moduleHandler.loadModules();
        } catch (InvalidPluginException | InvalidDescriptionException | IOException e) {
            getHexLogger().error(e.getMessage());
        }
    }

    private void listenTasks() {
        TaskConfig taskConfig = new TaskConfig();
        taskConfig.connect();
    }
}
