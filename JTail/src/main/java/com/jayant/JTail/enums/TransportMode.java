package com.jayant.JTail.enums;

// Enum representing different modes of transport for shipping orders, with associated cost rates and distance thresholds. This enum is used in the Order entity to determine the appropriate transport mode based on the distance between the warehouse and the customer's delivery address, which in turn affects the shipping cost calculation.
public enum TransportMode {

    MINI_VAN(3.0, 0, 100),
    TRUCK(2.0, 100, 500),
    AEROPLANE(1.0, 500, Double.MAX_VALUE);

    private final double ratePerKmPerKg;

    private final double minDistanceKm;

    private final double maxDistanceKm;

    TransportMode(double ratePerKmPerKg, double minDistanceKm, double maxDistanceKm) {
        this.ratePerKmPerKg = ratePerKmPerKg;
        this.minDistanceKm  = minDistanceKm;
        this.maxDistanceKm  = maxDistanceKm;
    }

    public double getRatePerKmPerKg() { return ratePerKmPerKg; }
    public double getMinDistanceKm()  { return minDistanceKm; }
    public double getMaxDistanceKm()  { return maxDistanceKm; }

   
    public static TransportMode fromDistance(double distanceKm) {
        for (TransportMode mode : values()) {
            if (distanceKm >= mode.minDistanceKm && distanceKm < mode.maxDistanceKm) {
                return mode;
            }
        }
        return AEROPLANE;
    }
}
