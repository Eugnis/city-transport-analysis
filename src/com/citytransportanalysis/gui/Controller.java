package com.citytransportanalysis.gui;

import com.citytransportanalysis.modeling.Modeling;
import com.citytransportanalysis.modeling.entity.RouteSegment;
import com.citytransportanalysis.modeling.entity.Transport;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Callback;

import java.time.LocalTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * GUI main controller
 */
public class Controller {
    public Button button;
    //public Label label;
    public TableView logTable;
    public ListView<String> stopListView;
    public ListView<String> transportListView;
    public TextField timeFromTextField;
    public TextField timeToTextField;
    public Spinner<Integer> transportCountSpinner;
    public Spinner<Integer> periodSpinner;
    public TextFlow textFlowStationInfo;
    public Button resetFilter;
    public LineChart lineChart;
    public CategoryAxis timeAxis;
    public NumberAxis passengersAxis;

    private Modeling modeling;

    public void initialize() {
        timeFromTextField.setText("06:00");
        timeToTextField.setText("22:00");
        //       transportCountSpinner = new Spinner<>();
//        periodSpinner = new Spinner<>();
        transportCountSpinner.getValueFactory().setValue(5);
        periodSpinner.getValueFactory().setValue(10);

        stopListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                //System.out.println("Selected item: " + newValue);
                Text text1 = new Text(String.format("Інформація про зупинку %s\n", newValue));
                text1.setFill(Color.RED);
                int passengersCount = 0;
                int leftPassengersCount = 0;
                for (Modeling.Event event : modeling.eventsLog) {
                    if (event.getType() == Modeling.Event.Type.SittingPassenger && newValue.equals(event.getStop().getName())) {
                        passengersCount += event.getStop().getSittedPassengers();
                        leftPassengersCount += event.getStop().getPassengers().size();
                    }
                }
                Text text2 = new Text(String.format("Пройшло %d пасажирів, не змогло зайти %d пасажирів", passengersCount, leftPassengersCount));
                textFlowStationInfo.getChildren().clear();
                textFlowStationInfo.getChildren().addAll(text1, text2);
                //List<Modeling.Event> events = modeling.eventsLog;
                //List<Modeling.Event> eventsnew = modeling.eventsLog.stream().filter(u -> u.getStop() != null && u.getStop().getName().equals(newValue.getName())).collect(Collectors.toList());
                ObservableList<Modeling.Event> data = FXCollections.observableList(modeling.eventsLog.stream().filter(u -> u.getStop() != null && u.getStop().getName().equals(newValue)).collect(Collectors.toList()));
                initTable(data);

            }
        });
        stopListView.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            @Override
            public ListCell<String> call(ListView<String> stringListView) {
                return new ListCell<String>() {

                    @Override
                    protected void updateItem(String s, boolean b) {
                        super.updateItem(s, b);    //To change body of overridden methods use File | Settings | File Templates.
                        if (b) {
                            setText(null);
                            setGraphic(null);
                        }
                        if (s != null && s.contains("1")) {
                            setStyle("-fx-background-color: red");
                            //setGraphic(your graphics);
                            setText(s);
                        }
                        if (s != null) {
                            //setStyle("-fx-background-color: red");
                            //setGraphic(your graphics);
                            setText(s);
                        }
                    }
                };
            }
        });

        transportListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                //System.out.println("Selected item: " + newValue);
                Text text1 = new Text(String.format("Інформація про транспорт %s\n", newValue));
                text1.setFill(Color.RED);
                Transport transport = null;
                for (Modeling.Event event : modeling.eventsLog)
                    if (event.getType() == Modeling.Event.Type.EndDay && newValue.equals(event.getTransport().toString())) {
                        transport = event.getTransport();
                        break;
                    }

                if (transport != null) {
                    Text text2 = new Text(String.format("Проходив шлях %d разів і перевіз %d чел\n", transport.getTripCount(), transport.getAllPassengersCount()));
                    textFlowStationInfo.getChildren().clear();
                    textFlowStationInfo.getChildren().addAll(text1, text2);
                }

                //List<Modeling.Event> events = modeling.eventsLog;
                //List<Modeling.Event> eventsnew = modeling.eventsLog.stream().filter(u -> u.getStop() != null && u.getStop().getName().equals(newValue.getName())).collect(Collectors.toList());
                ObservableList<Modeling.Event> data = FXCollections.observableList(modeling.eventsLog.stream().filter(u -> u.getTransport() != null && u.getTransport().toString().equals(newValue)).collect(Collectors.toList()));
                initTable(data);


            }
        });
        transportListView.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            @Override
            public ListCell<String> call(ListView<String> stringListView) {
                return new ListCell<String>() {
                    @Override
                    protected void updateItem(String s, boolean b) {
                        super.updateItem(s, b);    //To change body of overridden methods use File | Settings | File Templates.
                        if (b) {
                            setText(null);
                            setGraphic(null);
                        }
                        if (s != null && s.contains("1")) {
                            setStyle("-fx-background-color: red");
                            //setGraphic(your graphics);
                            setText(s);
                        }
                        if (s != null) {
                            //setStyle("-fx-background-color: red");
                            //setGraphic(your graphics);
                            setText(s);
                        }
                    }
                };
            }
        });

    }


    public void buttonClicked(ActionEvent actionEvent) {
        System.out.println("Button clicked!!!");
        //label.setText("!");
        modeling = new Modeling();
        LocalTime startTime = LocalTime.parse(timeFromTextField.getText());
        LocalTime endTime = LocalTime.parse(timeToTextField.getText());
        LinkedList<RouteSegment> routeSegments = modeling.routeData();
        int transportCount = transportCountSpinner.getValueFactory().getValue();
        int periodCount = periodSpinner.getValueFactory().getValue();
        System.out.printf("New transport count %d period %d", transportCount, periodCount);
        LinkedList<Transport> transportList = modeling.transportData(transportCount, startTime, periodCount);
        modeling.Launch(routeSegments, transportList, startTime, endTime, periodCount);
        LinkedList<RouteSegment> route = modeling.routeData();

        ObservableList<String> olist = FXCollections.observableArrayList();
        for (RouteSegment r : route)
            if (r == route.getFirst())
                olist.addAll(r.getTwoStops().get(0).getName(), r.getTwoStops().get(1).getName());
            else olist.add(r.getTwoStops().get(1).getName());
        stopListView.setItems(olist);

        olist = FXCollections.observableArrayList();
        for (Transport t : transportList)
            olist.add(t.toString());
        transportListView.setItems(olist);

        ObservableList<Modeling.Event> data = FXCollections.observableList(modeling.eventsLog);
        initTable(data);

        lineChart.getData().clear();

        for (Transport t : transportList) {
            XYChart.Series<String, Integer> series = new XYChart.Series();
            series.setName(t.toString());
            //series.

            LocalTime curTime = startTime;
            do {
                //System.out.println(curTime);
                final int hours = curTime.getHour();
                List<Modeling.Event> curEvents = modeling.eventsLog.stream().filter(event ->
                        event.getType().equals(Modeling.Event.Type.OnWay) &&
                                event.getTransport().equals(t) &&
                                event.getTime().getHour() == hours).collect(Collectors.toList());
                //System.out.printf("CurEvents size %d, hour %s", curEvents.size(), curTime);
                if (curEvents.size() != 0) {
                    Modeling.Event curEvent = Collections.max(curEvents, Comparator.comparing(Modeling.Event::getFilledPlaces));
                    //curEvents.stream().max(Comparator.comparing(ev -> ev.getTransport().getOccupiedPlaces())).ifPresent(s -> System.out.println(s.getTransport().getOccupiedPlaces()));
                    //.stream().max(Comparator.comparing(event -> event.getTransport().getPassengers().size())).get();
                    //System.out.println("Places:" + curEvent.getFilledPlaces());
                    series.getData().add(new XYChart.Data<>(curTime.toString(), curEvent.getFilledPlaces()));
                }


                curTime = curTime.plusHours(1);
            } while (curTime.isBefore(endTime));
            lineChart.getData().add(series);

        }

    }

    public void resetFilterClicked(ActionEvent actionEvent) {
        stopListView.getSelectionModel().clearSelection();
        transportListView.getSelectionModel().clearSelection();
        ObservableList<Modeling.Event> data = FXCollections.observableList(modeling.eventsLog);
        initTable(data);
    }


    public void initTable(ObservableList<Modeling.Event> data) {
        logTable.setItems(data);
        TableColumn timeCol = new TableColumn("Час");
        timeCol.setCellValueFactory(new PropertyValueFactory("textTime"));
        TableColumn idCol = new TableColumn("Транспорт");
        idCol.setCellValueFactory(new PropertyValueFactory("transportId"));
        TableColumn descrCol = new TableColumn("Опис");
        descrCol.setCellValueFactory(new PropertyValueFactory("description"));

        logTable.getColumns().setAll(timeCol, idCol, descrCol);
        //logTable.setPrefWidth(450);
        //logTable.setPrefHeight(300);
        //logTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);


    }


}
