package com.stepaniuk.zrobleno.payload.order;


import com.stepaniuk.zrobleno.types.order.OrderStatusName;
import lombok.*;
import org.springframework.lang.Nullable;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class OrderUpdateRequest {

  @Nullable
  private OrderStatusName status;

  @Nullable
  private String comment;
}
