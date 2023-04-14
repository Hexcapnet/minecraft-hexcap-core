package net.hexcap.minecraft.core.event;

import net.hexcap.minecraft.core.handler.ModuleHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.InvalidPluginException;

import java.io.IOException;

public class ServerLoadEventHandler implements Listener {
    @EventHandler
    public void onServerLoad(ServerLoadEvent event) throws InvalidPluginException, InvalidDescriptionException, IOException, InterruptedException {
        ModuleHandler moduleHandler = new ModuleHandler();
        moduleHandler.loadModules();

    }
}
