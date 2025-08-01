package com.example.adadminservice.infrastructure.security.filter;

import com.example.adadminservice.application.out.cache.CacheProvider;
import com.example.adadminservice.application.out.token.TokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtFilter extends OncePerRequestFilter {
    private final TokenProvider jwtProvider;
    private final CacheProvider cacheProvider;

    public JwtFilter(TokenProvider jwtProvider, CacheProvider cacheProvider) {
        this.jwtProvider = jwtProvider;
        this.cacheProvider = cacheProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = resolveToken(request);
        if (token != null && jwtProvider.validateToken(token) && !isTokenBlocked(token)) {
            String id = jwtProvider.getClaim(token, "id", String.class);
            if (!isUserBlocked(id)) {
                UserDetails admin = User.builder()
                        .username(id)
                        .password("")
                        .roles("ADMIN")
                        .build();
                SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(id, token, admin.getAuthorities()));
            }
        }


        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if (StringUtils.hasText(bearer) && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }

    private boolean isTokenBlocked(String value) {
        return cacheProvider.hasKey(String.format("blocked:token:%s", value));
    }

    private boolean isUserBlocked(String value) {
        return cacheProvider.hasKey(String.format("blocked:id:%s", value));
    }
}
