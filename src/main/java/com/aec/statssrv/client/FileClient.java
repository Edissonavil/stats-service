package com.aec.statssrv.client;

import java.time.Instant;
import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "file-service", url = "${file.service.url}/api/files")
public interface FileClient {
  @GetMapping("/downloads/counts")
  Map<Long, Integer> getDownloadCountsByProduct( 
    @RequestParam("from") Instant from,
    @RequestParam("to")   Instant to
  );
}
