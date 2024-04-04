package com.stepaniuk.service.category;

import com.stepaniuk.zrobleno.payload.service.category.ServiceCategoryResponse;
import org.mapstruct.*;
import org.springframework.hateoas.Link;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ServiceCategoryMapper {
  @BeanMapping(qualifiedByName = "addLinks")
  ServiceCategoryResponse toResponse(ServiceCategory serviceCategory);

  @AfterMapping
  @Named("addLinks")
  default ServiceCategoryResponse addLinks(ServiceCategory serviceCategory,
      @MappingTarget ServiceCategoryResponse response) {
    response.add(Link.of("/services/category/" + serviceCategory.getId()).withSelfRel());
    return response;
  }
}
