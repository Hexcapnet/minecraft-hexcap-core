package net.hexcap.minecraft.core.service.auth;

import net.hexcap.minecraft.core.dto.auth.RegisterDTO;

import java.util.List;

public interface AuthService {
    Boolean isRegistered(String username);

    Boolean login(String username, String password);

    Boolean register(String username, String email, String password);
    Boolean registerAll(List<RegisterDTO> registerDTOs);

    Boolean unRegister(String username);

    Boolean updateEmail(String username, String email);
}
