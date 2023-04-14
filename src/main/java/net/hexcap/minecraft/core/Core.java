package net.hexcap.minecraft.core;

import io.javalin.Javalin;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import net.hexcap.minecraft.core.config.file.FileManager;
import net.hexcap.minecraft.core.config.webserver.WebServerLauncher;
import net.hexcap.minecraft.core.event.ServerLoadEventHandler;
import net.hexcap.minecraft.core.model.config.Config;
import net.hexcap.minecraft.core.service.logger.Logger;
import org.bukkit.plugin.java.JavaPlugin;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;

@Getter
@Setter
public final class Core extends JavaPlugin {
    public static Core instance;
    private Javalin javalin;
    private HttpClient httpClient;
    private HttpRequest.Builder httpRequestBuilder;

    @Override
    public void onLoad() {
        _init();
    }

    @SneakyThrows
    @Override
    public void onEnable() {
        _registerEvents();
        Config config = new Config();
        String apiKey = config.getYaml().getString("backend.security.api-key");
        String secretKey = config.getYaml().getString("backend.security.api-secret");
        setHttpRequestBuilder(HttpRequest.newBuilder()
                .header("X-API-KEY", apiKey)
                .header("X-API-SECRET", secretKey));
    }

    @Override
    public void onDisable() {
        if (getJavalin() != null) getJavalin().close();
    }

    private void _init() {
        instance = this;
        setHttpClient(HttpClient.newHttpClient());
        FileManager fileManager = new FileManager();
        fileManager._init();
        WebServerLauncher launcher = new WebServerLauncher();
        launcher.run()
                .thenAccept(this::setJavalin);
    }

    private void _registerEvents() {
        getServer().getPluginManager().registerEvents(new ServerLoadEventHandler(), this);
    }

    public Logger getHexLogger() {
        return new Logger();
    }
}
