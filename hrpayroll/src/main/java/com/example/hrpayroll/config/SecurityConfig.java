package com.example.hrpayroll.config;

import org.springframework.beans.factory.annotation.Value;
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
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Value("${app.cors.allowed-origins:http://localhost:5173}")
    private List<String> allowedOrigins;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        return request -> {
            CorsConfiguration cfg = new CorsConfiguration();
            cfg.setAllowedOrigins(allowedOrigins);
            cfg.setAllowedMethods(Arrays.asList("GET","POST","PUT","DELETE","PATCH","OPTIONS"));
            cfg.setAllowedHeaders(Arrays.asList("Authorization","Content-Type","Accept"));
            cfg.setExposedHeaders(List.of("Location"));
            cfg.setAllowCredentials(true);
            return cfg;
        };
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/departments/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/employees/**").hasAnyRole("ADMIN","EMPLOYEE")
                        .requestMatchers("/api/employees/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/payroll/runs/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/payroll/runs/**").hasAnyRole("ADMIN","EMPLOYEE")
                        .requestMatchers(HttpMethod.GET, "/api/payroll/items/**").hasAnyRole("ADMIN","EMPLOYEE")
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults());
        return http.build();
    }

    @Bean
    public UserDetailsService users(PasswordEncoder encoder) {
        return new InMemoryUserDetailsManager(
                User.withUsername("admin").password(encoder.encode("password")).roles("ADMIN").build(),
                User.withUsername("employee").password(encoder.encode("password")).roles("EMPLOYEE").build()
        );
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
