package org.example.model;

public record Route(
        String id,
        Location origin,
        Location destination,
        double distanceKm,
        int estimatedMinutes,
        double baseCost,
        double carbonEmission,
        int trafficDelayMinutes,
        boolean available
) {
    public Route {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Route ID cannot be empty");
        }

        if (origin == null || destination == null) {
            throw new IllegalArgumentException("Locations cannot be null");
        }

        if (distanceKm < 0 || estimatedMinutes <= 0 ||
                baseCost < 0 || carbonEmission < 0 ||
                trafficDelayMinutes < 0) {
            throw new IllegalArgumentException("Invalid route data");
        }
    }
}