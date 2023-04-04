package net.hexcap.minecraft.core.service.auth;

import java.io.IOException;

public interface AuthService {
    Boolean isRegistered(String username) throws IOException, InterruptedException;

    Boolean login(String username, String password) throws IOException, InterruptedException;

    Boolean register(String username,String email, String password) throws IOException, InterruptedException;

    Boolean unRegister(String username) throws IOException, InterruptedException;

    Boolean updateEmail(String username, String email) throws IOException, InterruptedException;
}
