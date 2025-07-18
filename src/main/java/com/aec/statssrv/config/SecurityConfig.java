// src/main/java/com/aec/statssrv/config/SecurityConfig.java
package com.aec.statssrv.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   JwtAuthenticationConverter jwtAuthConverter) throws Exception {
        http
          .csrf().disable()
          .authorizeHttpRequests(auth -> auth
              // colaborador solo a creator
              .requestMatchers("/api/stats/creator/**")
                .hasAuthority("ROL_COLABORADOR")
              // admin solo a cualquier /admin/*
              .requestMatchers("/api/stats/admin/**")
                .hasAuthority("ROL_ADMIN")
              .anyRequest().authenticated()
          )
          .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
          .oauth2ResourceServer(oauth2 -> oauth2
              .jwt(jwt -> jwt
                  .decoder(jwtDecoder())
                  .jwtAuthenticationConverter(jwtAuthConverter)
              )
          );
        return http.build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        byte[] key = Base64.getDecoder().decode(jwtSecret);
        return NimbusJwtDecoder
                 .withSecretKey(new SecretKeySpec(key, "HmacSHA256"))
                 .build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter delegate = new JwtGrantedAuthoritiesConverter();
        delegate.setAuthoritiesClaimName("role");
        delegate.setAuthorityPrefix("");

        JwtAuthenticationConverter conv = new JwtAuthenticationConverter();
        conv.setJwtGrantedAuthoritiesConverter(delegate);
        return conv;
    }
}

