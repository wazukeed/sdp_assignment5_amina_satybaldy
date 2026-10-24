package org.example.strategy;

import org.example.exception.NoSuitableRouteException;
import org.example.model.DeliveryRequest;
import org.example.model.Route;
import org.example.model.RouteDecision;
import org.example.service.RouteValidator;

import java.util.List;

public class FastestRouteStrategy implements RouteStrategy {

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
        int bestTime = Integer.MAX_VALUE;
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

            int totalTime = RouteValidator.calculateTotalTime(route);

            if (bestRoute == null || totalTime < bestTime) {
                bestRoute = route;
                bestTime = totalTime;
            }
        }

        if (bestRoute == null) {
            throw new NoSuitableRouteException(
                    "No suitable route found. " + rejected
            );
        }

        String explanation =
                "Estimated time: " + bestRoute.estimatedMinutes()
                        + " min, traffic delay: "
                        + bestRoute.trafficDelayMinutes()
                        + " min, total: " + bestTime
                        + " min. " + rejected;

        return new RouteDecision(
                bestRoute,
                getStrategyName(),
                bestTime,
                explanation
        );
    }

    @Override
    public String getStrategyName() {
        return "Fastest Route";
    }
}