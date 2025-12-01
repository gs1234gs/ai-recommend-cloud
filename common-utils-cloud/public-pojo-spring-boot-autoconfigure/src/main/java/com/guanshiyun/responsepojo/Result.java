package com.guanshiyun.responsepojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Result 类用于表示操作的结果。
 * 该类包含操作的状态码、消息、返回的数据以及可能的令牌信息。
 * 提供了静态方法来快速创建成功或错误的结果对象。
 */
@Data
@Builder// 用于创建对象时，自动填充属性的注解，使用链式调用
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)// 表示序列化时，如果属性值为 null，则不进行序列化
@JsonDeserialize(builder = Result.ResultBuilder.class)  // 关键注解：告诉 Jackson 用 Builder 反序列
@Accessors(chain = true)
public class Result {

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
    private Object data;

    // 创建一个成功的结果对象，并设置状态码和消息。
    public static Result success() {
        return Result.builder().code(200).msg("OK").data(null).build();
    }

    // 创建一个成功的结果对象，并设置状态码、消息和数据。
    public static Result success(int code, String msg, Object data) {
        return Result.builder().code(code).msg(msg).data(data).build();
    }

    // 创建一个成功的结果对象，并设置状态码、消息
    public static Result success(int code, String msg) {
        return Result.builder().code(code).msg(msg).data(null).build();
    }

    // 创建一个成功的结果对象，消息
    public static Result success(String msg) {
        return Result.builder().code(200).msg(msg).data(null).build();
    }
    // 创建一个成功的结果对象，并设置状态码
    public static Result success(int code) {
        return Result.builder().code(code).msg("OK").data(null).build();
    }
    // 创建一个成功的结果对象，并设置数据。
    public static Result success(Object data) {
        return Result.builder().code(200).msg("OK").data(data).build();
    }
    // 创建一个成功的结果对象，并设置状态码和数据。
    public static Result success(int code, Object data) {
        return Result.builder().code(code).msg("OK").data(data).build();
    }
    // 创建一个成功的结果对象，并设置消息和数据。
    public static Result success(String msg, Object data) {
        return Result.builder().code(200).msg(msg).data(data).build();
    }
    // 创建一个失败的结果对象，并设置状态码和消息。
    public static Result error() {
        return Result.builder().code(400).msg("error").data(null).build();
    }
    // 创建一个失败的结果对象，并设置状态码、消息和数据。
    public static Result error(int code, String msg, Object data) {
        return Result.builder().code(code).msg(msg).data(data).build();
    }
    // 创建一个失败的结果对象，并设置状态码和消息。
    public static Result error(int code, String msg) {
        return Result.builder().code(code).msg(msg).data(null).build();
    }

    // 创建一个失败的结果对象，消息
    public static Result error(String msg) {
        return Result.builder().code(400).msg(msg).data(null).build();
    }
    // 创建一个失败的结果对象，并设置状态码。
    public static Result error(int code) {
        return Result.builder().code(code).msg("error").data(null).build();
    }
}