package com.citytransportanalysis.gui;

import com.citytransportanalysis.modeling.Modeling;
import com.citytransportanalysis.modeling.entity.RouteSegment;
import com.citytransportanalysis.modeling.entity.Stop;
import com.citytransportanalysis.modeling.entity.Transport;
import com.citytransportanalysis.utils.ExceptionDialog;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Callback;
import javafx.util.Duration;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.swing.JRViewer;

import javax.swing.*;
import java.awt.image.BufferedImage;
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
    public Button reportBtn;
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

    /**
     *  VISUALIZATION
     */

    private Text livober, entuz, entuz2, bridge, bridge2, library, library2, buvet, buvet2, post, post2, davidova, davidova2, okipnoi, slavutich;
    private StackPane stack;
    private String resultTime;
    private int second, minute, hour;
    private Timeline timeline;

    @FXML
    private Pane contentPane;
    @FXML
    private Button startAnimation;
    @FXML
    private Button stopAnimation;
    @FXML
    private Button pauseAnimation;
    @FXML
    private Button resumeAnimation;
    @FXML
    private TextField speedField;

    @FXML
    private void startAnimationAction(ActionEvent event) throws InterruptedException {

        contentPane.getChildren().removeAll(stack,livober, entuz, entuz2, bridge, bridge2, library, library2, buvet, buvet2, post, post2, davidova, davidova2,okipnoi, slavutich);
        List<String[]> list = new ArrayList<>(mainList);

        Rectangle rectTime = new Rectangle(100, 100, 95, 30); //time rectangle
        rectTime.setFill(Color.rgb(170,180,173));
        final Text timeText = new Text ();
        timeText.setFont(Font.font(18));

        Font stopFont = Font.font("Verdana", FontWeight.BLACK, 9);

        livober = new Text (140, 30, "");
        entuz = new Text(30, 215, "");
        entuz2 = new Text(45, 145, "");
        bridge = new Text(185, 295, "");
        bridge2 = new Text(220, 220, "");
        library = new Text(270, 330, "");
        library2 = new Text(390, 300, "");
        buvet = new Text(325, 405, "");
        buvet2 = new Text(420, 365, "");
        post = new Text(295, 470, "");
        post2 = new Text(380, 500, "");
        davidova = new Text(145, 555, "");
        davidova2 = new Text(265, 570, "");
        okipnoi = new Text(105, 90, "");
        slavutich = new Text(145, 635, "");

        livober.setFont(stopFont);
        entuz.setFont(stopFont);
        entuz2.setFont(stopFont);
        bridge.setFont(stopFont);
        bridge2.setFont(stopFont);
        library.setFont(stopFont);
        library2.setFont(stopFont);
        buvet.setFont(stopFont);
        buvet2.setFont(stopFont);
        post.setFont(stopFont);
        post2.setFont(stopFont);
        davidova.setFont(stopFont);
        davidova2.setFont(stopFont);
        okipnoi.setFont(stopFont);
        slavutich.setFont(stopFont);

        second = Integer.parseInt(GetStartSecond());
        minute = Integer.parseInt(GetStartMinute());
        hour = Integer.parseInt(GetStartHour());
        resultTime = GetStartHour() + ":" + GetStartMinute() + ":" + GetStartSecond();

        double speed = Double.parseDouble(speedField.getText());
        timeline = new Timeline(
                new KeyFrame(Duration.seconds(speed),
                        actionEvent -> {
                            String timeToCompare = SetClock(timeText);
                            for (int i=0; i<list.size()-1; i++)
                            {
                                if (timeToCompare.equals(list.get(i)[0])) {

                                    String setInfo = String.format("#%s\nout %s(%s)\nin %s(%s)\nleft %s", list.get(i)[1], list.get(i)[6], list.get(i)[7], list.get(i)[3], list.get(i)[4], list.get(i)[5]);
                                    Color busColor = Color.web(String.valueOf(busColors.get(list.get(i)[1])));
                                    String leftEffect = Integer.parseInt(list.get(i)[5]) > 0 ? "-fx-effect: dropshadow( one-pass-box , red , 8 , 0.0 , 1 , 0 )" : "";

                                    switch (list.get(i)[2]) {
                                        case "livober":
                                            livober.setText(setInfo);
                                            livober.setFill(busColor);
                                            livober.setStyle(leftEffect);
                                            break;
                                        case "entuz":
                                            entuz.setText(setInfo);
                                            entuz.setFill(busColor);
                                            entuz.setStyle(leftEffect);
                                            break;
                                        case "entuz2":
                                            entuz2.setText(setInfo);
                                            entuz2.setFill(busColor);
                                            entuz2.setStyle(leftEffect);
                                            break;
                                        case "bridge":
                                            bridge.setText(setInfo);
                                            bridge.setFill(busColor);
                                            bridge.setStyle(leftEffect);
                                            break;
                                        case "bridge2":
                                            bridge2.setText(setInfo);
                                            bridge2.setFill(busColor);
                                            bridge2.setStyle(leftEffect);
                                            break;
                                        case "library":
                                            library.setText(setInfo);
                                            library.setFill(busColor);
                                            library.setStyle(leftEffect);
                                            break;
                                        case "library2":
                                            library2.setText(setInfo);
                                            library2.setFill(busColor);
                                            library2.setStyle(leftEffect);
                                            break;
                                        case "buvet":
                                            buvet.setText(setInfo);
                                            buvet.setFill(busColor);
                                            buvet.setStyle(leftEffect);
                                            break;
                                        case "buvet2":
                                            buvet2.setText(setInfo);
                                            buvet2.setFill(busColor);
                                            buvet2.setStyle(leftEffect);
                                            break;
                                        case "post":
                                            post.setText(setInfo);
                                            post.setFill(busColor);
                                            post.setStyle(leftEffect);
                                            break;
                                        case "post2":
                                            post2.setText(setInfo);
                                            post2.setFill(busColor);
                                            post2.setStyle(leftEffect);
                                            break;
                                        case "davidova":
                                            davidova.setText(setInfo);
                                            davidova.setFill(busColor);
                                            davidova.setStyle(leftEffect);
                                            break;
                                        case "davidova2":
                                            davidova2.setText(setInfo);
                                            davidova2.setFill(busColor);
                                            davidova2.setStyle(leftEffect);
                                            break;
                                        case "okipnoi":
                                            okipnoi.setText(setInfo);
                                            okipnoi.setFill(busColor);
                                            okipnoi.setStyle(leftEffect);
                                            break;
                                        case "slavutich":
                                            slavutich.setText(setInfo);
                                            slavutich.setFill(busColor);
                                            slavutich.setStyle(leftEffect);
                                            break;
                                    }
                                    list.remove(i);
                                }
                            }
                        }
                ),
                new KeyFrame(Duration.seconds(speed))
        );

        stack = new StackPane();
        stack.getChildren().addAll(rectTime, timeText);
        contentPane.getChildren().addAll(stack, okipnoi, slavutich, livober, entuz, entuz2, bridge, bridge2, library, library2, buvet, buvet2, post, post2, davidova, davidova2);

        timeline.setCycleCount(GetCycleCount(mainList));
        timeline.playFromStart();

        speedField.setDisable(false);
        startAnimation.setDisable(false);
        stopAnimation.setDisable(false);
        pauseAnimation.setDisable(false);
        resumeAnimation.setDisable(true);

    }

    @FXML
    public void stopAnimationAction(ActionEvent actionEvent) {
        contentPane.getChildren().removeAll(stack,livober, entuz, entuz2, bridge, bridge2, library, library2, buvet, buvet2, post, post2, davidova, davidova2,okipnoi, slavutich);
        timeline.stop();

        speedField.setDisable(false);
        startAnimation.setDisable(false);
        stopAnimation.setDisable(true);
        pauseAnimation.setDisable(true);
        resumeAnimation.setDisable(true);

    }

    @FXML
    public void pauseAnimationAction(ActionEvent actionEvent) {
        timeline.pause();

        speedField.setDisable(true);
        startAnimation.setDisable(true);
        stopAnimation.setDisable(true);
        pauseAnimation.setDisable(true);
        resumeAnimation.setDisable(false);
    }

    @FXML
    public void resumeAnimationAction(ActionEvent actionEvent) {
        timeline.play();

        speedField.setDisable(false);
        startAnimation.setDisable(false);
        stopAnimation.setDisable(false);
        pauseAnimation.setDisable(false);
        resumeAnimation.setDisable(true);

    }

    private int GetCycleCount(List<String[]> list)
    {
        int cycleCount;
        String endTime = list.get(list.size()-1)[0];
        if (endTime.length()==8)
        {
            String hourEnd = endTime.substring(0,2);
            String minuteEnd = endTime.substring(3,5);
            String secondEnd = endTime.substring(6,8);
            int middleHour = Integer.parseInt(hourEnd) - hour; //count cycle Count
            int middleMinute = Math.abs(Integer.parseInt(minuteEnd) - minute);
            int middleSecond = Integer.parseInt(secondEnd);
            cycleCount = middleHour*3600 + middleMinute*60 + middleSecond;
        } else {
            String hourEnd = endTime.substring(0,2);
            String minuteEnd = endTime.substring(3,5);
            int middleHour = Integer.parseInt(hourEnd) - hour; //count cycle Count
            int middleMinute = Math.abs(Integer.parseInt(minuteEnd) - minute);
            cycleCount = middleHour*3600 + middleMinute*60;
        }
        return cycleCount;
    }

    private String GetStartHour() {
        return (timeFromTextField.getText()).substring(0,2);
    }

    private String GetStartMinute() {
        return (timeFromTextField.getText()).substring(3,5);
    }

    private String GetStartSecond() { return "00"; }

    private String SetClock(Text timeText){
        second++;
        if (second == 60) {
            second = 0;
            minute++;
            if (minute == 60) {
                minute = 0;
                hour++;
                if (hour == 24) {
                    hour = 0;
                }
            }
        }
        resultTime = String.format("%02d", hour) + ":" + String.format("%02d", minute) + ":" + String.format("%02d", second);
        timeText.setText(resultTime);

        return second == 0 ? String.format("%02d", hour) + ":" + String.format("%02d", minute) : String.format("%02d", hour) + ":" + String.format("%02d", minute) + ":" + String.format("%02d", second);
    }

    /**
     *  END OF VISUALIZATION
     */


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
//        gmaps.getEngine().load(getClass().getResource("/gmaps/index.html").toExternalForm());
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

        reportBtn.setDisable(false);

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

    private BufferedImage saveChartAsImg(LineChart chart) {
        WritableImage image = chart.snapshot(new SnapshotParameters(), null);

        try {
            return SwingFXUtils.fromFXImage(image, null);
        } catch (Exception e) {
            ExceptionDialog.Show(e, "Помилка у збереженні зображення графіку");
        }
        return null;
    }
}
