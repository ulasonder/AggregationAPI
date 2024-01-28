package com.fedex.aggregation.service;

import lombok.Data;

import java.util.Map;

@Data
public class QueryCallback<T extends Map> {

    private final String queryParam;

    private Object queryResult;

    public void handleResponse(T apiResponse) {
        this.queryResult = apiResponse.getOrDefault(queryParam, null);
    }
}
