package com.encryptiontool.algorithms.asymmetric;

import com.encryptiontool.algorithms.EncryptionAlgorithm;
import com.encryptiontool.util.Base64Util;

import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class DSAAlgorithm implements EncryptionAlgorithm {
    private static final String ALGORITHM = "DSA";
    private static final int KEY_SIZE = 1024;

    @Override
    public String encrypt(String data, String key) throws Exception {
        System.out.println("DSA生成签名开始:");
        System.out.println("  密钥输入长度: " + key.length());
        System.out.println("  密钥输入前50字符: " + (key.length() > 50 ? key.substring(0, 50) + "..." : key));

        // 使用Base64Util智能清理密钥
        String cleanKey = Base64Util.smartExtractKey(key);
        System.out.println("  清理后密钥长度: " + cleanKey.length());

        if (cleanKey.isEmpty()) {
            throw new IllegalArgumentException("无法提取有效的DSA私钥");
        }

        PrivateKey privateKey = getPrivateKey(cleanKey);
        Signature signature = Signature.getInstance("SHA256withDSA");
        signature.initSign(privateKey);
        signature.update(data.getBytes("UTF-8"));
        byte[] digitalSignature = signature.sign();

        // 生成签名
        String result = Base64Util.safeEncode(digitalSignature);
        System.out.println("DSA签名生成完成，签名长度: " + result.length());
        return result;
    }

    @Override
    public String decrypt(String encryptedData, String key) throws Exception {
        throw new UnsupportedOperationException("DSA是数字签名算法，不用于加密/解密");
    }

    public boolean verifySignature(String data, String signature, String publicKeyStr) throws Exception {
        System.out.println("DSA验证签名开始:");
        System.out.println("  公钥输入长度: " + publicKeyStr.length());
        System.out.println("  公钥输入前50字符: " + (publicKeyStr.length() > 50 ? publicKeyStr.substring(0, 50) + "..." : publicKeyStr));

        // 使用Base64Util智能清理密钥
        String cleanSignature = Base64Util.smartExtractKey(signature);
        String cleanPublicKey = Base64Util.smartExtractKey(publicKeyStr);

        if (cleanPublicKey.isEmpty()) {
            throw new IllegalArgumentException("无法提取有效的DSA公钥");
        }

        PublicKey publicKey = getPublicKey(cleanPublicKey);
        Signature verifier = Signature.getInstance("SHA256withDSA");
        verifier.initVerify(publicKey);
        verifier.update(data.getBytes("UTF-8"));
        byte[] signatureBytes = Base64Util.safeDecode(cleanSignature);

        boolean result = verifier.verify(signatureBytes);
        System.out.println("DSA验证签名完成，结果: " + result);
        return result;
    }

    @Override
    public String getAlgorithmName() {
        return "DSA (数字签名)";
    }

    @Override
    public String generateKey() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM);
        keyPairGenerator.initialize(KEY_SIZE);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        // 确保生成的密钥是干净的Base64字符串
        String publicKey = Base64Util.safeEncode(keyPair.getPublic().getEncoded());
        String privateKey = Base64Util.safeEncode(keyPair.getPrivate().getEncoded());

        System.out.println("生成的DSA密钥对:");
        System.out.println("  公钥长度: " + publicKey.length());
        System.out.println("  私钥长度: " + privateKey.length());

        return "公钥: " + publicKey + "\n私钥: " + privateKey;
    }

    @Override
    public boolean requiresKey() {
        return true;
    }

    private PublicKey getPublicKey(String key) throws Exception {
        try {
            byte[] keyBytes = Base64Util.safeDecode(key);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
            return keyFactory.generatePublic(spec);
        } catch (Exception e) {
            throw new Exception("无法解析DSA公钥: " + e.getMessage() +
                    " 密钥长度: " + key.length() +
                    " 密钥前50字符: " + (key.length() > 50 ? key.substring(0, 50) + "..." : key), e);
        }
    }

    private PrivateKey getPrivateKey(String key) throws Exception {
        try {
            byte[] keyBytes = Base64Util.safeDecode(key);
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
            return keyFactory.generatePrivate(spec);
        } catch (Exception e) {
            throw new Exception("无法解析DSA私钥: " + e.getMessage() +
                    " 密钥长度: " + key.length() +
                    " 密钥前50字符: " + (key.length() > 50 ? key.substring(0, 50) + "..." : key), e);
        }
    }
}