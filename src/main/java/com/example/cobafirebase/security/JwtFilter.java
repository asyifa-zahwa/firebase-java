package com.example.cobafirebase.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.cobafirebase.services.UserDetailService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.ArrayList;

// @Component
// public class JwtFilter extends OncePerRequestFilter {

//     @Autowired
//     private JwtUtil jwtUtil;

//     @Override
//     protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
//             throws ServletException, IOException {

//         String authHeader = request.getHeader("Authorization");

//         if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//             chain.doFilter(request, response);
//             return;
//         }

//         String token = authHeader.substring(7); // Ambil token setelah "Bearer "
//         if (jwtUtil.validateToken(token)) {
//             String email = jwtUtil.extractEmail(token);
//             // Bisa set email ke SecurityContext kalau ada UserDetailsService
//             System.out.println("User Authenticated: " + email);
//         }

//         chain.doFilter(request, response);
//     }
// }
// @Component
// public class JwtFilter extends OncePerRequestFilter {

//     @Autowired
//     private JwtUtil jwtUtil;

//     @Autowired
//     private UserDetailService userDetailsService; // Digunakan untuk memuat informasi user

//     @Override
//     protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
//             throws ServletException, IOException {

//         String authHeader = request.getHeader("Authorization");

//         if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//             chain.doFilter(request, response);
//             return;
//         }

//         String token = authHeader.substring(7); // Ambil token setelah "Bearer "
//         if (jwtUtil.validateToken(token)) {
//             String email = jwtUtil.extractEmail(token);

//             // Load user details dari database (Firestore)
//             UserDetails userDetails = userDetailsService.loadUserByUsername(email);

//             // Buat token autentikasi untuk Spring Security
//             UsernamePasswordAuthenticationToken authentication =
//                     new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

//             authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//             SecurityContextHolder.getContext().setAuthentication(authentication);
//         }

//         chain.doFilter(request, response);
//     }
// }

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("JWT Filter: Tidak ada token, lanjut tanpa autentikasi");
            chain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);
        System.out.println("JWT Filter: Token diterima -> " + token);

        if (jwtUtil.validateToken(token)) {
            String email = jwtUtil.extractEmail(token);
            UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(email, null, new ArrayList<>());
            SecurityContextHolder.getContext().setAuthentication(authentication); // ✅ Set User ke Context

            System.out.println("JWT Filter: Token valid, user -> " + email);

            System.out.println("JWT Filter: Authentication set in SecurityContext");
        } else {
            System.out.println("JWT Filter: Token tidak valid!");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT Token");
            return;
        }

        chain.doFilter(request, response);
    }
}

