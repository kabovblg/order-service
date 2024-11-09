package com.blagovestkabov.order_service.external.response;

import com.blagovestkabov.order_service.model.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentResponse {

    private long paymentId;
    private String status;
    private PaymentMethod paymentMethod;
    private long amount;
    private Instant paymentDate;
    private long orderId;
}