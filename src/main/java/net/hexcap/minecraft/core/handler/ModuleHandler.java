package net.hexcap.minecraft.core.handler;

import net.hexcap.minecraft.core.Core;
import net.hexcap.minecraft.core.service.logger.Logger;
import org.bukkit.Bukkit;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;

public class ModuleHandler {
    private final Logger logger = Core.instance.getHexLogger();
    private List<Plugin> modules = new ArrayList<>();

    public void loadModules() throws InvalidPluginException, InvalidDescriptionException, IOException {
        logger.info("Loading modules... please wait.");
        File dataFolder = Core.getPlugin(Core.class).getDataFolder();
        File modules = new File(dataFolder.getPath(), "modules");
        boolean haveDataFolder = dataFolder.exists() || dataFolder.mkdir();
        boolean haveModulesFolder = modules.exists() || modules.mkdir();
        if (!haveDataFolder || !haveModulesFolder) {
            logger.error("Data folder or modules folder is not exist.");
            return;
        }
        File[] files = modules.listFiles();
        if (files == null) return;
        for (File file : files) {
            if (file.getName().endsWith(".jar")) {
                JarFile jarFile = new JarFile(file);
                String iss = jarFile.getManifest().getMainAttributes().getValue("Module-Issuer");
                if (iss == null || !iss.equals("hexcap.net")) {
                    logger.error("Module &7" + file.getName() + " &ais not valid.");
                    continue;
                }
                Plugin plugin = Bukkit.getPluginManager().loadPlugin(file);
                this.modules.add(plugin);
                logger.info("Module " + file.getName() + " is loaded.");
                assert plugin != null;
                Bukkit.getPluginManager().enablePlugin(plugin);
            }
        }
    }

    public void unloadModules() {
        logger.info("Unloading modules... please wait.");
        for (Plugin plugin : modules) {
            Bukkit.getPluginManager().disablePlugin(plugin);
            logger.info("Module " + plugin.getName() + " is unloaded.");
        }
    }


}
