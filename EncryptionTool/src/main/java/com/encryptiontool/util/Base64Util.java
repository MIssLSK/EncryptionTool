package com.encryptiontool.util;

import java.util.Base64;
import java.util.regex.Pattern;

public class Base64Util {

    private Base64Util() {
        // 私有构造函数，防止实例化
    }

    /**
     * 清理Base64字符串，移除所有非法字符
     */
    public static String cleanBase64(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }

        // 移除所有非Base64字符（包括空格、换行、单引号、中文字符等）
        String cleaned = str.replaceAll("[^A-Za-z0-9+/=]", "");

        // 修复Base64格式：确保长度是4的倍数
        int remainder = cleaned.length() % 4;
        if (remainder > 0) {
            // 添加适当的填充
            cleaned += "====".substring(0, 4 - remainder);
        }

        // 确保填充正确：结尾最多只能有两个'='
        while (cleaned.length() >= 2 && cleaned.endsWith("==")) {
            // 已经是正确的，不处理
            break;
        }

        if (cleaned.endsWith("===")) {
            cleaned = cleaned.substring(0, cleaned.length() - 1);
        }

        return cleaned;
    }

    /**
     * 安全解码Base64字符串，自动清理
     */
    public static byte[] safeDecode(String base64Str) throws IllegalArgumentException {
        if (base64Str == null || base64Str.isEmpty()) {
            throw new IllegalArgumentException("Base64字符串为空");
        }

        try {
            // 先清理字符串
            String cleaned = cleanBase64(base64Str);
            if (cleaned.isEmpty()) {
                throw new IllegalArgumentException("清理后的Base64字符串为空");
            }

            // 尝试解码
            return Base64.getDecoder().decode(cleaned);

        } catch (IllegalArgumentException e) {
            // 如果解码失败，尝试修复填充问题
            String cleaned = cleanBase64(base64Str);

            // 确保长度是4的倍数
            int remainder = cleaned.length() % 4;
            if (remainder > 0) {
                // 尝试添加填充字符
                cleaned += "===".substring(0, 4 - remainder);
            }

            try {
                return Base64.getDecoder().decode(cleaned);
            } catch (IllegalArgumentException ex) {
                throw new IllegalArgumentException("Base64解码失败: " + ex.getMessage() +
                        " 原始字符串: " + base64Str.substring(0, Math.min(50, base64Str.length())) +
                        " 清理后: " + cleaned);
            }
        }
    }

    /**
     * 安全编码为Base64字符串
     */
    public static String safeEncode(byte[] data) {
        if (data == null || data.length == 0) {
            return "";
        }
        return Base64.getEncoder().encodeToString(data);
    }

    /**
     * 验证Base64字符串是否有效
     */
    public static boolean isValidBase64(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }

        // 清理字符串
        String cleaned = cleanBase64(str);
        if (cleaned.isEmpty()) {
            return false;
        }

        // 长度应该是4的倍数
        if (cleaned.length() % 4 != 0) {
            return false;
        }

        // 尝试解码
        try {
            Base64.getDecoder().decode(cleaned);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 智能提取Base64密钥
     */
    public static String smartExtractKey(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }

        System.out.println("原始密钥文本: " + text.substring(0, Math.min(100, text.length())) + "...");

        // 1. 尝试提取PEM格式密钥（包含BEGIN/END）
        String pemKey = extractPEMKey(text);
        if (!pemKey.isEmpty()) {
            System.out.println("检测到PEM格式密钥");
            return pemKey;
        }

        // 2. 尝试提取标准Base64密钥
        String base64Key = extractBase64Key(text);
        if (!base64Key.isEmpty()) {
            System.out.println("提取到Base64密钥，长度: " + base64Key.length());
            return base64Key;
        }

        // 3. 如果以上都失败，返回清理后的文本
        String cleaned = cleanBase64(text);
        System.out.println("使用清理后的文本，长度: " + cleaned.length());
        return cleaned;
    }

    /**
     * 提取PEM格式密钥
     */
    private static String extractPEMKey(String text) {
        // 查找BEGIN和END之间的内容
        String beginPattern = "-----BEGIN[A-Z\\s]+-----";
        String endPattern = "-----END[A-Z\\s]+-----";

        Pattern p = Pattern.compile(
                beginPattern + "(.*?)" + endPattern,
                Pattern.DOTALL
        );
        java.util.regex.Matcher m = p.matcher(text);

        if (m.find()) {
            String keyContent = m.group(1);
            // 移除所有非Base64字符
            return cleanBase64(keyContent);
        }

        return "";
    }

    /**
     * 提取标准Base64密钥
     */
    private static String extractBase64Key(String text) {
        // 寻找最长的Base64连续字符串
        Pattern pattern = Pattern.compile(
                "[A-Za-z0-9+/=]{50,}"  // 至少50个Base64字符
        );
        java.util.regex.Matcher matcher = pattern.matcher(text);

        String longestKey = "";
        while (matcher.find()) {
            String candidate = matcher.group();
            if (isValidBase64(candidate) && candidate.length() > longestKey.length()) {
                longestKey = candidate;
            }
        }

        if (!longestKey.isEmpty()) {
            // 确保格式正确
            return fixBase64Format(longestKey);
        }

        return "";
    }

    /**
     * 修复Base64格式问题
     */
    private static String fixBase64Format(String base64Str) {
        if (base64Str == null) return "";

        // 移除所有非Base64字符
        String cleaned = cleanBase64(base64Str);

        // 确保长度是4的倍数
        int remainder = cleaned.length() % 4;
        if (remainder > 0) {
            // 添加正确的填充
            cleaned += "====".substring(0, 4 - remainder);
        }

        return cleaned;
    }
}