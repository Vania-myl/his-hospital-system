package com.his.common.util;

import com.his.common.LoginUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT 工具：生成 / 解析 token
 */
public class JwtUtil {

    private static final byte[] SECRET_KEY = "his-hospital-system-secret-key-2026".getBytes(StandardCharsets.UTF_8);
    private static final long EXPIRE_MILLIS = 2 * 60 * 60 * 1000L;

    public static String generateToken(LoginUser user) {
        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("id", user.getId())
                .claim("realname", user.getRealname())
                .claim("role", user.getRole())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE_MILLIS))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    public static LoginUser parseToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
        LoginUser user = new LoginUser();
        user.setId(toInt(claims.get("id")));
        user.setUsername(claims.getSubject());
        user.setRealname((String) claims.get("realname"));
        user.setRole((String) claims.get("role"));
        return user;
    }

    private static Integer toInt(Object value) {
        return value == null ? null : ((Number) value).intValue();
    }
}
