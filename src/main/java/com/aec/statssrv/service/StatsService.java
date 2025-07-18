// src/main/java/com/aec/statssrv/service/StatsService.java
package com.aec.statssrv.service;

import com.aec.statssrv.client.OrderClient;
import com.aec.statssrv.client.ProductClient;
import com.aec.statssrv.dto.CollaboratorMonthlyStatsDto;
import com.aec.statssrv.dto.OrderDto;
import com.aec.statssrv.dto.ProductAdminStatsDto;
import com.aec.statssrv.dto.ProductDto;
import com.aec.statssrv.dto.ProductSalesDto;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatsService {

    private final OrderClient orderClient;
    private final ProductClient productClient;

    /**
     * ADMIN: ventas globales de todos los creadores,
     * desglosadas por colaborador → producto/método
     */
    public List<CollaboratorMonthlyStatsDto> statsForAdmin(
        YearMonth month,
        String token
    ) {
        try {
            return orderClient.getAllCollaboratorsMonthlySalesStats(
                month, "Bearer " + token
            );
        } catch (FeignException e) {
            return Collections.emptyList();
        }
    }

    /**
     * ADMIN: estadísticas por producto (global),
     * desglosadas por método de pago
     */

 public List<ProductAdminStatsDto> statsForAdminProducts(
      YearMonth month,
      String token
  ) {
    // 1) calcula rango
    Instant start = month.atDay(1).atStartOfDay(ZoneOffset.UTC).toInstant();
    Instant end   = month.atEndOfMonth()
                         .atTime(23,59,59)
                         .atZone(ZoneOffset.UTC)
                         .toInstant();

    // 2) trae todas las órdenes completadas
    List<com.aec.statssrv.dto.OrderDto> orders =
      orderClient.findCompletedBetween(start, end, "Bearer " + token);

    // 3) agrupa todos los ítems por productId + paymentMethod
    Map<String, ProductAdminStatsDto> map = new HashMap<>();
    for (var o : orders) {
      String method = o.getPaymentMethod();
      for (var item : o.getItems()) {
        Long pid = item.getProductId();
        Long qty = (long) item.getCantidad();
        BigDecimal revenue = item.getSubtotal();  // ¡item.getSubtotal() ya es BigDecimal!
        String key = pid + "|" + method;

        map.compute(key, (k, dto) -> {
          if (dto == null) {
            dto = ProductAdminStatsDto.builder()
              .productId(pid)
              .productName(null)
              .totalSold(0L)
              .totalRevenue(BigDecimal.ZERO)
              .byPaymentMethod(new HashMap<>())
              .build();
          }
          dto.setTotalSold(dto.getTotalSold() + qty);
          dto.setTotalRevenue(dto.getTotalRevenue().add(revenue));
          dto.getByPaymentMethod()
             .merge(method, qty, Long::sum);
          return dto;
        });
      }
    }

    // 4) Rellena el nombre de cada producto
    String bearer = "Bearer " + token;
    map.values().forEach(dto -> {
      try {
        dto.setProductName(
          productClient.getById(dto.getProductId(), bearer).getNombre()
        );
      } catch (Exception e) {
        dto.setProductName("N/A");
      }
    });

    return new ArrayList<>(map.values());
  }


    /**
     * COLABORADOR: ventas de **mis** productos,
     * desglosadas por producto y método de pago
     */
 public List<ProductAdminStatsDto> statsForCreatorProducts(
      String uploader,
      YearMonth month,
      String token
  ) {
    String bearer = "Bearer " + token;

    // 1) Pido directamente las ventas filtradas por uploader
    List<ProductSalesDto> sales;
    try {
      sales = orderClient.getUploaderMonthlySalesStats(uploader, month, bearer);
    } catch (FeignException e) {
      return Collections.emptyList();
    }

    // 2) Agrupo en ProductAdminStatsDto
    Map<Long, ProductAdminStatsDto> map = new HashMap<>();
    for (var ps : sales) {
      map.compute(ps.getProductId(), (id, dto) -> {
        if (dto == null) {
          dto = ProductAdminStatsDto.builder()
            .productId(id)
            .productName(null)
            .totalSold(0L)
            .totalRevenue(BigDecimal.ZERO)
            .byPaymentMethod(new HashMap<>())
            .build();
        }
        dto.setTotalSold(dto.getTotalSold() + ps.getTotalQuantity());
        dto.setTotalRevenue(dto.getTotalRevenue().add(ps.getTotalSalesAmount()));
        dto.getByPaymentMethod().merge(ps.getPaymentMethod(), ps.getOrderCount(), Long::sum);
        return dto;
      });
    }

    // 3) Ponemos nombre al producto
    map.values().forEach(dto -> {
      try {
        dto.setProductName(
          productClient.getById(dto.getProductId(), bearer).getNombre()
        );
      } catch (Exception ignore) {
        dto.setProductName("UNKNOWN");
      }
    });

    return new ArrayList<>(map.values());
  }


}
