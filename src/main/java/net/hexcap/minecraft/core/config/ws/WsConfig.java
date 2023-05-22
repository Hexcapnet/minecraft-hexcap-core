package net.hexcap.minecraft.core.config.ws;

public class WsConfig {
    /*private static final Core core = Core.instance;
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
        String url = "http://local.hexcap.net/api/v1/ws";
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
            logger.error(e.getMessage());
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
                    logger.info("Received task from the backend server.");
                    logger.info(payload.toString());
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
                    logger.info("Received task from the backend server.");
                    logger.info(payload.toString());
                    Task task = (Task) payload;
                    backendTaskHandler.handle(task);
                }
            });
        }

        @Override
        public void handleException(@NotNull StompSession session, StompCommand command, @NotNull StompHeaders headers, byte @NotNull [] payload, Throwable exception) {
            logger.error("Exception occurred: " + exception.getMessage());
        }
    }*/
}
