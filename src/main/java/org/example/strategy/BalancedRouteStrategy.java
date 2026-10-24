package org.example.strategy;

import org.example.exception.NoSuitableRouteException;
import org.example.model.DeliveryRequest;
import org.example.model.Route;
import org.example.model.RouteDecision;
import org.example.service.RouteValidator;

import java.util.ArrayList;
import java.util.List;

public class BalancedRouteStrategy implements RouteStrategy {

    private final double timeWeight;
    private final double costWeight;
    private final double distanceWeight;
    private final double emissionWeight;

    public BalancedRouteStrategy(
            double timeWeight,
            double costWeight,
            double distanceWeight,
            double emissionWeight) {

        if (!Double.isFinite(timeWeight) ||
                !Double.isFinite(costWeight) ||
                !Double.isFinite(distanceWeight) ||
                !Double.isFinite(emissionWeight)) {
            throw new IllegalArgumentException("Weights must be finite");
        }

        if (timeWeight < 0 || costWeight < 0 ||
                distanceWeight < 0 || emissionWeight < 0) {
            throw new IllegalArgumentException("Weights cannot be negative");
        }

        double sum = timeWeight + costWeight
                + distanceWeight + emissionWeight;

        if (Math.abs(sum - 1.0) > 0.000001) {
            throw new IllegalArgumentException("Weights must sum to 1.0");
        }

        this.timeWeight = timeWeight;
        this.costWeight = costWeight;
        this.distanceWeight = distanceWeight;
        this.emissionWeight = emissionWeight;
    }

    private double normalize(double value, double max) {
        if (max == 0) {
            return 0;
        }
        return value / max;
    }

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

        double maxTime = 0;
        double maxCost = 0;
        double maxDistance = 0;
        double maxEmission = 0;

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

            maxTime = Math.max(maxTime,
                    RouteValidator.calculateTotalTime(route));

            maxCost = Math.max(maxCost,
                    RouteValidator.calculateTotalCost(route, request));

            maxDistance = Math.max(maxDistance, route.distanceKm());
            maxEmission = Math.max(maxEmission, route.carbonEmission());
        }

        if (validRoutes.isEmpty()) {
            throw new NoSuitableRouteException(
                    "No suitable route found. " + rejected
            );
        }

        Route bestRoute = null;
        double bestScore = Double.MAX_VALUE;

        for (Route route : validRoutes) {

            double timeScore = normalize(
                    RouteValidator.calculateTotalTime(route), maxTime);

            double costScore = normalize(
                    RouteValidator.calculateTotalCost(route, request), maxCost);

            double distanceScore = normalize(
                    route.distanceKm(), maxDistance);

            double emissionScore = normalize(
                    route.carbonEmission(), maxEmission);

            double score = timeScore * timeWeight
                    + costScore * costWeight
                    + distanceScore * distanceWeight
                    + emissionScore * emissionWeight;

            if (bestRoute == null || score < bestScore) {
                bestRoute = route;
                bestScore = score;
            }
        }

        String explanation =
                "Time: " + RouteValidator.calculateTotalTime(bestRoute)
                        + " min, cost: "
                        + RouteValidator.calculateTotalCost(bestRoute, request)
                        + " KZT, distance: " + bestRoute.distanceKm()
                        + " km, CO2: " + bestRoute.carbonEmission()
                        + " kg. Balanced score: " + bestScore
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
        return "Balanced Route";
    }
}