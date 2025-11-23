package com.lvlup.tienda.security;

import io.jsonwebtoken.Jwts;

import javax.crypto.SecretKey;

public class TokenJwtConfig {
    // Genera una clave secreta para firmar los tokens JWT
    public static final SecretKey SECRET_KEY = Jwts.SIG.HS256.key().build();
    public static final String JWT_TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final String CONTENT_TYPE = "application/json";
}
