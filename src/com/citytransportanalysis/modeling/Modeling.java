package com.citytransportanalysis.modeling;

import com.citytransportanalysis.modeling.entity.RouteSegment;
import com.citytransportanalysis.modeling.entity.Stop;
import com.citytransportanalysis.modeling.entity.Transport;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Modeling class for modeling all that shit.
 */
public class Modeling {
    public List<Event> eventsLog;
    public Modeling() {
        System.out.println("Modeling init.");
    }

    public void Launch(LinkedList<RouteSegment> routeSegments, LinkedList<Transport> transportList, LocalTime startTime, LocalTime endTime) {
        System.out.println("Modeling started...");
        eventsLog = new ArrayList<>();

        Movement(routeSegments, transportList, startTime, endTime);

        System.out.println("Modeling finished.");
    }

    private void Movement(LinkedList<RouteSegment> routeSegments, LinkedList<Transport> transportList, LocalTime startTime, LocalTime endTime) {
        double moveTime = 0;
        double length = 0;

        long minutesPeriod = 10;                            //период подхода нового транспорта в минутах

        //LocalTime currentTime = startTime;
        LocalTime lastEndTime = startTime;

        boolean timeIsOver = false;

        while(!timeIsOver && lastEndTime.isBefore(endTime)) {       //пока время движения в заданных рамках
            ListIterator<Transport> transportListIterator = transportList.listIterator(0);
            while (transportListIterator.hasNext()){
                Transport transport = transportListIterator.next();
                System.out.printf("Id:%s Time:%s\n", transport.getId(), transport.getStartTime());
                eventsLog.add(new Event(transport.getCurrentTime(), transport, null, null, Event.Type.RouteStart));
                for (RouteSegment routeSegment : routeSegments) {
                    Stop stop1 = routeSegment.getTwoStops().get(0);
                    Stop stop2 = routeSegment.getTwoStops().get(1);

                    if (routeSegment == routeSegments.getFirst()) {
                        transport.addMoveTime(stop1.getWaitTime());
                        stop1.SettingInTransport(transport);
                        transport.addToAllPassengersCount(stop1.getSittedPassengers());
                        eventsLog.add(new Event(transport.getCurrentTime(), transport, routeSegment, stop1, Event.Type.OnStop));
                        eventsLog.add(new Event(transport.getCurrentTime(), transport, routeSegment, stop1, Event.Type.SittingPassenger));
                    }

                    transport.addMoveTime(routeSegment.getPassingTime());
                    eventsLog.add(new Event(transport.getCurrentTime(), transport, routeSegment, null, Event.Type.OnWay));

                    if (routeSegment == routeSegments.getLast()) {
                        transport.addMoveTime(stop2.getWaitTime());
                        stop2.GettingOutFromTransport(transport);
                        eventsLog.add(new Event(transport.getCurrentTime(), transport, routeSegment, stop2, Event.Type.OnStop));
                        eventsLog.add(new Event(transport.getCurrentTime(), transport, routeSegment, stop2, Event.Type.GettingOutPassenger));
                    }
                    else{
                        transport.addMoveTime(stop2.getWaitTime());
                        stop2.GettingOutFromTransport(transport);
                        eventsLog.add(new Event(transport.getCurrentTime(), transport, routeSegment, stop2, Event.Type.OnStop));
                        eventsLog.add(new Event(transport.getCurrentTime(), transport, routeSegment, stop2, Event.Type.GettingOutPassenger));
                        stop2.SettingInTransport(transport);
                        transport.addToAllPassengersCount(stop2.getSittedPassengers());
                        eventsLog.add(new Event(transport.getCurrentTime(), transport, routeSegment, stop2, Event.Type.SittingPassenger));
                    }

                    length = routeSegment.getLength();
                    moveTime = transport.getMoveTime();
                }
                lastEndTime = transport.getCurrentTime();

                transport.setEndTime(lastEndTime);
                eventsLog.add(new Event(transport.getCurrentTime(), transport, null, null, Event.Type.RouteFinish));
                transport.cleanMoveTime();
                transport.addTripCount();

                LocalTime newStartTime;

                if(transportListIterator.previousIndex()!=0){
                    Transport previousTransport = transportList.get(transportListIterator.previousIndex()-1);
                    newStartTime = previousTransport.getStartTime().plusMinutes(minutesPeriod);
                }
                else{
                    newStartTime = transportList.getLast().getStartTime().plusMinutes(minutesPeriod);
                }
                transport.setStartTime(newStartTime);

                if(lastEndTime.isAfter(endTime)) {
                    timeIsOver = true;
                    break;
                }
            }

        }
        System.out.printf("Шлях %s метрів пройдений за %s секунд\n", length, moveTime);
        for (Transport transport : transportList) {
            System.out.printf("%s %d по маршруту №%s проходив шлях %d разів і перевіз %d чел\n",
                    transport.getTransportType().name(), transport.getId(), transport.getRouteNumber(), transport.getTripCount(), transport.getAllPassengersCount());
        }
    }

    //private void

    public LinkedList<Transport> transportData(int count, LocalTime startTime, long minutesPeriod) {
        LinkedList<Transport> transportList = new LinkedList<>();
        LocalTime MStartTime = startTime;
        //long minutesPeriod = 10;

        for (int i = 1; i <= count; i++) {
            Transport marshrutka = new Transport();
            marshrutka.setId(i);
            marshrutka.setRouteNumber("48");
            marshrutka.setSeatPlaces(22);
            marshrutka.setStandPlaces(21);
            marshrutka.setStatus(Transport.Status.OnStop);
            marshrutka.setTransportType(Transport.Type.Microbus);
            marshrutka.setPassengers(new ArrayList<>());
            marshrutka.setStartTime(MStartTime);
            MStartTime = MStartTime.plusMinutes(minutesPeriod);
            //marshrutka.setStartTime(startTime);
            //startTime = startTime.plusMinutes(minutesPeriod);
            //System.out.println(marshrutka.getStartTime());

            transportList.add(marshrutka);
        }
        return transportList;
    }

    private Map<LocalTime, Double> passengerComingTimeGen(){
        return new HashMap<LocalTime, Double>() {
            {
                put(LocalTime.parse("06:00"), ThreadLocalRandom.current().nextDouble(100.0, 300.0));
                put(LocalTime.parse("07:00"), ThreadLocalRandom.current().nextDouble(100.0, 300.0));
                put(LocalTime.parse("08:00"), ThreadLocalRandom.current().nextDouble(100.0, 300.0));
                put(LocalTime.parse("09:00"), ThreadLocalRandom.current().nextDouble(100.0, 300.0));
                put(LocalTime.parse("10:00"), ThreadLocalRandom.current().nextDouble(100.0, 300.0));
                put(LocalTime.parse("11:00"), ThreadLocalRandom.current().nextDouble(100.0, 300.0));
                put(LocalTime.parse("12:00"), ThreadLocalRandom.current().nextDouble(100.0, 300.0));
                put(LocalTime.parse("13:00"), ThreadLocalRandom.current().nextDouble(100.0, 300.0));
                put(LocalTime.parse("14:00"), ThreadLocalRandom.current().nextDouble(100.0, 300.0));
                put(LocalTime.parse("15:00"), ThreadLocalRandom.current().nextDouble(100.0, 300.0));
                put(LocalTime.parse("16:00"), ThreadLocalRandom.current().nextDouble(100.0, 300.0));
                put(LocalTime.parse("17:00"), ThreadLocalRandom.current().nextDouble(100.0, 300.0));
                put(LocalTime.parse("18:00"), ThreadLocalRandom.current().nextDouble(100.0, 300.0));
                put(LocalTime.parse("19:00"), ThreadLocalRandom.current().nextDouble(100.0, 300.0));
                put(LocalTime.parse("20:00"), ThreadLocalRandom.current().nextDouble(100.0, 300.0));
                put(LocalTime.parse("21:00"), ThreadLocalRandom.current().nextDouble(100.0, 300.0));
                put(LocalTime.parse("22:00"), ThreadLocalRandom.current().nextDouble(100.0, 300.0));
            }
        };
    }

    private Map<LocalTime, Double> passengerExitProbabilityGen(){
        return new HashMap<LocalTime, Double>() {
            {
                put(LocalTime.parse("06:00"), ThreadLocalRandom.current().nextDouble(0.12, 0.30));
                put(LocalTime.parse("07:00"), ThreadLocalRandom.current().nextDouble(0.12, 0.30));
                put(LocalTime.parse("08:00"), ThreadLocalRandom.current().nextDouble(0.12, 0.30));
                put(LocalTime.parse("09:00"), ThreadLocalRandom.current().nextDouble(0.12, 0.30));
                put(LocalTime.parse("10:00"), ThreadLocalRandom.current().nextDouble(0.12, 0.30));
                put(LocalTime.parse("11:00"), ThreadLocalRandom.current().nextDouble(0.12, 0.30));
                put(LocalTime.parse("12:00"), ThreadLocalRandom.current().nextDouble(0.12, 0.30));
                put(LocalTime.parse("13:00"), ThreadLocalRandom.current().nextDouble(0.12, 0.30));
                put(LocalTime.parse("14:00"), ThreadLocalRandom.current().nextDouble(0.12, 0.30));
                put(LocalTime.parse("15:00"), ThreadLocalRandom.current().nextDouble(0.12, 0.30));
                put(LocalTime.parse("16:00"), ThreadLocalRandom.current().nextDouble(0.12, 0.30));
                put(LocalTime.parse("17:00"), ThreadLocalRandom.current().nextDouble(0.12, 0.30));
                put(LocalTime.parse("18:00"), ThreadLocalRandom.current().nextDouble(0.12, 0.30));
                put(LocalTime.parse("19:00"), ThreadLocalRandom.current().nextDouble(0.12, 0.30));
                put(LocalTime.parse("20:00"), ThreadLocalRandom.current().nextDouble(0.12, 0.30));
                put(LocalTime.parse("21:00"), ThreadLocalRandom.current().nextDouble(0.12, 0.30));
                put(LocalTime.parse("22:00"), ThreadLocalRandom.current().nextDouble(0.12, 0.30));
            }
        };
    }

    public LinkedList<RouteSegment> routeData() {

        Map<LocalTime, Double> passengerComingTimeLast = new HashMap<LocalTime, Double>() {
            {
                put(LocalTime.parse("06:00"), 0.0);
                put(LocalTime.parse("07:00"), 0.0);
                put(LocalTime.parse("08:00"), 0.0);
                put(LocalTime.parse("09:00"), 0.0);
                put(LocalTime.parse("10:00"), 0.0);
                put(LocalTime.parse("11:00"), 0.0);
                put(LocalTime.parse("12:00"), 0.0);
                put(LocalTime.parse("13:00"), 0.0);
                put(LocalTime.parse("14:00"), 0.0);
                put(LocalTime.parse("15:00"), 0.0);
                put(LocalTime.parse("16:00"), 0.0);
                put(LocalTime.parse("17:00"), 0.0);
                put(LocalTime.parse("18:00"), 0.0);
                put(LocalTime.parse("19:00"), 0.0);
                put(LocalTime.parse("20:00"), 0.0);
                put(LocalTime.parse("21:00"), 0.0);
                put(LocalTime.parse("22:00"), 0.0);
            }
        };
        Map<LocalTime, Double> passengerExitProbability = new HashMap<LocalTime, Double>() {
            {
                put(LocalTime.parse("06:00"), 0.0);
                put(LocalTime.parse("07:00"), 0.0);
                put(LocalTime.parse("08:00"), 0.0);
                put(LocalTime.parse("09:00"), 0.0);
                put(LocalTime.parse("10:00"), 0.0);
                put(LocalTime.parse("11:00"), 0.0);
                put(LocalTime.parse("12:00"), 0.0);
                put(LocalTime.parse("13:00"), 0.0);
                put(LocalTime.parse("14:00"), 0.0);
                put(LocalTime.parse("15:00"), 0.0);
                put(LocalTime.parse("16:00"), 0.0);
                put(LocalTime.parse("17:00"), 0.0);
                put(LocalTime.parse("18:00"), 0.0);
                put(LocalTime.parse("19:00"), 0.0);
                put(LocalTime.parse("20:00"), 0.0);
                put(LocalTime.parse("21:00"), 0.0);
                put(LocalTime.parse("22:00"), 0.0);
            }
        };
        Map<LocalTime, Double> passengerExitProbabilityLast = new HashMap<LocalTime, Double>() {
            {
                put(LocalTime.parse("06:00"), 1.0);
                put(LocalTime.parse("07:00"), 1.0);
                put(LocalTime.parse("08:00"), 1.0);
                put(LocalTime.parse("09:00"), 1.0);
                put(LocalTime.parse("10:00"), 1.0);
                put(LocalTime.parse("11:00"), 1.0);
                put(LocalTime.parse("12:00"), 1.0);
                put(LocalTime.parse("13:00"), 1.0);
                put(LocalTime.parse("14:00"), 1.0);
                put(LocalTime.parse("15:00"), 1.0);
                put(LocalTime.parse("16:00"), 1.0);
                put(LocalTime.parse("17:00"), 1.0);
                put(LocalTime.parse("18:00"), 1.0);
                put(LocalTime.parse("19:00"), 1.0);
                put(LocalTime.parse("20:00"), 1.0);
                put(LocalTime.parse("21:00"), 1.0);
                put(LocalTime.parse("22:00"), 1.0);
            }
        };
        Stop stop1 = new Stop("Станція метро \"Лівобережна\"", passengerComingTimeGen(), passengerExitProbability, 30);
        Stop stop2 = new Stop("Вулиця Ентузіастів", passengerComingTimeGen(), passengerExitProbabilityGen(), 20);
        Stop stop3 = new Stop("Пішохідний міст", passengerComingTimeGen(), passengerExitProbabilityGen(), 20);
        Stop stop4 = new Stop("Бібліотека", passengerComingTimeGen(), passengerExitProbabilityGen(), 20);
        Stop stop5 = new Stop("Бювет", passengerComingTimeGen(), passengerExitProbabilityGen(), 20);
        Stop stop6 = new Stop("Пошта №154", passengerComingTimeGen(), passengerExitProbabilityGen(), 20);
        Stop stop7 = new Stop("Бульвар Олексія Давидова", passengerComingTimeGen(), passengerExitProbabilityGen(), 20);
        Stop stop8 = new Stop("Готель \"Славутич\"", passengerComingTimeGen(), passengerExitProbabilityGen(), 20);
        Stop stop9 = new Stop("Бульвар Олексія Давидова", passengerComingTimeGen(), passengerExitProbabilityGen(), 20);
        Stop stop10 = new Stop("Пошта №154", passengerComingTimeGen(), passengerExitProbabilityGen(), 20);
        Stop stop11 = new Stop("Залізнична платформа Київ-Русанівка", passengerComingTimeGen(), passengerExitProbabilityGen(), 20);
        Stop stop12 = new Stop("Бібліотека", passengerComingTimeGen(), passengerExitProbabilityGen(), 20);
        Stop stop13 = new Stop("Пішохідний міст", passengerComingTimeGen(), passengerExitProbabilityGen(), 20);
        Stop stop14 = new Stop("Вулиця Ентузіастів", passengerComingTimeGen(), passengerExitProbabilityGen(), 20);
        Stop stop15 = new Stop("Вулиця Раїси Окіпної", passengerComingTimeGen(), passengerExitProbabilityGen(), 20);
        Stop stop16 = new Stop("Станція метро \"Лівобережна\"", passengerComingTimeLast, passengerExitProbabilityLast, 30);


        LinkedList<RouteSegment> routeSegments = new LinkedList<>();
        routeSegments.add(new RouteSegment(stop1, stop2, 200, 1000));
        routeSegments.add(new RouteSegment(stop2, stop3, 100, 500));
        routeSegments.add(new RouteSegment(stop3, stop4, 100, 500));
        routeSegments.add(new RouteSegment(stop4, stop5, 60, 300));
        routeSegments.add(new RouteSegment(stop5, stop6, 60, 300));
        routeSegments.add(new RouteSegment(stop6, stop7, 120, 600));
        routeSegments.add(new RouteSegment(stop7, stop8, 80, 400));
        routeSegments.add(new RouteSegment(stop8, stop9, 80, 400));
        routeSegments.add(new RouteSegment(stop9, stop10, 80, 400));
        routeSegments.add(new RouteSegment(stop10, stop11, 80, 400));
        routeSegments.add(new RouteSegment(stop11, stop12, 60, 300));
        routeSegments.add(new RouteSegment(stop12, stop13, 120, 600));
        routeSegments.add(new RouteSegment(stop13, stop14, 100, 500));
        routeSegments.add(new RouteSegment(stop14, stop15, 100, 500));
        routeSegments.add(new RouteSegment(stop15, stop16, 120, 600));

        return routeSegments;
    }

    /**
     * Класс для вывода событий
     */

    public static class Event {
        private LocalTime time;
        //private String description;
        private Stop stop;
        private Transport transport;
        private RouteSegment routeSegment;
        private Type type;

        private SimpleStringProperty textTime;
        private SimpleStringProperty description;
        private SimpleIntegerProperty transportId;

        public enum Type {
            RouteStart, RouteFinish, OnStop, OnWay, SittingPassenger, GettingOutPassenger
        }

        Event(LocalTime time, Transport transport, RouteSegment routeSegment, Stop stop, Type type) {
            this.time = time;
            this.transport = transport;
            this.routeSegment = routeSegment;
            this.stop = stop;
            this.type = type;


            this.textTime = new SimpleStringProperty(time.toString());
            this.transportId = new SimpleIntegerProperty(transport.getId());
            this.description = new SimpleStringProperty(eventDescription(type));
        }

        private String eventDescription(Type eventType) {
            switch (eventType) {
                case RouteStart:
                    return String.format("Початок руху о %s\n", transport.getStartTime());
                case RouteFinish:
                    return String.format("Кінець руху о %s. Пройдено за %s секунд\n", transport.getCurrentTime(), transport.getMoveTime());
                case OnStop:
                    return String.format("Зупинка на зупинці \"%s\" %s секунд\n", stop.getName(), stop.getWaitTime());
                case OnWay:
                    return String.format("Їде між зупинками \"%s\" і \"%s\" %s секунд\n", routeSegment.getTwoStops().get(0).getName(), routeSegment.getTwoStops().get(1).getName(), routeSegment.getPassingTime());
                case SittingPassenger:
                    return String.format("Посадка. Зайшло %d чел, зайнято %d/%d місць. Не зайшло %d чел\n", stop.getSittedPassengers(), transport.getOccupiedPlaces(), transport.getTotalPlaces(), stop.getPassengers().size());
                case GettingOutPassenger:
                    return String.format("Висадка. Вийшло %d чел, зайнято %d/%d місць.\n", stop.getGettedOutPassengers(), transport.getOccupiedPlaces(), transport.getTotalPlaces());
                default:
                    return "NONE";
            }
        }

        public Stop getStop() {
            return stop;
        }

        public LocalTime getTime() {
            return time;
        }

        public String getDescription() {
            return description.get();
        }

        public int getTransportId() {
            return transportId.get();
        }

        public SimpleStringProperty textTimeProperty() {
            return textTime;
        }

        public void setTextTime(String textTime) {
            this.textTime.set(textTime);
        }

        public SimpleStringProperty descriptionProperty() {
            return description;
        }

        public void setDescription(String description) {
            this.description.set(description);
        }

        @Override
        public String toString() {
            return (String.format("[%s](Транспорт №%d) %s", textTime.get(), transportId.get(), description.get()));
        }
    }

}

