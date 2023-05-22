package net.hexcap.minecraft.core.config.task;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.sse.SseEventSource;
import net.hexcap.minecraft.core.Core;
import net.hexcap.minecraft.core.handler.ServerTaskHandler;
import net.hexcap.minecraft.core.model.config.Config;
import net.hexcap.minecraft.core.model.task.Task;
import net.hexcap.minecraft.core.service.logger.Logger;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.net.URI;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

public class TaskConfig {
    private static final Logger logger = Core.instance.getHexLogger();
    private final ServerTaskHandler serverTaskHandler = new ServerTaskHandler();

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

    public static SSLContext sslContext() {
        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts(), new SecureRandom());
            return sslContext;
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    public void connect() {
        try {
            Config config = new Config();
            String domain = config.getYaml().getString("backend.domain");
            URI uri = URI.create("https://" + domain + "/api/v1/tasks/queue");
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts(), new SecureRandom());
            Client client = ClientBuilder.newBuilder()
                    .hostnameVerifier((s, sslSession) -> true)
                    .register(TaskRequestContext.class)
                    .sslContext(sslContext).build();
            WebTarget target = client.target(uri);
            SseEventSource sseEventSource = SseEventSource
                    .target(target)
                    .reconnectingEvery(5, TimeUnit.SECONDS)
                    .build();
            sseEventSource.register(event -> {
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    Task task = mapper.readValue(event.readData(), Task.class);
                    if (task.getId() == null) return;
                    logger.info("Received a task from the backend server. Task ID -> " + task.getId());
                    serverTaskHandler.handle(task);
                } catch (JsonProcessingException e) {
                    logger.error(e.getMessage());
                }
            }, ex -> logger.error(ex.getMessage()));
            Thread thread = new Thread(sseEventSource::open);
            thread.start();
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            logger.error(e.getMessage());
        }
    }
}