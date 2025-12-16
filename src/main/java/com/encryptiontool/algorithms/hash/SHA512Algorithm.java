package com.encryptiontool.algorithms.hash;

import com.encryptiontool.algorithms.EncryptionAlgorithm;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class SHA512Algorithm implements EncryptionAlgorithm {

    @Override
    public String encrypt(String data, String key) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-512");
        byte[] hashBytes = md.digest(data.getBytes(StandardCharsets.UTF_8));

        // 转换为16进制字符串
        StringBuilder hexString = new StringBuilder();
        for (byte b : hashBytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    @Override
    public String decrypt(String encryptedData, String key) throws Exception {
        throw new UnsupportedOperationException("SHA-512是哈希算法，不可逆，无法解密");
    }

    @Override
    public String getAlgorithmName() {
        return "SHA-512";
    }

    @Override
    public String generateKey() throws Exception {
        return ""; // 哈希算法不需要密钥
    }

    @Override
    public boolean requiresKey() {
        return false;
    }

    // 可选：提供验证方法
    public boolean verify(String data, String expectedHash) throws Exception {
        String actualHash = encrypt(data, "");
        return actualHash.equals(expectedHash.toLowerCase());
    }
}