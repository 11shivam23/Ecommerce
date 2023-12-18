package com.microservice.payment.controller;

import com.microservice.payment.config.AppConfig;
import com.paytm.pg.merchant.PaytmChecksum;
import com.razorpay.*;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.net.URL;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    @PostMapping("/createPayment")
    @ResponseBody
    public String createOrder(@RequestBody Map<String, Object> data) throws RazorpayException {
        System.out.println(data);

        int amount = Integer.parseInt(data.get("amount").toString());

        RazorpayClient client = new RazorpayClient("rzp_test_XX7kMHWjOCl5Dt", "3OcWVvmivFMPvbXPTF90x5uO");

        JSONObject jsObj = new JSONObject();
        jsObj.put("amount", amount * 100);
        jsObj.put("currency", "INR");
        jsObj.put("receipt", "txn_123");

        var order = client.orders.create(jsObj);

        System.out.println(order);

        //Save this to database



        return order.toString();
    }
    

    Random random = new Random();

    ResponseEntity<Map> response = null;
    

    @PostMapping("/start")
    public Map<String, Object> startPayment(@RequestBody Map<String, Object> data) {

        int orderId = random.nextInt(100000000);

        //param created
        JSONObject paytmParams = new JSONObject();

        JSONObject userInfo = new JSONObject();
        userInfo.put("custId", "SHIVAM_001");

        JSONObject txnAmount = new JSONObject();
        txnAmount.put("value", data.get("amount"));
        txnAmount.put("currency", "INR");

        //body information
        JSONObject body = new JSONObject();
        body.put("requestType", "Payment");
        body.put("mid", AppConfig.MERCHANT_ID);
        body.put("websiteName", AppConfig.WEBSITE);
        body.put("orderId", orderId);
        body.put("callbackUrl", "http://localhost:8080/payment/payment-success");
        body.put("txnAmount", txnAmount);
        body.put("userInfo", userInfo);

        String responseData = "";

        try{

            String checkSum = PaytmChecksum.generateSignature(body.toString(), AppConfig.MERCHANT_KEY);

            JSONObject head = new JSONObject();
            head.put("signature", checkSum);

            paytmParams.put("body", body);
            paytmParams.put("head", head);

//            String postData = paytmParams.toString();

//          for staging
            URL url = new URL("https://securegw-stage.paytm.in/theia/api/v1/initiateTransaction?mid="+AppConfig.MERCHANT_ID+"&orderId="+orderId+"");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String,Object>> httpEntity = new HttpEntity<>(paytmParams.toMap(), headers);

            //calling api
            RestTemplate restTemplate = new RestTemplate();
            response = restTemplate.postForEntity(url.toString(), httpEntity, Map.class);

            System.out.println(response);

        } catch (Exception e) {
            e.printStackTrace();
        }

        Map body1 = response.getBody();
        body1.put("orderId", orderId);
        body1.put("amount", txnAmount.get("value"));

        return body1;
    }


}
