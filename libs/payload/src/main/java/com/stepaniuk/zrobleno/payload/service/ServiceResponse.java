package com.stepaniuk.zrobleno.payload.service;

import com.stepaniuk.zrobleno.validation.service.Description;
import com.stepaniuk.zrobleno.validation.service.ImageUrl;
import com.stepaniuk.zrobleno.validation.service.Title;
import com.stepaniuk.zrobleno.validation.shared.Id;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;
import org.springframework.lang.Nullable;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = true)
@Relation(collectionRelation = "services", itemRelation = "service")
public class ServiceResponse extends RepresentationModel<ServiceResponse> {
  @Id
  @NotNull
  private Long id;

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
  private List<@ImageUrl String> imageUrls;

  @Nullable
  private BigDecimal price;

  @NotNull
  private Integer priority;

  @NotNull
  private final Instant createdAt;

  @NotNull
  private final Instant lastModifiedAt;
}
