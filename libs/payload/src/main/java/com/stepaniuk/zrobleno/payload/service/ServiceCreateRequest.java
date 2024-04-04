package com.stepaniuk.zrobleno.payload.service;

import com.stepaniuk.zrobleno.validation.service.Description;
import com.stepaniuk.zrobleno.validation.service.ImageUrl;
import com.stepaniuk.zrobleno.validation.service.Title;
import com.stepaniuk.zrobleno.validation.shared.Id;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.lang.Nullable;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class ServiceCreateRequest {
  @Id
  @NotNull
  private Long categoryId;

  @NotNull
  private UUID ownerId;

  @Title
  @NotNull
  private String title;

  @Description
  @NotNull
  private String description;

  @NotNull
  private Integer priority;

  @NotNull
  private List<@ImageUrl String> imageUrls;

  @Nullable
  private BigDecimal price;
}
