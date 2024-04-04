package com.stepaniuk.service;



import com.stepaniuk.zrobleno.payload.service.ServiceResponse;
import org.mapstruct.*;
import org.springframework.hateoas.Link;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ServiceMapper {
  @BeanMapping(qualifiedByName = "addLinks")
  ServiceResponse toResponse(Service service);

  @AfterMapping
  @Named("addLinks")
  default ServiceResponse addLinks(Service service, @MappingTarget ServiceResponse response) {
    response.add(Link.of("/services/" + service.getId()).withSelfRel());
    return response;
  }
}
