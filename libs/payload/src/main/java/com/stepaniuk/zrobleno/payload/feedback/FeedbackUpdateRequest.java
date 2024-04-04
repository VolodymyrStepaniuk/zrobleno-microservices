package com.stepaniuk.zrobleno.payload.feedback;

import com.stepaniuk.zrobleno.validation.feedback.Comment;
import com.stepaniuk.zrobleno.validation.feedback.Rating;
import lombok.*;
import org.springframework.lang.Nullable;
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class FeedbackUpdateRequest {

  @Rating
  @Nullable
  private Integer rating;

  @Comment
  @Nullable
  private String comment;

}
