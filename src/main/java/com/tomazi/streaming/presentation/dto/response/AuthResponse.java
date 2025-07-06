package com.tomazi.streaming.presentation.dto.response;

public class AuthResponse {

    private String token;
    private UserResponse user;

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public UserResponse getUser() { return user; }
    public void setUser(UserResponse user) { this.user = user; }
}
