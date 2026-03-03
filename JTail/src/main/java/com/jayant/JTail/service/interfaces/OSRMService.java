package com.jayant.JTail.service.interfaces;

public interface OSRMService {

    double getDistanceKm(double fromLat, double fromLng,
                         double toLat, double toLng);
}