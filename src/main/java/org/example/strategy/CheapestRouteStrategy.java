package org.example.strategy;

import org.example.exception.NoSuitableRouteException;
import org.example.model.DeliveryRequest;
import org.example.model.Route;
import org.example.model.RouteDecision;
import org.example.service.RouteValidator;

import java.util.List;

public class CheapestRouteStrategy implements RouteStrategy {

    @Override
    public RouteDecision makeDecision(
            List<Route> routes, DeliveryRequest request) {

        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }

        if (routes == null || routes.isEmpty()) {
            throw new NoSuitableRouteException("Route list is empty");
        }

        Route bestRoute = null;
        double lowestCost = Double.MAX_VALUE;
        StringBuilder rejected = new StringBuilder();

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

            double totalCost = RouteValidator.calculateTotalCost(
                    route, request
            );

            if (bestRoute == null || totalCost < lowestCost) {
                bestRoute = route;
                lowestCost = totalCost;
            }
        }

        if (bestRoute == null) {
            throw new NoSuitableRouteException(
                    "No suitable route found. " + rejected
            );
        }

        String explanation =
                "Base cost: " + bestRoute.baseCost()
                        + ", distance: " + bestRoute.distanceKm()
                        + " km, total cost: " + lowestCost
                        + " KZT. " + rejected;

        return new RouteDecision(
                bestRoute,
                getStrategyName(),
                lowestCost,
                explanation
        );
    }

    @Override
    public String getStrategyName() {
        return "Cheapest Route";
    }
}