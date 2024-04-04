package com.stepaniuk.order;


import com.stepaniuk.order.client.FeedbackClient;
import com.stepaniuk.order.client.ServiceClient;
import com.stepaniuk.order.status.OrderStatusRepository;
import com.stepaniuk.zrobleno.payload.order.OrderCreateRequest;
import com.stepaniuk.zrobleno.payload.order.OrderResponse;
import com.stepaniuk.zrobleno.payload.order.OrderUpdateRequest;
import com.stepaniuk.zrobleno.types.exception.order.IllegalOrderStatusException;
import com.stepaniuk.zrobleno.types.exception.order.NoSuchOrderByIdException;
import com.stepaniuk.zrobleno.types.exception.order.NoSuchOrderStatusByNameException;
import com.stepaniuk.zrobleno.types.exception.service.NoSuchServiceByIdException;
import com.stepaniuk.zrobleno.types.order.OrderStatusName;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

  private final OrderRepository orderRepository;
  private final OrderStatusRepository orderStatusRepository;
  private final OrderMapper orderMapper;
  private final FeedbackClient feedbackClient;
  private final ServiceClient serviceClient;

  public OrderResponse createOrder(OrderCreateRequest request) {
    Order order = new Order();

    var orderStatus = orderStatusRepository.findByName(OrderStatusName.CREATED)
        .orElseThrow(() -> new NoSuchOrderStatusByNameException(OrderStatusName.CREATED));

    order.setStatus(orderStatus);
    order.setOwnerId(request.getOwnerId());
    order.setComment(request.getComment());
    var service = serviceClient.getServiceById(request.getServiceId());

    if(service == null) {
      throw new NoSuchServiceByIdException(request.getServiceId());
    }

    order.setServiceId(request.getServiceId());

    var savedOrder = orderRepository.save(order);
    return orderMapper.toResponse(savedOrder, null);
  }

  public OrderResponse getOrder(Long id) {
    var order = orderRepository.findById(id).orElseThrow(
        () -> new NoSuchOrderByIdException(id)
    );

    var feedback = feedbackClient.getAllFeedbacks(Pageable.ofSize(1),
        null, order.getId()).getContent().stream().findFirst().orElse(null);
    return orderMapper.toResponse(order, feedback);
  }

  public OrderResponse updateOrder(Long id, OrderUpdateRequest request) {
    var order = orderRepository.findById(id).orElseThrow(() -> new NoSuchOrderByIdException(id));
    var feedback = feedbackClient.getAllFeedbacks(Pageable.ofSize(1), null, id).getContent()
        .stream().findFirst().orElse(null);

    if (request.getStatus() != null) {
      var orderStatus = orderStatusRepository.findByName(request.getStatus())
          .orElseThrow(() -> new NoSuchOrderStatusByNameException(request.getStatus()));
      checkStatusForIllegal(request.getStatus(), order.getStatus().getName(), id);
      order.setStatus(orderStatus);
    }

    return orderMapper.toResponse(orderRepository.save(order), feedback);
  }

  public Page<OrderResponse> getAllOrders(Pageable pageable,
      @Nullable UUID ownerId) {

    Specification<Order> specification = Specification.where(null);

    if (ownerId != null) {
      specification = specification.and(
          (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("ownerId"), ownerId));
    }

    var orders = orderRepository.findAll(specification, pageable);

    return orders.map(order -> {
      var feedback = feedbackClient.getAllFeedbacks(Pageable.ofSize(1), null, order.getId())
          .getContent().stream().findFirst().orElse(null);
      return orderMapper.toResponse(order, feedback);
    });
  }

  public OrderResponse changeStatus(Long id, OrderStatusName status) {
    var order = orderRepository.findById(id).orElseThrow(() -> new NoSuchOrderByIdException(id));
    var orderStatus = orderStatusRepository.findByName(status)
        .orElseThrow(() -> new NoSuchOrderStatusByNameException(status));
    var feedback = feedbackClient.getAllFeedbacks(Pageable.ofSize(1), null, id).getContent()
        .stream().findFirst().orElse(null);

    checkStatusForIllegal(status, order.getStatus().getName(), id);

    order.setStatus(orderStatus);

    return orderMapper.toResponse(orderRepository.save(order), feedback);
  }

  private void checkStatusForIllegal(OrderStatusName newStatus, OrderStatusName oldStatus,
                                     Long id) {

    switch (oldStatus) {
      case CREATED:
        if (newStatus != OrderStatusName.CANCELED && newStatus != OrderStatusName.CONFIRMED) {
          throw new IllegalOrderStatusException(newStatus, oldStatus, id);
        }
        break;
      case CONFIRMED:
        if (newStatus != OrderStatusName.CANCELED && newStatus != OrderStatusName.IN_PROGRESS) {
          throw new IllegalOrderStatusException(newStatus, oldStatus, id);
        }
        break;
      case IN_PROGRESS:
        if (newStatus != OrderStatusName.COMPLETED) {
          throw new IllegalOrderStatusException(newStatus, oldStatus, id);
        }
        break;
      case COMPLETED, CANCELED:
        throw new IllegalOrderStatusException(newStatus, oldStatus, id);
    }

  }
}
