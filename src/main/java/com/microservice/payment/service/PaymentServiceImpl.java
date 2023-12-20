package com.microservice.payment.service;

import com.microservice.payment.constant.AppConstant;
import com.microservice.payment.repository.PaymentRepository;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.UUID;

@Service
public class PaymentServiceImpl implements PaymentService{

    @Value("${razorpay.keyId}")
    private String merchantKeyId;

    @Value("${razorpay.keySecret}")
    private String merchantKeySecret;

    @Autowired
    private PaymentRepository paymentRepository;

    @Override
    public Order createOrder(Map<String, Object> paymentData) throws RazorpayException {

        RazorpayClient client = new RazorpayClient(merchantKeyId, merchantKeySecret);

        String receiptId = getReceiptId();
        double amount = Double.parseDouble(paymentData.get("amount").toString());
        String currency = paymentData.get("currency").toString();
        int userId = Integer.parseInt(paymentData.get("userId").toString());

        JSONObject jsObj = new JSONObject();
        jsObj.put("amount", amount * 100);
        jsObj.put("currency", currency);
        jsObj.put("receipt", receiptId);

        Order order = client.orders.create(jsObj);

        //save it to DB
        paymentRepository.saveOrder(order.get("id").toString(), order.toString());

        return order;
    }

    public String getReceiptId() {

        UUID uuid = UUID.randomUUID();
        return AppConstant.RECEIPT + uuid.toString()
                .replace("-","")
                .substring(0,10)
                .toUpperCase();

    }

}
