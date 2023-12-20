package com.microservice.payment.repository;

import com.aerospike.client.AerospikeClient;
import com.aerospike.client.Bin;
import com.aerospike.client.Key;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class PaymentRepository {

    @Value("${aerospike.host}")
    private String aerospikeHost;

    @Value("${aerospike.port}")
    private int aerospikePort;

    @Value("${aerospike.namespace}")
    private String aerospikeNamespace;

    public void saveOrder(String orderId, String orderDetails) {

        AerospikeClient aerospikeClient = new AerospikeClient(aerospikeHost, aerospikePort);

        Key key = new Key(aerospikeNamespace, "orders", orderId);
        Bin bin = new Bin("orderDetails", orderDetails);

        aerospikeClient.put(null, key, bin);
        aerospikeClient.close();

    }
}
