package com.sistemabarberia.fadex_backend.auth.security.filter;

import com.sistemabarberia.fadex_backend.auth.security.jwt.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;



@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {


        try {
            final String authHeader = request.getHeader("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }
            String path = request.getServletPath();

            if (path.startsWith("/api/v1/auth")) {
                filterChain.doFilter(request, response);
                return;
            }


            if (request.getMethod().equals("OPTIONS")) {
                filterChain.doFilter(request, response);
                return;
            }

            final String jwt = authHeader.substring(7);

            if (!jwtService.validateToken(jwt)) {
                filterChain.doFilter(request, response);
                return;
            }

            if (SecurityContextHolder.getContext().getAuthentication() != null) {
                filterChain.doFilter(request, response);
                return;
            }

            final String username = jwtService.extractClaim(jwt, Claims::getSubject);

            final List<String> authoritiesStr = jwtService.extractClaim(jwt, claims -> claims.get("permisos", List.class));
            List<GrantedAuthority> authorities = authoritiesStr.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());

            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(
                            username,
                            null,
                            authorities
                    );

            authToken.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
            );


            SecurityContextHolder.getContext().setAuthentication(authToken);

            log.debug("Usuario autenticado vía JWT: {}", username);


        } catch (ExpiredJwtException e) {
            log.warn("Token JWT expirado: {}", e.getMessage());

        } catch (JwtException e) {
            log.error("Error procesando JWT: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado en JwtAuthenticationFilter: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);

    }
}
