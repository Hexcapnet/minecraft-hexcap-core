package net.hexcap.minecraft.core.config.ws;

import net.hexcap.minecraft.core.Core;
import net.hexcap.minecraft.core.handler.BackendTaskHandler;
import net.hexcap.minecraft.core.handler.ServerTaskHandler;
import net.hexcap.minecraft.core.model.config.Config;
import net.hexcap.minecraft.core.model.task.Task;
import net.hexcap.minecraft.core.service.logger.Logger;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.websocket.ContainerProvider;
import javax.websocket.WebSocketContainer;
import java.lang.reflect.Type;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class WsConfig {
    private static final Core core = Core.instance;
    private static final Logger logger = core.getHexLogger();
    private static StompSession session;

    public static StompSession getSession() {
        if (session == null) {
            logger.warn("Reconnecting to the backend server...");
            connect();
            return session;
        }
        return session;
    }

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

    public static void connect() {
        Config config = new Config();
        String domain = config.getYaml().getString("backend.domain");
        String url = "https://" + domain + "/api/v1/ws";
        String apiKey = config.getYaml().getString("backend.security.api-key");
        String secretKey = config.getYaml().getString("backend.security.api-secret");
        WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
        headers.add("X-API-KEY", apiKey);
        headers.add("X-API-SECRET", secretKey);

        if (domain == null) {
            logger.error("Please check your configurations. (config.yml)");
            Bukkit.getPluginManager().disablePlugin(WsConfig.core);
            return;
        }
        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts(), new SecureRandom());

            StompSessionHandler sessionHandler = new StompSessionHandler();


            StandardWebSocketClient webSocketClient = new StandardWebSocketClient();
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.setDefaultMaxBinaryMessageBufferSize(1024 * 1024);
            container.setDefaultMaxTextMessageBufferSize(1024 * 1024);
            webSocketClient.setUserProperties(Collections.singletonMap("org.apache.tomcat.websocket.SSL_CONTEXT", sslContext));

            WebSocketStompClient client =
                    new WebSocketStompClient(new SockJsClient(List.of(new WebSocketTransport(webSocketClient))));
            client.setMessageConverter(new MappingJackson2MessageConverter());
            client.setDefaultHeartbeat(new long[]{0, 0});
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
            client.setMessageConverter(new MappingJackson2MessageConverter());
            session = client.connect(url, headers, sessionHandler).get();
            logger.info("Connected to the backend server.");
        } catch (HttpClientErrorException | InterruptedException | ExecutionException e) {
            logger.error("Please check your configurations. (config.yml)");
            Bukkit.getPluginManager().disablePlugin(Core.getInstance());
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw new RuntimeException(e);
        }
    }

    public static class StompSessionHandler extends StompSessionHandlerAdapter {
        private final Core core = Core.instance;
        private final Logger logger = core.getHexLogger();
        private final ServerTaskHandler serverTaskHandler = new ServerTaskHandler();
        private final BackendTaskHandler backendTaskHandler = new BackendTaskHandler();

        @Override
        public void afterConnected(StompSession session, @NotNull StompHeaders connectedHeaders) {
            session.subscribe("/topic/server/tasks", new StompFrameHandler() {

                @Override
                public @NotNull Type getPayloadType(@NotNull StompHeaders headers) {
                    return Task.class;
                }

                @Override
                public void handleFrame(@NotNull StompHeaders headers, Object payload) {
                    Task task = (Task) payload;
                    serverTaskHandler.handle(task);
                }
            });
            session.subscribe("/topic/backend/tasks", new StompFrameHandler() {
                @Override
                public @NotNull Type getPayloadType(@NotNull StompHeaders headers) {
                    return Task.class;
                }

                @Override
                public void handleFrame(@NotNull StompHeaders headers, Object payload) {
                    Task task = (Task) payload;
                    backendTaskHandler.handle(task);
                }
            });
        }

        @Override
        public void handleException(@NotNull StompSession session, StompCommand command, @NotNull StompHeaders headers, byte @NotNull [] payload, Throwable exception) {
            logger.error("Exception occurred: " + exception.getMessage());
        }
    }
}
