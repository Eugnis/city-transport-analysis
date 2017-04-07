package com.citytransportanalysis.modeling.entity;


import java.util.Arrays;
import java.util.List;

/**
 * RouteSegment entity representing route.
 */
public class RouteSegment {
    private List<Stop> twoStops;   //exactly two stops
    private double passingTime;
    private double length;

    public RouteSegment(Stop stop1, Stop stop2, double passingTime, double length) {
        this.twoStops = Arrays.asList(stop1, stop2);
        this.passingTime = passingTime;
        this.length = length;
    }

    public List<Stop> getTwoStops() {
        return twoStops;
    }

    public void setTwoStops(Stop stop1, Stop stop2) {
        this.twoStops = Arrays.asList(stop1, stop2);
    }

    public double getPassingTime() {
        return passingTime;
    }

    public void setPassingTime(double passingTime) {
        this.passingTime = passingTime;
    }

    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
    }
}
