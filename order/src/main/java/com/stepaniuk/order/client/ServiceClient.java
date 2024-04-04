package com.stepaniuk.order.client;

import com.stepaniuk.zrobleno.payload.service.ServiceResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "service-service", url = "${application.config.service.url}")
public interface ServiceClient {

    @GetMapping("/{id}")
    ServiceResponse getServiceById(@PathVariable Long id);
}
