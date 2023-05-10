package net.hexcap.minecraft.core.service.auth;

public interface AuthService {
    Boolean isRegistered(String username);

    Boolean login(String username, String password);

    Boolean register(String username, String email, String password);

    Boolean unRegister(String username);

    Boolean updateEmail(String username, String email);
}
