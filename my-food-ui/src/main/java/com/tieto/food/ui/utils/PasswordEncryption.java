package com.tieto.food.ui.utils;

import java.security.MessageDigest;

public final class PasswordEncryption {
    private static final char DIGITS[] = { '0', '1', '2', '3', '4', '5', '6',
            '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

    private PasswordEncryption() {

    }

    private static String byteArrayToHexString(byte[] b) {
        StringBuffer hexString = new StringBuffer(b.length);
        for (int i = 0; i < b.length; i++) {
            hexString.append(DIGITS[(b[i] & 0xF0) >> 4]);
            hexString.append(DIGITS[b[i] & 0x0F]);
        }
        return hexString.toString();
    }

    public static String encrypt(String plaintext) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA");
            md.update(plaintext.getBytes("UTF-8"));
            byte[] mdBytes = md.digest();
            return byteArrayToHexString(mdBytes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
