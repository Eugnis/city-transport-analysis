package com.citytransportanalysis;

import com.citytransportanalysis.modeling.entity.RouteSegment;
import com.citytransportanalysis.modeling.entity.Stop;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import com.citytransportanalysis.modeling.entity.Transport;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.File;

/**
 * Created by User on 18.05.2017.
 */
public class ReadingXML {

    /**
     * Генерация транспорта
     *
     * @param count         количество транспорта
     * @param sitPlaces     мест для сидения
     * @param standPlaces   мест для стояния
     * @param startTime     время начала движения
     * @param minutesPeriod период движения в минутах
     * @return Список транспорта по порядку, учавствующего в поездке
     */
    public LinkedList<Transport> transportData(int count, int sitPlaces, int standPlaces, LocalTime startTime, long minutesPeriod) {
        LinkedList<Transport> transportList = new LinkedList<>();
        LocalTime MStartTime = startTime;
        //long minutesPeriod = 10;

        for (int i = 1; i <= count; i++) {
            Transport marshrutka = new Transport();
            marshrutka.setId(i);
            marshrutka.setRouteNumber("--");
            marshrutka.setSeatPlaces(sitPlaces);
            marshrutka.setStandPlaces(standPlaces);
            marshrutka.setStatus(Transport.Status.OnStop);
            marshrutka.setTransportType(Transport.Type.Microbus);
            marshrutka.setPassengers(new ArrayList<>());
            marshrutka.setStartTime(MStartTime);
            MStartTime = MStartTime.plusMinutes(minutesPeriod);

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

    public LinkedList<RouteSegment> routeDataMy(LinkedList<RouteSegment> routeSegments) {

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

        List<Stop> stopList = new ArrayList<Stop>();
        //LinkedList<RouteSegment> routeSegments = new LinkedList<>();

        try {
            File fXmlFile = new File("route1.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);

            doc.getDocumentElement().normalize();

            NodeList nList = doc.getElementsByTagName("zupynka");
            double passingtime = 0, length = 0;

            for (int temp = 0; temp < nList.getLength(); temp++) {

                Node nNode = nList.item(temp);

                if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                    Element eElement = (Element) nNode;

                    if (temp != 0 && temp != nList.getLength()-1) {
                        stopList.add(new Stop(eElement.getAttribute("name"), passengerComingTimeGen(), passengerExitProbabilityGen(), Double.parseDouble(eElement.getAttribute("waittime")), eElement.getAttribute("idname")));
                    } else if (temp == nList.getLength()-1) {
                        stopList.add(new Stop(eElement.getAttribute("name"), passengerComingTimeLast, passengerExitProbabilityLast, Double.parseDouble(eElement.getAttribute("waittime")), eElement.getAttribute("idname")));
                    } else {
                        stopList.add(new Stop(eElement.getAttribute("name"), passengerComingTimeGen(), passengerExitProbability, Double.parseDouble(eElement.getAttribute("waittime")), eElement.getAttribute("idname")));
                    }

                    if (temp > 0) {
                        routeSegments.add(new RouteSegment(stopList.get(temp-1), stopList.get(temp), passingtime, length));
                    }

                    passingtime = Double.parseDouble(eElement.getAttribute("passingtime"));
                    length = Double.parseDouble(eElement.getAttribute("length"));

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return routeSegments;
    }

}

