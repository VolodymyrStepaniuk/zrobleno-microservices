package com.stepaniuk.zrobleno.payload.feedback;

import com.stepaniuk.zrobleno.validation.feedback.Comment;
import com.stepaniuk.zrobleno.validation.feedback.Rating;
import com.stepaniuk.zrobleno.validation.shared.Id;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.lang.Nullable;

import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class FeedbackCreateRequest {

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

}
