package com.stepaniuk.zrobleno.payload.service.category;

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

import java.time.Instant;
import java.util.List;

@Getter
@AllArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = true)
@Relation(collectionRelation = "serviceCategories", itemRelation = "serviceCategories")
public class ServiceCategoryResponse extends RepresentationModel<ServiceCategoryResponse> {

  @Id
  @NotNull
  private Long id;

  @Title
  @NotNull
  private String title;

  @Description
  @NotNull
  private String description;

  @NotNull
  private List<@ImageUrl String> imageUrls;

  @NotNull
  private final Instant createdAt;

  @NotNull
  private final Instant lastModifiedAt;
}
