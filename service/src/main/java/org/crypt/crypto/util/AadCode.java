package org.crypt.crypto.util;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.TreeMap;

public final class AadCode {
    private AadCode(){}

    public static byte[] encode(Map<String, String> aad) {
        if (aad == null || aad.isEmpty()) {
            return new byte[0];
        }

        TreeMap<String, String> sorted = new TreeMap<>();
        for (Map.Entry<String, String> entry : aad.entrySet()) {
            if (entry.getKey() == null) {
                throw new IllegalArgumentException("AAD key must not be null");
            }
            if (entry.getValue() == null) {
                throw new IllegalArgumentException("AAD value must not be null for key: " + entry.getKey());
            }
            sorted.put(entry.getKey(), entry.getValue());
        }

        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, String> entry : sorted.entrySet()) {
            if (entry.getKey() == null || entry.getValue() == null) {
                throw new IllegalArgumentException("AAD keys and values must not be null");
            }
            if (entry.getKey().isBlank()) {
                throw new IllegalArgumentException("AAD key must not be blank");
            }

            builder.append(escape(entry.getKey()))
                    .append('=')
                    .append(escape(entry.getValue()))
                    .append('\n');
        }
        return builder.toString().getBytes(StandardCharsets.UTF_8);
    }

    private static String escape(String value) {
        return value.replace("\\", "\\\\")
                .replace("\n", "\\n")
                .replace("=", "\\=");
    }
}
