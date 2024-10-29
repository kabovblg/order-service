package com.blagovestkabov.order_service.service;

import com.blagovestkabov.order_service.model.OrderRequest;

public interface OrderService {
    long placeOrder(OrderRequest orderRequest);
}
