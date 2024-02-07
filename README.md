# FedEx - AggregationAPI

## Overview
<p>AggregationAPI is a RESTful API that's built with Java (version: 21) / Maven / Spring Boot (version 3.2.2)
that aggregates responses from various other backend service endpoints (pricing/track/shipments) with reduced number of
calls. It exposes one HTTP GET endpoint:</p>

- HTTP GET - http://localhost:8081/aggregation - Returns an aggregate response for any combination of pricing, track and
  shipments queries (Content-Type:
  application/json)

  <p>Request parameters:</p>

    - pricing (optional): Comma seperated list of ISO-2 country codes (i.e.: 'NL,UK,FR')
    - track (optional): Comma seperated list of one or more 9-digit order numbers (i.e.: '
      123456891,123456892,123456893')
    - shipments (optional): Comma seperated list of one or more 9-digit order numbers (i.e.: '
      123456891,123456892,123456893')

    <p>Example request:</p>
  
    ```http://localhost:8081/aggregation?pricing=NL,CN&track=109347263,123456891&shipments=109347263,123456891```
- 
    <p>Example response:</p>

    ```{"pricing": {"NL": 14.242090605778, "CN": 20.503467806384},"track": {"109347263": null, "123456891": "COLLECTING"},"shipments": {"109347263": ["box", "box", "pallet"], "123456891": null}}```

## Requirements

A Docker environment and JDK (version: 21) is required to build and run AggregationApi.

## Build & Run

<p>To build AggregationAPI, run following command from project folder:</p>

```mvnw clean install```

<p>To build a Docker image for AggregationApi, run one of following commands from project folder:

```docker-compose build```

or

```docker build . -t aggregationapi```

<p>To run the Docker image (with backend-service), run following command from project folder:

```docker-compose up```

<p>Port number (default: 8081) and backend-service URL  (default: http://localhost:8080) can be changed with following command:</p>

```docker run -d -p <port_number>:8081 -e backend.service.url=<backend_service_url> aggregationapi```

AggregationAPI will run on an embedded Tomcat server on port 8081 by default. This can be changed by setting an
environment variable as following:

```server.port=<port number>```
