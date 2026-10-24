package org.example.service;

import org.example.model.DeliveryPriority;
import org.example.model.DeliveryRequest;
import org.example.strategy.*;

import java.util.EnumMap;
import java.util.Map;

public class StrategySelector {

    private final Map<DeliveryPriority, RouteStrategy> strategies =
            new EnumMap<>(DeliveryPriority.class);

    public StrategySelector() {
        registerStrategy(DeliveryPriority.URGENT, new FastestRouteStrategy());
        registerStrategy(DeliveryPriority.ECONOMY, new CheapestRouteStrategy());
        registerStrategy(DeliveryPriority.ECO, new EcoFriendlyRouteStrategy());
        registerStrategy(DeliveryPriority.STANDARD,
                new BalancedRouteStrategy(0.4, 0.3, 0.1, 0.2));
    }

    public void registerStrategy(
            DeliveryPriority priority, RouteStrategy strategy) {

        if (priority == null || strategy == null) {
            throw new IllegalArgumentException("Invalid strategy registration");
        }

        strategies.put(priority, strategy);
    }

    public RouteStrategy selectStrategy(DeliveryRequest request) {

        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }

        DeliveryPriority priority = request.priority();

        if (priority == DeliveryPriority.STANDARD && request.ecoPreferred()) {
            priority = DeliveryPriority.ECO;
        }

        RouteStrategy strategy = strategies.get(priority);

        if (strategy == null) {
            throw new IllegalStateException("Strategy not found");
        }

        return strategy;
    }
}