package com.stepaniuk.order;


import com.stepaniuk.order.status.OrderStatus;
import com.stepaniuk.zrobleno.payload.feedback.FeedbackResponse;
import com.stepaniuk.zrobleno.testspecific.MapperLevelUnitTest;
import com.stepaniuk.zrobleno.types.order.OrderStatusName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@MapperLevelUnitTest
@ContextConfiguration(classes = {OrderMapperImpl.class})
class OrderMapperTest {

  @Autowired
  private OrderMapper orderMapper;

  @Test
  void shouldMapOrderToOrderResponse() {
    // given
    Instant timeOfCreation = Instant.now().plus(Duration.ofHours(10));
    Instant timeOfModification = Instant.now().plus(Duration.ofHours(20));
    var orderStatus = new OrderStatus(1L, OrderStatusName.CREATED);
    var ownerId = UUID.randomUUID();

    var feedback = new FeedbackResponse(1L, 1L, ownerId, 5, "text", timeOfCreation,
        timeOfModification);

    Order orderToMap = new Order(
        1L, ownerId, orderStatus, 1L, "New comment", timeOfCreation, timeOfModification
    );

    // when
    var orderResponse = orderMapper.toResponse(orderToMap, feedback);

    // then
    assertNotNull(orderResponse);
    assertEquals(orderToMap.getId(), orderResponse.getId());
    assertEquals(orderToMap.getOwnerId(), orderResponse.getOwnerId());
    assertEquals(orderToMap.getStatus().getName(), orderResponse.getStatus());
    assertEquals(orderToMap.getServiceId(), orderResponse.getServiceId());
    assertEquals(orderToMap.getComment(), orderResponse.getComment());
    assertEquals(orderToMap.getCreatedAt(), orderResponse.getCreatedAt());
    assertEquals(orderToMap.getLastModifiedAt(), orderResponse.getLastModifiedAt());
    assertNotNull(orderResponse.getFeedback());
    assertTrue(orderResponse.hasLinks());
  }
}
