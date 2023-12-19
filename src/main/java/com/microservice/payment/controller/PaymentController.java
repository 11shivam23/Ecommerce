package com.microservice.payment.controller;

import com.microservice.payment.service.PaymentService;
import com.razorpay.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    @Autowired
    PaymentService paymentService;

    @PostMapping("/createPayment")
    @ResponseBody
    public ResponseEntity<String> createOrder(@RequestBody Map<String, Object> paymentData) {

        try {
            Order order = paymentService.createOrder(paymentData);
            return ResponseEntity.ok(order.toString());
        } catch (RazorpayException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error creating order");
        }

    }


}
