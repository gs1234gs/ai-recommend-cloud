package com.guanshiyun.consts;

import java.util.List;

public class PublicEndpoints {
    public static final List<String> PERMSSION_WHITE_LIST = List.of(
            "/sys-api/signInUp/signIn",
            "/sys-api/signInUp/signUp",
            "/sys-api/logout",
            "/sys-api/refresh",
            "/sys-api/verify",
            "/sys-api/reset/forget",
            "/sys-api/customer/use");
}
