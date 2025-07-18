package com.aec.statssrv.service;

import com.aec.statssrv.repository.OrderFactRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AggregationService {

    private final OrderFactRepository repo;

    public BigDecimal getTotalUsd(Instant from, Instant to) {
        return repo.totalUsd(from, to);
    }

    public List<Map<String, Object>> getAmountByCliente(Instant from, Instant to) {
        return repo.totalUsdByCliente(from, to);
    }
}

