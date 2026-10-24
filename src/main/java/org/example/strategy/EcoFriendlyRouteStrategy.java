package org.example.strategy;

import org.example.exception.NoSuitableRouteException;
import org.example.model.DeliveryRequest;
import org.example.model.Route;
import org.example.model.RouteDecision;
import org.example.service.RouteValidator;

import java.util.ArrayList;
import java.util.List;

public class EcoFriendlyRouteStrategy implements RouteStrategy {

    private static final double EMISSION_WEIGHT = 0.7;
    private static final double DISTANCE_WEIGHT = 0.3;

    @Override
    public RouteDecision makeDecision(
            List<Route> routes, DeliveryRequest request) {

        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }

        if (routes == null || routes.isEmpty()) {
            throw new NoSuitableRouteException("Route list is empty");
        }

        List<Route> validRoutes = new ArrayList<>();
        StringBuilder rejected = new StringBuilder();

        double maxEmission = 0;
        double maxDistance = 0;

        for (Route route : routes) {

            if (route == null) {
                throw new IllegalArgumentException("Route cannot be null");
            }

            String reason = RouteValidator.getRejectionReason(route, request);

            if (reason != null) {
                rejected.append(route.id())
                        .append(": ")
                        .append(reason)
                        .append(". ");
                continue;
            }

            validRoutes.add(route);

            maxEmission = Math.max(maxEmission, route.carbonEmission());
            maxDistance = Math.max(maxDistance, route.distanceKm());
        }

        if (validRoutes.isEmpty()) {
            throw new NoSuitableRouteException(
                    "No suitable route found. " + rejected
            );
        }

        Route bestRoute = null;
        double bestScore = Double.MAX_VALUE;

        for (Route route : validRoutes) {

            double emissionScore = maxEmission == 0 ? 0
                    : route.carbonEmission() / maxEmission;

            double distanceScore = maxDistance == 0 ? 0
                    : route.distanceKm() / maxDistance;

            double score = emissionScore * EMISSION_WEIGHT
                    + distanceScore * DISTANCE_WEIGHT;

            if (bestRoute == null || score < bestScore) {
                bestRoute = route;
                bestScore = score;
            }
        }

        String explanation =
                "CO2: " + bestRoute.carbonEmission()
                        + " kg, distance: " + bestRoute.distanceKm()
                        + " km, eco score: " + bestScore
                        + ". " + rejected;

        return new RouteDecision(
                bestRoute,
                getStrategyName(),
                bestScore,
                explanation
        );
    }

    @Override
    public String getStrategyName() {
        return "Eco-Friendly Route";
    }
}