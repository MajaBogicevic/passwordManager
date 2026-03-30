package org.crypt.crypto.util;

import java.util.Arrays;

public class ZeroUtils {
    private ZeroUtils(){}
    public static void zero(byte[] bytes) {
        if (bytes != null) {
            Arrays.fill(bytes, (byte) 0);
        }
    }
}
