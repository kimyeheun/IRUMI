package com.ssafy.pocketc_backend.global.auth.security;

import java.util.List;
import java.util.stream.Stream;

public class AuthWhiteList {
    public static final List<String> AUTH_WHITELIST_DEFAULT = List.of(
        "/api/v1/users/login",
        "/api/v1/users", "/api/v1/users/reissue",
        "/swagger-ui/**",
        "/v3/api-docs/**",
        "/swagger-resources/**",
        "/h2-console/**"
    );

    public static final List<String> AUTH_WHITELIST_WILDCARD = List.of(
        "/api/public/**"
    );
    public static final String[] AUTH_WHITELIST = Stream.concat(
            AUTH_WHITELIST_DEFAULT.stream(),
            AUTH_WHITELIST_WILDCARD.stream()
    ).toArray(String[]::new);
}
