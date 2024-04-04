package com.stepaniuk.zrobleno.payload.order;


import com.stepaniuk.zrobleno.payload.feedback.FeedbackResponse;
import com.stepaniuk.zrobleno.types.order.OrderStatusName;
import com.stepaniuk.zrobleno.validation.shared.Id;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;
import org.springframework.lang.Nullable;

import java.time.Instant;
import java.util.UUID;

@Getter
@AllArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = true)
@Relation(collectionRelation = "orders", itemRelation = "order")
public class OrderResponse extends RepresentationModel<OrderResponse> {

  @Id
  @NotNull
  private Long id;

  @NotNull
  private UUID ownerId;

  @NotNull
  private OrderStatusName status;

  @NotNull
  @Id
  private Long serviceId;

  @Nullable
  private String comment;

  @Nullable
  private final FeedbackResponse feedback;

  @NotNull
  private final Instant createdAt;

  @NotNull
  private final Instant lastModifiedAt;

}
