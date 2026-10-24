package org.example.model;

public record DeliveryRequest(
        DeliveryPriority priority,
        double maxBudget,
        int maxDeliveryMinutes,
        boolean ecoPreferred
) {
    public DeliveryRequest {
        if (priority == null) {
            throw new IllegalArgumentException("Priority is required");
        }

        if (maxBudget < 0 || maxDeliveryMinutes <= 0) {
            throw new IllegalArgumentException("Invalid delivery limits");
        }
    }
}