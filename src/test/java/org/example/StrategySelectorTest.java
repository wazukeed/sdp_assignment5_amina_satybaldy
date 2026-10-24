package org.example;

import org.example.model.*;
import org.example.service.StrategySelector;
import org.example.strategy.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StrategySelectorTest {

    @Test
    void selectsStrategyByPriority() {
        StrategySelector selector = new StrategySelector();

        assertInstanceOf(FastestRouteStrategy.class,
                selector.selectStrategy(new DeliveryRequest(
                        DeliveryPriority.URGENT, 5000, 40, false)));

        assertInstanceOf(CheapestRouteStrategy.class,
                selector.selectStrategy(new DeliveryRequest(
                        DeliveryPriority.ECONOMY, 5000, 60, false)));

        assertInstanceOf(EcoFriendlyRouteStrategy.class,
                selector.selectStrategy(new DeliveryRequest(
                        DeliveryPriority.ECO, 5000, 60, true)));

        assertInstanceOf(BalancedRouteStrategy.class,
                selector.selectStrategy(new DeliveryRequest(
                        DeliveryPriority.STANDARD, 5000, 60, false)));
    }

    @Test
    void ecoPreferenceChangesStandardSelection() {
        StrategySelector selector = new StrategySelector();

        RouteStrategy strategy = selector.selectStrategy(
                new DeliveryRequest(
                        DeliveryPriority.STANDARD, 5000, 60, true));

        assertInstanceOf(EcoFriendlyRouteStrategy.class, strategy);
    }

    @Test
    void supportsStrategyRegistration() {
        StrategySelector selector = new StrategySelector();

        selector.registerStrategy(
                DeliveryPriority.STANDARD, new ShortestRouteStrategy());

        RouteStrategy strategy = selector.selectStrategy(
                new DeliveryRequest(
                        DeliveryPriority.STANDARD, 5000, 60, false));

        assertInstanceOf(ShortestRouteStrategy.class, strategy);
    }
}