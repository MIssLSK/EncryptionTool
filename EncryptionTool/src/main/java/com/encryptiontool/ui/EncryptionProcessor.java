package com.encryptiontool.ui;

import com.encryptiontool.algorithms.EncryptionAlgorithm;
import com.encryptiontool.algorithms.asymmetric.DSAAlgorithm;
import com.encryptiontool.util.Base64Util;

public class EncryptionProcessor {
    private AlgorithmPanel algorithmPanel;

    public EncryptionProcessor(AlgorithmPanel algorithmPanel) {
        this.algorithmPanel = algorithmPanel;
    }

    public String processEncryption() throws Exception {
        String input = algorithmPanel.getInputText();
        String selected = algorithmPanel.getSelectedAlgorithm();
        EncryptionAlgorithm algorithm = algorithmPanel.getAlgorithm();
        String key = algorithmPanel.getKey();

        // 输入验证
        if (input.isEmpty()) {
            throw new IllegalArgumentException("请输入要加密的文本");
        }

        if (selected == null) {
            throw new IllegalArgumentException("未选择有效的算法");
        }

        if (algorithm.requiresKey()) {
            validateKey(key, selected);
        }

        try {
            // 特殊处理DSA（数字签名）
            if ("DSA".equals(selected)) {
                return processDSAEncryption(input, key);
            }

            // 特殊处理RSA
            if ("RSA".equals(selected)) {
                return processRSAEncryption(input, key);
            }

            // 其他算法
            String result = algorithm.encrypt(input, key);
            return formatResult("加密成功", result, false);

        } catch (Exception e) {
            throw handleEncryptionException(e);
        }
    }

    public String processDecryption() throws Exception {
        String input = algorithmPanel.getInputText();
        String selected = algorithmPanel.getSelectedAlgorithm();
        EncryptionAlgorithm algorithm = algorithmPanel.getAlgorithm();
        String key = algorithmPanel.getKey();

        // 输入验证
        if (input.isEmpty()) {
            throw new IllegalArgumentException("请输入要解密的文本");
        }

        if (selected == null) {
            throw new IllegalArgumentException("未选择有效的算法");
        }

        // 哈希算法无法解密
        if (selected.contains("MD5") || selected.contains("SHA")) {
            throw new IllegalArgumentException("哈希算法不可逆，无法解密");
        }

        if (algorithm.requiresKey()) {
            validateKey(key, selected);
        }

        try {
            // 特殊处理DSA（验证签名）
            if ("DSA".equals(selected)) {
                return processDSADecryption(input, key);
            }

            // 特殊处理RSA
            if ("RSA".equals(selected)) {
                return processRSADecryption(input, key);
            }

            // 其他算法
            String result = algorithm.decrypt(input, key);
            return formatResult("解密成功", result, true);

        } catch (Exception e) {
            throw handleDecryptionException(e);
        }
    }

    private void validateKey(String key, String algorithm) throws IllegalArgumentException {
        if (key.isEmpty() || key.equals("哈希算法不需要密钥")) {
            throw new IllegalArgumentException("请输入密钥");
        }

        // 检查RSA/DSA密钥格式
        if ("RSA".equals(algorithm) || "DSA".equals(algorithm)) {
            if (!key.contains("-----BEGIN") || !key.contains("-----END")) {
                throw new IllegalArgumentException(
                        "密钥格式不正确！\n\n" +
                                "对于" + algorithm + "算法，请使用以下格式：\n" +
                                "1. 完整的PEM格式（包含-----BEGIN和-----END）\n" +
                                "2. 使用'生成密钥'按钮自动生成正确格式\n" +
                                "3. 确保密钥没有在中间被截断"
                );
            }
        }
    }

    private String processDSAEncryption(String input, String key) throws Exception {
        String privateKey = extractPrivateKey(key);
        DSAAlgorithm dsa = (DSAAlgorithm) algorithmPanel.getAlgorithm();
        String signature = dsa.encrypt(input, privateKey);

        return "★ 数字签名生成成功 ★\n\n" +
                "原始数据: " + input + "\n\n" +
                "数字签名: " + signature + "\n\n" +
                "验证签名格式: " + input + "|" + signature + "\n\n" +
                "══════════════════════════════════════\n" +
                "验证方法：\n" +
                "1. 选择DSA算法\n" +
                "2. 在输入框粘贴上面的'验证签名格式'\n" +
                "3. 在密钥框粘贴公钥\n" +
                "4. 点击'解密'进行验证\n" +
                "══════════════════════════════════════";
    }

    private String processDSADecryption(String input, String key) throws Exception {
        String[] parts = input.split("\\|", 2);
        if (parts.length != 2) {
            throw new IllegalArgumentException(
                    "DSA签名验证格式不正确！\n\n" +
                            "正确的格式应为：原始数据|签名\n" +
                            "示例：这是测试文本|MIICdjCCAd2gAwIBAgIBATAN...\n\n" +
                            "请使用DSA加密时生成的格式进行验证。"
            );
        }

        String data = parts[0];
        String signature = parts[1];
        String publicKey = extractPublicKey(key);
        DSAAlgorithm dsa = (DSAAlgorithm) algorithmPanel.getAlgorithm();
        boolean isValid = dsa.verifySignature(data, signature, publicKey);

        return "★ 数字签名验证结果 ★\n\n" +
                "验证状态: " + (isValid ? "✓ 签名有效" : "✗ 签名无效") + "\n\n" +
                "原始数据: " + data + "\n" +
                "签名长度: " + signature.length() + " 字符\n\n" +
                "══════════════════════════════════════\n" +
                (isValid ?
                        "✓ 该签名通过验证，数据未被篡改" :
                        "✗ 该签名验证失败，数据可能已被篡改") +
                "\n══════════════════════════════════════";
    }

    private String processRSAEncryption(String input, String key) throws Exception {
        String publicKey = extractPublicKey(key);
        String encrypted = algorithmPanel.getAlgorithm().encrypt(input, publicKey);

        return "★ RSA加密成功 ★\n\n" +
                "加密结果: " + encrypted + "\n\n" +
                "══════════════════════════════════════\n" +
                "解密方法：\n" +
                "1. 选择RSA算法\n" +
                "2. 在输入框粘贴上面的加密结果\n" +
                "3. 在密钥框粘贴对应的私钥\n" +
                "4. 点击'解密'获取原文\n" +
                "══════════════════════════════════════";
    }

    private String processRSADecryption(String input, String key) throws Exception {
        String privateKey = extractPrivateKey(key);
        String decrypted = algorithmPanel.getAlgorithm().decrypt(input, privateKey);

        return "★ RSA解密成功 ★\n\n" +
                "解密结果: " + decrypted + "\n\n" +
                "══════════════════════════════════════\n" +
                "验证：比较解密结果与原始数据是否一致" +
                "\n══════════════════════════════════════";
    }

    private String extractPublicKey(String keyText) {
        if (keyText.contains("-----BEGIN PUBLIC KEY-----")) {
            return extractPEMKey(keyText, "-----BEGIN PUBLIC KEY-----", "-----END PUBLIC KEY-----");
        }
        throw new IllegalArgumentException("无法从输入中提取有效的公钥");
    }

    private String extractPrivateKey(String keyText) {
        if (keyText.contains("-----BEGIN PRIVATE KEY-----")) {
            return extractPEMKey(keyText, "-----BEGIN PRIVATE KEY-----", "-----END PRIVATE KEY-----");
        }
        throw new IllegalArgumentException("无法从输入中提取有效的私钥");
    }

    private String extractPEMKey(String text, String beginMarker, String endMarker) {
        int beginIndex = text.indexOf(beginMarker);
        int endIndex = text.indexOf(endMarker);

        if (beginIndex >= 0 && endIndex > beginIndex) {
            int contentStart = beginIndex + beginMarker.length();
            String keyContent = text.substring(contentStart, endIndex).trim();
            return Base64Util.cleanBase64(keyContent);
        }
        return "";
    }

    private String formatResult(String title, String result, boolean isDecryption) {
        if (isDecryption) {
            return "★ " + title + " ★\n\n" + result;
        } else {
            return "★ " + title + " ★\n\n" + result;
        }
    }

    private Exception handleEncryptionException(Exception e) {
        String errorMsg = e.getMessage();
        if (errorMsg != null && (errorMsg.contains("Unable to decode key") || errorMsg.contains("无法解析"))) {
            return new Exception(
                    "密钥解析失败！\n\n" +
                            "详细原因：\n" +
                            "1. 密钥格式不正确或已损坏\n" +
                            "2. 密钥被截断（不完整）\n" +
                            "3. 密钥包含非法字符\n" +
                            "4. 密钥类型不匹配（如用私钥加密）\n\n" +
                            "解决方案：\n" +
                            "1. 重新生成密钥\n" +
                            "2. 检查密钥是否完整复制\n" +
                            "3. 确保使用正确的密钥类型\n\n" +
                            "技术详情: " + errorMsg
            );
        }
        return e;
    }

    private Exception handleDecryptionException(Exception e) {
        String errorMsg = e.getMessage();
        if (errorMsg != null && (errorMsg.contains("Unable to decode key") || errorMsg.contains("无法解析"))) {
            return new Exception(
                    "密钥解析失败！\n\n" +
                            "可能的原因：\n" +
                            "1. 密钥格式不正确\n" +
                            "2. 密钥被截断\n" +
                            "3. 使用了错误的密钥类型\n" +
                            "4. 密钥包含非法字符\n\n" +
                            "对于RSA/DSA：\n" +
                            "✓ 加密时使用公钥，解密时使用私钥\n" +
                            "✓ 确保密钥是完整的PEM格式\n" +
                            "✓ 使用'复制密钥'按钮确保完整复制\n\n" +
                            "技术详情: " + errorMsg
            );
        }
        return e;
    }
}