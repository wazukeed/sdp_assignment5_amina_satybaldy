package org.example.service;

import org.example.model.DeliveryRequest;
import org.example.model.Route;
import org.example.model.RouteDecision;
import org.example.strategy.RouteStrategy;

import java.util.List;

public class RoutePlanner {

    private RouteStrategy strategy;

    public RoutePlanner(RouteStrategy strategy) {
        setStrategy(strategy);
    }

    public void setStrategy(RouteStrategy strategy) {

        if (strategy == null) {
            throw new IllegalArgumentException("Strategy cannot be null");
        }

        this.strategy = strategy;
    }

    public Route plan(
            List<Route> routes, DeliveryRequest request) {

        return strategy.selectRoute(routes, request);
    }

    public RouteDecision planWithExplanation(
            List<Route> routes, DeliveryRequest request) {

        return strategy.makeDecision(routes, request);
    }

    public String getCurrentStrategyName() {
        return strategy.getStrategyName();
    }
}