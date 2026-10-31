package com.inventar.inventar.auth.security;

import com.inventar.inventar.auth.repository.UserRepository;
import com.inventar.inventar.auth.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String username = jwtService.extractUsername(header.substring(7));
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                Instant now = Instant.now();
                // İstifadəçi silinibsə və ya hazırda bloklanıbsa token qəbul olunmur
                boolean allowed = userRepository.findByUsername(username)
                        .map(u -> u.getLockUntil() == null || !now.isBefore(u.getLockUntil()))
                        .orElse(false);
                if (allowed) {
                    var auth = new UsernamePasswordAuthenticationToken(username, null, List.of());
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            }
        }
        chain.doFilter(request, response);
    }
}