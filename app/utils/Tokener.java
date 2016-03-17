package utils;

import java.security.SecureRandom;

public class Tokener {
    // RandomString generates random strings
    private static final SecureRandom random = new SecureRandom();
    private static final String chars = "1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ-_~.";
    public static String randomString(int length) {
        StringBuilder builder = new StringBuilder();
        int max = chars.length();
        for (int i=0; i < length; i++){
            int idx = random.nextInt(max);
            builder.append(chars.charAt(idx));
        }
        return builder.toString();
    }
}
