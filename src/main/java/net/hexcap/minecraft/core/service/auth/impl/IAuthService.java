package net.hexcap.minecraft.core.service.auth.impl;

import net.hexcap.minecraft.core.Core;
import net.hexcap.minecraft.core.config.ws.WsConfig;
import net.hexcap.minecraft.core.dto.auth.RegisterDTO;
import net.hexcap.minecraft.core.model.task.Task;
import net.hexcap.minecraft.core.service.auth.AuthService;
import net.hexcap.minecraft.core.service.logger.Logger;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import static net.hexcap.minecraft.core.model.task.TaskAssignee.BACKEND;
import static net.hexcap.minecraft.core.model.task.TaskType.REGISTER;
import static net.hexcap.minecraft.core.model.task.TaskType.UNREGISTER;

public class IAuthService implements AuthService {
    private final Core core = Core.instance;
    private final Logger logger = core.getHexLogger();

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
            Task task = new Task();
            task.setType(REGISTER);
            RegisterDTO registerDTO = new RegisterDTO();
            registerDTO.setUsername(username);
            registerDTO.setEmail(email);
            registerDTO.setPassword(password);
            task.setData(registerDTO);
            StompSession session = WsConfig.getSession();
            session.send("/app/backend/tasks", task);
            return true;
        } catch (Exception e) {
            logger.error(e.getMessage());
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
            Task task = new Task();
            task.setType(UNREGISTER);
            task.setData(username);
            StompSession session = WsConfig.getSession();
            session.send("/app/backend/tasks", task);
            return true;
        } catch (Exception e) {
            logger.error(e.getMessage());
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
