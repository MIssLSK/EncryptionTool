package com.encryptiontool.algorithms;

public interface EncryptionAlgorithm {
    String encrypt(String data, String key) throws Exception;
    String decrypt(String encryptedData, String key) throws Exception;
    String getAlgorithmName();
    String generateKey() throws Exception;
    boolean requiresKey();
}