package com.microservice.payment.service;

import com.microservice.payment.config.AppConfig;
import com.microservice.payment.constant.AppConstant;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.UUID;

@Service
public class PaymentServiceImpl implements PaymentService{

    @Override
    public Order createOrder(Map<String, Object> paymentData) throws RazorpayException {

        RazorpayClient client = new RazorpayClient(AppConfig.MERCHANT_ID, AppConfig.MERCHANT_KEY);

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
