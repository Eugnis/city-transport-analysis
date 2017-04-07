package com.citytransportanalysis.modeling.entity;

/**
 * Passenger entity representing passenger.
 */
public class Passenger {
    private String name;
    private Stop from;
    private Stop to;

    public Passenger(Stop from) {
        this.from = from;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Stop getFrom() {
        return from;
    }

    public void setFrom(Stop from) {
        this.from = from;
    }

    public Stop getTo() {
        return to;
    }

    public void setTo(Stop to) {
        this.to = to;
    }
}
