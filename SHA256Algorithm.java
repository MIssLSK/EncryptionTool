package com.encryptiontool.algorithms.hash;

import com.encryptiontool.algorithms.EncryptionAlgorithm;
import com.encryptiontool.util.EncodingUtil;

import java.security.MessageDigest;

public class SHA256Algorithm implements EncryptionAlgorithm {

    @Override
    public String encrypt(String data, String key) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = md.digest(EncodingUtil.toUtf8Bytes(data));

        // 转换为16进制字符串
        return bytesToHex(hashBytes);
    }

    @Override
    public String decrypt(String encryptedData, String key) throws Exception {
        throw new UnsupportedOperationException("SHA-256是哈希算法，不可逆，无法解密");
    }

    @Override
    public String getAlgorithmName() {
        return "SHA-256";
    }

    @Override
    public String generateKey() throws Exception {
        return ""; // 哈希算法不需要密钥
    }

    @Override
    public boolean requiresKey() {
        return false;
    }

    // 字节数组转换为十六进制字符串
    private String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}