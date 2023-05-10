package net.hexcap.minecraft.core.config.file;

import net.hexcap.minecraft.core.Core;
import net.hexcap.minecraft.core.service.logger.Logger;

import java.io.File;
import java.io.IOException;

public class FileManager {
    public static final String pluginFolder = Core.instance.getDataFolder().getAbsolutePath();
    private final Logger logger = Core.instance.getHexLogger();

    public void _init() {
        for (FileType fileType : FileType.values()) {
            File file = new File(Core.instance.getDataFolder(), fileType.getFileName());
            if (!file.exists()) {
                try {
                    Core.instance.saveResource(fileType.getFileName(), true);
                    if (file.createNewFile()) logger.info(fileType.getFileName() + " created.");
                } catch (IOException e) {
                    logger.error(e.getClass().getSimpleName());
                }
            }
            File moduleFolder = new File(pluginFolder + File.separator + "modules");
            if(!moduleFolder.exists() && moduleFolder.mkdir()) logger.info("modules folder created.");
        }
    }
}
