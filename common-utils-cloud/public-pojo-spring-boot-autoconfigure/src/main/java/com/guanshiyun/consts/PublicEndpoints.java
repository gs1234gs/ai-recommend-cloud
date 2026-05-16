package com.guanshiyun.consts;

import java.util.List;

public class PublicEndpoints {
    public static final List<String> PERMSSION_WHITE_LIST = List.of(
            "/sys-api/signInUp/signIn",
            "/sys-api/signInUp/signUp",
            "/sys-api/signInUp/findCode",
            "/sys-api/refresh",
            "/sys-api/verify",
            "/sys-api/reset/forget",
            "/sys-api/customer/use",
            "/goods-api/product/carousal/findByType/",
            "/swagger-ui.html",
            "/swagger-ui/swagger-ui.html",
            "/swagger-ui/index.html",
            "/v3/api-docs",
            "/v3/api-docs/**",
            "/webjars/**");
    public static final List<String> PERMISSION_WHITE_PREFIX_LIST = List.of(
            "/goods-api/product/carousal/findByType/",
            "/v3/api-docs/"
            );

    public static final List<String> RECOMMEND_WHITE_LIST = List.of(
            "/goods-api/product/carousal/findByType/",
            "/v3/api-docs/",
            "/goods-api/recommendProduct/"
    );
}
