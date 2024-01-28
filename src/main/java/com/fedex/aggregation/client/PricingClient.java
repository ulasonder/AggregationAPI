package com.fedex.aggregation.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.Map;

@FeignClient(value = "pricing", url = "${backend.service.url}")
public interface PricingClient extends APIClient<Map<String, BigDecimal>> {

    @RequestMapping(method = RequestMethod.GET, value = "/pricing")
    Map<String, BigDecimal> getResponse(@RequestParam(name = "q") String query);
}
