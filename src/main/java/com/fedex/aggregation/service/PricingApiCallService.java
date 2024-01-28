package com.fedex.aggregation.service;

import com.fedex.aggregation.client.PricingClient;
import jakarta.annotation.PreDestroy;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class PricingApiCallService extends ApiCallService<Map<String, BigDecimal>> {

    public PricingApiCallService(@Autowired PricingClient pricingClient,
                                 @Value("${service.call.batch.size}") Integer batchSize,
                                 @Value("${service.call.batch.timeout}") Integer batchTimeout,
                                 @Value("${service.call.threadpool.size}") Integer serviceCallThreadPoolSize,
                                 @Value("${batch.call.threadpool.size}") Integer batchCallThreadPoolSize) {
        super(pricingClient, batchSize, batchTimeout, serviceCallThreadPoolSize, batchCallThreadPoolSize);
    }

    @PreDestroy
    @SneakyThrows
    void shutdownExecutors() {
        batchCallExecutorService.shutdown();
        if (!batchCallExecutorService.awaitTermination(5, TimeUnit.SECONDS)) {
            batchCallExecutorService.shutdownNow();
        }
        serviceCallexecutorService.shutdown();
        if (!serviceCallexecutorService.awaitTermination(5, TimeUnit.SECONDS)) {
            serviceCallexecutorService.shutdownNow();
        }
    }
}
