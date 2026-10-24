package org.example;

import org.example.data.DemoData;
import org.example.model.*;
import org.example.service.RouteComparisonService;
import org.example.strategy.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StrategyTest {

    private final List<Route> routes = DemoData.createRoutes();

    private final DeliveryRequest request =
            new DeliveryRequest(DeliveryPriority.STANDARD, 5000, 60, false);

    @Test
    void fastestChoosesR3() {
        Route result = new FastestRouteStrategy().selectRoute(routes, request);
        assertEquals("R3", result.id());
    }

    @Test
    void cheapestChoosesR6() {
        Route result = new CheapestRouteStrategy().selectRoute(routes, request);
        assertEquals("R6", result.id());
    }

    @Test
    void ecoChoosesR4() {
        Route result = new EcoFriendlyRouteStrategy().selectRoute(routes, request);
        assertEquals("R4", result.id());
    }

    @Test
    void balancedChoosesR4() {
        Route result = new BalancedRouteStrategy(0.4, 0.3, 0.1, 0.2)
                .selectRoute(routes, request);

        assertEquals("R4", result.id());
    }

    @Test
    void shortestChoosesR4() {
        Route result = new ShortestRouteStrategy().selectRoute(routes, request);
        assertEquals("R4", result.id());
    }

    @Test
    void fastestConsidersTraffic() {
        Location location = new Location("Test", 51.1, 71.4);

        Route a = new Route("A", location, location,
                10, 20, 1000, 1, 20, true);

        Route b = new Route("B", location, location,
                10, 30, 1000, 1, 0, true);

        Route result = new FastestRouteStrategy()
                .selectRoute(List.of(a, b), request);

        assertEquals("B", result.id());
    }

    @Test
    void invalidBalancedWeightsRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new BalancedRouteStrategy(0.5, 0.5, 0.5, 0.5));
    }

    @Test
    void negativeBalancedWeightRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new BalancedRouteStrategy(-0.1, 0.5, 0.3, 0.3));
    }

    @Test
    void comparisonIncludesTwoStrategies() {
        RouteComparisonService comparison = new RouteComparisonService();

        List<String> results = comparison.compare(
                List.of(new FastestRouteStrategy(), new CheapestRouteStrategy()),
                routes,
                request
        );

        assertEquals(2, results.size());
        assertTrue(results.get(0).contains("R3"));
        assertTrue(results.get(1).contains("R6"));
    }
}