package com.priyakdey.jraf.http;

// https://datatracker.ietf.org/doc/html/rfc2616#section-9

/**
 * Enum representing the HTTP methods supported by the framework.
 * <p>
 * These methods correspond to standard HTTP operations like GET, POST, PUT,
 * DELETE, etc.
 * <br>
 * <a href="https://datatracker.ietf.org/doc/html/rfc2616#section-9">RFC-2616: Method Definitions</a>
 * </p>
 *
 * @author priyakdey
 */
public enum HttpMethod {
    OPTIONS,
    GET,
    HEAD,
    POST,
    PUT,
    DELETE,
    TRACE,
    CONNECT;

    private HttpMethod() {
    }

    public static HttpMethod toHttpMethod(String method) {
        return switch (method) {
            case "GET" -> GET;
            default ->
                    throw new IllegalArgumentException("Only support GET " +
                            "method right now");
        };
    }
}