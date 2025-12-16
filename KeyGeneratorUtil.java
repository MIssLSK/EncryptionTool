package com.encryptiontool.algorithms.util;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Base64;

public class KeyGeneratorUtil {

    /**
     * 生成RSA密钥对（增强版，生成PEM格式）
     * @param keySize 密钥长度（通常为1024, 2048, 4096）
     * @return 包含公钥和私钥的数组 [公钥, 私钥]
     */
    public static String[] generateRSAKeyPair(int keySize) throws Exception {
        validateKeySize("RSA", keySize);

        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        keyPairGen.initialize(keySize);
        KeyPair keyPair = keyPairGen.generateKeyPair();

        // 生成PEM格式密钥
        String publicKey = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
        String privateKey = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());

        System.out.println("生成的RSA密钥对:");
        System.out.println("  密钥长度: " + keySize + " 位");
        System.out.println("  公钥Base64长度: " + publicKey.length() + " 字符");
        System.out.println("  私钥Base64长度: " + privateKey.length() + " 字符");

        return new String[]{formatKeyToPEM(publicKey, "PUBLIC"), formatKeyToPEM(privateKey, "PRIVATE")};
    }

    /**
     * 生成DSA密钥对（增强版）
     * @param keySize 密钥长度（通常为512-1024）
     * @return 包含公钥和私钥的数组 [公钥, 私钥]
     */
    public static String[] generateDSAKeyPair(int keySize) throws Exception {
        validateKeySize("DSA", keySize);

        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("DSA");
        keyPairGen.initialize(keySize);
        KeyPair keyPair = keyPairGen.generateKeyPair();

        String publicKey = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
        String privateKey = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());

        System.out.println("生成的DSA密钥对:");
        System.out.println("  密钥长度: " + keySize + " 位");
        System.out.println("  公钥Base64长度: " + publicKey.length() + " 字符");
        System.out.println("  私钥Base64长度: " + privateKey.length() + " 字符");

        return new String[]{formatKeyToPEM(publicKey, "PUBLIC"), formatKeyToPEM(privateKey, "PRIVATE")};
    }

    /**
     * 将Base64密钥格式化为PEM格式
     */
    private static String formatKeyToPEM(String base64Key, String type) {
        StringBuilder pem = new StringBuilder();

        if ("PUBLIC".equals(type)) {
            pem.append("-----BEGIN PUBLIC KEY-----\n");
        } else if ("PRIVATE".equals(type)) {
            pem.append("-----BEGIN PRIVATE KEY-----\n");
        }

        // 每64个字符换行
        for (int i = 0; i < base64Key.length(); i += 64) {
            int end = Math.min(i + 64, base64Key.length());
            pem.append(base64Key.substring(i, end)).append("\n");
        }

        if ("PUBLIC".equals(type)) {
            pem.append("-----END PUBLIC KEY-----\n");
        } else if ("PRIVATE".equals(type)) {
            pem.append("-----END PRIVATE KEY-----\n");
        }

        return pem.toString();
    }

    /**
     * 生成随机密钥（用于对称加密）
     * @param algorithm 算法名称（AES, DES, Blowfish等）
     * @param keySize 密钥长度（位）
     * @return Base64编码的密钥
     */
    public static String generateSymmetricKey(String algorithm, int keySize) throws Exception {
        validateKeySize(algorithm, keySize);

        KeyGenerator keyGen = KeyGenerator.getInstance(algorithm);
        keyGen.init(keySize);
        SecretKey secretKey = keyGen.generateKey();
        return Base64.getEncoder().encodeToString(secretKey.getEncoded());
    }

    /**
     * 验证密钥长度
     */
    private static void validateKeySize(String algorithm, int keySize) throws IllegalArgumentException {
        switch (algorithm.toUpperCase()) {
            case "DES":
                if (keySize != 56) {
                    throw new IllegalArgumentException("DES算法必须使用56位密钥长度");
                }
                break;
            case "AES":
                if (keySize != 128 && keySize != 192 && keySize != 256) {
                    throw new IllegalArgumentException("AES算法支持128、192、256位密钥长度");
                }
                break;
            case "BLOWFISH":
                if (keySize < 32 || keySize > 448 || keySize % 8 != 0) {
                    throw new IllegalArgumentException("Blowfish算法支持32-448位密钥长度，且必须是8的倍数");
                }
                break;
            case "RSA":
                if (keySize < 1024) {
                    throw new IllegalArgumentException("RSA密钥长度必须至少为1024位，建议使用2048或4096位");
                }
                if (keySize % 128 != 0) {
                    throw new IllegalArgumentException("RSA密钥长度必须是128的倍数");
                }
                break;
            case "DSA":
                if (keySize < 512 || keySize > 1024 || keySize % 64 != 0) {
                    throw new IllegalArgumentException("DSA密钥长度必须在512-1024位之间，且必须是64的倍数");
                }
                break;
            default:
                // 对于其他算法，不验证长度
        }
    }
}