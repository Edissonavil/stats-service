package com.aec.statssrv.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "order_facts")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class OrderFact {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private Long orderId;

  @Column(nullable = false)
  private String clienteUsername;

  @Column(nullable = false, precision = 18, scale = 2)
  private BigDecimal totalUsd;

  @Column(nullable = false)
  private Instant createdAt;
}
