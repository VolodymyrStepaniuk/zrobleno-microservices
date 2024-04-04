package com.stepaniuk.order;


import com.stepaniuk.order.client.FeedbackClient;
import com.stepaniuk.order.client.ServiceClient;
import com.stepaniuk.order.status.OrderStatus;
import com.stepaniuk.order.status.OrderStatusRepository;
import com.stepaniuk.zrobleno.payload.feedback.FeedbackResponse;
import com.stepaniuk.zrobleno.payload.order.OrderCreateRequest;
import com.stepaniuk.zrobleno.payload.order.OrderResponse;
import com.stepaniuk.zrobleno.payload.order.OrderUpdateRequest;
import com.stepaniuk.zrobleno.payload.service.ServiceResponse;
import com.stepaniuk.zrobleno.testspecific.ServiceLevelUnitTest;
import com.stepaniuk.zrobleno.types.exception.order.IllegalOrderStatusException;
import com.stepaniuk.zrobleno.types.exception.order.NoSuchOrderByIdException;
import com.stepaniuk.zrobleno.types.order.OrderStatusName;
import org.junit.jupiter.api.Test;
import org.mockito.stubbing.Answer1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ContextConfiguration;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.answer;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ServiceLevelUnitTest
@ContextConfiguration(classes = {OrderService.class, OrderMapperImpl.class})
class OrderServiceTest {

  @Autowired
  private OrderService orderService;

  @MockBean
  private OrderRepository orderRepository;

  @MockBean
  private OrderStatusRepository orderStatusRepository;


  @MockBean
  private FeedbackClient feedbackClient;

  @MockBean
  private ServiceClient serviceClient;

  @Test
  void shouldReturnOrderResponseWhenCreateOrder() {
    // given
    var ownerId = UUID.randomUUID();
    var serviceId = 1L;
    var orderStatus = new OrderStatus(1L, OrderStatusName.CREATED);
    var comment = "Comment";

    var orderCreateRequest = new OrderCreateRequest(ownerId, serviceId, comment);

    when(orderStatusRepository.findByName(OrderStatusName.CREATED)).thenReturn(
        Optional.of(orderStatus));
    when(orderRepository.save(any())).thenAnswer(answer(getFakeSave(1L)));
    when(serviceClient.getServiceById(serviceId)).thenReturn(new ServiceResponse(1L, 1L,ownerId,"Service", "desc",List.of(""), BigDecimal.valueOf(10), 2, Instant.now(), Instant.now()));


    // when
    var response = orderService.createOrder(orderCreateRequest);

    // then
    assertNotNull(response);
    assertEquals(1L, response.getId());
    assertEquals(ownerId, response.getOwnerId());
    assertEquals(orderStatus.getName(), response.getStatus());
    assertEquals(serviceId, response.getServiceId());
    assertEquals(comment, response.getComment());
    assertTrue(response.hasLinks());
  }

  @Test
  void shouldReturnOrderResponseWhenGettingOrderById(){
    // given
    var orderId = 1L;
    var ownerId = UUID.randomUUID();
    var comment = "Comment";
    var order = getNewOrderWithAllFields(orderId, ownerId, comment);
    var feedback = new FeedbackResponse(
        1L, orderId, ownerId, 5, "comment", Instant.now(), Instant.now().plus(Duration.ofHours(15))
    );

    when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
    when(feedbackClient.getAllFeedbacks(any(Pageable.class),any(),any())).thenReturn(
        new PageImpl<>(List.of(feedback)));
    // when
    var response = orderService.getOrder(orderId);
    // then
    assertNotNull(response);
    assertEquals(1L, response.getId());
    assertEquals(order.getOwnerId(), response.getOwnerId());
    assertEquals(order.getStatus().getName(), response.getStatus());
    assertEquals(order.getServiceId(), response.getServiceId());
    assertEquals(feedback, response.getFeedback());
    assertEquals(order.getComment(), response.getComment());
    assertEquals(order.getCreatedAt(), response.getCreatedAt());
    assertEquals(order.getLastModifiedAt(), response.getLastModifiedAt());
    assertTrue(response.hasLinks());
  }

  @Test
  void shouldThrowNoSuchOrderByIdExceptionWhenGetByNonExistingId(){
    // given
    when(orderRepository.findById(1L)).thenReturn(Optional.empty());
    // when && then
    assertThrows(NoSuchOrderByIdException.class, () -> orderService.getOrder(1L));
  }

  @Test
  void shouldUpdateAndReturnOrderResponseWhenChangingOrderStatus(){
    // given
    var orderId = 2L;
    var ownerId = UUID.randomUUID();
    var orderStatusName = OrderStatusName.CONFIRMED;
    var comment = "Comment";
    var order = getNewOrderWithAllFields(orderId, ownerId,comment);
    var feedback = new FeedbackResponse(
        2L, orderId, ownerId, 5, "comment", Instant.now(), Instant.now().plus(Duration.ofHours(15))
    );
    var orderUpdateRequest = new OrderUpdateRequest(orderStatusName, null);

    //when
    when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
    when(orderStatusRepository.findByName(orderStatusName)).thenReturn(
        Optional.of(new OrderStatus(2L, orderStatusName)));
    when(feedbackClient.getAllFeedbacks(any(Pageable.class),any(),eq(orderId))).thenReturn(
        new PageImpl<>(List.of(feedback)));
    when(orderRepository.save(any())).thenAnswer(answer(getFakeSave(orderId)));

    OrderResponse orderResponse = orderService.updateOrder(orderId, orderUpdateRequest);

    //then
    assertNotNull(orderResponse);
    assertEquals(order.getId(), orderResponse.getId());
    assertEquals(order.getOwnerId(), orderResponse.getOwnerId());
    assertEquals(orderStatusName, orderResponse.getStatus());
    assertEquals(order.getServiceId(), orderResponse.getServiceId());
    assertEquals(feedback, orderResponse.getFeedback());
    assertEquals(order.getComment(), orderResponse.getComment());
    assertEquals(order.getCreatedAt(), orderResponse.getCreatedAt());
    assertEquals(order.getLastModifiedAt(), orderResponse.getLastModifiedAt());
    assertTrue(orderResponse.hasLinks());
  }

  @Test
  void shouldChangeAndReturnOrderResponseWhenChangingOrderStatusFromCreatedToConfirmed(){
    // given
    var orderId = 2L;
    var ownerId = UUID.randomUUID();
    var orderStatusName = OrderStatusName.CONFIRMED;
    var comment = "Comment";
    var order = getNewOrderWithAllFields(orderId, ownerId, comment);
    var feedback = new FeedbackResponse(
        2L, orderId, ownerId, 5, "comment", Instant.now(), Instant.now().plus(Duration.ofHours(15))
    );

    when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
    when(orderStatusRepository.findByName(orderStatusName)).thenReturn(
        Optional.of(new OrderStatus(2L, orderStatusName)));
    when(feedbackClient.getAllFeedbacks(any(Pageable.class),any(),eq(orderId))).thenReturn(
        new PageImpl<>(List.of(feedback)));
    when(orderRepository.save(any())).thenAnswer(answer(getFakeSave(orderId)));

    OrderResponse orderResponse = orderService.changeStatus(orderId, orderStatusName);

    //then
    assertNotNull(orderResponse);
    assertEquals(order.getId(), orderResponse.getId());
    assertEquals(order.getOwnerId(), orderResponse.getOwnerId());
    assertEquals(orderStatusName, orderResponse.getStatus());
    assertEquals(order.getServiceId(), orderResponse.getServiceId());
    assertEquals(feedback, orderResponse.getFeedback());
    assertEquals(order.getComment(), orderResponse.getComment());
    assertEquals(order.getCreatedAt(), orderResponse.getCreatedAt());
    assertEquals(order.getLastModifiedAt(), orderResponse.getLastModifiedAt());
    assertTrue(orderResponse.hasLinks());
  }

  @Test
  void shouldThrowNoSuchOrderByIdExceptionWhenChangingStatusOfNonExistingOrder() {
    var orderStatusName = OrderStatusName.IN_PROGRESS;
    when(orderRepository.findById(1L)).thenReturn(Optional.empty());

    // when && then
    assertThrows(NoSuchOrderByIdException.class,
        () -> orderService.changeStatus(1L, orderStatusName));
  }

  @Test
  void shouldThrowIllegalOrderStatusExceptionWhenChangingStatusFromCreatedToInProgress() {
    // given
    var orderId = 2L;
    var ownerId = UUID.randomUUID();
    var orderStatusName = OrderStatusName.IN_PROGRESS;
    var comment = "Comment";
    var order = getNewOrderWithAllFields(orderId, ownerId, comment);
    var feedback = new FeedbackResponse(
        2L, orderId, ownerId, 5, "comment", Instant.now(), Instant.now().plus(Duration.ofHours(15))
    );

    when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
    when(orderStatusRepository.findByName(orderStatusName)).thenReturn(
        Optional.of(new OrderStatus(2L, orderStatusName)));
    when(feedbackClient.getAllFeedbacks(any(Pageable.class),any(),eq(orderId))).thenReturn(
        new PageImpl<>(List.of(feedback)));

    // when && then
    assertThrows(IllegalOrderStatusException.class,
        () -> orderService.changeStatus(orderId, orderStatusName));
  }

  @Test
  void shouldReturnPageOfOrderResponsesWhenGettingAllOrders(){
    //given
    var ownerId = UUID.randomUUID();
    var comment = "Comment";
    var pageable = Pageable.ofSize(1);

    var order = getNewOrderWithAllFields(1L, ownerId,comment);
    var orders = List.of(order);
    var feedback = new FeedbackResponse(
        1L, 1L, ownerId, 5, "comment", Instant.now(), Instant.now().plus(Duration.ofHours(15))
    );
    Specification<Order> specification = Specification.where(null);

    when(feedbackClient.getAllFeedbacks(any(Pageable.class),any(),any())).thenReturn(
        new PageImpl<>(List.of(feedback)));
    when(orderRepository.findAll(specification, pageable)).thenReturn(
        new PageImpl<>(orders, pageable, orders.size()));

    var pageOfResponses = orderService.getAllOrders(pageable, null);
    var response = pageOfResponses.getContent().get(0);

    assertNotNull(pageOfResponses);
    assertEquals(1, pageOfResponses.getTotalElements());
    assertEquals(1, pageOfResponses.getTotalPages());
    assertEquals(1, pageOfResponses.getNumberOfElements());

    assertNotNull(response);
    assertEquals(order.getId(), response.getId());
    assertEquals(order.getOwnerId(), response.getOwnerId());
    assertEquals(order.getStatus().getName(), response.getStatus());
    assertEquals(order.getServiceId(), response.getServiceId());
    assertEquals(feedback, response.getFeedback());
    assertEquals(order.getComment(), response.getComment());
    assertEquals(order.getCreatedAt(), response.getCreatedAt());
    assertEquals(order.getLastModifiedAt(), response.getLastModifiedAt());
    assertTrue(response.hasLinks());
  }

  @Test
  void shouldReturnPageOfOrderResponsesWhenGettingAllOrdersAndWhenOwnerIdIsNotNull(){
//given
    var ownerId = UUID.randomUUID();
    var comment = "Comment";
    var pageable = Pageable.ofSize(1);

    var order = getNewOrderWithAllFields(1L, ownerId, comment);
    var orders = List.of(order);
    var feedback = new FeedbackResponse(
        1L, 1L, ownerId, 5, "comment", Instant.now(), Instant.now().plus(Duration.ofHours(15))
    );

    when(feedbackClient.getAllFeedbacks(any(Pageable.class),any(),any())).thenReturn(
        new PageImpl<>(List.of(feedback)));
    when(orderRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(
        new PageImpl<>(orders, pageable, orders.size()));

    var pageOfResponses = orderService.getAllOrders(pageable, null);
    var response = pageOfResponses.getContent().get(0);

    assertNotNull(pageOfResponses);
    assertEquals(1, pageOfResponses.getTotalElements());
    assertEquals(1, pageOfResponses.getTotalPages());
    assertEquals(1, pageOfResponses.getNumberOfElements());

    assertNotNull(response);
    assertEquals(order.getId(), response.getId());
    assertEquals(order.getOwnerId(), response.getOwnerId());
    assertEquals(order.getStatus().getName(), response.getStatus());
    assertEquals(order.getServiceId(), response.getServiceId());
    assertEquals(feedback, response.getFeedback());
    assertEquals(order.getComment(), response.getComment());
    assertEquals(order.getCreatedAt(), response.getCreatedAt());
    assertEquals(order.getLastModifiedAt(), response.getLastModifiedAt());
    assertTrue(response.hasLinks());
  }

  private static Order getNewOrderWithAllFields(Long id, UUID ownerId, String comment) {
    Instant timeOfCreation = Instant.now().plus(Duration.ofHours(10));
    Instant timeOfModification = Instant.now().plus(Duration.ofHours(20));

    var orderStatus = new OrderStatus(1L, OrderStatusName.CREATED);

    return new Order(id, ownerId, orderStatus, 1L, comment, timeOfCreation, timeOfModification);
  }

  private Answer1<Order, Order> getFakeSave(long id) {
    return order -> {
      order.setId(id);
      return order;
    };
  }
}
