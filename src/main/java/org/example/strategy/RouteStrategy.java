package org.example.strategy;

import org.example.model.DeliveryRequest;
import org.example.model.Route;
import org.example.model.RouteDecision;

import java.util.List;

public interface RouteStrategy {

    RouteDecision makeDecision(
            List<Route> routes,
            DeliveryRequest request
    );

    default Route selectRoute(
            List<Route> routes,
            DeliveryRequest request
    ) {
        return makeDecision(routes, request).route();
    }

    String getStrategyName();
}