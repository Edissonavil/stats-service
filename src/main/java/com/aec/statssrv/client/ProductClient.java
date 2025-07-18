// src/main/java/com/aec/statssrv/client/ProductClient.java
package com.aec.statssrv.client;

import com.aec.statssrv.dto.ProductDto;
import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "product-service", url = "${product.service.url}")
public interface ProductClient {

    @GetMapping("/api/products/{id}")
    @Headers("Authorization: {authorization}")
    ProductDto getById(
        @PathVariable("id") Long id,
        @RequestHeader("Authorization") String authorization
    );

    @GetMapping("/api/products/uploader/{username}")
    @Headers("Authorization: {authorization}")
    List<ProductDto> findByUploader(
        @PathVariable("username") String username,
        @RequestHeader("Authorization") String authorization
    );

    
}
