package com.stepaniuk.service;


import com.stepaniuk.zrobleno.payload.service.ServiceResponse;
import com.stepaniuk.zrobleno.testspecific.MapperLevelUnitTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@MapperLevelUnitTest
@ContextConfiguration(classes = {ServiceMapperImpl.class})
class ServiceMapperTest {

  @Autowired
  private ServiceMapper serviceMapper;

  @Test
  void shouldMapServiceToServiceResponse() {
    // given
    Instant timeOfCreation = Instant.now().plus(Duration.ofHours(10));
    Instant timeOfModification = Instant.now().plus(Duration.ofHours(20));

    Service serviceToMap = new Service(
        1L, 1L, UUID.randomUUID(), "title", "description", List.of("https://image.com/1"), BigDecimal.valueOf(100),
        1, timeOfCreation, timeOfModification
    );

    // when
    ServiceResponse serviceResponse = serviceMapper.toResponse(
        serviceToMap);

    // then
    assertNotNull(serviceResponse);
    assertEquals(serviceToMap.getId(), serviceResponse.getId());
    assertEquals(serviceToMap.getTitle(), serviceResponse.getTitle());
    assertEquals(serviceToMap.getDescription(), serviceResponse.getDescription());
    assertEquals(serviceToMap.getImageUrls(), serviceResponse.getImageUrls());
    assertEquals(serviceToMap.getCreatedAt(), serviceResponse.getCreatedAt());
    assertEquals(serviceToMap.getLastModifiedAt(),
        serviceResponse.getLastModifiedAt());
    assertTrue(serviceResponse.hasLinks());
  }
}
