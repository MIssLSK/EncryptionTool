package com.encryptiontool.algorithms.symmetric;

import com.encryptiontool.algorithms.EncryptionAlgorithm;
import com.encryptiontool.util.EncodingUtil;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;

public class AESAlgorithm implements EncryptionAlgorithm {
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding"; // 改为CBC模式
    private static final int IV_SIZE = 16; // AES块大小
    private static final int KEY_SIZE = 128;

    @Override
    public String encrypt(String data, String key) throws Exception {
        // 清理并验证密钥
        byte[] keyBytes = validateAndPrepareKey(key, 16); // AES-128需要16字节

        // 生成随机IV
        byte[] iv = new byte[IV_SIZE];
        new SecureRandom().nextBytes(iv);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        SecretKeySpec secretKey = new SecretKeySpec(keyBytes, ALGORITHM);
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);

        byte[] dataBytes = EncodingUtil.toUtf8Bytes(data);
        byte[] encryptedBytes = cipher.doFinal(dataBytes);

        // 组合IV和加密数据：IV + 加密数据
        byte[] combined = new byte[IV_SIZE + encryptedBytes.length];
        System.arraycopy(iv, 0, combined, 0, IV_SIZE);
        System.arraycopy(encryptedBytes, 0, combined, IV_SIZE, encryptedBytes.length);

        return EncodingUtil.base64Encode(combined);
    }

    @Override
    public String decrypt(String encryptedData, String key) throws Exception {
        // 清理并验证密钥
        byte[] keyBytes = validateAndPrepareKey(key, 16);

        // 解码并分离IV和加密数据
        byte[] combined = EncodingUtil.base64Decode(encryptedData);
        if (combined.length < IV_SIZE) {
            throw new IllegalArgumentException("加密数据格式错误：缺少IV");
        }

        byte[] iv = new byte[IV_SIZE];
        byte[] encryptedBytes = new byte[combined.length - IV_SIZE];
        System.arraycopy(combined, 0, iv, 0, IV_SIZE);
        System.arraycopy(combined, IV_SIZE, encryptedBytes, 0, encryptedBytes.length);

        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        SecretKeySpec secretKey = new SecretKeySpec(keyBytes, ALGORITHM);
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);

        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        return EncodingUtil.fromUtf8Bytes(decryptedBytes);
    }

    @Override
    public String getAlgorithmName() {
        return "AES (CBC模式)";
    }

    @Override
    public String generateKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
        keyGen.init(KEY_SIZE);
        SecretKey secretKey = keyGen.generateKey();
        return EncodingUtil.base64Encode(secretKey.getEncoded());
    }

    @Override
    public boolean requiresKey() {
        return true;
    }

    private byte[] validateAndPrepareKey(String key, int requiredLength) {
        if (key == null || key.trim().isEmpty()) {
            throw new IllegalArgumentException("密钥不能为空");
        }

        byte[] keyBytes;
        String trimmedKey = key.trim();

        // 检查是否为Base64
        if (trimmedKey.matches("^[A-Za-z0-9+/]*={0,2}$")) {
            try {
                keyBytes = EncodingUtil.base64Decode(trimmedKey);
            } catch (Exception e) {
                keyBytes = EncodingUtil.toUtf8Bytes(trimmedKey);
            }
        } else {
            keyBytes = EncodingUtil.toUtf8Bytes(trimmedKey);
        }

        // 验证密钥长度
        if (keyBytes.length < requiredLength) {
            throw new IllegalArgumentException(
                    String.format("AES密钥至少需要%d字节（%d位），当前为%d字节",
                            requiredLength, requiredLength * 8, keyBytes.length)
            );
        }

        // 截取或填充到所需长度
        byte[] result = new byte[requiredLength];
        System.arraycopy(keyBytes, 0, result, 0, Math.min(keyBytes.length, requiredLength));

        return result;
    }
}