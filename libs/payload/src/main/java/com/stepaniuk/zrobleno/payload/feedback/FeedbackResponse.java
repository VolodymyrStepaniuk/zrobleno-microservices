package com.stepaniuk.zrobleno.payload.feedback;

import com.stepaniuk.zrobleno.validation.feedback.Comment;
import com.stepaniuk.zrobleno.validation.feedback.Rating;
import com.stepaniuk.zrobleno.validation.shared.Id;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;
import org.springframework.lang.Nullable;

import java.time.Instant;
import java.util.UUID;

@Getter
@AllArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = true)
@Relation(collectionRelation = "feedbacks", itemRelation = "feedback")
public class FeedbackResponse extends RepresentationModel<FeedbackResponse> {
  @Id
  @NotNull
  private Long id;

  @Id
  @NotNull
  private Long orderId;

  @NotNull
  private UUID ownerId;

  @Rating
  @NotNull
  private Integer rating;

  @Comment
  @Nullable
  private String comment;

  @NotNull
  private Instant createdAt;

  @NotNull
  private Instant lastModifiedAt;

}
