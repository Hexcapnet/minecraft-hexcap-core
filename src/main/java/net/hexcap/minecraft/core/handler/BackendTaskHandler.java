package net.hexcap.minecraft.core.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.hexcap.minecraft.core.Core;
import net.hexcap.minecraft.core.dto.auth.RegisterDTO;
import net.hexcap.minecraft.core.model.task.Task;
import net.hexcap.minecraft.core.service.auth.AuthService;
import net.hexcap.minecraft.core.service.auth.impl.IAuthService;
import net.hexcap.minecraft.core.service.logger.Logger;

public class BackendTaskHandler {
    private final Core core = Core.instance;
    private final Logger logger = core.getHexLogger();
    private final ObjectMapper mapper = new ObjectMapper();

    public void handle(Task task) {
        switch (task.getType()) {
            case REGISTER:
                handleRegister(task);
                break;
            case UNREGISTER:
                handleUnregister(task);
                break;
        }
    }

    private void handleRegister(Task task) {
        RegisterDTO registerDTO = mapper.convertValue(task.getData(), RegisterDTO.class);
        String username = registerDTO.getUsername();
        String email = registerDTO.getEmail();
        String password = registerDTO.getPassword();
        AuthService authService = new IAuthService();
        authService.register(username, email, password);
        logger.info("Register " + username + " to backend.");
    }

    private void handleUnregister(Task task) {
        String username = mapper.convertValue(task.getData(), String.class);
        AuthService authService = new IAuthService();
        authService.unRegister(username);
        logger.info("Unregister " + username + " from backend.");

    }
}
