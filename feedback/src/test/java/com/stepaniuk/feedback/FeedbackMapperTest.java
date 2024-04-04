package com.stepaniuk.feedback;


import com.stepaniuk.zrobleno.payload.feedback.FeedbackResponse;
import com.stepaniuk.zrobleno.testspecific.MapperLevelUnitTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@MapperLevelUnitTest
@ContextConfiguration(classes = {FeedbackMapperImpl.class})
class FeedbackMapperTest {

  @Autowired
  private FeedbackMapper feedbackMapper;

  @Test
  void shouldMapFeedbackToFeedbackResponse() {
    // given
    Instant timeOfCreation = Instant.now().plus(Duration.ofHours(10));
    Instant timeOfModification = Instant.now().plus(Duration.ofHours(20));

    Feedback feedbackToMap = new Feedback(
        1L, 1L, UUID.randomUUID(), 5, "comment", timeOfCreation, timeOfModification
    );

    // when
    FeedbackResponse feedbackResponse = feedbackMapper.toResponse(
        feedbackToMap);

    // then
    assertNotNull(feedbackResponse);
    assertEquals(feedbackToMap.getId(), feedbackResponse.getId());
    assertEquals(feedbackToMap.getOrderId(), feedbackResponse.getOrderId());
    assertEquals(feedbackToMap.getOwnerId(), feedbackResponse.getOwnerId());
    assertEquals(feedbackToMap.getRating(), feedbackResponse.getRating());
    assertEquals(feedbackToMap.getComment(), feedbackResponse.getComment());
    assertEquals(feedbackToMap.getCreatedAt(), feedbackResponse.getCreatedAt());
    assertEquals(feedbackToMap.getLastModifiedAt(),
        feedbackResponse.getLastModifiedAt());
    assertTrue(feedbackResponse.hasLinks());
  }
}
