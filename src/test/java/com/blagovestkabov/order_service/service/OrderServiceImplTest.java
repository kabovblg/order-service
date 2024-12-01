package com.blagovestkabov.order_service.service;


import com.blagovestkabov.order_service.entity.Order;
import com.blagovestkabov.order_service.exception.CustomException;
import com.blagovestkabov.order_service.external.client.PaymentService;
import com.blagovestkabov.order_service.external.client.ProductService;
import com.blagovestkabov.order_service.external.response.PaymentResponse;
import com.blagovestkabov.order_service.external.response.ProductResponse;
import com.blagovestkabov.order_service.model.OrderResponse;
import com.blagovestkabov.order_service.model.PaymentMethod;
import com.blagovestkabov.order_service.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class OrderServiceImplTest {
    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductService productService;

    @Mock
    private PaymentService paymentService;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    OrderService orderService = new OrderServiceImpl();

    @Test
    void testSuccessfulOrder() {
        // 1. Mocking the stuff
        // 2. Calling the actual methods
        // 3. Verify
        // 4. Assert
        Order order = getMockedOrder();

        Mockito.when(orderRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(order));

        Mockito.when(restTemplate.getForObject("http://PRODUCT-SERVICE/product/" + order.getProductId(), ProductResponse.class)).thenReturn(getMockedProductResponse());

        Mockito.when(restTemplate.getForObject("http://PAYMENT-SERVICE/payment/order/" + order.getId(), PaymentResponse.class)).thenReturn(getMockedPaymentResponse());

        OrderResponse orderResponse = orderService.getOrderDetails(1);

        Mockito.verify(orderRepository, Mockito.times(1)).findById(ArgumentMatchers.anyLong());

        assertNotNull(orderResponse);

        assertEquals(order.getId(), orderResponse.getOrderId());
    }

    @Test
    void orderNotFound() {

        Mockito.when(orderRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.ofNullable(null));
        
        CustomException customException = assertThrows(CustomException.class, () -> orderService.getOrderDetails(1));

        assertEquals("NOT_FOUND", customException.getErrorCode());

        assertEquals(404, customException.getStatusCode());
    }

    private PaymentResponse getMockedPaymentResponse() {
        return PaymentResponse.builder()
                .paymentId(1)
                .paymentDate(Instant.now())
                .paymentMethod(PaymentMethod.CASH)
                .amount(11)
                .orderId(1)
                .status("ACCEPTED")
                .build();
    }

    private ProductResponse getMockedProductResponse() {
        return ProductResponse.builder()
                .productId(2)
                .productName("iPhone")
                .price(1400)
                .quantity(11)
                .build();
    }

    private Order getMockedOrder() {
        return Order.builder()
                .orderStatus("PLACED")
                .orderDate(Instant.now())
                .id(1)
                .amount(11)
                .quantity(11)
                .productId(2)
                .build();
    }
}