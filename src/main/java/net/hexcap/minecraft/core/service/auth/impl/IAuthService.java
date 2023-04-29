package net.hexcap.minecraft.core.service.auth.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.hexcap.minecraft.core.Core;
import net.hexcap.minecraft.core.model.config.Config;
import net.hexcap.minecraft.core.service.auth.AuthService;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class IAuthService implements AuthService {
    private final HttpClient httpClient = Core.instance.getHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();
    private final HttpRequest.Builder httpRequestBuilder = Core.instance.getHttpRequestBuilder();
    private final Config config = new Config();
    private final String baseUrl = config.getYaml().getString("backend.base-url");

    /*
     * @param username
     * @param email
     * @param password
     * Description: Check if the user is registered
     * @return Boolean
     * */
    @Override
    public Boolean isRegistered(String username) throws IOException, InterruptedException {
        HttpRequest request = httpRequestBuilder
                .uri(URI.create(baseUrl + "/users/" + username))
                .GET()
                .build();
        HttpResponse<Void> response = httpClient.send(request, HttpResponse.BodyHandlers.discarding());
        return response.statusCode() == 200;
    }


    /*
     * @param username
     * @param password
     * Description: Login the user
     * @return Boolean
     */
    @Override
    public Boolean login(String username, String password) throws IOException, InterruptedException {
        Map<String, String> body = Map.of("identifier", username, "password", password);
        String json = mapper.writeValueAsString(body);
        HttpRequest request = httpRequestBuilder
                .uri(URI.create(baseUrl + "/auth/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                .build();
        HttpResponse<Void> response = httpClient.send(request, HttpResponse.BodyHandlers.discarding());
        return response.statusCode() == 200;
    }

    /*
     * @param username
     * @param email
     * @param password
     * Description: Register the user
     * @return Boolean
     */
    @Override
    public Boolean register(String username, String email, String password) throws IOException, InterruptedException {
        Map<String, String> body = new HashMap<>();
        body.put("username", username);
        body.put("email", email);
        body.put("password", password);
        body.put("confirmPassword", password);
        String json = mapper.writeValueAsString(body);
        HttpRequest request = httpRequestBuilder
                .uri(URI.create(baseUrl + "/auth/register"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                .build();
        HttpResponse<Void> response = httpClient.send(request, HttpResponse.BodyHandlers.discarding());
        return response.statusCode() == 200;
    }

    /*
     * @param username
     * Description: Unregister the user
     * @return Boolean
     */
    @Override
    public Boolean unRegister(String username) throws IOException, InterruptedException {
        HttpRequest request = httpRequestBuilder
                .uri(URI.create(baseUrl + "/users?identifier=" + username))
                .DELETE()
                .build();
        HttpResponse<Void> response = httpClient.send(request, HttpResponse.BodyHandlers.discarding());
        return response.statusCode() == 200;
    }

    /*
     * @param username
     * @param email
     * Description: Update the user's email
     * @return Boolean
     */
    @Override
    public Boolean updateEmail(String username, String email) throws IOException, InterruptedException {
        Map<String, String> body = Map.of("email", email);
        String json = mapper.writeValueAsString(body);
        HttpRequest request = httpRequestBuilder
                .uri(URI.create(baseUrl + "/users?identifier=" + username))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                .build();
        HttpResponse<Void> response = httpClient.send(request, HttpResponse.BodyHandlers.discarding());
        return response.statusCode() == 200;
    }
}
