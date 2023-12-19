package com.microservice.payment.service;

import com.razorpay.Order;
import com.razorpay.RazorpayException;

import java.util.Map;

public interface PaymentService {

    public Order createOrder(Map<String, Object> paymentData) throws RazorpayException;

}
