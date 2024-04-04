package com.stepaniuk.feedback.client;

import com.stepaniuk.zrobleno.payload.order.OrderResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "order-service", url = "${application.config.order.url}", primary = false)
public interface OrderClient {

    @GetMapping("/{id}")
    OrderResponse getOrderById(@PathVariable Long id);
}
