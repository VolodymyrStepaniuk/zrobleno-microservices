package com.stepaniuk.feedback;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.stepaniuk.feedback.testspecific.ControllerLevelUnitTest;
import com.stepaniuk.zrobleno.payload.feedback.FeedbackCreateRequest;
import com.stepaniuk.zrobleno.payload.feedback.FeedbackResponse;
import com.stepaniuk.zrobleno.payload.feedback.FeedbackUpdateRequest;
import com.stepaniuk.zrobleno.types.exception.feedback.NoSuchFeedbackByIdException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.hateoas.Link;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static com.stepaniuk.zrobleno.testspecific.hamcrest.TemporalStringMatchers.instantComparesEqualTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ControllerLevelUnitTest(controllers = FeedbackController.class)
@WithMockUser(username = "user", roles = "USER")
class FeedbackControllerTest {
  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private FeedbackService feedbackService;

  @Autowired
  private ObjectMapper objectMapper;

  private static FeedbackResponse getNewFeedbackResponseWithAllFields(Long feedbackId, Long orderId, UUID ownerId, Integer rating, String comment){
    return new FeedbackResponse(feedbackId, orderId, ownerId, rating,comment, Instant.now().plus(Duration.ofHours(10)),
        Instant.now().plus(Duration.ofHours(20)));
  }

  @Test
  void shouldReturnFeedbackResponseWhenCreatingFeedback() throws Exception {
    var orderId = 1L;
    var ownerId = UUID.randomUUID();
    var feedbackId = 1L;

    var request = new FeedbackCreateRequest(orderId, ownerId, 5, "Some comment");
    var response = getNewFeedbackResponseWithAllFields(feedbackId, orderId, ownerId, 5, "Some comment");
    response.add(Link.of("http://localhost/feedbacks/" + feedbackId, "self"));

    when(feedbackService.createFeedback(eq(request))).thenReturn(response);

    // when && then
    // @formatter:off
    mockMvc.perform(post("/feedbacks")
        .contentType("application/json")
        .content(objectMapper.writeValueAsString(request))
      )
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id", is(response.getId()), Long.class))
        .andExpect(jsonPath("$.orderId", is(request.getOrderId()), Long.class))
        .andExpect(jsonPath("$.ownerId", is(request.getOwnerId().toString())))
        .andExpect(jsonPath("$.rating", is(request.getRating())))
        .andExpect(jsonPath("$.comment", is(request.getComment())))
        .andExpect(jsonPath("$.createdAt", instantComparesEqualTo(response.getCreatedAt())))
        .andExpect(jsonPath("$.lastModifiedAt", instantComparesEqualTo(response.getLastModifiedAt())))
        .andExpect(jsonPath("$._links.self.href", is("http://localhost/feedbacks/" + feedbackId)));
    // @formatter:on
  }

  @Test
  void shouldReturnFeedbackResponseWhenGettingFeedbackById() throws Exception {
    // given
    var feedbackId = 1L;
    var orderId = 1L;
    var ownerId = UUID.randomUUID();

    var response = getNewFeedbackResponseWithAllFields(feedbackId, orderId, ownerId, 5, "Some comment");
    response.add(Link.of("http://localhost/feedbacks/" + feedbackId, "self"));

    when(feedbackService.getFeedback(eq(feedbackId))).thenReturn(response);

    // when && then
    // @formatter:off
    mockMvc.perform(get("/feedbacks/" + feedbackId)
            .contentType("application/json")
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(response.getId()), Long.class))
        .andExpect(jsonPath("$.orderId", is(response.getOrderId()), Long.class))
        .andExpect(jsonPath("$.ownerId", is(response.getOwnerId().toString())))
        .andExpect(jsonPath("$.rating", is(response.getRating())))
        .andExpect(jsonPath("$.comment", is(response.getComment())))
        .andExpect(jsonPath("$.createdAt", instantComparesEqualTo(response.getCreatedAt())))
        .andExpect(jsonPath("$.lastModifiedAt", instantComparesEqualTo(response.getLastModifiedAt())))
        .andExpect(jsonPath("$._links.self.href", is("http://localhost/feedbacks/" + feedbackId)));
    // @formatter:on
  }

  @Test
  void shouldReturnErrorResponseWhenGetByIdAndNoSuchFeedbackByIdException() throws Exception {
    // given
    var feedbackId = 10L;
    when(feedbackService.getFeedback(eq(feedbackId))).thenThrow(new NoSuchFeedbackByIdException(feedbackId));

    // when && then
    // @formatter:off
    mockMvc.perform(get("/feedbacks/"+feedbackId)
            .contentType("application/json")
        )
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.status", is(404)))
        .andExpect(jsonPath("$.title", is("No such feedback")))
        .andExpect(jsonPath("$.detail", is("No feedback with id 10 found")))
        .andExpect(jsonPath("$.instance", is("/feedbacks/10")));
    // @formatter:on
  }

  @Test
  void shouldReturnNoContentWhenDeletingFeedback() throws Exception {
    // given
    var feedbackId = 1L;

    // when && then
    // @formatter:off
    mockMvc.perform(delete("/feedbacks/"+feedbackId)
            .contentType("application/json")
        )
        .andExpect(status().isNoContent());
    // @formatter:on
  }

  @Test
  void shouldUpdateAndReturnFeedbackResponseWhenChangingComment() throws Exception {
    // given
    var feedbackId = 1L;
    var orderId = 1L;
    var ownerId = UUID.randomUUID();
    var newComment = "New comment";
    var request = new FeedbackUpdateRequest(null, newComment);
    var response = getNewFeedbackResponseWithAllFields(feedbackId, orderId, ownerId, 5, newComment);
    response.add(Link.of("http://localhost/feedbacks/" + feedbackId, "self"));

    when(feedbackService.updateFeedback(eq(feedbackId), eq(request))).thenReturn(response);

    // when && then
    // @formatter:off
    mockMvc.perform(patch("/feedbacks/"+feedbackId)
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(request))
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(response.getId()), Long.class))
        .andExpect(jsonPath("$.orderId", is(response.getOrderId()), Long.class))
        .andExpect(jsonPath("$.ownerId", is(response.getOwnerId().toString())))
        .andExpect(jsonPath("$.rating", is(response.getRating())))
        .andExpect(jsonPath("$.comment", is(newComment)))
        .andExpect(jsonPath("$.createdAt", instantComparesEqualTo(response.getCreatedAt())))
        .andExpect(jsonPath("$.lastModifiedAt", instantComparesEqualTo(response.getLastModifiedAt())))
        .andExpect(jsonPath("$._links.self.href", is("http://localhost/feedbacks/" + feedbackId)));
    // @formatter:on
  }

  @Test
  void shouldReturnErrorWhenChangingRatingOfNonExistingFeedback() throws Exception {
    // given
    var feedbackId = 10L;
    var request = new FeedbackUpdateRequest(5, null);
    when(feedbackService.updateFeedback(eq(feedbackId), eq(request))).thenThrow(new NoSuchFeedbackByIdException(feedbackId));

    // when && then
    // @formatter:off
    mockMvc.perform(patch("/feedbacks/"+feedbackId)
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(request))
        )
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.status", is(404)))
        .andExpect(jsonPath("$.title", is("No such feedback")))
        .andExpect(jsonPath("$.detail", is("No feedback with id 10 found")))
        .andExpect(jsonPath("$.instance", is("/feedbacks/10")));
    // @formatter:on
  }

  @Test
  void shouldReturnPageOfFeedbackResponsesWhenGettingAllFeedbacks() throws Exception {
    // given
    var feedbackId = 1L;
    var orderId = 1L;
    var ownerId = UUID.randomUUID();
    var pageable = PageRequest.of(0, 2);

    var response = getNewFeedbackResponseWithAllFields(feedbackId, orderId, ownerId, 5, "Some comment");
    response.add(Link.of("http://localhost/feedbacks/" + feedbackId, "self"));

    when(feedbackService.getAllFeedbacks(eq(pageable), eq(null), eq(null))).thenReturn(new PageImpl<>(
        List.of(response), pageable, 1)
    );

    // when && then
    // @formatter:off
    mockMvc.perform(get("/feedbacks/v1")
            .contentType("application/json")
            .param("page", "0")
            .param("size", "2")
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].id", is(response.getId()), Long.class))
        .andExpect(jsonPath("$.content[0].orderId", is(response.getOrderId()), Long.class))
        .andExpect(jsonPath("$.content[0].ownerId", is(response.getOwnerId().toString())))
        .andExpect(jsonPath("$.content[0].rating", is(response.getRating())))
        .andExpect(jsonPath("$.content[0].comment", is(response.getComment())))
        .andExpect(jsonPath("$.content[0].createdAt", instantComparesEqualTo(response.getCreatedAt())))
        .andExpect(jsonPath("$.content[0].lastModifiedAt", instantComparesEqualTo(response.getLastModifiedAt())))
        .andExpect(jsonPath("$.content[0].links[0].href", is("http://localhost/feedbacks/" + feedbackId)));
  }

  @Test
  void shouldReturnPageOfFeedbackResponsesWhenGettingAllFeedbacksAndOwnerIdIsNotNull() throws Exception {
    // given
    var feedbackId = 1L;
    var orderId = 1L;
    var ownerId = UUID.randomUUID();
    var pageable = PageRequest.of(0, 2);

    var response = getNewFeedbackResponseWithAllFields(feedbackId, orderId, ownerId, 5, "Some comment");
    response.add(Link.of("http://localhost/feedbacks/" + feedbackId, "self"));

    when(feedbackService.getAllFeedbacks(eq(pageable), eq(ownerId), eq(null))).thenReturn(new PageImpl<>(
        List.of(response), pageable, 1)
    );

    // when && then
    // @formatter:off
    mockMvc.perform(get("/feedbacks/v1")
            .contentType("application/json")
            .param("page", "0")
            .param("size", "2")
            .param("ownerId", ownerId.toString())
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].id", is(response.getId()), Long.class))
        .andExpect(jsonPath("$.content[0].orderId", is(response.getOrderId()), Long.class))
        .andExpect(jsonPath("$.content[0].ownerId", is(response.getOwnerId().toString())))
        .andExpect(jsonPath("$.content[0].rating", is(response.getRating())))
        .andExpect(jsonPath("$.content[0].comment", is(response.getComment())))
        .andExpect(jsonPath("$.content[0].createdAt", instantComparesEqualTo(response.getCreatedAt())))
        .andExpect(jsonPath("$.content[0].lastModifiedAt", instantComparesEqualTo(response.getLastModifiedAt())))
        .andExpect(jsonPath("$.content[0].links[0].href", is("http://localhost/feedbacks/" + feedbackId)));
  }

  @Test
  void shouldReturnPageOfFeedbackResponsesWhenGettingAllFeedbacksAndOrderIdIsNotNull() throws Exception{
    // given
    var feedbackId = 1L;
    var orderId = 1L;
    var ownerId = UUID.randomUUID();
    var pageable = PageRequest.of(0, 2);

    var response = getNewFeedbackResponseWithAllFields(feedbackId, orderId, ownerId, 5, "Some comment");
    response.add(Link.of("http://localhost/feedbacks/" + feedbackId, "self"));

    when(feedbackService.getAllFeedbacks(eq(pageable), eq(null), eq(orderId))).thenReturn(new PageImpl<>(
        List.of(response), pageable, 1)
    );

    // when && then
    // @formatter:off
    mockMvc.perform(get("/feedbacks/v2")
            .contentType("application/json")
            .param("page", "0")
            .param("size", "2")
            .param("orderId", String.valueOf(orderId))
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].id", is(response.getId()), Long.class))
        .andExpect(jsonPath("$.content[0].orderId", is(response.getOrderId()), Long.class))
        .andExpect(jsonPath("$.content[0].ownerId", is(response.getOwnerId().toString())))
        .andExpect(jsonPath("$.content[0].rating", is(response.getRating())))
        .andExpect(jsonPath("$.content[0].comment", is(response.getComment())))
        .andExpect(jsonPath("$.content[0].createdAt", instantComparesEqualTo(response.getCreatedAt())))
        .andExpect(jsonPath("$.content[0].lastModifiedAt", instantComparesEqualTo(response.getLastModifiedAt())))
        .andExpect(jsonPath("$.content[0].links[0].href", is("http://localhost/feedbacks/" + feedbackId)));
  }
}
