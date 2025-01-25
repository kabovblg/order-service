package com.blagovestkabov.order_service.service;


import com.blagovestkabov.order_service.entity.Order;
import com.blagovestkabov.order_service.exception.CustomException;
import com.blagovestkabov.order_service.external.client.PaymentService;
import com.blagovestkabov.order_service.external.client.ProductService;
import com.blagovestkabov.order_service.external.request.PaymentRequest;
import com.blagovestkabov.order_service.external.response.PaymentResponse;
import com.blagovestkabov.order_service.external.response.ProductResponse;
import com.blagovestkabov.order_service.model.OrderRequest;
import com.blagovestkabov.order_service.model.OrderResponse;
import com.blagovestkabov.order_service.repository.OrderRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;

@Service
@Log4j2
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${microservices.product}")
    private String productSvcUrl;

    @Value("${microservices.payment}")
    private String paymentSvcUrl;

    @Override
    public long placeOrder(OrderRequest orderRequest) {
        log.info("Placing the order request: {} ", orderRequest);

        productService.reduceQuantity(orderRequest.getProductId(), orderRequest.getQuantity());

        log.info("Order with status 'created' has been created !");
        Order order = Order
                .builder()
                .amount(orderRequest.getTotalAmount())
                .productId(orderRequest.getProductId())
                .orderStatus("CREATED")
                .orderDate(Instant.now())
                .quantity(orderRequest.getQuantity())
                .build();

        order = orderRepository.save(order);

        log.info("Calling payment-service");

        PaymentRequest paymentRequest  = PaymentRequest
                .builder()
                .orderId(order.getId())
                .paymentMethod(orderRequest.getPaymentMethod())
                .amount(orderRequest.getTotalAmount())
                .build();

        String orderStatus = null;

        try {
            paymentService.doPayment(paymentRequest);
            log.info("Payment is done successfully ! ");
            orderStatus = "COMPLETED";
        } catch (Exception e) {
            log.error("Error appeared in payment. Changing the status to F A I L E D !");
            orderStatus = "PAYMENT_FAILED";
        }

        order.setOrderStatus(orderStatus);
        orderRepository.save(order);

        log.info("Order {} was successfully placed !", order.getId());
        return order.getId();
    }

    @Override
    public OrderResponse getOrderDetails(long orderId) {
        log.info("Getting order details for order: {} ", orderId);

        Order order = orderRepository
                .findById(orderId)
                .orElseThrow(() -> new CustomException("Order id {" + orderId + "} not found !", "NOT_FOUND", 404));

        log.info("Calling product-service to fetch data");
        log.info("Calling Product Service at URL: {}", productSvcUrl + order.getProductId());

        ProductResponse productResponse
                = restTemplate.getForObject(
                        productSvcUrl + order.getProductId(),
                ProductResponse.class
        );

        OrderResponse.ProductDetails productDetails =
                OrderResponse.ProductDetails
                        .builder()
                        .productId(productResponse.getProductId())
                        .productName(productResponse.getProductName())
                        .build();

        log.info("Getting payment information form the payment Service");
        PaymentResponse paymentResponse
                = restTemplate.getForObject(
                paymentSvcUrl + "order/" + order.getId(),
                PaymentResponse.class
        );

        OrderResponse.PaymentDetails paymentDetails
                = OrderResponse.PaymentDetails
                .builder()
                .paymentId(paymentResponse.getPaymentId())
                .paymentStatus(paymentResponse.getStatus())
                .paymentDate(paymentResponse.getPaymentDate())
                .paymentMethod(paymentResponse.getPaymentMethod())
                .build();

        OrderResponse orderResponse
                = OrderResponse.builder()
                .orderId(order.getId())
                .orderStatus(order.getOrderStatus())
                .amount(order.getAmount())
                .orderDate(order.getOrderDate())
                .productDetails(productDetails)
                .paymentDetails(paymentDetails)
                .build();

        return orderResponse;
    }
}
