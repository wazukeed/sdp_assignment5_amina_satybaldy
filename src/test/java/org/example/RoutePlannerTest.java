package org.example;

import org.example.data.DemoData;
import org.example.model.*;
import org.example.service.RoutePlanner;
import org.example.strategy.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RoutePlannerTest {

    private final List<Route> routes = DemoData.createRoutes();

    private final DeliveryRequest request =
            new DeliveryRequest(DeliveryPriority.STANDARD, 5000, 60, false);

    @Test
    void runtimeSwitchingChangesRoute() {
        RoutePlanner planner = new RoutePlanner(new FastestRouteStrategy());

        assertEquals("R3", planner.plan(routes, request).id());

        planner.setStrategy(new CheapestRouteStrategy());
        assertEquals("R6", planner.plan(routes, request).id());

        planner.setStrategy(new EcoFriendlyRouteStrategy());
        assertEquals("R4", planner.plan(routes, request).id());
    }

    @Test
    void nullStrategyRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new RoutePlanner(null));
    }

    @Test
    void decisionContainsExplanation() {
        RoutePlanner planner = new RoutePlanner(new FastestRouteStrategy());

        RouteDecision decision = planner.planWithExplanation(routes, request);

        assertEquals("R3", decision.route().id());
        assertTrue(decision.explanation().contains("traffic delay"));
        assertEquals(25, decision.score());
    }
}