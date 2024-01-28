package com.fedex.aggregation.service;

import com.fedex.aggregation.data.Aggregation;
import jakarta.annotation.PreDestroy;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.*;

import static java.util.function.Predicate.not;

@Service
public class AggregationService {

    private final PricingApiCallService pricingApiCallService;

    private final TrackApiCallService trackApiCallService;

    private final ShipmentsApiCallService shipmentsApiCallService;

    private final ExecutorService aggregationExecutorService = Executors.newFixedThreadPool(20);

    public AggregationService(final @Autowired PricingApiCallService pricingApiCallService,
                              final @Autowired TrackApiCallService trackApiCallService,
                              final @Autowired ShipmentsApiCallService shipmentsApiCallService) {
        this.pricingApiCallService = pricingApiCallService;
        this.trackApiCallService = trackApiCallService;
        this.shipmentsApiCallService = shipmentsApiCallService;
    }

    public Aggregation getAggregation(String pricingQuery, String trackQuery, String shipmentsQuery) {
        Aggregation aggregation = new Aggregation();

        List<CompletableFuture<Void>> completableFutures = new ArrayList<>();
        if (pricingQuery != null) {
            completableFutures.add(CompletableFuture.runAsync(() -> aggregation.setPricing(getPricing(pricingQuery)), aggregationExecutorService));
        }
        if (trackQuery != null) {
            completableFutures.add(CompletableFuture.runAsync(() -> aggregation.setTrack(getTrack(trackQuery)), aggregationExecutorService));
        }
        if (shipmentsQuery != null) {
            completableFutures.add(CompletableFuture.runAsync(() -> aggregation.setShipments(getShipments(shipmentsQuery)), aggregationExecutorService));
        }

        try {
            CompletableFuture.allOf(completableFutures.toArray(new CompletableFuture[completableFutures.size()])).get(10, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            // nothing to do
        }

        return aggregation;
    }

    public Map<String, List<String>> getShipments(String query) {
        final Map<String, List<String>> shipments = new ConcurrentHashMap<>();
        List<QueryCallback<Map<String, List<String>>>> cbList = new ArrayList<>();
        getQueryValues(query).forEach(v -> cbList.add(new QueryCallback(v)));
        shipmentsApiCallService.handleServiceRequest(cbList);
        try {
            CompletableFuture.runAsync(() -> {
                while (true) {
                    if (cbList.stream().map(QueryCallback::getQueryResult).allMatch(Objects::nonNull)) {
                        cbList.forEach(cb -> shipments.put(cb.getQueryParam(), (List<String>) cb.getQueryResult()));
                        return;
                    }
                    try {
                        TimeUnit.MILLISECONDS.sleep(10);
                    } catch (InterruptedException e) {
                        // nothing to do
                    }
                }
                // within 10 seconds response timeout
            }, aggregationExecutorService).get(9, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            // nothing to do
        }
        return shipments;
    }

    public Map<String, BigDecimal> getPricing(String query) {
        final Map<String, BigDecimal> pricing = new ConcurrentHashMap<>();
        List<QueryCallback<Map<String, BigDecimal>>> cbList = new ArrayList<>();
        getQueryValues(query).forEach(v -> cbList.add(new QueryCallback(v)));
        pricingApiCallService.handleServiceRequest(cbList);
        try {
            CompletableFuture.runAsync(() -> {
                while (true) {
                    if (cbList.stream().map(QueryCallback::getQueryResult).allMatch(Objects::nonNull)) {
                        cbList.forEach(cb -> pricing.put(cb.getQueryParam(), (BigDecimal) cb.getQueryResult()));
                        return;
                    }
                    try {
                        TimeUnit.MILLISECONDS.sleep(10);
                    } catch (InterruptedException e) {
                        // do nothing
                    }
                }
                // within 10 seconds response timeout
            }, aggregationExecutorService).get(9, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            // nothing to do
        }
        return pricing;
    }

    public Map<String, String> getTrack(String query) {
        final Map<String, String> track = new ConcurrentHashMap<>();
        List<QueryCallback<Map<String, String>>> cbList = new ArrayList<>();
        getQueryValues(query).forEach(v -> cbList.add(new QueryCallback(v)));
        trackApiCallService.handleServiceRequest(cbList);
        try {
            CompletableFuture.runAsync(() -> {
                while (true) {
                    if (cbList.stream().map(QueryCallback::getQueryResult).allMatch(Objects::nonNull)) {
                        cbList.forEach(cb -> track.put(cb.getQueryParam(), (String) cb.getQueryResult()));
                        return;
                    }
                    try {
                        TimeUnit.MILLISECONDS.sleep(10);
                    } catch (InterruptedException e) {
                        // nothing to do
                    }
                }
                // within 10 seconds response timeout
            }, aggregationExecutorService).get(9, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            // nothing to do
        }
        return track;
    }

    private List<String> getQueryValues(String query) {
        return Arrays.stream(query.split(",")).filter(not(String::isBlank)).distinct().toList();
    }

    @PreDestroy
    @SneakyThrows
    void shutdownExecutors() {
        aggregationExecutorService.shutdown();
        if (!aggregationExecutorService.awaitTermination(5, TimeUnit.SECONDS)) {
            aggregationExecutorService.shutdownNow();
        }
    }
}
