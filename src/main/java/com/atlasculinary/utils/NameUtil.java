package com.atlasculinary.utils;

public class NameUtil {
    public static String getNameFromEmail(String email) {
        if (email == null || !email.contains("@")) return "Unknown Vendor";
        String username = email.split("@")[0].trim();
        if (username.isEmpty()) return "Unknown Vendor";
        return Character.toUpperCase(username.charAt(0)) + username.substring(1);
    }
}
