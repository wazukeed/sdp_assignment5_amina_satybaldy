package org.example.data;

import org.example.model.Location;
import org.example.model.Route;

import java.util.List;

public class DemoData {

    public static List<Route> createRoutes() {

        Location warehouse = new Location("Warehouse", 51.13, 71.43);
        Location customer = new Location("Customer", 51.15, 71.47);

        return List.of(
                new Route("R1", warehouse, customer,
                        12, 25, 1500, 2.4, 10, true),

                new Route("R2", warehouse, customer,
                        10, 30, 1300, 1.8, 3, true),

                new Route("R3", warehouse, customer,
                        15, 20, 2100, 3.0, 5, true),

                new Route("R4", warehouse, customer,
                        8, 35, 1400, 1.0, 4, true),

                new Route("R5", warehouse, customer,
                        16, 28, 900, 2.3, 10, true),

                new Route("R6", warehouse, customer,
                        9, 33, 1100, 1.4, 5, true),

                new Route("R7", warehouse, customer,
                        14, 22, 1800, 2.9, 16, true),

                new Route("R8", warehouse, customer,
                        11, 26, 1200, 1.7, 8, false)
        );
    }
}