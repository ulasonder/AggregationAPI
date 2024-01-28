package com.fedex.aggregation.service;

import com.fedex.aggregation.client.APIClient;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.joining;

public abstract class ApiCallService<T extends Map> {

    private final Integer batchSize;

    private final Integer batchTimeout;

    private final APIClient<T> apiClient;

    protected final ExecutorService batchCallExecutorService;

    protected final ExecutorService serviceCallexecutorService;

    private final Queue<QueryCallback<T>> requestQueue;

    private final CyclicBarrier cyclicBarrier;

    public ApiCallService(APIClient<T> apiClient,
                          Integer batchSize,
                          Integer batchTimeout,
                          Integer serviceCallThreadPoolSize,
                          Integer batchCallThreadPoolSize) {
        this.apiClient = apiClient;
        this.batchSize = batchSize;
        this.batchTimeout = batchTimeout;
        this.batchCallExecutorService = Executors.newFixedThreadPool(batchCallThreadPoolSize);
        this.serviceCallexecutorService = Executors.newFixedThreadPool(serviceCallThreadPoolSize);
        this.requestQueue = new ConcurrentLinkedQueue<>();
        this.cyclicBarrier = new CyclicBarrier(batchSize, this::batchApiCall);
    }

    public void handleServiceRequest(List<QueryCallback<T>> callbacks) {
        callbacks.forEach(cb -> {
            requestQueue.add(cb);
            batchCallExecutorService.submit(() -> {
                try {
                    cyclicBarrier.await(batchTimeout, TimeUnit.SECONDS);
                } catch (InterruptedException | BrokenBarrierException | TimeoutException e) {
                    batchApiCall();
                }
            });
        });
    }

    private void batchApiCall() {
        final List<QueryCallback<T>> cbList = IntStream.rangeClosed(1, batchSize)
                .mapToObj(i -> requestQueue.poll())
                .filter(Objects::nonNull).toList();
        String query = cbList.stream().map(QueryCallback::getQueryParam).collect(joining(","));

        if (!query.isBlank()) {
            serviceCallexecutorService.submit(() -> {
                T response = getResponse(query);
                cbList.forEach(cb -> cb.handleResponse(response));
            });
        }
    }

    private T getResponse(String query) {
        T response = null;
        try {
            response = apiClient.getResponse(query);
        } catch (Exception e) {
            // nothing to do, response will be null
        }
        return response;
    }
}
