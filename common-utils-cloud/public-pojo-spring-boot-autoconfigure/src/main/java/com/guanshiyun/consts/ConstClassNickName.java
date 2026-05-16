package com.guanshiyun.consts;

/**
 * 有关redis存储的常量
 * */
public class ConstClassNickName {
    //redis 常量
    //token存储键
    public static final String REDIS_TOKEN_KEY = "guanshiyun:security:login";
    //角色存储键
    public static final String REDIS_ROLE_KEY = "guanshiyun:security:role";
    //ip存储键
    public static final String REDIS_IP_KEY = "guanshiyun:security:ip";

    //权限存储键
    public static final String REDIS_PERMISSION_KEY = "guanshiyun:security:permission";
    //用户名存储键
    public static final String REDIS_USERNAME_KEY = "guanshiyun:security:username";
    //用户id存储键
    public static final String REDIS_USERID_KEY = "guanshiyun:security:userId";

    //用户存储键
    public static final String REDIS_USER_KEY = "guanshiyun:security:user";

    //授权
    public static final String REDIS_AUTHORITY_KEY = "guanshiyun:security:authority";

    //访问路劲存储键
    public static final String REDIS_REQUEST_URL_KEY = "guanshiyun:security:path";
    //邮箱存储键
    public static final String REDIS_EMAIL_KEY = "guanshiyun:security:email";
}
