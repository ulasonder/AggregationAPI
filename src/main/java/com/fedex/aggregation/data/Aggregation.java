package com.fedex.aggregation.data;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
public class Aggregation {

    private Map<String, BigDecimal> pricing = new ConcurrentHashMap<>();
    private Map<String, String> track = new ConcurrentHashMap<>();
    private Map<String, List<String>> shipments = new ConcurrentHashMap<>();
}
