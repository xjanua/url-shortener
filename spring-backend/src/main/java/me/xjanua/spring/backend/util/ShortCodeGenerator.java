package me.xjanua.spring.backend.util;

import java.security.SecureRandom;

public final class ShortCodeGenerator {

    private static final String BASE62 = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * Sinh short code với độ dài mặc định là 7 ký tự.
     */
    public static String generate() {
        return generate(7);
    }

    /**
     * Sinh short code theo độ dài mong muốn.
     */
    public static String generate(int length) {
        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            sb.append(BASE62.charAt(RANDOM.nextInt(BASE62.length())));
        }

        return sb.toString();
    }
}