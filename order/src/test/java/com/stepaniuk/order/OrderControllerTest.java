package com.stepaniuk.order;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.stepaniuk.order.testspecific.ControllerLevelUnitTest;
import com.stepaniuk.zrobleno.payload.feedback.FeedbackResponse;
import com.stepaniuk.zrobleno.payload.order.OrderCreateRequest;
import com.stepaniuk.zrobleno.payload.order.OrderResponse;
import com.stepaniuk.zrobleno.payload.order.OrderUpdateRequest;
import com.stepaniuk.zrobleno.types.exception.order.IllegalOrderStatusException;
import com.stepaniuk.zrobleno.types.exception.order.NoSuchOrderByIdException;
import com.stepaniuk.zrobleno.types.order.OrderStatusName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.hateoas.Link;
import org.springframework.lang.Nullable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static com.stepaniuk.zrobleno.testspecific.hamcrest.TemporalStringMatchers.instantComparesEqualTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ControllerLevelUnitTest(controllers = OrderController.class)
@WithMockUser(username = "user", roles = "USER")
class OrderControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private OrderService orderService;

  @Autowired
  private ObjectMapper objectMapper;

  private static OrderResponse getNewOrderResponseWithAllFields(Long orderId, UUID ownerId,
                                                                Long serviceId, OrderStatusName orderStatusName, FeedbackResponse feedback,
                                                                @Nullable String comment) {
    return new OrderResponse(orderId, ownerId, orderStatusName, serviceId, comment, feedback,
        Instant.now(), Instant.now());
  }

  @Test
  void shouldReturnOrderResponseWhenCreatingOrder() throws Exception {
    var ownerId = UUID.randomUUID();
    var orderId = 1L;
    var serviceId = 2L;
    var comment = "Comment";
    var feedback = new FeedbackResponse(1L, orderId, ownerId, 5, "text", Instant.now(),
        Instant.now());

    var request = new OrderCreateRequest(ownerId, serviceId, comment);
    var response = getNewOrderResponseWithAllFields(orderId, ownerId, serviceId,
        OrderStatusName.CREATED, feedback, comment);
    response.add(Link.of("http://localhost/orders/" + orderId, "self"));
    response.add(Link.of("http://localhost/orders/" + orderId + "/cancel", "cancel"));
    response.add(Link.of("http://localhost/orders/" + orderId + "/confirm", "confirm"));

    when(orderService.createOrder(eq(request))).thenReturn(response);

    mockMvc.perform(post("/orders")
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(request))
        )
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id", is(orderId), Long.class))
        .andExpect(jsonPath("$.ownerId", is(ownerId.toString())))
        .andExpect(jsonPath("$.status", is(OrderStatusName.CREATED.toString())))
        .andExpect(jsonPath("$.serviceId", is(serviceId), Long.class))
        .andExpect(jsonPath("$.comment", is(comment)))
        .andExpect(jsonPath("$.feedback.id", is(feedback.getId()), Long.class))
        .andExpect(jsonPath("$.feedback.orderId", is(orderId), Long.class))
        .andExpect(jsonPath("$.feedback.ownerId", is(ownerId.toString())))
        .andExpect(jsonPath("$.feedback.rating", is(feedback.getRating()), Integer.class))
        .andExpect(jsonPath("$.feedback.comment", is(feedback.getComment())))
        .andExpect(
            jsonPath("$.feedback.createdAt", instantComparesEqualTo(feedback.getCreatedAt())))
        .andExpect(jsonPath("$.feedback.lastModifiedAt",
            instantComparesEqualTo(feedback.getLastModifiedAt())))
        .andExpect(jsonPath("$.createdAt", instantComparesEqualTo(response.getCreatedAt())))
        .andExpect(
            jsonPath("$.lastModifiedAt", instantComparesEqualTo(response.getLastModifiedAt())))
        .andExpect(jsonPath("$._links.self.href", is("http://localhost/orders/" + orderId)))
        .andExpect(
            jsonPath("$._links.cancel.href", is("http://localhost/orders/" + orderId + "/cancel")))
        .andExpect(jsonPath("$._links.confirm.href",
            is("http://localhost/orders/" + orderId + "/confirm")));
  }

  @Test
  void shouldReturnOrderResponseWhenGettingOrderById() throws Exception {
    var ownerId = UUID.randomUUID();
    var orderId = 1L;
    var serviceId = 2L;
    var comment = "Comment";

    var response = getNewOrderResponseWithAllFields(orderId, ownerId, serviceId,
        OrderStatusName.CREATED, null, comment);
    response.add(Link.of("http://localhost/orders/" + orderId, "self"));
    response.add(Link.of("http://localhost/orders/" + orderId + "/cancel", "cancel"));
    response.add(Link.of("http://localhost/orders/" + orderId + "/confirm", "confirm"));

    when(orderService.getOrder(eq(orderId))).thenReturn(response);

    mockMvc.perform(get("/orders/" + orderId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(orderId), Long.class))
        .andExpect(jsonPath("$.ownerId", is(ownerId.toString())))
        .andExpect(jsonPath("$.status", is(OrderStatusName.CREATED.toString())))
        .andExpect(jsonPath("$.serviceId", is(serviceId), Long.class))
        .andExpect(jsonPath("$.comment", is(comment)))
        .andExpect(jsonPath("$.createdAt", instantComparesEqualTo(response.getCreatedAt())))
        .andExpect(
            jsonPath("$.lastModifiedAt", instantComparesEqualTo(response.getLastModifiedAt())))
        .andExpect(jsonPath("$._links.self.href", is("http://localhost/orders/" + orderId)))
        .andExpect(
            jsonPath("$._links.cancel.href", is("http://localhost/orders/" + orderId + "/cancel")))
        .andExpect(jsonPath("$._links.confirm.href",
            is("http://localhost/orders/" + orderId + "/confirm")));
  }

  @Test
  void shouldReturnErrorResponseWhenGetOrderByIdAndNoSuchOrderByIdException() throws Exception {
    var orderId = 1L;

    when(orderService.getOrder(eq(orderId))).thenThrow(new NoSuchOrderByIdException(orderId));

    // when && then
    // @formatter:off
    mockMvc.perform(get("/orders/"+orderId)
            .contentType("application/json")
        )
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.status", is(404)))
        .andExpect(jsonPath("$.title", is("No such order")))
        .andExpect(jsonPath("$.detail", is("No order with id 1 found")))
        .andExpect(jsonPath("$.instance", is("/orders/1")));
    // @formatter:on
  }

  @Test
  void shouldUpdateAndReturnOrderResponseWhenUpdatingOrderStatus() throws Exception {
    var orderId = 1L;
    var ownerId = UUID.randomUUID();
    var serviceId = 2L;
    var feedback = new FeedbackResponse(1L, orderId, ownerId, 5, "text", Instant.now(),
        Instant.now());
    var request = new OrderUpdateRequest(OrderStatusName.CANCELED, null);

    var response = getNewOrderResponseWithAllFields(orderId, ownerId, serviceId,
        OrderStatusName.CANCELED, feedback, null);
    response.add(Link.of("http://localhost/orders/" + orderId, "self"));
    response.add(Link.of("http://localhost/orders/" + orderId + "/cancel", "cancel"));
    response.add(Link.of("http://localhost/orders/" + orderId + "/in-progress", "in-progress"));

    when(orderService.updateOrder(orderId, request)).thenReturn(response);

    // when && then
    mockMvc.perform(patch("/orders/" + orderId)
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(request))
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(orderId), Long.class))
        .andExpect(jsonPath("$.ownerId", is(ownerId.toString())))
        .andExpect(jsonPath("$.status", is(request.getStatus().toString())))
        .andExpect(jsonPath("$.serviceId", is(serviceId), Long.class))
        .andExpect(jsonPath("$.feedback.id", is(feedback.getId()), Long.class))
        .andExpect(jsonPath("$.feedback.orderId", is(orderId), Long.class))
        .andExpect(jsonPath("$.feedback.ownerId", is(ownerId.toString())))
        .andExpect(jsonPath("$.feedback.rating", is(feedback.getRating()), Integer.class))
        .andExpect(jsonPath("$.feedback.comment", is(feedback.getComment())))
        .andExpect(
            jsonPath("$.feedback.createdAt", instantComparesEqualTo(feedback.getCreatedAt())))
        .andExpect(jsonPath("$.feedback.lastModifiedAt",
            instantComparesEqualTo(feedback.getLastModifiedAt())))
        .andExpect(jsonPath("$.createdAt", instantComparesEqualTo(response.getCreatedAt())))
        .andExpect(
            jsonPath("$.lastModifiedAt", instantComparesEqualTo(response.getLastModifiedAt())))
        .andExpect(jsonPath("$._links.self.href", is("http://localhost/orders/" + orderId)))
        .andExpect(
            jsonPath("$._links.cancel.href", is("http://localhost/orders/" + orderId + "/cancel")))
        .andExpect(jsonPath("$._links.in-progress.href",
            is("http://localhost/orders/" + orderId + "/in-progress")));
  }

  @Test
  void shouldReturnErrorResponseWhenChangingCommentOfNonExistingOrder()
      throws Exception {
    var orderId = 1L;
    var request = new OrderUpdateRequest(null, "New comment");

    when(orderService.updateOrder(orderId, request)).thenThrow(
        new NoSuchOrderByIdException(orderId));

    // when && then
    // @formatter:off
    mockMvc.perform(patch("/orders/"+orderId)
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(request))
        )
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.status", is(404)))
        .andExpect(jsonPath("$.title", is("No such order")))
        .andExpect(jsonPath("$.detail", is("No order with id 1 found")))
        .andExpect(jsonPath("$.instance", is("/orders/1")));
    // @formatter:on
  }

  @Test
  void shouldReturnOrderResponseWhenCancelOrderById() throws Exception {
    var orderId = 1L;
    var ownerId = UUID.randomUUID();
    var serviceId = 2L;
    var feedback = new FeedbackResponse(1L, orderId, ownerId, 5, "text", Instant.now(),
        Instant.now());
    var response = getNewOrderResponseWithAllFields(orderId, ownerId, serviceId,
        OrderStatusName.CANCELED, feedback, null);
    response.add(Link.of("http://localhost/orders/" + orderId, "self"));

    when(orderService.changeStatus(orderId, OrderStatusName.CANCELED)).thenReturn(response);

    mockMvc.perform(post("/orders/" + orderId + "/cancel")
            .contentType("application/json")
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(orderId), Long.class))
        .andExpect(jsonPath("$.ownerId", is(ownerId.toString())))
        .andExpect(jsonPath("$.status", is(response.getStatus().toString())))
        .andExpect(jsonPath("$.serviceId", is(serviceId), Long.class))
        .andExpect(jsonPath("$.feedback.id", is(feedback.getId()), Long.class))
        .andExpect(jsonPath("$.feedback.orderId", is(orderId), Long.class))
        .andExpect(jsonPath("$.feedback.ownerId", is(ownerId.toString())))
        .andExpect(jsonPath("$.feedback.rating", is(feedback.getRating()), Integer.class))
        .andExpect(jsonPath("$.feedback.comment", is(feedback.getComment())))
        .andExpect(
            jsonPath("$.feedback.createdAt", instantComparesEqualTo(feedback.getCreatedAt())))
        .andExpect(jsonPath("$.feedback.lastModifiedAt",
            instantComparesEqualTo(feedback.getLastModifiedAt())))
        .andExpect(jsonPath("$.createdAt", instantComparesEqualTo(response.getCreatedAt())))
        .andExpect(
            jsonPath("$.lastModifiedAt", instantComparesEqualTo(response.getLastModifiedAt())))
        .andExpect(jsonPath("$._links.self.href", is("http://localhost/orders/" + orderId)));
  }

  @Test
  void shouldReturnOrderResponseWhenConfirmOrderById() throws Exception {
    var orderId = 1L;
    var ownerId = UUID.randomUUID();
    var serviceId = 2L;
    var feedback = new FeedbackResponse(1L, orderId, ownerId, 5, "text", Instant.now(),
        Instant.now());
    var response = getNewOrderResponseWithAllFields(orderId, ownerId, serviceId,
        OrderStatusName.CANCELED, feedback, null);
    response.add(Link.of("http://localhost/orders/" + orderId, "self"));
    response.add(Link.of("http://localhost/orders/" + orderId + "/cancel", "cancel"));
    response.add(Link.of("http://localhost/orders/" + orderId + "/in-progress", "in-progress"));

    when(orderService.changeStatus(orderId, OrderStatusName.CONFIRMED)).thenReturn(response);

    mockMvc.perform(post("/orders/" + orderId + "/confirm")
            .contentType("application/json")
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(orderId), Long.class))
        .andExpect(jsonPath("$.ownerId", is(ownerId.toString())))
        .andExpect(jsonPath("$.status", is(response.getStatus().toString())))
        .andExpect(jsonPath("$.serviceId", is(serviceId), Long.class))
        .andExpect(jsonPath("$.feedback.id", is(feedback.getId()), Long.class))
        .andExpect(jsonPath("$.feedback.orderId", is(orderId), Long.class))
        .andExpect(jsonPath("$.feedback.ownerId", is(ownerId.toString())))
        .andExpect(jsonPath("$.feedback.rating", is(feedback.getRating()), Integer.class))
        .andExpect(jsonPath("$.feedback.comment", is(feedback.getComment())))
        .andExpect(
            jsonPath("$.feedback.createdAt", instantComparesEqualTo(feedback.getCreatedAt())))
        .andExpect(jsonPath("$.feedback.lastModifiedAt",
            instantComparesEqualTo(feedback.getLastModifiedAt())))
        .andExpect(jsonPath("$.createdAt", instantComparesEqualTo(response.getCreatedAt())))
        .andExpect(
            jsonPath("$.lastModifiedAt", instantComparesEqualTo(response.getLastModifiedAt())))
        .andExpect(jsonPath("$._links.self.href", is("http://localhost/orders/" + orderId)));
  }

  @Test
  void shouldReturnOrderResponseWhenSettingOrderInProgressById() throws Exception {
    var orderId = 1L;
    var ownerId = UUID.randomUUID();
    var serviceId = 2L;
    var feedback = new FeedbackResponse(1L, orderId, ownerId, 5, "text", Instant.now(),
        Instant.now());
    var response = getNewOrderResponseWithAllFields(orderId, ownerId, serviceId,
        OrderStatusName.IN_PROGRESS, feedback, null);
    response.add(Link.of("http://localhost/orders/" + orderId, "self"));
    response.add(Link.of("http://localhost/orders/" + orderId + "/complete", "complete"));

    when(orderService.changeStatus(orderId, OrderStatusName.IN_PROGRESS)).thenReturn(response);

    mockMvc.perform(post("/orders/" + orderId + "/in-progress")
            .contentType("application/json")
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(orderId), Long.class))
        .andExpect(jsonPath("$.ownerId", is(ownerId.toString())))
        .andExpect(jsonPath("$.status", is(response.getStatus().toString())))
        .andExpect(jsonPath("$.serviceId", is(serviceId), Long.class))
        .andExpect(jsonPath("$.feedback.id", is(feedback.getId()), Long.class))
        .andExpect(jsonPath("$.feedback.orderId", is(orderId), Long.class))
        .andExpect(jsonPath("$.feedback.ownerId", is(ownerId.toString())))
        .andExpect(jsonPath("$.feedback.rating", is(feedback.getRating()), Integer.class))
        .andExpect(jsonPath("$.feedback.comment", is(feedback.getComment())))
        .andExpect(
            jsonPath("$.feedback.createdAt", instantComparesEqualTo(feedback.getCreatedAt())))
        .andExpect(jsonPath("$.feedback.lastModifiedAt",
            instantComparesEqualTo(feedback.getLastModifiedAt())))
        .andExpect(jsonPath("$.createdAt", instantComparesEqualTo(response.getCreatedAt())))
        .andExpect(
            jsonPath("$.lastModifiedAt", instantComparesEqualTo(response.getLastModifiedAt())))
        .andExpect(jsonPath("$._links.self.href", is("http://localhost/orders/" + orderId)));
  }

  @Test
  void shouldReturnOrderResponseWhenCompleteOrderById() throws Exception {
    var orderId = 1L;
    var ownerId = UUID.randomUUID();
    var serviceId = 2L;
    var feedback = new FeedbackResponse(1L, orderId, ownerId, 5, "text", Instant.now(),
        Instant.now());
    var response = getNewOrderResponseWithAllFields(orderId, ownerId, serviceId,
        OrderStatusName.COMPLETED, feedback, null);
    response.add(Link.of("http://localhost/orders/" + orderId, "self"));

    when(orderService.changeStatus(orderId, OrderStatusName.COMPLETED)).thenReturn(response);

    mockMvc.perform(post("/orders/" + orderId + "/complete")
            .contentType("application/json")
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(orderId), Long.class))
        .andExpect(jsonPath("$.ownerId", is(ownerId.toString())))
        .andExpect(jsonPath("$.status", is(response.getStatus().toString())))
        .andExpect(jsonPath("$.serviceId", is(serviceId), Long.class))
        .andExpect(jsonPath("$.feedback.id", is(feedback.getId()), Long.class))
        .andExpect(jsonPath("$.feedback.orderId", is(orderId), Long.class))
        .andExpect(jsonPath("$.feedback.ownerId", is(ownerId.toString())))
        .andExpect(jsonPath("$.feedback.rating", is(feedback.getRating()), Integer.class))
        .andExpect(jsonPath("$.feedback.comment", is(feedback.getComment())))
        .andExpect(
            jsonPath("$.feedback.createdAt", instantComparesEqualTo(feedback.getCreatedAt())))
        .andExpect(jsonPath("$.feedback.lastModifiedAt",
            instantComparesEqualTo(feedback.getLastModifiedAt())))
        .andExpect(jsonPath("$.createdAt", instantComparesEqualTo(response.getCreatedAt())))
        .andExpect(
            jsonPath("$.lastModifiedAt", instantComparesEqualTo(response.getLastModifiedAt())))
        .andExpect(jsonPath("$._links.self.href", is("http://localhost/orders/" + orderId)));
  }

  @Test
  void shouldReturnErrorResponseWhenConfirmingCanceledOrder() throws Exception {
    var orderId = 1L;
    var ownerId = UUID.randomUUID();
    var serviceId = 2L;
    var feedback = new FeedbackResponse(1L, orderId, ownerId, 5, "text", Instant.now(),
        Instant.now());
    var response = getNewOrderResponseWithAllFields(orderId, ownerId, serviceId,
        OrderStatusName.CANCELED, feedback, null);
    response.add(Link.of("http://localhost/orders/" + orderId, "self"));

    when(orderService.changeStatus(orderId, OrderStatusName.CONFIRMED)).thenThrow(
        new IllegalOrderStatusException(OrderStatusName.CONFIRMED, response.getStatus(), orderId));

    mockMvc.perform(post("/orders/" + orderId + "/confirm")
            .contentType("application/json")
        )
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status", is(400)))
        .andExpect(jsonPath("$.title", is("Illegal order status")))
        .andExpect(jsonPath("$.detail",
            is("Illegal order status! From CANCELED to CONFIRMED in order with id: 1")))
        .andExpect(jsonPath("$.instance", is("/orders/1")));
  }

  @Test
  void shouldReturnPageWhenGettingAllOrders() throws Exception {
    var orderId = 2L;
    var ownerId = UUID.randomUUID();
    var serviceId = 1L;
    var comment = "Comment";

    var response = getNewOrderResponseWithAllFields(orderId, ownerId, serviceId,
        OrderStatusName.CANCELED, null, comment);
    response.add(Link.of("http://localhost/orders/1", "self"));

    var pageable = PageRequest.of(0, 2);

    when(orderService.getAllOrders(eq(pageable), eq(null))).thenReturn(
        new PageImpl<>(List.of(response), pageable, 1));

    mockMvc.perform(get("/orders/v1")
        .contentType("application/json")
        .param("page", "0")
        .param("size", "2")
    )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].id", is(orderId), Long.class))
        .andExpect(jsonPath("$.content[0].ownerId", is(ownerId.toString())))
        .andExpect(jsonPath("$.content[0].status", is(OrderStatusName.CANCELED.toString())))
        .andExpect(jsonPath("$.content[0].serviceId", is(serviceId), Long.class))
        .andExpect(jsonPath("$.content[0].comment", is(comment)))
        .andExpect(jsonPath("$.content[0].createdAt", instantComparesEqualTo(response.getCreatedAt())))
        .andExpect(jsonPath("$.content[0].lastModifiedAt", instantComparesEqualTo(response.getLastModifiedAt())))
        .andExpect(jsonPath("$.content[0].links[0].href", is("http://localhost/orders/1")));
  }

  @Test
  void shouldReturnPageWhenGettingAllOrdersAndOwnerIdIsNotNull() throws Exception {
    var orderId = 2L;
    var ownerId = UUID.randomUUID();
    var serviceId = 1L;
    var comment = "Comment";

    var response = getNewOrderResponseWithAllFields(orderId, ownerId, serviceId,
        OrderStatusName.CANCELED, null, comment);
    response.add(Link.of("http://localhost/orders/1", "self"));

    var pageable = PageRequest.of(0, 2);

    when(orderService.getAllOrders(eq(pageable), eq(ownerId))).thenReturn(
        new PageImpl<>(List.of(response), pageable, 1));

    mockMvc.perform(get("/orders/v2")
            .contentType("application/json")
            .param("page", "0")
            .param("size", "2")
            .param("ownerId", ownerId.toString())
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].id", is(orderId), Long.class))
        .andExpect(jsonPath("$.content[0].ownerId", is(ownerId.toString())))
        .andExpect(jsonPath("$.content[0].status", is(OrderStatusName.CANCELED.toString())))
        .andExpect(jsonPath("$.content[0].serviceId", is(serviceId), Long.class))
        .andExpect(jsonPath("$.content[0].comment", is(comment)))
        .andExpect(jsonPath("$.content[0].createdAt", instantComparesEqualTo(response.getCreatedAt())))
        .andExpect(jsonPath("$.content[0].lastModifiedAt", instantComparesEqualTo(response.getLastModifiedAt())))
        .andExpect(jsonPath("$.content[0].links[0].href", is("http://localhost/orders/1")));
  }
}
