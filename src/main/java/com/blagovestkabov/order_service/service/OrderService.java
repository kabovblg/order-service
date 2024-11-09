package com.blagovestkabov.order_service.service;

import com.blagovestkabov.order_service.model.OrderRequest;
import com.blagovestkabov.order_service.model.OrderResponse;

public interface OrderService {
    long placeOrder(OrderRequest orderRequest);

    OrderResponse getOrderDetails(long orderId);
}
