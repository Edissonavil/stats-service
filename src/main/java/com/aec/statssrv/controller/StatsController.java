// src/main/java/com/aec/statssrv/controller/StatsController.java
package com.aec.statssrv.controller;

import com.aec.statssrv.dto.CollaboratorMonthlyStatsDto;
import com.aec.statssrv.dto.ProductAdminStatsDto;
import com.aec.statssrv.service.StatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.List;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class StatsController {

    private final StatsService svc;

    /**
     * ADMIN: ventas de todos los creadores,
     * por colaborador → list de CollaboratorMonthlyStatsDto
     * GET /api/stats/admin/monthly-sales?month=yyyy-MM
     */
    @GetMapping("/admin/monthly-sales")
    @PreAuthorize("hasAuthority('ROL_ADMIN')")
    public ResponseEntity<List<CollaboratorMonthlyStatsDto>> getAdminStats(
            @RequestParam("month") String monthStr,
            JwtAuthenticationToken auth) {
        YearMonth month;
        try {
            month = YearMonth.parse(monthStr);
        } catch (DateTimeParseException ex) {
            return ResponseEntity.badRequest().build();
        }

        List<CollaboratorMonthlyStatsDto> stats = svc.statsForAdmin(month, auth.getToken().getTokenValue());
        return ResponseEntity.ok(stats);

    }

    /**
     * ADMIN: ventas globales por producto y método
     * GET /api/stats/admin/product-stats?month=yyyy-MM
     */
    @GetMapping("/admin/product-stats")
    @PreAuthorize("hasAuthority('ROL_ADMIN')")
    public ResponseEntity<List<ProductAdminStatsDto>> getAdminProductStats(
            @RequestParam("month") String monthStr,
            JwtAuthenticationToken auth) {
        YearMonth month;
        try {
            month = YearMonth.parse(monthStr);
        } catch (DateTimeParseException ex) {
            return ResponseEntity.badRequest().build();
        }

        List<ProductAdminStatsDto> stats = svc.statsForAdminProducts(month, auth.getToken().getTokenValue());

        return ResponseEntity.ok(stats);

    }

    /**
     * COLABORADOR: ventas de sus productos
     * por producto y método de pago
     * GET /api/stats/creator/product-stats?month=yyyy-MM
     */
    /**
     * COLABORADOR: ventas de sus productos
     * por producto y método de pago
     * GET /api/stats/creator/product-stats?month=yyyy-MM
     */
    @GetMapping("/creator/product-stats")
    @PreAuthorize("hasAuthority('ROL_COLABORADOR')")
    public ResponseEntity<List<ProductAdminStatsDto>> getCreatorProductStats(
            @RequestParam("month") String monthStr,
            JwtAuthenticationToken auth) {
        YearMonth month;
        try {
            month = YearMonth.parse(monthStr);
        } catch (DateTimeParseException ex) {
            return ResponseEntity.badRequest().build();
        }

        String username = auth.getName();
        List<ProductAdminStatsDto> stats = svc.statsForCreatorProducts(username, month,
                auth.getToken().getTokenValue());

        // <-- aquí eliminamos el 'noContent' y siempre devolvemos OK
        return ResponseEntity.ok(stats);
    }

}
