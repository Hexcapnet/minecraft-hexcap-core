package net.hexcap.minecraft.core;

import io.javalin.Javalin;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import net.hexcap.minecraft.core.config.FileManager;
import net.hexcap.minecraft.core.event.ServerLoadEventHandler;
import net.hexcap.minecraft.core.model.config.Config;
import net.hexcap.minecraft.core.service.logger.Logger;
import net.hexcap.module.util.generator.PasswordGenerator;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

@Getter
@Setter
public final class Core extends JavaPlugin {
    public static Core instance;
    @Getter
    @Setter
    private static Javalin javalin;
    private HttpClient httpClient;
    private HttpRequest.Builder httpRequestBuilder;

    private static TrustManager[] trustAllCerts() {
        return new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    public void checkClientTrusted(
                            X509Certificate[] certs, String authType) {
                    }

                    public void checkServerTrusted(
                            X509Certificate[] certs, String authType) {
                    }
                }
        };
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

    @Override
    @SneakyThrows
    public void onLoad() {
        _init();
    }

    private void _registerEvents() {
        getServer().getPluginManager().registerEvents(new ServerLoadEventHandler(), this);
    }

    public Logger getHexLogger() {
        return new Logger();
    }

    private void _init() throws NoSuchAlgorithmException, KeyManagementException {
        instance = this;
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustAllCerts(), new SecureRandom());
        HttpClient.Builder builder = HttpClient.newBuilder()
                .sslContext(sslContext);
        setHttpClient(builder.build());
        FileManager fileManager = new FileManager();
        fileManager._init();
        Config config = new Config();
        FileConfiguration yaml = config.getYaml();
        yaml.addDefault("token", new PasswordGenerator().useNumbers(true).useUpperCase(true).useSpecialChars(false).setLength(32).build());
        config.setYaml(yaml);
    }
}
