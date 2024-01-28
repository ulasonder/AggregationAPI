package com.fedex.aggregation.service;

import com.fedex.aggregation.client.TrackClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class TrackApiCallService extends ApiCallService<Map<String, String>> {

    public TrackApiCallService(@Autowired TrackClient trackClient,
                               @Value("${service.call.batch.size}") Integer batchSize,
                               @Value("${service.call.batch.timeout}") Integer batchTimeout,
                               @Value("${service.call.threadpool.size}") Integer serviceCallThreadPoolSize,
                               @Value("${batch.call.threadpool.size}") Integer batchCallThreadPoolSize) {
        super(trackClient, batchSize, batchTimeout, serviceCallThreadPoolSize, batchCallThreadPoolSize);
    }
}
