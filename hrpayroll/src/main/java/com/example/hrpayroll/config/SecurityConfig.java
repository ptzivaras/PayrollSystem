package com.example.hrpayroll.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(req -> {
                    CorsConfiguration cfg = new CorsConfiguration();
                    cfg.setAllowedOrigins(List.of("http://localhost:5173"));
                    cfg.setAllowedMethods(List.of("GET","POST","PUT","DELETE","PATCH","OPTIONS"));
                    cfg.setAllowedHeaders(List.of("Authorization","Content-Type","Accept"));
                    cfg.setAllowCredentials(true);
                    return cfg;
                }))
                .csrf(AbstractHttpConfigurer::disable) // API-only
                .authorizeHttpRequests(auth -> auth
                        // Departments - only ADMIN
                        .requestMatchers("/api/departments/**").hasRole("ADMIN")

                        // Employees: GET allowed kai se EMPLOYEE, write mono ADMIN
                        .requestMatchers(HttpMethod.GET, "/api/employees/**").hasAnyRole("ADMIN","EMPLOYEE")
                        .requestMatchers("/api/employees/**").hasRole("ADMIN")

                        // Payroll runs: dimiourgia/post mono ADMIN, anagnwsi kai gia EMPLOYEE
                        .requestMatchers(HttpMethod.POST, "/api/payroll/runs/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/payroll/runs/**").hasAnyRole("ADMIN","EMPLOYEE")

                        // Payroll items: mono anagnwsi - both roles
                        .requestMatchers(HttpMethod.GET, "/api/payroll/items/**").hasAnyRole("ADMIN","EMPLOYEE")

                        // otidipote allo: authenticated
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults()); // Basic Auth
        return http.build();
    }

    @Bean
    public UserDetailsService users(PasswordEncoder encoder) {
        return new InMemoryUserDetailsManager(
                User.withUsername("admin")
                        .password(encoder.encode("password"))
                        .roles("ADMIN")
                        .build(),
                User.withUsername("employee")
                        .password(encoder.encode("password"))
                        .roles("EMPLOYEE")
                        .build()
        );
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
