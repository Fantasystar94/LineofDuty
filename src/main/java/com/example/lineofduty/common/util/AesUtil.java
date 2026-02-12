//package com.example.lineofduty.common.util;
//
//import jakarta.annotation.PostConstruct;
//import jakarta.persistence.AttributeConverter;
//import jakarta.persistence.Converter;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.util.StringUtils;
//import javax.crypto.Cipher;
//import javax.crypto.spec.SecretKeySpec;
//import java.nio.charset.StandardCharsets;
//import java.util.Base64;
//
//@Configuration
//public class AesUtil {
//
//    @Value("${AES_SECRET_KEY}")
//    private String secretKey;
//
//    // 암호화 알고리즘(AES)에 사용할 키 객체
//    private static SecretKeySpec secretKeySpec;
//
//    @PostConstruct
//    public void init() {
//
//        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
//
//        secretKeySpec = new SecretKeySpec(keyBytes, "AES");
//    }
//
//    // 암호화 (평문 -> 암호화)
//    public static String encrypt(String value) {
//
//        if (!StringUtils.hasText(value)) return null;
//
//        try {
//            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
//
//            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
//
//            return Base64.getEncoder().encodeToString(cipher.doFinal(value.getBytes(StandardCharsets.UTF_8)));
//        } catch (Exception e) {
//            throw new RuntimeException("Encryption failed", e);
//        }
//    }
//
//    // 복호화 (암호화 -> 평문)
//    public static String decrypt(String value) {
//        if (!StringUtils.hasText(value)) return null;
//
//        try {
//            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
//
//            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
//
//            return new String(cipher.doFinal(Base64.getDecoder().decode(value)), StandardCharsets.UTF_8);
//        } catch (Exception e) {
//            throw new RuntimeException("Decryption failed", e);
//        }
//    }
//
//    @Converter
//    public static class ResidentNumberConverter implements AttributeConverter<String, String> {
//
//        // DB에 저장할 때 자동으로 암호화
//        @Override
//        public String convertToDatabaseColumn(String attribute) {
//            return AesUtil.encrypt(attribute);
//        }
//
//        // DB의 데이터 읽어올 때 자동으로 복호화
//        @Override
//        public String convertToEntityAttribute(String dbData) {
//            return AesUtil.decrypt(dbData);
//        }
//    }
//
//}
