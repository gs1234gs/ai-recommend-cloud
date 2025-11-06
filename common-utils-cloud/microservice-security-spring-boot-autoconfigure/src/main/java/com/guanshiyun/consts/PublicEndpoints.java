package com.guanshiyun.consts;

import java.util.List;

public class PublicEndpoints {
    public static final List<String> PERMSSION_WHITE_LIST = List.of(
            "/signInUp/signIn",
            "/signInUp/signUp",
            "/logout",
            "/refresh",
            "/verify",
            "/reset/forget",
            "/customer/use");
}
