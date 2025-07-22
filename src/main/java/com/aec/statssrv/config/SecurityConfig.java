// src/main/java/com/aec/statssrv/config/SecurityConfig.java
package com.aec.statssrv.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.crypto.spec.SecretKeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cfg = new CorsConfiguration();

        // 1) Orígenes permitidos — incluye aquí localhost y tus dominios en producción
        cfg.setAllowedOrigins(List.of(
            "https://aecf-production.up.railway.app",
            "https://aecblock.com"
        ));
        // 2) Métodos HTTP permitidos
        cfg.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS"));
        // 3) Headers permitidos
        cfg.setAllowedHeaders(List.of("*"));
        // 4) Permitir enviar cookies o Authorization headers
        cfg.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Aplica a todas las rutas
        source.registerCorsConfiguration("/**", cfg);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   JwtAuthenticationConverter jwtAuthConverter) throws Exception {

        http
          .csrf(csrf -> csrf.disable())
          .cors(cors -> cors.configurationSource(corsConfigurationSource()))
          .authorizeHttpRequests(auth -> auth
              // Permite acceso sin autenticación a Swagger UI y health check
              .requestMatchers("/api/stats/health", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
              
              // Define las reglas de autorización basadas en roles/autoridades
              // *** CAMBIO CLAVE AQUÍ: Usar hasAuthority("ROL_ADMIN") ***
              .requestMatchers("/api/stats/admin/**").hasAuthority("ROL_ADMIN") 
              // Mantener hasAnyAuthority para colaborador, ya es correcto
              .requestMatchers("/api/stats/collaborator/**").hasAnyAuthority("ROL_COLABORADOR", "ROL_ADMIN")
              
              // Cualquier otra solicitud requiere autenticación
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
        byte[] keyBytes = Base64.getDecoder().decode(jwtSecret);
        return NimbusJwtDecoder
                 .withSecretKey(new SecretKeySpec(keyBytes,"HmacSHA256"))
                 .build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter delegate = new JwtGrantedAuthoritiesConverter();
        delegate.setAuthoritiesClaimName("role"); // El nombre del claim en tu JWT que contiene el/los rol/es
        delegate.setAuthorityPrefix(""); // No añadas prefijo "SCOPE_" o "ROLE_" si tu claim ya tiene "ROL_"

        JwtAuthenticationConverter conv = new JwtAuthenticationConverter();
        conv.setJwtGrantedAuthoritiesConverter(jwt -> 
              delegate.convert(jwt).stream()
                      .map(a -> new SimpleGrantedAuthority(a.getAuthority()))
                      .collect(Collectors.toList())
        );
        return conv;
    }
}
