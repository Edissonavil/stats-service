package com.aec.statssrv.config;

import feign.RequestInterceptor;
import feign.codec.Encoder;
import feign.jackson.JacksonEncoder;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.springframework.security.core.Authentication; // Necesario si quieres un fallback
import org.springframework.security.core.context.SecurityContextHolder; // Necesario si quieres un fallback

@Configuration
public class FeignConfig {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            // Intenta obtener los atributos de la petición HTTP actual
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

            if (attributes != null) {
                // Si hay atributos de petición, significa que estamos en un contexto de petición HTTP.
                // Obtenemos el encabezado de autorización directamente de la petición original.
                String authorizationHeader = attributes.getRequest().getHeader("Authorization");

                if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                    requestTemplate.header("Authorization", authorizationHeader);
                    // Opcional: Para depuración, puedes imprimir el token
                    System.out.println("DEBUG FeignInterceptor: Token copiado de Request Header: " + authorizationHeader.substring(0, Math.min(authorizationHeader.length(), 30)) + "...");
                } else {
                    System.out.println("DEBUG FeignInterceptor: No se encontró Authorization header en la petición original.");
                }
            } else {
                // Si RequestContextHolder es null (ej., en un hilo no asociado directamente a una petición HTTP),
                // podemos intentar como fallback obtenerlo del SecurityContextHolder.
                // Esto es menos común para Feign pero útil en otros escenarios asíncronos.
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                if (authentication != null && authentication.getCredentials() instanceof String) {
                    String token = (String) authentication.getCredentials();
                    requestTemplate.header("Authorization", "Bearer " + token);
                    System.out.println("DEBUG FeignInterceptor: Token obtenido de SecurityContextHolder (fallback).");
                } else {
                    System.out.println("DEBUG FeignInterceptor: No se encontró token en SecurityContextHolder ni en RequestContextHolder.");
                }
            }
        };
    }

    @Bean
    public Encoder feignEncoder() {
        ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return new JacksonEncoder(mapper);
    }

}