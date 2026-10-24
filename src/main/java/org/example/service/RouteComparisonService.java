package org.example.service;

import org.example.exception.NoSuitableRouteException;
import org.example.model.DeliveryRequest;
import org.example.model.Route;
import org.example.model.RouteDecision;
import org.example.strategy.RouteStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RouteComparisonService {

    public List<String> compare(
            List<RouteStrategy> strategies,
            List<Route> routes,
            DeliveryRequest request) {

        if (strategies == null || strategies.isEmpty()) {
            throw new IllegalArgumentException("No strategies provided");
        }

        List<String> results = new ArrayList<>();

        for (RouteStrategy strategy : strategies) {

            if (strategy == null) {
                throw new IllegalArgumentException("Strategy cannot be null");
            }

            try {
                RouteDecision decision = strategy.makeDecision(routes, request);

                String result = decision.strategyName()
                        + " -> " + decision.route().id()
                        + ", score: "
                        + String.format(Locale.US, "%.3f", decision.score())
                        + "\n" + decision.explanation();

                results.add(result);

            } catch (NoSuitableRouteException e) {
                results.add(strategy.getStrategyName()
                        + " -> " + e.getMessage());
            }
        }

        return results;
    }
}