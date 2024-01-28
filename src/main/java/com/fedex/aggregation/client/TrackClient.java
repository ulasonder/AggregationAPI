package com.fedex.aggregation.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(value = "track", url = "${backend.service.url}")
public interface TrackClient extends APIClient<Map<String, String>> {

    @RequestMapping(method = RequestMethod.GET, value = "/track")
    Map<String, String> getResponse(@RequestParam(name = "q") String query);
}
