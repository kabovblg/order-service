package com.blagovestkabov.order_service.service;


import com.blagovestkabov.order_service.entity.Order;
import com.blagovestkabov.order_service.model.OrderRequest;
import com.blagovestkabov.order_service.repository.OrderRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@Log4j2
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Override
    public long placeOrder(OrderRequest orderRequest) {
        log.info("Placing the order request: {} ", orderRequest);

        Order order = Order
                .builder()
                .amount(orderRequest.getTotalAmount())
                .productId(orderRequest.getProductId())
                .orderStatus("CREATED")
                .orderDate(Instant.now())
                .quantity(orderRequest.getQuantity())
                .build();

        order = orderRepository.save(order);

        log.info("Order {} was successfully placed !", order.getId());
        return order.getId();
    }
}
