////package com.aliyun.oss;
////
////import com.fasterxml.jackson.core.JsonProcessingException;
////import com.fasterxml.jackson.databind.ObjectMapper;
////import com.fasterxml.jackson.databind.SerializationFeature;
////import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
////import io.jsonwebtoken.Claims;
////import io.jsonwebtoken.Jws;
////import io.jsonwebtoken.Jwts;
////
////import javax.crypto.SecretKey;
////import java.util.Date;
////import java.util.Map;
////
/////**
//// * JJwtUtils - 用于生成和校验JWT的工具类
//// */
////public class JwtUtils {
////
////    // 用于JSON序列化和反序列化
////    private static final ObjectMapper objectMapper = new ObjectMapper();
////    // 创建token密钥的key,并且使用 HMAC-SHA-256 加密算法
////    private final static SecretKey key = Jwts.SIG.HS256.key().build();
////
////    /**
////     * 生成token
////     *
////     * @return 生成的JWT字符串
////     * @throws JsonProcessingException JSON处理异常
////     */
////    public static String genToken(
////
////            Map<String, Object> map, int userId
////
////    ) throws JsonProcessingException {
////        objectMapper.registerModule(new JavaTimeModule()); // 支持 Java 8 时间类型
////        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // 不使用时间戳
////        //将用户id转为字符串
////        String id = String.valueOf(userId);
////        // 将Map序列化为JSON字符串
////        String userMap = objectMapper.writeValueAsString(map);
////
////        // 构建JWT，设置签发人、签发者、自定义载荷数据，并签名生成JWt
////        String jwt = Jwts.builder()
////                // 设置JWT针对的用户
////                .subject(id)
////                // 设置签发人
////                .issuer("DTechInnoCMPbackend")
////                // 自定义载荷数据，存储序列化的Map
////                .claim("token", userMap)
////                // 使用密钥进行签名
////                .signWith(key)
////                // 设置token过期时间为20小时
////                .expiration(new Date(
////                        System.currentTimeMillis() +
////                                3600 * 10000*2))
////                // 生成token字符串
////                .compact();
////
////        return jwt;
////    }
////
////    /**
////     * token 校验
////     *
////     * @param token JWT字符串
////     * @return 校验结果消息
////     */
////    public static Claims checkToken(String token) {
//////        String msg = null;
//////        try {
////            // 解析JWT并验证签名
////            Jws<Claims> claimsJws = Jwts.parser()
////                    .verifyWith(key)
////                    .build()
////                    .parseSignedClaims(token);
////
////
////
////
////            // 获取载荷的一些数据信息,payload 为一个map对象
////            Claims payload = claimsJws.getPayload();
////        return payload;
////            }
////            /*// 获取针对的用户
////            String subject = payload.getSubject();
////            // 获取签发人
////            String issuer = payload.getIssuer();
////            // 获取过期时间
////            Date expiration = payload.getExpiration();
////
////            // 反序列化"name" claim，该claim存储了序列化的Map
////            String mapJson = payload.get("userMap", String.class);
////
////            // 反序列化ad claim
////            Map<String, Object> additionalInfo = objectMapper.readValue(
////                    mapJson, new TypeReference<Map<String, Object>>() {});*/
////
////
//////            // 打印载荷信息
//////            System.out.println("标准载荷：issuer===>" + issuer + "\texpiration=>" + expiration +
//////                    "\n" + "自定义载荷数据：" + additionalInfo );
////
//// /*           return "token正确";
////        } catch (SignatureException se) {
////            msg = "密钥错误";
////            return msg;
////        } catch (MalformedJwtException me) {
////            msg = "密钥算法或者密钥转换错误";
////            return msg;
////        } catch (MissingClaimException mce) {
////            msg = "密钥缺少校验数据";
////            return msg;
////        } catch (ExpiredJwtException ee) {
////            msg = "密钥已过期";
////            return msg;
////        } catch (JwtException jwte) {
////            msg = "密钥解析错误";
////            return msg;
////        } catch (JsonProcessingException e) {
////            throw new RuntimeException(e);
////        }
////    }*/
////
//
//package com.guanshiyun.security.jwt;
//
//import com.fasterxml.jackson.core.type.TypeReference;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.SerializationFeature;
//import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.Jws;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.security.SecurityException;
//
//import javax.crypto.SecretKey;
//import javax.crypto.spec.SecretKeySpec;
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.Date;
//import java.util.Map;
//
///**
// * JJwtUtils - 用于生成和校验JWT的工具类
// */
//public class JwtUtils {
//
//    private static final ObjectMapper objectMapper = new ObjectMapper();
//    private static final String SECRET_KEY_PATH = "jwt_secret.key"; // 密钥存储文件路径
//
//    static {
//        objectMapper.registerModule(new JavaTimeModule());
//        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
//    }
//
//    /**
//     * 从文件读取或生成密钥
//     */
//    private static SecretKey getSecretKey() throws IOException {
//        Path path = Paths.get(SECRET_KEY_PATH);
//        // 如果密钥文件不存在，生成新密钥并保存
//        if (!Files.exists(path)) {
//            SecretKey newKey = Jwts.SIG.HS256.key().build();
//            Files.write(path, newKey.getEncoded());
//        }
//        // 读取密钥文件内容
//        byte[] keyBytes = Files.readAllBytes(path);
//        return new SecretKeySpec(keyBytes, "HmacSHA256");
//    }
//
//    /**
//     * 生成Token
//     */
//    public static String genToken(Map<String, Object> map, String userId) throws IOException {
//        SecretKey key = getSecretKey(); // 从文件获取密钥
//        String userMap = objectMapper.writeValueAsString(map);
//
//        return Jwts.builder()
//                .subject(userId)
//                .issuer("sys-department-app")
//                .claim("token", userMap)
//                .signWith(key)
//                .expiration(new Date(System.currentTimeMillis() + 3600 * 10000 * 2))
//                .compact();
//    }
//
//    /**
//     * 校验Token并返回Claims
//     */
//    public static Claims checkToken(String token) throws IOException {
//        SecretKey key = getSecretKey(); // 从文件获取密钥
//        try {
//            Jws<Claims> claimsJws = Jwts.parser()
//                    .verifyWith(key)
//                    .build()
//                    .parseSignedClaims(token);
//            return claimsJws.getPayload();
//        } catch (SecurityException e) {
//            throw new SecurityException("Token签名无效或密钥不匹配", e);
//        }
//    }
//    //返回token解析是否成功
//    public static boolean isToken(String token) throws IOException {
//        SecretKey key = getSecretKey(); // 从文件获取密钥
//        try {
//            Jws<Claims> claimsJws = Jwts.parser()
//                    .verifyWith(key)
//                    .build()
//                    .parseSignedClaims(token);
//            return true;
//        } catch (SecurityException e) {
//            return false;
//        }
//    }
//
//    // 从Claims中获取Map, 并反序列化
//    public static Map<String, Object> getMap(Claims claims) throws IOException {
//        String mapJson = claims.get("token", String.class);
//        return objectMapper.readValue(mapJson, new TypeReference<Map<String, Object>>() {});
//    }
//
//}