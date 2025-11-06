package com.guanshiyun.consts.code;
/**
 * HTTP状态码常量类
 * 包含RFC标准定义的状态码及常见扩展状态码
 */
public class HttpCodeConst {
    // --- 1xx 信息响应 ---
    /**
     * 100 Continue - 客户端应继续请求
     */
    public static final int CONTINUE = 100;

    /**
     * 101 Switching Protocols - 服务器根据客户端请求切换协议
     */
    public static final int SWITCHING_PROTOCOLS = 101;

    /**
     * 102 Processing (WebDAV) - 服务器已收到请求，正在处理
     */
    public static final int PROCESSING = 102;

    /**
     * 103 Early Hints - 用于在最终HTTP消息之前返回部分响应头
     */
    public static final int EARLY_HINTS = 103;

    // --- 2xx 成功 ---
    /**
     * 200 OK - 请求成功
     */
    public static final int OK = 200;

    /**
     * 201 Created - 请求已被实现，新资源已创建
     */
    public static final int CREATED = 201;

    /**
     * 202 Accepted - 请求已接受，处理尚未完成
     */
    public static final int ACCEPTED = 202;

    /**
     * 203 Non-Authoritative Information - 返回的元信息来自缓存副本
     */
    public static final int NON_AUTHORITATIVE_INFORMATION = 203;

    /**
     * 204 No Content - 请求成功，但无返回内容
     */
    public static final int NO_CONTENT = 204;

    /**
     * 205 Reset Content - 请求成功，客户端应重置文档视图
     */
    public static final int RESET_CONTENT = 205;

    /**
     * 206 Partial Content - 服务器处理了部分GET请求
     */
    public static final int PARTIAL_CONTENT = 206;

    /**
     * 207 Multi-Status (WebDAV) - 代表之后的消息体将是一个XML消息
     */
    public static final int MULTI_STATUS = 207;

    /**
     * 208 Already Reported (WebDAV) - 避免重复枚举相同集合成员
     */
    public static final int ALREADY_REPORTED = 208;

    /**
     * 226 IM Used - 服务器已完成对资源的GET请求
     */
    public static final int IM_USED = 226;

    // --- 3xx 重定向 ---
    /**
     * 300 Multiple Choices - 请求的资源有一系列可供选择的回馈信息
     */
    public static final int MULTIPLE_CHOICES = 300;

    /**
     * 301 Moved Permanently - 资源的URI已永久变更
     */
    public static final int MOVED_PERMANENTLY = 301;

    /**
     * 302 Found - 资源临时从不同URI响应请求
     */
    public static final int FOUND = 302;

    /**
     * 303 See Other - 对应当前请求的响应可在另一个URI上找到
     */
    public static final int SEE_OTHER = 303;

    /**
     * 304 Not Modified - 资源未修改（缓存相关）
     */
    public static final int NOT_MODIFIED = 304;

    /**
     * 305 Use Proxy - 请求的资源必须通过代理访问
     */
    public static final int USE_PROXY = 305;

    /**
     * 307 Temporary Redirect - 临时重定向（保持请求方法不变）
     */
    public static final int TEMPORARY_REDIRECT = 307;

    /**
     * 308 Permanent Redirect - 永久重定向（保持请求方法不变）
     */
    public static final int PERMANENT_REDIRECT = 308;

    // --- 4xx 客户端错误 ---
    /**
     * 400 Bad Request - 请求语法错误，服务器无法理解
     */
    public static final int BAD_REQUEST = 400;

    /**
     * 401 Unauthorized - 请求需要用户认证
     */
    public static final int UNAUTHORIZED = 401;

    /**
     * 402 Payment Required - 保留状态码
     */
    public static final int PAYMENT_REQUIRED = 402;

    /**
     * 403 Forbidden - 服务器理解请求但拒绝执行
     */
    public static final int FORBIDDEN = 403;

    /**
     * 404 Not Found - 请求资源不存在
     */
    public static final int NOT_FOUND = 404;

    /**
     * 405 Method Not Allowed - 请求方法不被允许
     */
    public static final int METHOD_NOT_ALLOWED = 405;

    /**
     * 406 Not Acceptable - 服务器无法提供客户端可接受的内容
     */
    public static final int NOT_ACCEPTABLE = 406;

    /**
     * 407 Proxy Authentication Required - 需要代理认证
     */
    public static final int PROXY_AUTHENTICATION_REQUIRED = 407;

    /**
     * 408 Request Timeout - 服务器等待请求超时
     */
    public static final int REQUEST_TIMEOUT = 408;

    /**
     * 409 Conflict - 请求与资源当前状态冲突
     */
    public static final int CONFLICT = 409;

    /**
     * 410 Gone - 资源永久性消失
     */
    public static final int GONE = 410;

    /**
     * 411 Length Required - 需要有效Content-Length头字段
     */
    public static final int LENGTH_REQUIRED = 411;

    /**
     * 412 Precondition Failed - 请求头字段条件不满足
     */
    public static final int PRECONDITION_FAILED = 412;

    /**
     * 413 Payload Too Large - 请求实体过大
     */
    public static final int PAYLOAD_TOO_LARGE = 413;

    /**
     * 414 URI Too Long - 请求URI过长
     */
    public static final int URI_TOO_LONG = 414;

    /**
     * 415 Unsupported Media Type - 不支持的媒体格式
     */
    public static final int UNSUPPORTED_MEDIA_TYPE = 415;

    /**
     * 416 Range Not Satisfiable - 请求范围不符合要求
     */
    public static final int RANGE_NOT_SATISFIABLE = 416;

    /**
     * 417 Expectation Failed - 无法满足Expect请求头字段要求
     */
    public static final int EXPECTATION_FAILED = 417;

    /**
     * 418 I'm a teapot - 彩蛋状态码（愚人节笑话）
     */
    public static final int I_AM_A_TEAPOT = 418;

    /**
     * 421 Misdirected Request - 请求被指向到无法产生响应的服务器
     */
    public static final int MISDIRECTED_REQUEST = 421;

    /**
     * 422 Unprocessable Entity (WebDAV) - 请求格式正确但语义错误
     */
    public static final int UNPROCESSABLE_ENTITY = 422;

    /**
     * 423 Locked (WebDAV) - 资源被锁定
     */
    public static final int LOCKED = 423;

    /**
     * 424 Failed Dependency (WebDAV) - 由于之前的请求失败导致当前请求失败
     */
    public static final int FAILED_DEPENDENCY = 424;

    /**
     * 425 Too Early - 服务器拒绝处理可能被重放的风险请求
     */
    public static final int TOO_EARLY = 425;

    /**
     * 426 Upgrade Required - 客户端应切换到TLS/1.0
     */
    public static final int UPGRADE_REQUIRED = 426;

    /**
     * 428 Precondition Required - 要求先决条件
     */
    public static final int PRECONDITION_REQUIRED = 428;

    /**
     * 429 Too Many Requests - 客户端发送过多请求
     */
    public static final int TOO_MANY_REQUESTS = 429;

    /**
     * 431 Request Header Fields Too Large - 请求头字段过大
     */
    public static final int REQUEST_HEADER_FIELDS_TOO_LARGE = 431;

    /**
     * 451 Unavailable For Legal Reasons - 因法律原因不可用
     */
    public static final int UNAVAILABLE_FOR_LEGAL_REASONS = 451;

    // --- 5xx 服务器错误 ---
    /**
     * 500 Internal Server Error - 服务器内部错误
     */
    public static final int INTERNAL_SERVER_ERROR = 500;

    /**
     * 501 Not Implemented - 服务器不支持请求的功能
     */
    public static final int NOT_IMPLEMENTED = 501;

    /**
     * 502 Bad Gateway - 网关或代理从上游服务器收到无效响应
     */
    public static final int BAD_GATEWAY = 502;

    /**
     * 503 Service Unavailable - 服务暂时不可用
     */
    public static final int SERVICE_UNAVAILABLE = 503;

    /**
     * 504 Gateway Timeout - 网关超时
     */
    public static final int GATEWAY_TIMEOUT = 504;

    /**
     * 505 HTTP Version Not Supported - 不支持的HTTP版本
     */
    public static final int HTTP_VERSION_NOT_SUPPORTED = 505;

    /**
     * 506 Variant Also Negotiates - 服务器存在内部配置错误
     */
    public static final int VARIANT_ALSO_NEGOTIATES = 506;

    /**
     * 507 Insufficient Storage (WebDAV) - 服务器无法存储完成请求所需内容
     */
    public static final int INSUFFICIENT_STORAGE = 507;

    /**
     * 508 Loop Detected (WebDAV) - 服务器处理请求时检测到无限循环
     */
    public static final int LOOP_DETECTED = 508;

    /**
     * 510 Not Extended - 需要扩展请求
     */
    public static final int NOT_EXTENDED = 510;

    /**
     * 511 Network Authentication Required - 需要网络认证
     */
    public static final int NETWORK_AUTHENTICATION_REQUIRED = 511;

    // 防止实例化
    private HttpCodeConst() {
        throw new IllegalStateException("常量类不可实例化");
    }
}
