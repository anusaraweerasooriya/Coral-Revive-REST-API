package com.project.rest.api_gateway;

import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.Buildable;
import org.springframework.cloud.gateway.route.builder.PredicateSpec;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Function;

@Configuration
public class ApiGatewayConfiguration {

    @Bean
    public RouteLocator gatewayRouter(RouteLocatorBuilder builder) {

        return builder.routes()
                .route(p -> p.path("/get")
                        .filters(f -> f
                                .addRequestHeader("MyHeader", "MyURI")
                                .addRequestParameter("Param", "MyValue"))
                        .uri("http://httpbin.org:80"))
                .route(p -> p.path("/coral-growth-monitor-service/**")
                        .uri("lb://coral-growth-monitor-service"))
                .route(p -> p.path("/scheduling-service/**")
                        .uri("lb://scheduling-service"))
                .route(p -> p.path("/resource-allocation-service/**")
                        .uri("lb://resource-allocation-service"))
                .route(p -> p.path("/user-recommendation-service/**")
                        .uri("lb://user-recommendation-service"))
                .route(p -> p.path("/auth-service/**")
                        .uri("lb://auth-service"))
                .build();
    }
}
