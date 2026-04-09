package org.service.passwordman.infrastructure.security;

import jakarta.servlet.http.HttpServletRequest;

public class ClientIp {

    private static final String[] HEADER_CANDIDATES = {
            "X-Forwarded-For",
            "X-Real-IP",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_CLIENT_IP"
    };

    public String resolve(HttpServletRequest request) {
        if (request == null) {
            return "unknown-ip";
        }

        for (String headerName : HEADER_CANDIDATES) {
            String resolvedFromHeader = extractFirstUsableIp(request.getHeader(headerName));
            if (resolvedFromHeader != null) {
                return resolvedFromHeader;
            }
        }

        String remoteAddr = normalizeSingleIp(request.getRemoteAddr());
        return remoteAddr != null ? remoteAddr : "unknown-ip";
    }

    private String extractFirstUsableIp(String rawHeaderValue) {
        if (rawHeaderValue == null || rawHeaderValue.isBlank()) {
            return null;
        }

        String[] values = rawHeaderValue.split(",");
        for (String value : values) {
            String normalized = normalizeSingleIp(value);
            if (normalized != null) {
                return normalized;
            }
        }

        return null;
    }

    private String normalizeSingleIp(String ip) {
        if (ip == null) {
            return null;
        }

        String normalized = ip.trim();
        if (normalized.isEmpty() || "unknown".equalsIgnoreCase(normalized)) {
            return null;
        }

        if (normalized.startsWith("\"") && normalized.endsWith("\"") && normalized.length() > 1) {
            normalized = normalized.substring(1, normalized.length() - 1).trim();
        }

        if ("::1".equals(normalized) || "0:0:0:0:0:0:0:1".equals(normalized)) {
            return "127.0.0.1";
        }

        return normalized;
    }
}