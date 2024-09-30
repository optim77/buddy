package com.buddy.buddy.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .requestMatchers("/public/**", "/**", "/register", "/authenticate").permitAll() // Zezwala na dostęp bez autoryzacji do tych ścieżek
                .anyRequest().authenticated() // Wymaga autoryzacji dla pozostałych ścieżek
                .and()
                .csrf(AbstractHttpConfigurer::disable) // Wyłącza ochronę przed CSRF (opcjonalnie, zależnie od potrzeb)
                .formLogin().disable() // Wyłącza domyślny formularz logowania
                .httpBasic().disable(); // Wyłącza autoryzację Basic

        return http.build();
    }
}
