package com.example.cobafirebase.config;

import com.example.cobafirebase.security.JwtFilter;
import com.example.cobafirebase.services.UserDetailService;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// @Configuration
// @EnableWebSecurity
// public class SecurityConfig {

//     private final JwtFilter jwtFilter;

//     public SecurityConfig(JwtFilter jwtFilter) {
//         this.jwtFilter = jwtFilter;
//     }

//     @Bean
//     public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//         return http
//                 .csrf(csrf -> csrf.disable()) // Matikan CSRF (untuk API)
//                 .authorizeHttpRequests(auth -> auth
//                         .requestMatchers("/api/auth/login").permitAll() // Izinkan tanpa token
//                         .anyRequest().authenticated() // Semua endpoint lain harus pakai token
//                 )
//                 .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
//                 .build();
//     }
// }
@Configuration
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final UserDetailService userDetailsService;

    public SecurityConfig(JwtFilter jwtFilter, UserDetailService userDetailsService) {
        this.jwtFilter = jwtFilter;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/login/**").permitAll() // Endpoints auth tidak butuh token
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}