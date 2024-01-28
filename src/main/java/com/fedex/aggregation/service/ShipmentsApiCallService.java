package com.fedex.aggregation.service;

import com.fedex.aggregation.client.ShipmentsClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ShipmentsApiCallService extends ApiCallService<Map<String, List<String>>> {

    public ShipmentsApiCallService(@Autowired ShipmentsClient shipmentsClient,
                                   @Value("${service.call.batch.size}") Integer batchSize,
                                   @Value("${service.call.batch.timeout}") Integer batchTimeout,
                                   @Value("${service.call.threadpool.size}") Integer serviceCallThreadPoolSize,
                                   @Value("${batch.call.threadpool.size}") Integer batchCallThreadPoolSize) {
        super(shipmentsClient, batchSize, batchTimeout, serviceCallThreadPoolSize, batchCallThreadPoolSize);
    }
}
