package com.encryptiontool.algorithms.asymmetric;

import com.encryptiontool.algorithms.EncryptionAlgorithm;
import com.encryptiontool.util.Base64Util;

import javax.crypto.Cipher;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class RSAAlgorithm implements EncryptionAlgorithm {
    private static final String ALGORITHM = "RSA";
    private static final String TRANSFORMATION = "RSA/ECB/PKCS1Padding";
    private static final int KEY_SIZE = 2048;
    private static final int MAX_ENCRYPT_BLOCK = 245;  // (KEY_SIZE/8) - 11
    private static final int MAX_DECRYPT_BLOCK = 256;  // (KEY_SIZE/8)

    @Override
    public String encrypt(String data, String key) throws Exception {
        System.out.println("RSA加密开始:");
        System.out.println("  密钥输入长度: " + key.length());
        System.out.println("  密钥输入前50字符: " + (key.length() > 50 ? key.substring(0, 50) + "..." : key));

        // 使用Base64Util智能清理密钥
        String cleanKey = Base64Util.smartExtractKey(key);
        System.out.println("  清理后密钥长度: " + cleanKey.length());

        if (cleanKey.isEmpty()) {
            throw new IllegalArgumentException("无法提取有效的RSA公钥");
        }

        PublicKey publicKey = getPublicKey(cleanKey);
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);

        byte[] dataBytes = data.getBytes("UTF-8");
        int dataLength = dataBytes.length;
        int offset = 0;
        StringBuilder encryptedData = new StringBuilder();

        // 分段加密
        while (offset < dataLength) {
            int inputLen = Math.min(dataLength - offset, MAX_ENCRYPT_BLOCK);
            byte[] chunk = new byte[inputLen];
            System.arraycopy(dataBytes, offset, chunk, 0, inputLen);

            byte[] encryptedChunk = cipher.doFinal(chunk);
            encryptedData.append(Base64Util.safeEncode(encryptedChunk));

            offset += inputLen;
            if (offset < dataLength) {
                encryptedData.append("|"); // 分隔符
            }
        }

        System.out.println("RSA加密完成，结果长度: " + encryptedData.length());
        return encryptedData.toString();
    }

    @Override
    public String decrypt(String encryptedData, String key) throws Exception {
        System.out.println("RSA解密开始:");
        System.out.println("  密钥输入长度: " + key.length());
        System.out.println("  密钥输入前50字符: " + (key.length() > 50 ? key.substring(0, 50) + "..." : key));

        // 使用Base64Util智能清理密钥
        String cleanKey = Base64Util.smartExtractKey(key);
        System.out.println("  清理后密钥长度: " + cleanKey.length());

        if (cleanKey.isEmpty()) {
            throw new IllegalArgumentException("无法提取有效的RSA私钥");
        }

        PrivateKey privateKey = getPrivateKey(cleanKey);
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);

        // 清理加密数据
        String cleanEncryptedData = encryptedData.replaceAll("[^A-Za-z0-9+/=|]", "");
        String[] chunks = cleanEncryptedData.split("\\|");
        StringBuilder decryptedData = new StringBuilder();

        // 分段解密
        for (String chunk : chunks) {
            if (!chunk.isEmpty()) {
                byte[] encryptedBytes = Base64Util.safeDecode(chunk);
                byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
                decryptedData.append(new String(decryptedBytes, "UTF-8"));
            }
        }

        System.out.println("RSA解密完成，结果长度: " + decryptedData.length());
        return decryptedData.toString();
    }

    @Override
    public String getAlgorithmName() {
        return "RSA";
    }

    @Override
    public String generateKey() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM);
        keyPairGenerator.initialize(KEY_SIZE);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        // 生成干净的Base64密钥
        String publicKey = Base64Util.safeEncode(keyPair.getPublic().getEncoded());
        String privateKey = Base64Util.safeEncode(keyPair.getPrivate().getEncoded());

        System.out.println("生成的RSA密钥对:");
        System.out.println("  公钥长度: " + publicKey.length() + " 字符");
        System.out.println("  私钥长度: " + privateKey.length() + " 字符");

        return "公钥: " + publicKey + "\n私钥: " + privateKey;
    }

    @Override
    public boolean requiresKey() {
        return true;
    }

    private PublicKey getPublicKey(String base64Key) throws Exception {
        try {
            byte[] keyBytes = Base64Util.safeDecode(base64Key);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
            return keyFactory.generatePublic(spec);
        } catch (Exception e) {
            throw new Exception("无法解析RSA公钥: " + e.getMessage() +
                    " 密钥长度: " + base64Key.length() +
                    " 密钥前50字符: " + (base64Key.length() > 50 ? base64Key.substring(0, 50) + "..." : base64Key), e);
        }
    }

    private PrivateKey getPrivateKey(String base64Key) throws Exception {
        try {
            byte[] keyBytes = Base64Util.safeDecode(base64Key);
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
            return keyFactory.generatePrivate(spec);
        } catch (Exception e) {
            throw new Exception("无法解析RSA私钥: " + e.getMessage() +
                    " 密钥长度: " + base64Key.length() +
                    " 密钥前50字符: " + (base64Key.length() > 50 ? base64Key.substring(0, 50) + "..." : base64Key), e);
        }
    }
}