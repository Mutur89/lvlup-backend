package com.lvlup.tienda.dtos;

public class LoginResponseDto {

    private String token;
    private Userdto user;

    // Constructores
    public LoginResponseDto() {
    }

    public LoginResponseDto(String token, Userdto user) {
        this.token = token;
        this.user = user;
    }

    // Getters y Setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Userdto getUser() {
        return user;
    }

    public void setUser(Userdto user) {
        this.user = user;
    }
}