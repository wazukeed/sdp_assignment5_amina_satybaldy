package org.example.service;

import org.example.model.DeliveryPriority;
import org.example.model.DeliveryRequest;
import org.example.model.Route;

public class RouteValidator {

    public static double calculateTotalCost(
            Route route, DeliveryRequest request) {

        double distanceCost = route.distanceKm() * 100;
        double priorityFee = 0;

        if (request.priority() == DeliveryPriority.URGENT) {
            priorityFee = 500;
        }

        return route.baseCost() + distanceCost + priorityFee;
    }

    public static int calculateTotalTime(Route route) {
        return route.estimatedMinutes() + route.trafficDelayMinutes();
    }

    public static String getRejectionReason(
            Route route, DeliveryRequest request) {

        if (!route.available()) {
            return "Route is unavailable";
        }

        if (calculateTotalCost(route, request) > request.maxBudget()) {
            return "Budget exceeded";
        }

        if (calculateTotalTime(route) > request.maxDeliveryMinutes()) {
            return "Deadline exceeded";
        }

        return null;
    }

    public static boolean isValid(
            Route route, DeliveryRequest request) {

        return getRejectionReason(route, request) == null;
    }
}