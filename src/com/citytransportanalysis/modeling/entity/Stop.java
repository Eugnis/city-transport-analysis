package com.citytransportanalysis.modeling.entity;

import java.time.Duration;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Stop entity representing stop.
 */
public class Stop {
    private String name;
    private LinkedList<Passenger> passengers;
    private Map<LocalTime, Double> passengerComingTime;
    private Map<LocalTime, Double> passengerExitProbability;
    private LocalTime localTime;
    private double waitTime;
    private LocalTime lastPasengersGeneration;

    private int sittedPassengers;
    private int gettedOutPassengers;


    public Stop(String name, Map<LocalTime, Double> passengerComingTime, Map<LocalTime, Double> passengerExitProbability, double waitTime) {
        this.name = name;
        this.passengerComingTime = passengerComingTime;
        this.passengerExitProbability = passengerExitProbability;
        this.waitTime = waitTime;
        passengers = new LinkedList<>();

        //this.GeneratePassengers(LocalTime.parse("08:34"));
        //this.GeneratePassengers(LocalTime.parse("09:34"));
    }

    public double getWaitTime() {
        return waitTime;
    }

    public void setWaitTime(double waitTime) {
        this.waitTime = waitTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LinkedList<Passenger> getPassengers() {
        return passengers;
    }

    public void setPassengers(LinkedList<Passenger> passengers) {
        this.passengers = passengers;
    }

    public Map<LocalTime, Double> getPassengerComingTime() {
        return passengerComingTime;
    }

    public void setPassengerComingTime(Map<LocalTime, Double> passengerComingTime) {
        this.passengerComingTime = passengerComingTime;
    }

    public Map<LocalTime, Double> getPassengerExitProbability() {
        return passengerExitProbability;
    }

    public void setPassengerExitProbability(Map<LocalTime, Double> passengerExitProbability) {
        this.passengerExitProbability = passengerExitProbability;
    }

    public LocalTime getLocalTime() {
        return localTime;
    }

    public void setLocalTime(LocalTime localTime) {
        this.localTime = localTime;
    }

    private void GeneratePassengers(LocalTime time){
        Double difference;
        Double comingPeriod;
        LocalTime timeFrom = time.truncatedTo(ChronoUnit.HOURS);
        if (lastPasengersGeneration!=null) {
            difference = (double)Duration.between(lastPasengersGeneration, time).getSeconds();
        }
        else{
            difference = (double)time.getMinute() * 60; //difference in seconds from previous hour to current minutes
        }
        comingPeriod = passengerComingTime.get(timeFrom);
        //System.out.printf("%s\n", comingPeriod);
        for(double i=0; i<=difference; i=i+comingPeriod){
            passengers.add(new Passenger(this));
        }
        //System.out.printf("Added %s passengers\n", passengers.size());
        lastPasengersGeneration = time;


    }

    public List<Passenger> SettingInTransport(Transport transport){
        GeneratePassengers(transport.getCurrentTime());
        int toSit = transport.getFreePlaces();
        int setted = 0;
        while(toSit>0 && passengers.size()>0){
            transport.getPassengers().add(passengers.pollFirst());
            setted++;
            toSit--;
        }
        this.setSittedPassengers(setted);
        return transport.getPassengers();
    }

    public List<Passenger> GettingOutFromTransport(Transport transport){
        Double exitProbability = passengerExitProbability.get(transport.getCurrentTime().truncatedTo(ChronoUnit.HOURS));
        int exitCount = (int) Math.round(transport.getPassengers().size() * exitProbability);
        this.setGettedOutPassengers(exitCount);
        transport.getPassengers().subList(0, exitCount).clear();
        return transport.getPassengers();
    }

    public int getGettedOutPassengers() {
        return gettedOutPassengers;
    }

    private void setGettedOutPassengers(int gettedOutPassengers) {
        this.gettedOutPassengers = gettedOutPassengers;
    }

    public int getSittedPassengers() {
        return sittedPassengers;
    }

    private void setSittedPassengers(int sittedPassengers) {
        this.sittedPassengers = sittedPassengers;
    }
}
