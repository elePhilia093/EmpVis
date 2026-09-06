package com.gsz.empvis.security;

import com.gsz.empvis.entity.SysUser;
import com.gsz.empvis.service.SysUserService;
import com.gsz.empvis.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    private final SysUserService sysUserService;

    public JwtAuthenticationFilter(
            JwtUtil jwtUtil,
            SysUserService sysUserService) {

        this.jwtUtil = jwtUtil;
        this.sysUserService = sysUserService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String token = request.getHeader("token");

        if (token == null || token.isBlank()) {
            filterChain.doFilter(request, response);
            return;
        }

        try {

            Claims claims =
                    jwtUtil.parseToken(token);

            String username =
                    claims.getSubject();

            SysUser user =
                    sysUserService.getByUsername(username);

            if (user != null
                    && user.getStatus() != null
                    && user.getStatus() == 1) {

                LoginUser loginUser =
                        new LoginUser(
                                user,
                                sysUserService
                                        .getAuthoritiesByUserId(
                                                user.getId()
                                        )
                        );

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                loginUser,
                                null,
                                loginUser.getAuthorities()
                        );

                SecurityContextHolder
                        .getContext()
                        .setAuthentication(authentication);
            }

        } catch (JwtException
                 | IllegalArgumentException e) {

            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}