// src/main/java/com/aec/statssrv/controller/StatsController.java
package com.aec.statssrv.controller;

import com.aec.statssrv.dto.*;
import com.aec.statssrv.service.StatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stats")
@Tag(name = "Estadísticas", description = "API para consultar estadísticas de ventas")
@SecurityRequirement(name = "Bearer Authentication")
public class StatsController {
    
    @Autowired
    private StatsService statsService;
    
    @GetMapping("/admin/complete")
    @PreAuthorize("hasAuthority('ROL_ADMIN')")
    @Operation(summary = "Obtener estadísticas completas (Solo Admin)", 
               description = "Retorna estadísticas completas de todos los colaboradores y productos")
    @ApiResponse(responseCode = "200", description = "Estadísticas obtenidas exitosamente")
    @ApiResponse(responseCode = "403", description = "Acceso denegado - Se requiere rol ADMIN")
    public ResponseEntity<SalesStatsDto> getAdminCompleteStats(
            @Parameter(description = "Año para filtrar (requerido)")
            @RequestParam Integer year,
            @Parameter(description = "Mes para filtrar (opcional, 1-12)")
            @RequestParam(required = false) Integer month) {
        
        StatsFilterDto filter = new StatsFilterDto(year, month);
        SalesStatsDto stats = statsService.getAdminStats(filter);
        return ResponseEntity.ok(stats);
    }
    
    @GetMapping("/admin/collaborators")
    @PreAuthorize("hasAuthority('ROL_ADMIN')")
    @Operation(summary = "Obtener ventas por colaborador (Solo Admin)",
               description = "Retorna estadísticas de ventas agrupadas por colaborador")
    public ResponseEntity<List<CollaboratorSalesDto>> getCollaboratorSales(
            @RequestParam Integer year,
            @RequestParam(required = false) Integer month) {
        
        StatsFilterDto filter = new StatsFilterDto(year, month);
        List<CollaboratorSalesDto> sales = statsService.getCollaboratorSalesOnly(filter);
        return ResponseEntity.ok(sales);
    }
    
    @GetMapping("/admin/products")
    @PreAuthorize("hasAuthority('ROL_ADMIN')")
    @Operation(summary = "Obtener ventas por producto (Solo Admin)",
               description = "Retorna estadísticas de ventas agrupadas por producto")
    public ResponseEntity<List<ProductSalesDto>> getAllProductSales(
            @RequestParam Integer year,
            @RequestParam(required = false) Integer month,
            @Parameter(description = "Filtrar por colaborador específico (opcional)")
            @RequestParam(required = false) String collaborator) {
        
        StatsFilterDto filter = new StatsFilterDto(year, month);
        List<ProductSalesDto> sales = statsService.getProductSalesOnly(filter, collaborator);
        return ResponseEntity.ok(sales);
    }
    
    @GetMapping("/collaborator/my-stats")
    @PreAuthorize("hasAnyAuthority('ROL_COLABORADOR', 'ROL_ADMIN')")
    @Operation(summary = "Obtener estadísticas del colaborador actual",
               description = "Retorna estadísticas completas del colaborador autenticado")
    public ResponseEntity<SalesStatsDto> getMyCollaboratorStats(
            @RequestParam Integer year,
            @RequestParam(required = false) Integer month,
            Authentication authentication) {
        
        String username = authentication.getName();
        StatsFilterDto filter = new StatsFilterDto(year, month);
        SalesStatsDto stats = statsService.getCollaboratorStats(username, filter);
        return ResponseEntity.ok(stats);
    }
    
    @GetMapping("/collaborator/{username}")
    @PreAuthorize("hasAuthority('ROL_ADMIN')")
    @Operation(summary = "Obtener estadísticas de un colaborador específico (Solo Admin)",
               description = "Retorna estadísticas completas de un colaborador específico")
    public ResponseEntity<SalesStatsDto> getCollaboratorStats(
            @Parameter(description = "Username del colaborador")
            @PathVariable String username,
            @RequestParam Integer year,
            @RequestParam(required = false) Integer month) {
        
        StatsFilterDto filter = new StatsFilterDto(year, month);
        SalesStatsDto stats = statsService.getCollaboratorStats(username, filter);
        return ResponseEntity.ok(stats);
    }
    
    @GetMapping("/collaborator/my-products")
    @PreAuthorize("hasAnyAuthority('ROL_COLABORADOR', 'ROL_ADMIN')")
    @Operation(summary = "Obtener ventas de productos del colaborador actual",
               description = "Retorna estadísticas de productos del colaborador autenticado")
    public ResponseEntity<List<ProductSalesDto>> getMyProductSales(
            @RequestParam Integer year,
            @RequestParam(required = false) Integer month,
            Authentication authentication) {
        
        String username = authentication.getName();
        StatsFilterDto filter = new StatsFilterDto(year, month);
        List<ProductSalesDto> sales = statsService.getProductSalesOnly(filter, username);
        return ResponseEntity.ok(sales);
    }
    
    @PostMapping("/admin/complete")
    @PreAuthorize("hasAuthority('ROL_ADMIN')")
    @Operation(summary = "Obtener estadísticas completas con filtros avanzados (Solo Admin)",
               description = "Retorna estadísticas completas usando objeto de filtro")
    public ResponseEntity<SalesStatsDto> getAdminCompleteStatsWithFilter(
            @Valid @RequestBody StatsFilterDto filter) {
        
        SalesStatsDto stats = statsService.getAdminStats(filter);
        return ResponseEntity.ok(stats);
    }
    
    @PostMapping("/collaborator/stats")
    @PreAuthorize("hasAnyAuthority('ROL_COLABORADOR', 'ROL_ADMIN')")
    @Operation(summary = "Obtener estadísticas del colaborador con filtros avanzados",
               description = "Retorna estadísticas del colaborador usando objeto de filtro")
    public ResponseEntity<SalesStatsDto> getCollaboratorStatsWithFilter(
            @Valid @RequestBody StatsFilterDto filter,
            Authentication authentication) {
        
        String username = authentication.getName();
        SalesStatsDto stats = statsService.getCollaboratorStats(username, filter);
        return ResponseEntity.ok(stats);
    }
    
    @GetMapping("/health")
    @Operation(summary = "Health check del servicio")
    public ResponseEntity<HealthResponse> healthCheck() {
        return ResponseEntity.ok(new HealthResponse("Stats Service is running", "OK"));
    }
    
    private static class HealthResponse {
        private String message;
        private String status;
        
        public HealthResponse(String message, String status) {
            this.message = message;
            this.status = status;
        }
        
        public String getMessage() { return message; }
        public String getStatus() { return status; }
    }
}

