

package com.aliyun.oss;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.SecurityException;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Map;

/**
 * JJwtUtils - 用于生成和校验JWT的工具类
 */
public class JwtUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String SECRET_KEY_PATH = "jwt_secret.key"; // 密钥存储文件路径

    static {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    /**
     * 从文件读取或生成密钥
     */
    private static SecretKey getSecretKey() throws IOException {
        Path path = Paths.get(SECRET_KEY_PATH);
        // 如果密钥文件不存在，生成新密钥并保存
        if (!Files.exists(path)) {
            SecretKey newKey = Jwts.SIG.HS256.key().build();
            Files.write(path, newKey.getEncoded());
        }
        // 读取密钥文件内容
        byte[] keyBytes = Files.readAllBytes(path);
        return new SecretKeySpec(keyBytes, "HmacSHA256");
    }

    /**
     * 生成Token
     */
    public static String genToken(Map<String, Object> map, Object id) throws IOException {
        SecretKey key = getSecretKey(); // 从文件获取密钥
        String userMap = objectMapper.writeValueAsString(map);

        return Jwts.builder()
                .subject(String.valueOf(id))
                .issuer("guanshiyun")
                .claim("token", userMap)
                .signWith(key)
                .expiration(new Date(System.currentTimeMillis() + 3600 * 10000 * 2))
                .compact();
    }

    /**
     * 校验Token并返回Claims
     */
    public static Claims checkToken(String token) throws IOException {
        SecretKey key = getSecretKey(); // 从文件获取密钥
        try {
            Jws<Claims> claimsJws = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            return claimsJws.getPayload();
        } catch (SecurityException e) {
            throw new SecurityException("Token签名无效或密钥不匹配", e);
        }
    }
    //返回token解析是否成功
    public static boolean isToken(String token) throws IOException {
        SecretKey key = getSecretKey(); // 从文件获取密钥
        try {
            Jws<Claims> claimsJws = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (SecurityException e) {
            return false;
        }
    }

    // 从Claims中获取Map, 并反序列化
    public static Map<String, Object> getMap(Claims claims) throws IOException {
        String mapJson = claims.get("token", String.class);
        return objectMapper.readValue(mapJson, new TypeReference<Map<String, Object>>() {});
    }

}