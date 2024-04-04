package com.stepaniuk.order;


import com.stepaniuk.order.status.OrderStatus;
import com.stepaniuk.order.testspecific.JpaLevelTest;
import com.stepaniuk.zrobleno.types.order.OrderStatusName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@JpaLevelTest
@Sql(scripts = {"classpath:sql/order_statuses.sql", "classpath:sql/orders.sql"})
class OrderRepositoryTest {

  @Autowired
  private OrderRepository orderRepository;

  @Test
  void shouldSaveOrder() {
    Instant timeOfCreation = Instant.now().plus(Duration.ofHours(10));
    Instant timeOfModification = Instant.now().plus(Duration.ofHours(20));
    var orderStatus = new OrderStatus(1L, OrderStatusName.CREATED);
    var ownerId = UUID.randomUUID();

    Order orderToSave = new Order(
        1L, ownerId, orderStatus, 1L, "Comment", timeOfCreation, timeOfModification
    );

    Order savedOrder = orderRepository.save(orderToSave);

    assertNotNull(savedOrder);
    assertNotNull(savedOrder.getId());
    assertEquals(orderToSave.getOwnerId(), savedOrder.getOwnerId());
    assertEquals(orderToSave.getStatus(), savedOrder.getStatus());
    assertEquals(orderToSave.getServiceId(), savedOrder.getServiceId());
    assertEquals(orderToSave.getComment(), savedOrder.getComment());
    assertEquals(orderToSave.getCreatedAt(), savedOrder.getCreatedAt());
    assertEquals(orderToSave.getLastModifiedAt(), savedOrder.getLastModifiedAt());
  }

  @Test
  void shouldThrowExceptionWhenSavingOrderWithoutServiceIds(){
    Instant timeOfCreation = Instant.now().plus(Duration.ofHours(10));
    Instant timeOfModification = Instant.now().plus(Duration.ofHours(20));
    var orderStatus = new OrderStatus(1L, OrderStatusName.CREATED);
    var ownerId = UUID.randomUUID();

    Order orderToSave = new Order(
        null, ownerId, orderStatus, null, "Comment", timeOfCreation, timeOfModification
    );

    assertThrows(Exception.class, () -> orderRepository.save(orderToSave));
  }

  @Test
  void shouldReturnOrderWhenFindById(){
    Optional<Order> optionalOrder = orderRepository.findById(1L);

    assertTrue(optionalOrder.isPresent());

    Order foundOrder = optionalOrder.get();

    assertNotNull(foundOrder);
    assertEquals(1L, foundOrder.getId());
    assertNotNull(foundOrder.getOwnerId());
    assertNotNull(foundOrder.getStatus());
    assertEquals(1L, foundOrder.getServiceId());
    assertNotNull(foundOrder.getComment());
    assertNotNull(foundOrder.getCreatedAt());
    assertNotNull(foundOrder.getLastModifiedAt());
  }

  @Test
  void shouldUpdateOrderWhenChangingStatus(){
    var orderStatus = new OrderStatus(2L, OrderStatusName.IN_PROGRESS);
    Optional<Order> optionalOrder = orderRepository.findById(1L);
    assertTrue(optionalOrder.isPresent());

    Order foundOrder = optionalOrder.get();
    foundOrder.setStatus(orderStatus);
    foundOrder.setLastModifiedAt(Instant.now());

    Order updatedOrder = orderRepository.save(foundOrder);

    assertNotNull(updatedOrder);
    assertEquals(1L, updatedOrder.getId());
    assertNotNull(updatedOrder.getOwnerId());
    assertNotNull(updatedOrder.getStatus());
    assertEquals(1L, updatedOrder.getServiceId());
    assertNotNull(updatedOrder.getComment());
    assertNotNull(updatedOrder.getCreatedAt());
    assertNotNull(updatedOrder.getLastModifiedAt());
  }

  @Test
  void shouldDeleteOrderWhenDeletingByExistingOrder(){
    Order orderToFind = orderRepository.findById(1L).orElseThrow();

    orderRepository.delete(orderToFind);

    Optional<Order> optionalOrder = orderRepository.findById(1L);
    assertTrue(optionalOrder.isEmpty());
  }

  @Test
  void shouldDeleteOrderWhenDeletingByExistingServiceId(){
    orderRepository.deleteById(1L);

    assertTrue(orderRepository.findById(1L).isEmpty());
  }

  @Test
  void shouldReturnTrueWhenOrderExistsById(){
    assertTrue(orderRepository.existsById(1L));
  }

  @Test
  void shouldReturnNonEmptyListWhenFindAll(){
    List<Order> orders = orderRepository.findAll();

    assertNotNull(orders);
    assertFalse(orders.isEmpty());
  }
}
