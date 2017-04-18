package com.citytransportanalysis.gui;

import com.citytransportanalysis.modeling.Modeling;
import com.citytransportanalysis.modeling.entity.RouteSegment;
import com.citytransportanalysis.modeling.entity.Stop;
import com.citytransportanalysis.modeling.entity.Transport;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalTime;
import java.util.LinkedList;
import java.util.stream.Collectors;

/**
 * GUI main controller
 */
public class Controller {
    public Button button;
    public Label label;
    public TableView logTable;
    public ListView<Stop> stopListView;
    Modeling modeling;

    public void initialize() {
        stopListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("Selected item: " + newValue);
            //List<Modeling.Event> events = modeling.eventsLog;
            //List<Modeling.Event> eventsnew = modeling.eventsLog.stream().filter(u -> u.getStop() != null && u.getStop().getName().equals(newValue.getName())).collect(Collectors.toList());

            ObservableList<Modeling.Event> data = FXCollections.observableList(modeling.eventsLog.stream().filter(u -> u.getStop() != null && u.getStop().getName().equals(newValue.getName())).collect(Collectors.toList()));
            initTable(data);
        });
    }


    public void buttonClicked(ActionEvent actionEvent) {
        System.out.println("Button clicked!!!");
        label.setText("!");
        modeling = new Modeling();
        LocalTime startTime = LocalTime.parse("06:00");
        LocalTime endTime = LocalTime.parse("22:00");
        LinkedList<RouteSegment> routeSegments = modeling.routeData();
        LinkedList<Transport> transportList = modeling.transportData(5, startTime, 10);
        modeling.Launch(routeSegments, transportList, startTime, endTime);
        LinkedList<RouteSegment> route = modeling.routeData();
        ObservableList<Stop> olist = FXCollections.observableArrayList();
        for (RouteSegment r : route) {
            if (r == route.getFirst())
                olist.addAll(r.getTwoStops());
            else olist.add(r.getTwoStops().get(1));
        }
        stopListView.setItems(olist);

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
