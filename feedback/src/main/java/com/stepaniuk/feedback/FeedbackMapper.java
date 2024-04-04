package com.stepaniuk.feedback;


import com.stepaniuk.zrobleno.payload.feedback.FeedbackResponse;
import org.mapstruct.*;
import org.springframework.hateoas.Link;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface FeedbackMapper {
  @BeanMapping(qualifiedByName = "addLinks")
  FeedbackResponse toResponse(Feedback feedback);

  @AfterMapping
  @Named("addLinks")
  default FeedbackResponse addLinks(Feedback feedback, @MappingTarget FeedbackResponse response) {
    response.add(Link.of("/feedbacks/" + feedback.getId()).withSelfRel());
    return response;
  }
}
