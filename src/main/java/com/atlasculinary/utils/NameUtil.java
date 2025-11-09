package com.atlasculinary.utils;

public class NameUtil {

    public static String resolveName(String fullName, String email) {

        // 1. Kiểm tra và trả về fullName nếu nó hợp lệ
        if (fullName != null && !fullName.trim().isEmpty()) {
            return fullName;
        }

        // 2. Xử lý email nếu fullName không hợp lệ
        if (email == null || !email.contains("@")) return "Anonymous";

        String username = email.split("@")[0].trim();

        if (username.isEmpty()) return "Anonymous";

        // 3. Viết hoa chữ cái đầu và trả về
        return Character.toUpperCase(username.charAt(0)) + username.substring(1);
    }

    // Giữ lại hàm cũ để tránh lỗi biên dịch nếu nơi khác đang sử dụng
    public static String getNameFromEmail(String email) {
        return resolveName(null, email);
    }
}