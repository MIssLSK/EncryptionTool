package com.encryptiontool.util;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class EncodingUtil {

    private EncodingUtil() {
        // 私有构造函数，防止实例化
    }

    /**
     * 将字符串转换为UTF-8字节数组
     */
    public static byte[] toUtf8Bytes(String text) {
        return text.getBytes(StandardCharsets.UTF_8);
    }

    /**
     * 将UTF-8字节数组转换为字符串
     */
    public static String fromUtf8Bytes(byte[] bytes) {
        return new String(bytes, StandardCharsets.UTF_8);
    }

    /**
     * Base64编码
     */
    public static String base64Encode(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }

    /**
     * Base64解码
     */
    public static byte[] base64Decode(String data) {
        return Base64.getDecoder().decode(data);
    }

    /**
     * Base64编码字符串
     */
    public static String base64EncodeString(String text) {
        return base64Encode(toUtf8Bytes(text));
    }

    /**
     * Base64解码为字符串
     */
    public static String base64DecodeString(String base64Text) {
        return fromUtf8Bytes(base64Decode(base64Text));
    }
}