package com.api.materiais.infra.security;

import com.api.materiais.infra.security.service.TokenService;
import com.api.materiais.infra.security.tenant.TenantContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.Collections;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    private TokenService tokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        var token = this.recoverToken(request);

        if (token != null) {
            var login = tokenService.validateToken(token);

            if (login != null && !login.isEmpty()) {
                var tenantId = tokenService.getTenantIdFromToken(token);

                if (tenantId != null) {
                    TenantContext.setCurrentTenant(tenantId);

                    // IMPORTANTE: Criar autoridade ROLE_USER para não dar 403
                    var authority = new SimpleGrantedAuthority("ROLE_USER");
                    var authentication = new UsernamePasswordAuthenticationToken(
                            login,
                            null,
                            Collections.singletonList(authority)
                    );

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }
    }

    private String recoverToken(HttpServletRequest request) {
        var authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) return null;
        return authHeader.substring(7);
    }
}