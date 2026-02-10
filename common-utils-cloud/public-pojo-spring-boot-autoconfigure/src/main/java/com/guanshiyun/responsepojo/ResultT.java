package com.guanshiyun.responsepojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.guanshiyun.code.HttpCodeConst;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;


/**
 * Result 类用于表示操作的结果。
 * <p>
 * 该类包含操作的状态码、消息、返回的数据以及可能的令牌信息。
 * 提供了静态方法来快速创建成功或错误的结果对象。
 */
@Data
@Builder// 用于创建对象时，自动填充属性的注解，使用链式调用
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)// 表示序列化时，如果属性值为 null，则不进行序列化
//@JsonDeserialize(builder = ResultT.ResultTBuilder.class)  // 关键注解：告诉 Jackson 用 Builder 反序列
//@JsonPOJOBuilder(withPrefix = "")
@Accessors(chain = true)
public class ResultT<T> {
    /**
     * 操作的状态码，例如 200 表示成功，400 表示错误。
     */
    private int code;

    /**
     * 操作的消息描述，例如 "OK" 或 "error"。
     */
    private String msg;

    /**
     * 操作返回的数据，可以是任意对象。
     */
    private T data;
    public static <T> ResultT<T> success(T data){
        return ResultT.<T>builder()
                .code(HttpCodeConst.OK)
                .msg("OK")
                .data(data)
                .build();
    }

    public static <T> ResultT<T> success(){
        return ResultT.<T>builder()
                .code(HttpCodeConst.OK)
                .msg("OK")
                .build();
    }

    public static <T> ResultT<T> success(String msg){
        return ResultT.<T>builder()
                .code(HttpCodeConst.OK)
                .msg(msg)
                .build();
    }
    public static <T> ResultT<T> error(int code, String msg){
        return ResultT.<T>builder()
                .code(code)
                .msg(msg)
                .build();
    }
    public static <T> ResultT<T> error(int code){
        return ResultT.<T>builder()
                .code(code)
                .msg("error")
                .build();
    }
    public static <T> ResultT<T> error(int code, String msg, T data){
        return ResultT.<T>builder()
                .code(code)
                .msg(msg)
                .data(data)
                .build();
    }
    public static <T> ResultT<T> error(String msg){
        return ResultT.<T>builder()
                .code(HttpCodeConst.INTERNAL_SERVER_ERROR)
                .msg(msg)
                .build();
    }
    public static <T> ResultT<T> error(String msg, T data){
        return ResultT.<T>builder()
                .code(HttpCodeConst.INTERNAL_SERVER_ERROR)
                .msg(msg)
                .data(data)
                .build();
    }

    public static <T> ResultT<T> error(){
        return ResultT.<T>builder()
                .code(HttpCodeConst.INTERNAL_SERVER_ERROR)
                .msg("系统错误")
                .build();
    }

}
