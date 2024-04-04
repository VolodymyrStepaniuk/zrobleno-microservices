package com.stepaniuk.feedback;


import com.stepaniuk.feedback.testspecific.JpaLevelTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@JpaLevelTest
@Sql(scripts = "classpath:sql/feedbacks.sql")
class FeedbackRepositoryTest {

  @Autowired
  private FeedbackRepository feedbackRepository;
  @Test
  void shouldSaveFeedback(){
    Feedback feedbackToSave = new Feedback(
        1L, 1L, UUID.randomUUID(), 5, "comment", Instant.now(), Instant.now()
    );

    Feedback savedFeedback = feedbackRepository.save(feedbackToSave);

    assertNotNull(savedFeedback);
    assertNotNull(savedFeedback.getId());
    assertEquals(feedbackToSave.getOrderId(), savedFeedback.getOrderId());
    assertEquals(feedbackToSave.getOwnerId(), savedFeedback.getOwnerId());
    assertEquals(feedbackToSave.getRating(), savedFeedback.getRating());
    assertEquals(feedbackToSave.getComment(), savedFeedback.getComment());
    assertEquals(feedbackToSave.getCreatedAt(), savedFeedback.getCreatedAt());
    assertEquals(feedbackToSave.getLastModifiedAt(), savedFeedback.getLastModifiedAt());
  }

  @Test
  void shouldThrowExceptionWhenSavingFeedbackWithoutRating(){
    Feedback feedbackToSave = new Feedback(
        null, 1L, UUID.randomUUID(), null, "comment", Instant.now(), Instant.now()
    );

    assertThrows(Exception.class, () -> feedbackRepository.save(feedbackToSave));
  }

  @Test
  void shouldReturnFeedbackWhenFindById(){
    Optional<Feedback> optionalFeedback = feedbackRepository.findById(1L);

    assertTrue(optionalFeedback.isPresent());

    Feedback foundFeedback = optionalFeedback.get();

    assertNotNull(foundFeedback);
    assertEquals(1L, foundFeedback.getId());
    assertEquals(1L, foundFeedback.getOrderId());
    assertEquals(5, foundFeedback.getRating());
    assertEquals("Great service!", foundFeedback.getComment());
    assertNotNull(foundFeedback.getCreatedAt());
    assertNotNull(foundFeedback.getLastModifiedAt());
  }

  @Test
  void shouldUpdateFeedbackWhenChangingRating(){
    Feedback feedbackToUpdate = feedbackRepository.findById(1L).orElseThrow();
    feedbackToUpdate.setRating(4);

    Feedback updatedFeedback = feedbackRepository.save(
        feedbackToUpdate);

    assertEquals(feedbackToUpdate.getId(), updatedFeedback.getId());
    assertEquals(4, updatedFeedback.getRating());
  }

  @Test
  void shouldDeleteFeedbackWhenDeletingByExistingFeedback(){
    // given
    Feedback feedbackToDelete = feedbackRepository.findById(1L).orElseThrow();

    // when
    feedbackRepository.delete(feedbackToDelete);

    // then
    Optional<Feedback> optionalFeedback = feedbackRepository.findById(1L);
    assertTrue(optionalFeedback.isEmpty());
  }

  @Test
  void shouldDeleteFeedbackWhenDeletingByExistingFeedbackId(){
    // when
    feedbackRepository.deleteById(1L);

    assertTrue(feedbackRepository.findById(1L).isEmpty());
  }

  @Test
  void shouldReturnTrueWhenServiceExistsById(){
    assertTrue(feedbackRepository.existsById(1L));
  }

  @Test
  void shouldReturnNonEmptyListWhenFindAll(){
    List<Feedback> feedbacks = feedbackRepository.findAll();

    assertNotNull(feedbacks);
    assertFalse(feedbacks.isEmpty());
  }
}
