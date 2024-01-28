package com.fedex.aggregation.service;

import com.fedex.aggregation.client.PricingClient;
import com.fedex.aggregation.client.ShipmentsClient;
import com.fedex.aggregation.client.TrackClient;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.stream.IntStream;

import static java.util.stream.Collectors.joining;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class AggregationServiceTest {

    @Test
    public void test_single_batch_call() {
        PricingClient mockPricingClient = Mockito.mock(PricingClient.class);
        TrackClient mockTrackClient = Mockito.mock(TrackClient.class);
        ShipmentsClient mockShipmentsClient = Mockito.mock(ShipmentsClient.class);

        AggregationService aggregationService = createAggregationService(mockPricingClient, mockTrackClient, mockShipmentsClient);

        aggregationService.getAggregation("BR,FR,JP,UK,DE",
                "123456891,123456892,123456893,123456894,123456895",
                "223456891,223456892,223456893,223456894,223456895");

        verify(mockPricingClient, times(1)).getResponse(anyString());
        verify(mockTrackClient, times(1)).getResponse(anyString());
        verify(mockShipmentsClient, times(1)).getResponse(anyString());
    }

    @Test
    public void test_multiple_batch_calls() {
        PricingClient mockPricingClient = Mockito.mock(PricingClient.class);
        TrackClient mockTrackClient = Mockito.mock(TrackClient.class);
        ShipmentsClient mockShipmentsClient = Mockito.mock(ShipmentsClient.class);

        AggregationService aggregationService = createAggregationService(mockPricingClient, mockTrackClient, mockShipmentsClient);

        aggregationService.getAggregation("BR,FR,JP,UK,US,NL,CA,TR,IT,DE,ES",
                IntStream.rangeClosed((int) 1E9 + 1, (int) 1E9 + 17).mapToObj(Integer::toString).collect(joining(",")),
                IntStream.rangeClosed((int) 2E9 + 1, (int) 2E9 + 27).mapToObj(Integer::toString).collect(joining(",")));

        verify(mockPricingClient, atLeast(3)).getResponse(anyString());
        verify(mockTrackClient, atLeast(4)).getResponse(anyString());
        verify(mockShipmentsClient, atLeast(6)).getResponse(anyString());
    }

    private AggregationService createAggregationService(PricingClient mockPricingClient, TrackClient mockTrackClient, ShipmentsClient mockShipmentsClient) {
        PricingApiCallService pricingApiCallService = new PricingApiCallService(mockPricingClient, 5, 5, 10, 10);
        TrackApiCallService trackApiCallService = new TrackApiCallService(mockTrackClient, 5, 5, 10, 10);
        ShipmentsApiCallService shipmentsApiCallService = new ShipmentsApiCallService(mockShipmentsClient, 5, 5, 10, 10);

        return new AggregationService(pricingApiCallService, trackApiCallService, shipmentsApiCallService);
    }
}
