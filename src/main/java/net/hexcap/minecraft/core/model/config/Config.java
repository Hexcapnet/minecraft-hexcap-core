package net.hexcap.minecraft.core.model.config;

import lombok.Getter;
import lombok.Setter;
import net.hexcap.minecraft.core.config.FileManager;
import net.hexcap.minecraft.core.config.FileType;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

@Getter
@Setter
public class Config {
    private File file;
    private FileConfiguration yaml;

    public FileConfiguration getYaml() {
        file = new File(FileManager.pluginFolder, FileType.CONFIG.getFileName());
        yaml = YamlConfiguration.loadConfiguration(file);
        return yaml;
    }

    public void save() {
        try {
            yaml.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reload() {
        try {
            yaml.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }
}
