package com.stabilityos.backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {

    private static final String API_KEY_HEADER = "X-StabilityOS-Key";

    private final String configuredApiKey;

    public ApiKeyAuthenticationFilter(
            @Value("${stabilityos.api-key}") String configuredApiKey
    ) {
        this.configuredApiKey = configuredApiKey;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        if (isPublicEndpoint(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String providedApiKey = request.getHeader(API_KEY_HEADER);

        if (configuredApiKey == null || configuredApiKey.isBlank()) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "API key is not configured.");
            return;
        }

        if (providedApiKey == null || !providedApiKey.equals(configuredApiKey)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing or invalid API key.");
            return;
        }

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        "stabilityos-api-client",
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_API_CLIENT"))
                );

        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }

    private boolean isPublicEndpoint(HttpServletRequest request) {
        String path = request.getRequestURI();

        return path.equals("/api/health")
                || path.startsWith("/actuator/health")
                || path.equals("/api/telegram/webhook");
    }
}