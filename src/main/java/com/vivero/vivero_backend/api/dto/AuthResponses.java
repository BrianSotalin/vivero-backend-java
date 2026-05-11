package com.vivero.vivero_backend.api.dto;

public class AuthResponses {
    private String token;

    public AuthResponses(String token) {
        this.token = token;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
}
