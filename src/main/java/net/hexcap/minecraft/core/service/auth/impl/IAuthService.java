package net.hexcap.minecraft.core.service.auth.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.hexcap.minecraft.core.Core;
import net.hexcap.minecraft.core.dto.auth.RegisterDTO;
import net.hexcap.minecraft.core.model.config.Config;
import net.hexcap.minecraft.core.service.auth.AuthService;
import net.hexcap.minecraft.core.service.logger.Logger;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static net.hexcap.minecraft.core.config.task.TaskConfig.sslContext;

public class IAuthService implements AuthService {
    private final Core core = Core.instance;
    private final Logger logger = core.getHexLogger();
    private final HttpClient httpClient = HttpClient.newBuilder()
            .sslContext(sslContext())
            .build();

    /*
     * @param username
     * @param email
     * @param password
     * Description: Check if the user is registered
     * @return Boolean
     * */
    @Override
    public Boolean isRegistered(String username) {
        return false;
    }


    /*
     * @param username
     * @param password
     * Description: Login the user
     * @return Boolean
     */
    @Override
    public Boolean login(String username, String password) {
        return false;
    }

    /*
     * @param username
     * @param email
     * @param password
     * Description: Register the user
     * @return Boolean
     */
    @Override
    public Boolean register(String username, String email, String password) {
        try {
            Config config = new Config();
            String domain = config.getYaml().getString("backend.domain");
            String apiKey = config.getYaml().getString("backend.security.api-key");
            String apiSecret = config.getYaml().getString("backend.security.api-secret");
            ObjectMapper mapper = new ObjectMapper();
            RegisterDTO registerDTO = new RegisterDTO();
            registerDTO.setUsername(username);
            registerDTO.setEmail(email);
            registerDTO.setPassword(password);
            registerDTO.setConfirmPassword(password);
            String json = mapper.writeValueAsString(registerDTO);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://" + domain + "/api/v1/auth/register"))
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .header("Content-Type", "application/json")
                    .header("X-API-KEY", apiKey)
                    .header("X-API-SECRET", apiSecret)
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            logger.info(response.body());
            return response.statusCode() == 200;
        } catch (Exception e) {
            logger.error("Failed to register " + username + " : " + e.getMessage());
            return false;
        }
    }

    @Override
    public Boolean registerAll(List<RegisterDTO> registerDTOs) {
        try {
            Config config = new Config();
            String domain = config.getYaml().getString("backend.domain");
            String apiKey = config.getYaml().getString("backend.security.api-key");
            String apiSecret = config.getYaml().getString("backend.security.api-secret");
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(registerDTOs);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://" + domain + "/api/v1/users/save-all"))
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .header("Content-Type", "application/json")
                    .header("X-API-KEY", apiKey)
                    .header("X-API-SECRET", apiSecret)
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            boolean status = response.statusCode() == 200;
            if (status) logger.info("All players synced to backend.");
            return status;
        } catch (IOException | InterruptedException e) {
            logger.error("Failed to sync players to backend.");
            return false;
        }
    }

    /*
     * @param username
     * Description: Unregister the user
     * @return Boolean
     */
    @Override
    public Boolean unRegister(String username) {
        try {
            Config config = new Config();
            String domain = config.getYaml().getString("backend.domain");
            String apiKey = config.getYaml().getString("backend.security.api-key");
            String apiSecret = config.getYaml().getString("backend.security.api-secret");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://" + domain + "/api/v1/users/" + username))
                    .DELETE()
                    .header("X-API-KEY", apiKey)
                    .header("X-API-SECRET", apiSecret)
                    .build();
            HttpResponse<Void> response = httpClient.send(request, HttpResponse.BodyHandlers.discarding());
            return response.statusCode() == 200;
        } catch (IOException | InterruptedException e) {
            logger.error("Failed to unregister " + username + " : " + e.getMessage());
            return false;
        }
    }

    /*
     * @param username
     * @param email
     * Description: Update the user's email
     * @return Boolean
     */
    @Override
    public Boolean updateEmail(String username, String email) {
        return false;
    }
}
