package org.example;

import org.example.data.DemoData;
import org.example.exception.NoSuitableRouteException;
import org.example.model.*;
import org.example.service.RouteValidator;
import org.example.strategy.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RouteValidationTest {

    private final List<Route> routes = DemoData.createRoutes();

    @Test
    void unavailableRouteRejected() {
        Route route = routes.get(7);

        DeliveryRequest request = new DeliveryRequest(
                DeliveryPriority.STANDARD, 5000, 60, false);

        assertEquals("Route is unavailable",
                RouteValidator.getRejectionReason(route, request));
    }

    @Test
    void budgetRestrictionWorks() {
        Route route = routes.get(5);

        DeliveryRequest request = new DeliveryRequest(
                DeliveryPriority.ECONOMY, 1500, 60, false);

        assertEquals("Budget exceeded",
                RouteValidator.getRejectionReason(route, request));
    }

    @Test
    void deadlineRestrictionWorks() {
        Route route = routes.get(0);

        DeliveryRequest request = new DeliveryRequest(
                DeliveryPriority.STANDARD, 5000, 30, false);

        assertEquals("Deadline exceeded",
                RouteValidator.getRejectionReason(route, request));
    }

    @Test
    void exactBudgetAndDeadlineAccepted() {
        Route route = routes.get(5);

        DeliveryRequest request = new DeliveryRequest(
                DeliveryPriority.STANDARD, 2000, 38, false);

        assertTrue(RouteValidator.isValid(route, request));
    }

    @Test
    void noSuitableRouteThrowsException() {
        DeliveryRequest request = new DeliveryRequest(
                DeliveryPriority.STANDARD, 100, 60, false);

        assertThrows(NoSuitableRouteException.class,
                () -> new CheapestRouteStrategy().selectRoute(routes, request));
    }

    @Test
    void emptyRouteListThrowsException() {
        DeliveryRequest request = new DeliveryRequest(
                DeliveryPriority.STANDARD, 5000, 60, false);

        assertThrows(NoSuitableRouteException.class,
                () -> new FastestRouteStrategy().selectRoute(List.of(), request));
    }

    @Test
    void negativeDistanceRejected() {
        Location location = new Location("Test", 51.1, 71.4);

        assertThrows(IllegalArgumentException.class,
                () -> new Route("Invalid", location, location,
                        -5, 20, 1000, 1, 0, true));
    }

    @Test
    void urgentDeliveryAddsSurcharge() {
        Route route = routes.get(5);

        DeliveryRequest standard = new DeliveryRequest(
                DeliveryPriority.STANDARD, 5000, 60, false);

        DeliveryRequest urgent = new DeliveryRequest(
                DeliveryPriority.URGENT, 5000, 60, false);

        assertEquals(2000, RouteValidator.calculateTotalCost(route, standard));
        assertEquals(2500, RouteValidator.calculateTotalCost(route, urgent));
    }
}