package com.example.title.pojo;

/**
 * @Create: 26/04/2020 14:01
 * @Author: Q
 */
public class LoginUser {
    private String username;
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LoginUser(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
