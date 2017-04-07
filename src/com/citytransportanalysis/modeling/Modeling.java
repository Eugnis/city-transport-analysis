package com.citytransportanalysis.modeling;

import com.citytransportanalysis.modeling.entity.RouteSegment;
import com.citytransportanalysis.modeling.entity.Stop;
import com.citytransportanalysis.modeling.entity.Transport;

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

    public void Launch() {
        System.out.println("Modeling started...");
        //int step = 0;
        //System.out.printf("Поточний крок %d\n", step);


        eventsLog = new ArrayList<>();

        Movement(routeData(), transportData(), LocalTime.parse("06:00"), LocalTime.parse("22:00"));
        //Collections.sort(eventsLog, Comparator.comparing(Event::getTime));

        for (Event event: eventsLog){
            System.out.printf("[%s] %s", event.getTime(), event.getDescription());
        }


        System.out.println("Modeling finished.");
    }

    private void Movement(LinkedList<RouteSegment> routeSegments, List<Transport> transportList, LocalTime startTime, LocalTime endTime) {
        double moveTime = 0;
        double length = 0;

        long minutesPeriod = 20;                            //период подхода нового транспорта в минутах

        LocalTime currentTime = startTime;
        LocalTime lastEndTime = startTime;

        boolean timeIsOver = false;

        while(!timeIsOver && lastEndTime.isBefore(endTime)) {       //пока время движения в заданных рамках

            for (Transport transport : transportList) {
                transport.setStartTime(currentTime);
                currentTime = currentTime.plusMinutes(minutesPeriod);

                //System.out.printf("Початок руху %s %d по маршруту №%s о %s\n", transport.getTransportType().name(), transport.getId(), transport.getRouteNumber(), transport.getStartTime());
                eventsLog.add(new Event(transport.getCurrentTime(), String.format("Початок руху %s %d по маршруту №%s о %s\n", transport.getTransportType().name(), transport.getId(), transport.getRouteNumber(), transport.getStartTime())));

                for (RouteSegment routeSegment : routeSegments) {
                    Stop stop1 = routeSegment.getTwoStops().get(0);
                    Stop stop2 = routeSegment.getTwoStops().get(1);

                    if (routeSegment == routeSegments.getFirst()) {
                        transport.addMoveTime(stop1.getWaitTime());
                        stop1.SettingInTransport(transport.getCurrentTime(), transport.getPassengers(), transport.getFreePlaces());
                        //System.out.printf("Посадка. Зайшло %d чел, залишилось %d місць. Не зайшло %d чел\n", transport.getOccupiedPlaces(), transport.getFreePlaces(), stop1.getPassengers().size());
                        transport.addToAllPassengersCount(stop1.getSittedPassengers());
                        eventsLog.add(new Event(transport.getCurrentTime(), String.format("Посадка. Зайшло %d чел, зайнято %d/%d місць. Не зайшло %d чел\n", stop1.getSittedPassengers(), transport.getOccupiedPlaces(), transport.getTotalPlaces(), stop1.getPassengers().size())));
                        //System.out.printf("%s %d по маршруту №%s зупинився на зупинці \"%s\" %s секунд\n", transport.getTransportType().name(), transport.getId(), transport.getRouteNumber(), stop1.getName(), stop1.getWaitTime());
                        eventsLog.add(new Event(transport.getCurrentTime(), String.format("%s %d по маршруту №%s зупинився на зупинці \"%s\" %s секунд\n", transport.getTransportType().name(), transport.getId(), transport.getRouteNumber(), stop1.getName(), stop1.getWaitTime())));
                    }
                    //TODO работает вроде, но надо сделать норм

                    transport.addMoveTime(routeSegment.getPassingTime());
                    //System.out.printf("%s %d по маршруту №%s їде між зупинками \"%s\" і \"%s\" %s секунд\n", transport.getTransportType().name(), transport.getId(), transport.getRouteNumber(), stop1.getName(), stop2.getName(), routeSegment.getPassingTime());
                    eventsLog.add(new Event(transport.getCurrentTime(), String.format("%s %d по маршруту №%s їде між зупинками \"%s\" і \"%s\" %s секунд\n", transport.getTransportType().name(), transport.getId(), transport.getRouteNumber(), stop1.getName(), stop2.getName(), routeSegment.getPassingTime())));

                    if (routeSegment == routeSegments.getLast()) {
                        transport.addMoveTime(stop2.getWaitTime());
                        //System.out.printf("%s %d по маршруту №%s зупинився на зупинці \"%s\" %s секунд\n", transport.getTransportType().name(), transport.getId(), transport.getRouteNumber(), stop2.getName(), stop2.getWaitTime());
                        eventsLog.add(new Event(transport.getCurrentTime(), String.format("%s %d по маршруту №%s зупинився на зупинці \"%s\" %s секунд\n", transport.getTransportType().name(), transport.getId(), transport.getRouteNumber(), stop2.getName(), stop2.getWaitTime())));
                        transport.getPassengers().clear();
                        //System.out.printf("Висадка. Вийшли всі, залишилось %d чел на %d місць\n", transport.getOccupiedPlaces(), transport.getTotalPlaces());
                        eventsLog.add(new Event(transport.getCurrentTime(), String.format("Висадка. Вийшли всі, залишилось %d чел на %d місць\n", transport.getOccupiedPlaces(), transport.getTotalPlaces())));

                    }
                    else{
                        transport.addMoveTime(stop2.getWaitTime());
                        //System.out.printf("%s %d по маршруту №%s зупинився на зупинці \"%s\" %s секунд\n", transport.getTransportType().name(), transport.getId(), transport.getRouteNumber(), stop2.getName(), stop2.getWaitTime());
                        eventsLog.add(new Event(transport.getCurrentTime(), String.format("%s %d по маршруту №%s зупинився на зупинці \"%s\" %s секунд\n", transport.getTransportType().name(), transport.getId(), transport.getRouteNumber(), stop2.getName(), stop2.getWaitTime())));
                        stop2.GettingOutFromTransport(transport.getCurrentTime(), transport.getPassengers());
                        //System.out.printf("Висадка. Вийшло %d чел, залишилось %d чел на %d місць\n", exittedCount, transport.getOccupiedPlaces(), transport.getTotalPlaces());
                        eventsLog.add(new Event(transport.getCurrentTime(), String.format("Висадка. Вийшло %d чел, зайнято %d/%d місць.\n", stop2.getGettedOutPassengers(), transport.getOccupiedPlaces(), transport.getTotalPlaces())));
                        stop2.SettingInTransport(transport.getCurrentTime(), transport.getPassengers(), transport.getFreePlaces());
                        transport.addToAllPassengersCount(stop2.getSittedPassengers());
                        //System.out.printf("Посадка. Зайшло %d чел, залишилось %d місць. Не зайшло %d чел\n", transport.getOccupiedPlaces(), transport.getFreePlaces(), stop2.getPassengers().size());
                        eventsLog.add(new Event(transport.getCurrentTime(), String.format("Посадка. Зайшло %d чел, зайнято %d/%d місць. Не зайшло %d чел\n", stop2.getSittedPassengers(), transport.getOccupiedPlaces(), transport.getTotalPlaces(), stop2.getPassengers().size())));

                    }

                    length = routeSegment.getLength();
                    moveTime = transport.getMoveTime();
                }
                lastEndTime = transport.getStartTime().plusSeconds((long) transport.getMoveTime());

                transport.setEndTime(lastEndTime);
                //System.out.printf("%s %d по маршруту №%s пройшов за %s секунд, кінець маршруту о %s\n", transport.getTransportType().name(), transport.getId(), transport.getRouteNumber(), transport.getMoveTime(), lastEndTime);
                eventsLog.add(new Event(transport.getCurrentTime(), String.format("%s %d по маршруту №%s пройшов за %s секунд, кінець маршруту о %s\n", transport.getTransportType().name(), transport.getId(), transport.getRouteNumber(), transport.getMoveTime(), lastEndTime)));
                transport.cleanMoveTime();
                transport.addTripCount();

                if(lastEndTime.isAfter(endTime)) {
                    timeIsOver = true;
                    break;
                }
            }

        }
        System.out.printf("Шлях %s метрів пройдений за %s секунд\n", length, moveTime);
        for (Transport transport : transportList) {
            System.out.printf("%s %d по маршруту №%s проходив шлях %d разів і перевіз %d чел\n", transport.getTransportType().name(), transport.getId(), transport.getRouteNumber(), transport.getTripCount(), transport.getAllPassengersCount());
        }
    }

    //private void

    private List<Transport> transportData() {
        List<Transport> transportList = new ArrayList<>();
        //LocalTime startTime = LocalTime.parse("06:00");
        //long minutesPeriod = 10;

        for (int i = 1; i <= 4; i++) {
            Transport marshrutka = new Transport();
            marshrutka.setId(i);
            marshrutka.setRouteNumber("48");
            marshrutka.setSeatPlaces(20);
            marshrutka.setStandPlaces(15);
            marshrutka.setStatus(Transport.Status.OnStop);
            marshrutka.setTransportType(Transport.Type.Microbus);
            marshrutka.setPassengers(new ArrayList<>());
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

    private LinkedList<RouteSegment> routeData() {

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

    public class Event{
        private LocalTime time;
        private String description;

        public Event(LocalTime time, String description) {
            this.time = time;
            this.description = description;
        }

        public LocalTime getTime() {
            return time;
        }

        public String getDescription() {
            return description;
        }
    }

}

