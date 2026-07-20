package com.stabilityos.backend.security;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class ApiKeyAuthenticationFilterTest {
    @Test
    void allowsHealthEndpointWithoutApiKey() throws Exception {
        ApiKeyAuthenticationFilter filter = new ApiKeyAuthenticationFilter("secret");

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/health");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        filter.doFilter(request,response, chain);

        assertThat(response.getStatus()).isEqualTo(200);
    }

    @Test
    void rejectsProtectedEndpointWithoutApiKey() throws Exception {
        ApiKeyAuthenticationFilter filter = new ApiKeyAuthenticationFilter("secret");

        MockHttpServletRequest request = new MockHttpServletRequest("GET","/api/open-loops");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        filter.doFilter(request, response, chain);

        assertThat(response.getStatus()).isEqualTo(401);
    }

    @Test
    void allowsProtectedEndpointWithValidApiKey() throws Exception{
        ApiKeyAuthenticationFilter filter = new ApiKeyAuthenticationFilter("secret");

        MockHttpServletRequest request = new MockHttpServletRequest("GET","/api/open-loops");
        request.addHeader("X-StabilityOs-Key","secret");

        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        filter.doFilter(request, response, chain);

        assertThat(response.getStatus()).isEqualTo(200);
    }

    @Test
    void rejectsProtectedEndpointWithInvalidApiKey() throws Exception {
        ApiKeyAuthenticationFilter filter = new ApiKeyAuthenticationFilter("secret");

        MockHttpServletRequest request = new MockHttpServletRequest("GET","/api/open-loops");
        request.addHeader("X-StabilityOs-Key","wrong");

        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        filter.doFilter(request, response, chain);
        assertThat(response.getStatus()).isEqualTo(401);
    }
}
