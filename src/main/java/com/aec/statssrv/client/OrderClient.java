// src/main/java/com/aec/statssrv/client/OrderClient.java
package com.aec.statssrv.client;

import com.aec.statssrv.dto.ProductSalesDto; // Importa los DTOs de stats
import com.aec.statssrv.config.FeignConfig;
import com.aec.statssrv.dto.CollaboratorMonthlyStatsDto; // Importa los DTOs de stats
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.Instant; // Importar Instant
import java.time.YearMonth; // Importar YearMonth
import java.util.List;

// Asegúrate que 'ordersrv' sea el nombre de tu servicio en Eureka/Load Balancer
@FeignClient(
    name = "ordersrv", 
    url  = "${orders.service.url}",            // <-- aquí
    configuration = FeignConfig.class          // <-- si quieres aplicar tu interceptor
)public interface OrderClient {

    @GetMapping("/api/orders/stats/creator/{creatorUsername}")
List<ProductSalesDto> getCreatorMonthlySalesStats(
    @PathVariable String creatorUsername,
    @RequestParam("month") YearMonth month,
    @RequestHeader("Authorization") String authHeader
);

  
@GetMapping("/api/orders/stats/admin/monthly-sales")
List<CollaboratorMonthlyStatsDto> getAllCollaboratorsMonthlySalesStats(
    @RequestParam("month") YearMonth month,
    @RequestHeader("Authorization") String authHeader
);

  @GetMapping("/api/orders/stats/completed")
  List<com.aec.statssrv.dto.OrderDto> findCompletedBetween(
    @RequestParam("from") Instant from,
    @RequestParam("to")   Instant to,
    @RequestHeader("Authorization") String authHeader
  );

    @GetMapping("/api/orders/stats/uploader/monthly-sales")
  List<ProductSalesDto> getUploaderMonthlySalesStats(
    @RequestParam("uploader") String uploader,
    @RequestParam("month") YearMonth month,
    @RequestHeader("Authorization") String bearer
  );

  
}