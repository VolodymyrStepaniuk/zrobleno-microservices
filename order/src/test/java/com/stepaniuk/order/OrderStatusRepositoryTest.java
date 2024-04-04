package com.stepaniuk.order;


import com.stepaniuk.order.status.OrderStatus;
import com.stepaniuk.order.status.OrderStatusRepository;
import com.stepaniuk.order.testspecific.JpaLevelTest;
import com.stepaniuk.zrobleno.types.order.OrderStatusName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import static org.junit.jupiter.api.Assertions.*;

@JpaLevelTest
@Sql(scripts = {"classpath:sql/order_statuses.sql"})
class OrderStatusRepositoryTest {

  @Autowired
  private OrderStatusRepository orderStatusRepository;

  @Test
  void shouldSaveOrderStatus() {
    // given
    var orderStatusToSave = new OrderStatus(null, OrderStatusName.CREATED);

    // when
    var savedOrderStatus = orderStatusRepository.save(orderStatusToSave);

    // then
    assertNotNull(savedOrderStatus);
    assertNotNull(savedOrderStatus.getId());
    assertEquals(orderStatusToSave.getName(), savedOrderStatus.getName());
  }

  @Test
  void shouldThrowExceptionWhenSavingOrderStatusWithNullName() {
    // given
    var orderStatusToSave = new OrderStatus(null, null);

    // when
    assertThrows(Exception.class, () -> orderStatusRepository.save(orderStatusToSave));
  }

  @Test
  void shouldReturnOrderStatusWhenFindById() {
    // when
    var orderStatus = orderStatusRepository.findById(1L);

    // then
    assertTrue(orderStatus.isPresent());
    assertEquals(1L, orderStatus.get().getId());
    assertEquals(OrderStatusName.CREATED, orderStatus.get().getName());
  }

  @Test
  void shouldReturnOrderStatusWhenFindByName() {
    // when
    var optionalOrderStatus = orderStatusRepository.findByName(OrderStatusName.CREATED);

    // then
    assertTrue(optionalOrderStatus.isPresent());
    var orderStatus = optionalOrderStatus.get();
    assertEquals(1L, orderStatus.getId());
    assertEquals(OrderStatusName.CREATED, orderStatus.getName());
  }

  @Test
  void shouldUpdateOrderStatus() {
    // given
    var orderStatusToSave = new OrderStatus(null, OrderStatusName.CREATED);
    var savedOrderStatus = orderStatusRepository.save(orderStatusToSave);
    var savedId = savedOrderStatus.getId();
    var savedName = savedOrderStatus.getName();

    var orderStatusToUpdate = new OrderStatus(savedId, OrderStatusName.IN_PROGRESS);

    // when
    var updatedOrderStatus = orderStatusRepository.save(orderStatusToUpdate);

    // then
    assertNotNull(updatedOrderStatus);
    assertEquals(savedId, updatedOrderStatus.getId());
    assertNotEquals(savedName, updatedOrderStatus.getName());
    assertEquals(orderStatusToUpdate.getName(), updatedOrderStatus.getName());
  }

  @Test
  void shouldDeleteOrderStatus() {
    // when
    orderStatusRepository.deleteById(1L);

    // then
    assertFalse(orderStatusRepository.findById(1L).isPresent());
  }

  @Test
  void shouldReturnTrueWhenOrderStatusExists() {
    // when
    var exists = orderStatusRepository.existsById(1L);

    // then
    assertTrue(exists);
  }
}
