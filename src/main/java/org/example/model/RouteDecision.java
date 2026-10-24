package org.example.model;

public record RouteDecision(
        Route route,
        String strategyName,
        double score,
        String explanation
) {
}