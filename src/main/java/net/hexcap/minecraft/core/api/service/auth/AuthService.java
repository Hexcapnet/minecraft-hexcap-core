package net.hexcap.minecraft.core.api.service.auth;

public interface AuthService {
    Boolean register(String username, String email, String password);
}
