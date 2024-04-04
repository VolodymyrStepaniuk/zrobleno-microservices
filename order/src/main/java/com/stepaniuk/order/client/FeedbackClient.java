package com.stepaniuk.order.client;

import com.stepaniuk.zrobleno.payload.feedback.FeedbackResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@FeignClient(name = "feedback-service", url = "${application.config.feedback.url}")
public interface FeedbackClient {

    @GetMapping("/v2")
    Page<FeedbackResponse> getAllFeedbacks(Pageable pageable,
                                           @Nullable @RequestParam(required = false) UUID ownerId,
                                           @Nullable @RequestParam(required = false) Long orderId);
}
