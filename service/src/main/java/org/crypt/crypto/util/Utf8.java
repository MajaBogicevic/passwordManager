package org.crypt.crypto.util;

import java.nio.charset.StandardCharsets;

public class Utf8 {
    private Utf8(){}
    public static byte[] bytes(String value) {
        return value.getBytes(StandardCharsets.UTF_8);
    }

    public static String string(byte[] value) {
        return new String(value, StandardCharsets.UTF_8);
    }
}
