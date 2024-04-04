package com.stepaniuk.feedback;


import com.stepaniuk.zrobleno.payload.feedback.FeedbackCreateRequest;
import com.stepaniuk.zrobleno.payload.feedback.FeedbackResponse;
import com.stepaniuk.zrobleno.payload.feedback.FeedbackUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/feedbacks", produces = "application/json")
public class FeedbackController {

  private final FeedbackService feedbackService;

  @PostMapping
  public ResponseEntity<FeedbackResponse> createFeedback(@RequestBody FeedbackCreateRequest request) {
    return new ResponseEntity<>(feedbackService.createFeedback(request),
        HttpStatus.CREATED);
  }

  @GetMapping("/{id}")
  public ResponseEntity<FeedbackResponse> getFeedbackById(@PathVariable Long id) {
    return ResponseEntity.ok(feedbackService.getFeedback(id));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteFeedback(@PathVariable Long id) {
    feedbackService.deleteFeedback(id);
    return ResponseEntity.noContent().build();
  }

  @PatchMapping("/{id}")
  public ResponseEntity<FeedbackResponse> updateFeedback(@PathVariable Long id,
      @RequestBody FeedbackUpdateRequest request) {
    return ResponseEntity.ok(feedbackService.updateFeedback(id, request));
  }

  @GetMapping("/v1")
  public ResponseEntity<Page<FeedbackResponse>> getAllFeedbacks(Pageable pageable,
      @Nullable @RequestParam(required = false) UUID ownerId) {
    return ResponseEntity.ok(feedbackService.getAllFeedbacks(pageable, ownerId, null));
  }

  @GetMapping("/v2")
  public ResponseEntity<Page<FeedbackResponse>> getAllFeedbacks(Pageable pageable,
      @Nullable @RequestParam(required = false) UUID ownerId,
      @Nullable @RequestParam(required = false) Long orderId) {
    return ResponseEntity.ok(feedbackService.getAllFeedbacks(pageable, ownerId, orderId));
  }
}
