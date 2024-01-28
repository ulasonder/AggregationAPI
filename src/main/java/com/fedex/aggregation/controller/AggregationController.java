package com.fedex.aggregation.controller;

import com.fedex.aggregation.data.Aggregation;
import com.fedex.aggregation.service.AggregationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/aggregation")
public class AggregationController {

    @Autowired
    private AggregationService aggregationService;

    @GetMapping(produces = "application/json")
    public ResponseEntity<Aggregation> getAggregation(@RequestParam(value = "pricing", required = false) String pricingQuery,
                                                      @RequestParam(value = "track", required = false) String trackQuery,
                                                      @RequestParam(value = "shipments", required = false) String shipmentsQuery) {

        return ResponseEntity.ok(aggregationService.getAggregation(pricingQuery, trackQuery, shipmentsQuery));
    }
}
