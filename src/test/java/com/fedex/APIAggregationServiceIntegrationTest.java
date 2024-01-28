package com.fedex;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Testcontainers
@ContextConfiguration(initializers = APIAggregationServiceIntegrationTest.Initializer.class)
@AutoConfigureMockMvc
class APIAggregationServiceIntegrationTest {

    @Container
    public static GenericContainer backendServices = new GenericContainer(
            DockerImageName.parse("xyzassessment/backend-services:latest"))
            .withExposedPorts(8080);

    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext context) {
            TestPropertyValues.of("backend.service.url="
                            + String.format("http://%s:%d", backendServices.getHost(), backendServices.getFirstMappedPort()))
                    .applyTo(context.getEnvironment());
        }
    }

    @Autowired
    private MockMvc mvc;

    @Test
    void test_aggregation_with_single_batch_call_per_service() throws Exception {
        mvc.perform(get("/aggregation")
                        .queryParam("pricing", "NL,CN,US")
                        .queryParam("track", "123456891,123456892")
                        .queryParam("shipments", "223456891,223456892,223456893,223456894,223456895")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.pricing").exists())
                .andExpect(jsonPath("$.track").exists())
                .andExpect(jsonPath("$.shipments").exists());
    }

    @Test
    void test_aggregation_with_multiple_batch_calls_per_service() throws Exception {
        mvc.perform(get("/aggregation")
                        .queryParam("pricing", "NL,CN,EN,US,CA,FR")
                        .queryParam("track", "123456891,123456892,123456893,123456894,123456895,123456896,123456897")
                        .queryParam("shipments", "223456891,223456892,223456893,223456894,223456895,223456896,223456897,223456898")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.pricing").exists())
                .andExpect(jsonPath("$.track").exists())
                .andExpect(jsonPath("$.shipments").exists());
    }

    @Test
    void test_aggregation_with_missing_query_parameters() throws Exception {
        mvc.perform(get("/aggregation")
                        .queryParam("pricing", "NL,CN,EN")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.pricing").exists())
                .andExpect(jsonPath("$.track").exists())
                .andExpect(jsonPath("$.shipments").exists());
    }
}
