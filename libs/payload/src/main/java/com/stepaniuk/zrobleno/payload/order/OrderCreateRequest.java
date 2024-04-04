package com.stepaniuk.zrobleno.payload.order;

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
public class OrderCreateRequest {

  @NotNull
  private UUID ownerId;

  @NotNull
  @Id
  private Long serviceId;

  @Nullable
  private String comment;
}
