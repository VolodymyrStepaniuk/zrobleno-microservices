package com.stepaniuk.service.category;


import com.stepaniuk.zrobleno.payload.service.category.ServiceCategoryResponse;
import com.stepaniuk.zrobleno.testspecific.MapperLevelUnitTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@MapperLevelUnitTest
@ContextConfiguration(classes = {ServiceCategoryMapperImpl.class})
class ServiceCategoryMapperTest {

  @Autowired
  private ServiceCategoryMapper serviceCategoryMapper;

  @Test
  void shouldMapServiceCategoryToServiceCategoryResponse() {
    // given
    Instant timeOfCreation = Instant.now().plus(Duration.ofHours(10));
    Instant timeOfModification = Instant.now().plus(Duration.ofHours(20));

    ServiceCategory serviceCategoryToMap = new ServiceCategory(
        1L, "title", "description", List.of("https://image.com/1"), timeOfCreation, timeOfModification
    );

    // when
    ServiceCategoryResponse serviceCategoryResponse = serviceCategoryMapper.toResponse(
        serviceCategoryToMap);

    // then
    assertNotNull(serviceCategoryResponse);
    assertEquals(serviceCategoryToMap.getId(), serviceCategoryResponse.getId());
    assertEquals(serviceCategoryToMap.getTitle(), serviceCategoryResponse.getTitle());
    assertEquals(serviceCategoryToMap.getDescription(), serviceCategoryResponse.getDescription());
    assertEquals(serviceCategoryToMap.getImageUrls(), serviceCategoryResponse.getImageUrls());
    assertEquals(serviceCategoryToMap.getCreatedAt(), serviceCategoryResponse.getCreatedAt());
    assertEquals(serviceCategoryToMap.getLastModifiedAt(),
        serviceCategoryResponse.getLastModifiedAt());
    assertTrue(serviceCategoryResponse.hasLinks());
  }
}
