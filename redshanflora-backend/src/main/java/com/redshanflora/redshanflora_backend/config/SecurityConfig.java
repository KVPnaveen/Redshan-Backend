package com.redshanflora.redshanflora_backend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final CorsConfigurationSource corsConfigurationSource;

    private static final String[] SWAGGER_WHITELIST = {
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/swagger-resources/**",
            "/webjars/**"
    };

    @Bean
    @Profile("!prod")
    public SecurityFilterChain devSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Swagger UI & API docs
                        .requestMatchers(SWAGGER_WHITELIST).permitAll()

                        // Public endpoints
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/payment/notify", "/api/checkout/notify").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/products/**", "/api/categories/**", "/api/subcategories/**").permitAll()
                        .requestMatchers("/uploads/**", "/models/**").permitAll()
                        .requestMatchers(
                                "/api/admin/profile", "/api/admin/profile/**",
                                "/api/admin/settings", "/api/admin/settings/**",
                                "/api/admin/notifications", "/api/admin/notifications/**",
                                "/api/reports", "/api/reports/**"
                        ).permitAll()

                        // Role-specific protected endpoints
                        .requestMatchers("/api/admin/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers("/api/reports/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers("/api/manager/**").hasAnyRole("MANAGER", "ADMIN")
                        .requestMatchers("/api/dashboard/**").hasAnyRole("MANAGER", "ADMIN")
                        .requestMatchers("/api/employee/**", "/api/employees/**").hasAnyRole("EMPLOYEE", "MANAGER", "ADMIN")
                        .requestMatchers("/api/cart/**", "/api/wishlist/**", "/api/customer/**", "/api/loyalty/**").hasRole("CUSTOMER")
                        .requestMatchers("/api/checkout/**", "/api/payment/**", "/api/customized-bouquets/**").hasRole("CUSTOMER")

                        // Require authentication on all other endpoints
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    @Profile("prod")
    public SecurityFilterChain prodSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Swagger UI & API docs (Omit if Swagger shouldn't be accessible in prod)
                        .requestMatchers(SWAGGER_WHITELIST).permitAll()

                        // Public endpoints
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/payment/notify", "/api/checkout/notify").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/products/**", "/api/categories/**", "/api/subcategories/**").permitAll()
                        .requestMatchers("/uploads/**", "/models/**").permitAll()
                        .requestMatchers(
                                "/api/admin/profile", "/api/admin/profile/**",
                                "/api/admin/settings", "/api/admin/settings/**",
                                "/api/admin/notifications", "/api/admin/notifications/**",
                                "/api/reports", "/api/reports/**"
                        ).permitAll()

                        // Role-specific protected endpoints
                        .requestMatchers("/api/admin/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers("/api/reports/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers("/api/manager/**").hasAnyRole("MANAGER", "ADMIN")
                        .requestMatchers("/api/dashboard/**").hasAnyRole("MANAGER", "ADMIN")
                        .requestMatchers("/api/employee/**", "/api/employees/**").hasAnyRole("EMPLOYEE", "MANAGER", "ADMIN")
                        .requestMatchers("/api/cart/**", "/api/wishlist/**", "/api/customer/**", "/api/loyalty/**").hasRole("CUSTOMER")
                        .requestMatchers("/api/checkout/**", "/api/payment/**", "/api/customized-bouquets/**").hasRole("CUSTOMER")

                        // Require authentication on all other endpoints
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}