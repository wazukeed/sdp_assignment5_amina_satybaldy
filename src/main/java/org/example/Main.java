package org.example;

import org.example.data.DemoData;
import org.example.model.*;
import org.example.service.*;
import org.example.strategy.*;

import java.util.List;

public class Main {

    public static void main(String[] args) {

        List<Route> routes = DemoData.createRoutes();

        DeliveryRequest request = new DeliveryRequest(
                DeliveryPriority.STANDARD, 5000, 60, false
        );

        RoutePlanner planner = new RoutePlanner(new FastestRouteStrategy());

        System.out.println("=== RUNTIME STRATEGY SWITCHING ===");

        printDecision(planner.planWithExplanation(routes, request));

        planner.setStrategy(new CheapestRouteStrategy());
        printDecision(planner.planWithExplanation(routes, request));

        planner.setStrategy(new EcoFriendlyRouteStrategy());
        printDecision(planner.planWithExplanation(routes, request));

        planner.setStrategy(new BalancedRouteStrategy(0.4, 0.3, 0.1, 0.2));
        printDecision(planner.planWithExplanation(routes, request));

        System.out.println("\n=== AUTOMATIC STRATEGY SELECTION ===");

        StrategySelector selector = new StrategySelector();

        List<DeliveryRequest> requests = List.of(
                new DeliveryRequest(DeliveryPriority.URGENT, 5000, 35, false),
                new DeliveryRequest(DeliveryPriority.ECONOMY, 3000, 60, false),
                new DeliveryRequest(DeliveryPriority.ECO, 5000, 60, true),
                new DeliveryRequest(DeliveryPriority.STANDARD, 5000, 60, false)
        );

        for (DeliveryRequest delivery : requests) {
            planner.setStrategy(selector.selectStrategy(delivery));
            System.out.println("\nPriority: " + delivery.priority());
            printDecision(planner.planWithExplanation(routes, delivery));
        }

        System.out.println("\n=== STRATEGY COMPARISON ===");

        List<RouteStrategy> strategies = List.of(
                new FastestRouteStrategy(),
                new CheapestRouteStrategy(),
                new EcoFriendlyRouteStrategy(),
                new BalancedRouteStrategy(0.4, 0.3, 0.1, 0.2)
        );

        RouteComparisonService comparison = new RouteComparisonService();

        for (String result : comparison.compare(strategies, routes, request)) {
            System.out.println(result);
            System.out.println();
        }
    }

    private static void printDecision(RouteDecision decision) {
        System.out.println("\nStrategy: " + decision.strategyName());
        System.out.println("Selected: " + decision.route().id());
        System.out.println("Reason: " + decision.explanation());
    }
}