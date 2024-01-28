package com.fedex.aggregation.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient(value = "shipments", url = "${backend.service.url}")
public interface ShipmentsClient extends APIClient<Map<String, List<String>>> {

    @RequestMapping(method = RequestMethod.GET, value = "/shipments")
    Map<String, List<String>> getResponse(@RequestParam(name = "q") String query);
}
