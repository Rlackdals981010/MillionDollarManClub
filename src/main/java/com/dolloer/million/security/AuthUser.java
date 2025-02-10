package com.dolloer.million.security;


import lombok.Getter;

@Getter
public class AuthUser {
    private final Long userId;
    private final String name;

    public AuthUser(Long userId, String name) {
        this.userId = userId;
        this.name = name;
    }


}