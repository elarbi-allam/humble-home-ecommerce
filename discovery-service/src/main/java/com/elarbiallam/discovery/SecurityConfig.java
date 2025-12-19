package com.elarbiallam.discovery.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 1. Désactiver CSRF (sinon les services ne peuvent pas s'enregistrer)
            .csrf(csrf -> csrf.ignoringRequestMatchers("/eureka/**"))
            // 2. Tout sécuriser sauf les endpoints de santé (Actuator)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/**").permitAll()
                .anyRequest().authenticated()
            )
            // 3. Activer l'auth basique (utilise le user/password du .env)
            .httpBasic(withDefaults());

        return http.build();
    }
}