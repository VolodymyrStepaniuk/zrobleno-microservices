package com.stepaniuk.order.status;

import com.stepaniuk.zrobleno.types.order.OrderStatusName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderStatusRepository extends JpaRepository<OrderStatus, Long> {

  Optional<OrderStatus> findByName(OrderStatusName orderStatusName);

}