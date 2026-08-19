package me.xjanua.spring.backend.util;

import java.time.LocalDateTime;

public final class CommonUtils {

    private CommonUtils() {
        // Prevent instantiation
    }

    public static boolean isExpired(LocalDateTime expiresAt) {
        return expiresAt != null
                && !LocalDateTime.now().isBefore(expiresAt);
    }
}