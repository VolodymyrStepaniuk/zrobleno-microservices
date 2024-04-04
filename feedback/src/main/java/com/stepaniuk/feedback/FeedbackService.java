package com.stepaniuk.feedback;


import com.stepaniuk.feedback.client.OrderClient;
import com.stepaniuk.zrobleno.payload.feedback.FeedbackCreateRequest;
import com.stepaniuk.zrobleno.payload.feedback.FeedbackResponse;
import com.stepaniuk.zrobleno.payload.feedback.FeedbackUpdateRequest;
import com.stepaniuk.zrobleno.types.exception.feedback.NoSuchFeedbackByIdException;
import com.stepaniuk.zrobleno.types.exception.order.NoSuchOrderByIdException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FeedbackService {

  private final FeedbackRepository feedbackRepository;
  private final FeedbackMapper feedbackMapper;
  private final OrderClient orderClient;

  public FeedbackResponse createFeedback(FeedbackCreateRequest request) {
    Feedback feedback = new Feedback();

    var order = orderClient.getOrderById(request.getOrderId());

    if(order == null)
      throw new NoSuchOrderByIdException(request.getOrderId());

    feedback.setOrderId(request.getOrderId());
    feedback.setOwnerId(request.getOwnerId());
    feedback.setRating(request.getRating());
    feedback.setComment(request.getComment());

    var savedFeedback = feedbackRepository.save(feedback);
    return feedbackMapper.toResponse(savedFeedback);
  }

  public FeedbackResponse getFeedback(Long id) {
    return feedbackMapper.toResponse(feedbackRepository.findById(id).orElseThrow(
        () -> new NoSuchFeedbackByIdException(id)
    ));
  }

  public Page<FeedbackResponse> getAllFeedbacks(Pageable pageable, @Nullable UUID ownerId,
      @Nullable Long orderId) {
    Specification<Feedback> specification = Specification.where(null);

    if (ownerId != null) {
      specification.and((root, query, criteriaBuilder) -> criteriaBuilder
          .equal(root.get("ownerId"), ownerId)
      );
    }

    if (orderId != null) {
      specification.and((root, query, criteriaBuilder) -> criteriaBuilder
          .equal(root.get("orderId"), orderId)
      );
    }

    return feedbackRepository.findAll(specification, pageable).map(feedbackMapper::toResponse);
  }

  public void deleteFeedback(Long id) {
    var feedback = feedbackRepository.findById(id).orElseThrow(
        () -> new NoSuchFeedbackByIdException(id)
    );
    feedbackRepository.delete(feedback);
  }

  public FeedbackResponse updateFeedback(Long id, FeedbackUpdateRequest request) {
    var feedback = feedbackRepository.findById(id).orElseThrow(
        () -> new NoSuchFeedbackByIdException(id)
    );

    if(request.getRating() != null)
      feedback.setRating(request.getRating());
    if(request.getComment() != null)
      feedback.setComment(request.getComment());

    var updatedFeedback = feedbackRepository.save(feedback);
    return feedbackMapper.toResponse(updatedFeedback);
  }
}
