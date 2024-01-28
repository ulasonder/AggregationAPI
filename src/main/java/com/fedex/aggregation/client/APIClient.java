package com.fedex.aggregation.client;

public interface APIClient<R> {

    R getResponse(String query);
}
