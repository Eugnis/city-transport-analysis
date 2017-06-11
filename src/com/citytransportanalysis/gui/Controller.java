package com.citytransportanalysis.gui;

import com.citytransportanalysis.modeling.Modeling;
import com.citytransportanalysis.modeling.entity.RouteSegment;
import com.citytransportanalysis.modeling.entity.Stop;
import com.citytransportanalysis.modeling.entity.Transport;
import com.citytransportanalysis.utils.ExceptionDialog;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.scene.web.WebView;
import javafx.util.Callback;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.swing.JRViewer;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * GUI main controller
 */
public class Controller extends Modeling{
    /**
     * Объекты с шаблона main.fxml
     */
    public Button button;
    public TableView logTable;
    public ListView<String> stopListView;
    public ListView<String> transportListView;
    public TextField timeFromTextField;
    public TextField timeToTextField;
    public Spinner<Integer> transportCountSpinner;
    public Spinner<Integer> periodSpinner;
    public Spinner<Integer> transportPlacesSit;
    public Spinner<Integer> transportPlacesStand;
    public Spinner<Integer> maxPercentPlaces;
    public Label totalPlacesCountLabel;
    public Label percentPlacesCountLabel;
    public TextFlow textFlowStationInfo;
    public Button resetFilter;
    public LineChart transportLineChart;
    public LineChart stopsLineChart;
    public CategoryAxis timeAxis;
    public NumberAxis passengersAxis;
    public WebView gmaps;
    public SplitPane logSplitPane;
    public TabPane visualTabPane;

    /**
     *  списки вынесены на глобальный уровень чтоб данные были доступны из функций графика
     */
    private LinkedList<RouteSegment> routeSegmentsList;
    private LinkedList<Transport> transportList;
    private LinkedList<Stop> stopsList;

    private ArrayList<Transport> overUsedTransport;
    private ArrayList<Stop> overUsedStops;
    private ObservableList<String> olist, tlist;

    /** Список событий: переменная {@link Modeling.eventsLog} доступна всегда,
     * так как "public class Controller extends Modeling"*/

    //private Modeling modeling;

    @SuppressWarnings("JavadocReference")
    private int totalPlaces;
    private int percentPlaces;

    private LocalTime chosenStartTime, chosenEndTime;

    public void initialize() {
        /* Стартовые значения */
        logSplitPane.setDisable(true);
        visualTabPane.setDisable(true);
        Label placeHolder = new Label("Натисніть \"Моделювати\" для відображення статистики");
        placeHolder.setStyle("-fx-font-size: 18");
        logTable.setPlaceholder(placeHolder);
        timeFromTextField.setText("06:00");     // Начальное время
        chosenStartTime = LocalTime.parse("06:00");
        timeToTextField.setText("22:00");       // Конец движения
        chosenEndTime = LocalTime.parse("22:00");
        transportCountSpinner.getValueFactory().setValue(5);    //Кол-во транспорта
        periodSpinner.getValueFactory().setValue(10);       // Период движения в минутах
        transportPlacesSit.getValueFactory().setValue(22);    //Сидячих мест
        transportPlacesStand.getValueFactory().setValue(21);       // Стоячих мест
        maxPercentPlaces.getValueFactory().setValue(75);            //відсоток місць

        totalPlaces = transportPlacesSit.getValueFactory().getValue() + transportPlacesStand.getValueFactory().getValue();
        totalPlacesCountLabel.setText(Integer.toString(totalPlaces));
        percentPlaces = (int) ((totalPlaces * (maxPercentPlaces.getValueFactory().getValue() / 100.0)) + 0.5);
        percentPlacesCountLabel.setText(Integer.toString(percentPlaces));
        /*
         *  Пересчет количества мест в транспорте при изменении ползунков
         */
        transportPlacesStand.valueProperty().addListener(((observable, oldValue, newValue) -> {
            totalPlaces = transportPlacesSit.getValueFactory().getValue() + transportPlacesStand.getValueFactory().getValue();
            totalPlacesCountLabel.setText(Integer.toString(totalPlaces));
            percentPlaces = (int) ((totalPlaces * (maxPercentPlaces.getValueFactory().getValue() / 100.0)) + 0.5);
            percentPlacesCountLabel.setText(Integer.toString(percentPlaces));
        }));
        /*
         *  Пересчет количества мест в транспорте при изменении ползунков
         */
        transportPlacesSit.valueProperty().addListener(((observable, oldValue, newValue) -> {
            totalPlaces = transportPlacesSit.getValueFactory().getValue() + transportPlacesStand.getValueFactory().getValue();
            totalPlacesCountLabel.setText(Integer.toString(totalPlaces));
            percentPlaces = (int) ((totalPlaces * (maxPercentPlaces.getValueFactory().getValue() / 100.0)) + 0.5);
            percentPlacesCountLabel.setText(Integer.toString(percentPlaces));
        }));
        /*
         *  Пересчет процента мест в транспорте при изменении ползунков
         */
        maxPercentPlaces.valueProperty().addListener(((observable, oldValue, newValue) -> {
            percentPlaces = (int) ((totalPlaces * (maxPercentPlaces.getValueFactory().getValue() / 100.0)) + 0.5);
            percentPlacesCountLabel.setText(Integer.toString(percentPlaces));
            transportListView.setItems(null);
            transportListView.setItems(tlist);
        }));
        /*
         *  Фильтрация лога и графика для выбранной остановки
         */
        stopListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                int passengersCount = 0;
                int leftPassengersCount = 0;
                for (Modeling.Event event : eventsLog) {
                    if (event.getType() == Modeling.Event.Type.SittingPassenger && newValue.equals(event.getStop().getName())) {
                        passengersCount += event.getStop().getSittedPassengers();
                        leftPassengersCount += event.getStop().getPassengers().size();
                    }
                }

                Text text1 = new Text(String.format("Інформація про зупинку %s\n", newValue));
                text1.setFill(Color.RED);
                //text1.setStyle("-fx-font-size: 18;");
                Text text2 = new Text(String.format("Пройшло %d пасажирів, не змогло зайти %d пасажирів", passengersCount, leftPassengersCount));
                //text2.setStyle("-fx-font-size: 18;");
                textFlowStationInfo.getChildren().clear();
                textFlowStationInfo.getChildren().addAll(text1, text2);

                ObservableList<Modeling.Event> data = FXCollections.observableList(eventsLog.stream().filter(u -> u.getStop() != null && u.getStop().getName().equals(newValue)).collect(Collectors.toList()));
                initTable(data);


                /* Заполнение графика заполненности ВЫБРАННОЙ остановки */
                fillStopsChart(stopsList.stream().filter(t -> t.toString().equals(newValue)).collect(Collectors.toList()));

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
                            setStyle(null);
                        } else if (s != null && overUsedStops.stream().filter(stop -> stop.getName().equals(s)).count() > 0) {
                            setStyle("-fx-background-color: red");
                            setText(s);
                        } else if (s != null) {
                            setStyle(null);
                            setText(s);
                        }
                    }
                };
            }
        });     // отмечает остановки не подходящие по требованиям
        /*
         *  Фильтрация лога и графика для выбранного транспорта
         */
        transportListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                //System.out.println("Selected item: " + newValue);
                Text text1 = new Text(String.format("Інформація про транспорт %s\n", newValue));
                text1.setFill(Color.RED);
                //text1.setStyle("-fx-font-size: 18;");
                Transport transport = null;
                for (Modeling.Event event : eventsLog)
                    if (event.getType() == Modeling.Event.Type.EndDay && newValue.equals(event.getTransport().toString())) {
                        transport = event.getTransport();
                        break;
                    }

                if (transport != null) {
                    Text text2 = new Text(String.format("Проходив шлях %d разів і перевіз %d чел\n", transport.getTripCount(), transport.getAllPassengersCount()));
                    //text2.setStyle("-fx-font-size: 18;");
                    textFlowStationInfo.getChildren().clear();
                    textFlowStationInfo.getChildren().addAll(text1, text2);
                }

                ObservableList<Modeling.Event> data = FXCollections.observableList(eventsLog.stream().filter(u -> u.getTransport() != null && !u.getType().equals(Modeling.Event.Type.EndDay) && u.getTransport().toString().equals(newValue)).collect(Collectors.toList()));
                initTable(data);

                /* Заполнение графика заполненности ВЫБРАННОГО транспорта */
                fillTransportsChart(transportList.stream().filter(t -> t.toString().equals(newValue)).collect(Collectors.toList()));


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
                            setStyle(null);
                        } else if (s != null && overUsedTransport.stream().filter(transport -> transport.toString().equals(s)).count() > 0) {
                            setStyle("-fx-background-color: red");
                            setText(s);
                        } else if (s != null) {
                            setStyle(null);
                            setText(s);
                        }
                    }

                };
            }
        });     // отмечает транспорт не подходящие по требованиям
        gmaps.getEngine().load(getClass().getResource("/gmaps/index.html").toExternalForm());
    }



    public void startModellingButtonClicked(ActionEvent actionEvent) {
        /* Тест
        try{gmaps.getEngine().executeScript("calcRoute('Kyiv, Pravdi str', 'Kyiv, Pobedi str');");}
        catch (Exception ignored){}
        transportListView.setItems(null);
        */

        /* Получения данных моделирования */
        //modeling = new Modeling();
        chosenStartTime = LocalTime.parse(timeFromTextField.getText());
        chosenEndTime = LocalTime.parse(timeToTextField.getText());
        routeSegmentsList = routeData();
        int transportCount = transportCountSpinner.getValueFactory().getValue();
        int periodCount = periodSpinner.getValueFactory().getValue();
        int sitPlacesCount = transportPlacesSit.getValueFactory().getValue();
        int standPlacesCount = transportPlacesStand.getValueFactory().getValue();
        //System.out.printf("New transport count %d period %d", transportCount, periodCount);
        transportList = transportData(transportCount, sitPlacesCount, standPlacesCount, chosenStartTime, periodCount);
        try{
            LaunchMovement(routeSegmentsList, transportList, chosenStartTime, chosenEndTime, periodCount);
            /* Выводим результаты в форму */
            refillDisplayedData();

            logSplitPane.setDisable(false);
            visualTabPane.setDisable(false);
        } catch (Exception e) {
            ExceptionDialog.Show(e, "Невірні вхідні дані");
        }

        //LinkedList<RouteSegment> route = routeData();

        //eventsLog = modeling.eventsLog;


    }

    private void refillDisplayedData(){
                /* Заполнения списка остановок */
        stopsList = new LinkedList<>();
        for (RouteSegment r : routeSegmentsList)
            if (r == routeSegmentsList.getFirst()) {
                stopsList.add(r.getTwoStops().get(0));
                stopsList.add(r.getTwoStops().get(1));
            } else stopsList.add(r.getTwoStops().get(1));

        olist = FXCollections.observableArrayList();
        for (Stop s : stopsList)
            olist.add(s.getName());
        stopListView.setItems(olist);

        /* Заполнения списка транспорта */
        tlist = FXCollections.observableArrayList();
        for (Transport t : transportList)
            tlist.add(t.toString());
        transportListView.setItems(tlist);

        /* Заполнения списка событий лог */
        ObservableList<Modeling.Event> data = FXCollections.observableList(eventsLog.stream().filter(event ->
                !event.getType().equals(Modeling.Event.Type.EndDay)).collect(Collectors.toList()));
        initTable(data);

        /* Выводим общую инфу */
        Text text1 = new Text(String.format("З %s по %s перевезено %d транспортами всього %d пасажирів.\n",
                chosenStartTime, chosenEndTime, transportList.size(), transportList.stream().mapToInt(Transport::getAllPassengersCount).sum()));
        textFlowStationInfo.getChildren().clear();
        textFlowStationInfo.getChildren().addAll(text1);

        /* Заполнение графика заполненности транспорта */
        fillTransportsChart(transportList);

        /* Заполнение графика заполненности остановок */
        fillStopsChart(stopsList);

        /* Обновляем списки транспорта и остановок с подсветкой */
        transportListView.refresh();
        stopListView.refresh();
    }

    public void resetFilterClicked(ActionEvent actionEvent) {
        stopListView.getSelectionModel().clearSelection();
        transportListView.getSelectionModel().clearSelection();
        ObservableList<Modeling.Event> data = FXCollections.observableList(eventsLog.stream().filter(event ->
                !event.getType().equals(Modeling.Event.Type.EndDay)).collect(Collectors.toList()));
        initTable(data);

        /* Выводим результаты в форму */
        refillDisplayedData();
    }


    public void initTable(ObservableList<Modeling.Event> data) {
        logTable.setItems(data);
        TableColumn timeCol = new TableColumn("Час");
        timeCol.setCellValueFactory(new PropertyValueFactory("textTime"));
        TableColumn idCol = new TableColumn("№");
        idCol.setCellValueFactory(new PropertyValueFactory("transportId"));
        TableColumn descrCol = new TableColumn("Опис події");
        descrCol.setCellValueFactory(new PropertyValueFactory("description"));

        logTable.getColumns().setAll(timeCol, idCol, descrCol);
    }

    private void fillStopsChart(List<Stop> stopsList){
        /* Заполнение графика заполненности остановок */
        overUsedStops = new ArrayList<>();
        stopsLineChart.getData().clear();
        stopsLineChart.getXAxis().setAnimated(false);
        boolean shortNames = stopsList.size()>1;
        for (Stop s : stopsList) {
            XYChart.Series series = new XYChart.Series();
            if(shortNames){
                String cuttedStr = s.getName().substring(0, Math.min(s.getName().length(), 12));
                if(!cuttedStr.equals(s.getName())) cuttedStr += ".";
                series.setName(cuttedStr);
            }
            else series.setName(s.getName());
            LocalTime curTime = chosenStartTime;
            do {
                final int hours = curTime.getHour();
                List<Modeling.Event> curEvents = eventsLog.stream().filter(event ->
                        event.getType().equals(Modeling.Event.Type.OnStop) &&
                                event.getStop().getName().equals(s.getName()) &&
                                event.getTime().getHour() == hours).collect(Collectors.toList());
                if (curEvents.size() != 0) {
                    Modeling.Event curEvent = Collections.max(curEvents, Comparator.comparing(Modeling.Event::getPassengersOnStop));
                    series.getData().add(new XYChart.Data<>(curTime.toString(), curEvent.getPassengersOnStop()));
                    if (curEvent.getPassengersLeft() > 0)
                        if (!overUsedStops.contains(curEvent.getStop()))
                            overUsedStops.add(curEvent.getStop());
                }
                curTime = curTime.plusHours(1);
            } while (curTime.isBefore(chosenEndTime));
            stopsLineChart.getData().add(series);
        }
    }

    private void fillTransportsChart(List<Transport> transportList){
        /* Заполнение графика заполненности транспорта */
        overUsedTransport = new ArrayList<>();
        transportLineChart.getData().clear();
        transportLineChart.getXAxis().setAnimated(false);
        boolean shortNames = transportList.size()>1;
        for (Transport t : transportList) {
            XYChart.Series series = new XYChart.Series();
            if(shortNames){
                String cuttedStr = t.toString().substring(0, Math.min(t.toString().length(), 12));
                if(!cuttedStr.equals(t.toString())) cuttedStr += ".";
                series.setName(cuttedStr);
            }
            else series.setName(t.toString());
            LocalTime curTime = chosenStartTime;
            do {
                final int hours = curTime.getHour();
                List<Modeling.Event> curEvents = eventsLog.stream().filter(event ->
                        event.getType().equals(Modeling.Event.Type.OnWay) &&
                                event.getTransport().equals(t) &&
                                event.getTime().getHour() == hours).collect(Collectors.toList());
                if (curEvents.size() != 0) {
                    Modeling.Event curEvent = Collections.max(curEvents, Comparator.comparing(Modeling.Event::getFilledPlaces));
                    series.getData().add(new XYChart.Data<>(curTime.toString(), curEvent.getFilledPlaces()));
                    if (curEvent.getFilledPlaces() > percentPlaces)
                        if (!overUsedTransport.contains(curEvent.getTransport()))
                            overUsedTransport.add(curEvent.getTransport());
                }
                curTime = curTime.plusHours(1);
            } while (curTime.isBefore(chosenEndTime));
            transportLineChart.getData().add(series);
        }
    }

    public void generateReportClicked(ActionEvent actionEvent) {
        InputStream reportName = null;
        try {
            reportName = getClass().getResource("/report_templates/Simple_Blue.jasper").openStream();

        } catch (Exception e) {
            ExceptionDialog.Show(e, "Не знайдено шаблон");
        }
        JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(eventsLog); //создаем коллекцию Jasper Report Bean Collection

        Map parameters = new HashMap();
        parameters.put("nomer", eventsLog.get(0).getTransport().getRouteNumber());
        parameters.put("stopsChart", saveChartAsImg(stopsLineChart));
        parameters.put("transportChart", saveChartAsImg(transportLineChart));

        System.out.println(eventsLog.get(0).getTransport().getRouteNumber());
        JasperPrint jp = null;
            try {
                jp = JasperFillManager.fillReport(reportName, parameters, beanCollectionDataSource);
                JRViewer jv = new JRViewer(jp); // компонент просмотра отчета

                JFrame reportFrame = new JFrame();
                reportFrame.setSize(768, 1024);
                reportFrame.getContentPane().add(jv);
                reportFrame.validate();
                reportFrame.setVisible(true);
            } catch (Exception e) {
                ExceptionDialog.Show(e, "Помилка у побудові звіту");
            }
    }

    public BufferedImage saveChartAsImg(LineChart chart) {
        WritableImage image = chart.snapshot(new SnapshotParameters(), null);

        // TODO: probably use a file chooser here
        File file = new File("chart.png");

        try {
            return SwingFXUtils.fromFXImage(image, null);
        } catch (Exception e) {
            ExceptionDialog.Show(e, "Помилка у збереженні зображення графіку");
        }
        return null;
    }
}
