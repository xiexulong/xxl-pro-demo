package com.xxl.jasypt.demo;

import org.jasypt.properties.PropertyValueEncryptionUtils;
import org.jasypt.util.text.BasicTextEncryptor;

public final class JasyptEncryptorUtils {


    private static final String salt = "lybgeek";

    private static BasicTextEncryptor basicTextEncryptor = new BasicTextEncryptor();

    static {
        basicTextEncryptor.setPassword(salt);
    }

    private JasyptEncryptorUtils(){}

    /**
     * 明文加密
     * @param plaintext
     * @return
     */
    public static String encode(String plaintext){
        System.out.println("明文字符串：" + plaintext);
        String ciphertext = basicTextEncryptor.encrypt(plaintext);
        System.out.println("加密后字符串：" + ciphertext);
        return ciphertext;
    }

    /**
     * 解密
     * @param ciphertext
     * @return
     */
    public static String decode(String ciphertext){
        System.out.println("加密字符串：" + ciphertext);
        ciphertext = "ENC(" + ciphertext + ")";
        if (PropertyValueEncryptionUtils.isEncryptedValue(ciphertext)){
            String plaintext = PropertyValueEncryptionUtils.decrypt(ciphertext,basicTextEncryptor);
            System.out.println("解密后的字符串：" + plaintext);
            return plaintext;
        }
        System.out.println("解密失败");
        return "";
    }

    public static void main(String[] args) {
        String a = encode("xxl");
        encode("helloxxl");
        encode("jdbc:mysql://localhost:3306/xxl_activiti6?serverTimezone=UTC&useSSL=false&allowPublicKeyRetrieval=true&nullCatalogMeansCurrent=true");
    }
}

