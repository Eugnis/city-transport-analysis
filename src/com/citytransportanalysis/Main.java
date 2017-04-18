package com.citytransportanalysis;

import com.citytransportanalysis.modeling.Modeling;
import com.citytransportanalysis.modeling.entity.RouteSegment;
import com.citytransportanalysis.modeling.entity.Transport;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.time.LocalTime;
import java.util.LinkedList;

import static javafx.application.Platform.exit;

public class Main extends Application {

    public static void main(String[] args) {

        // моделирование с выводом в консоль пока что
        Modeling modeling = new Modeling();


        LocalTime startTime = LocalTime.parse("06:00");     //время начала движения
        LocalTime endTime = LocalTime.parse("22:00");       //время конца движения
        LinkedList<RouteSegment> routeSegments = modeling.routeData();      //список участков пути (между двумя остановками)
        LinkedList<Transport> transportList = modeling.transportData(5, startTime, 10);    //список список маршруток (одинаковых), период движения

        /**TODO надо сделать отдельный класс для загрузки начальных данных с XML файла
         *  На вход подается имя xml файла
         *  На выходе - 2 списка (LinkedList<RouteSegment> routeSegments и LinkedList<Transport> transportList,
         *  пример заполнения списков в функциях modeling.routeData() и modeling.transportData(), они сейчас генерируются рандомно)
         *  Так же с XML файла считывать время начала пути, конца. Пока что все, .
         */

        modeling.Launch(routeSegments, transportList, startTime, endTime);  //запуск моделирования

        //вывод текстового лога в консоль
        //Collections.sort(modeling.eventsLog, Comparator.comparing(Event::getTime));    //сортировка лога по времени

        for (Modeling.Event event : modeling.eventsLog) {     //сам вывод лога в консоль построчно
            System.out.printf(event.toString());
        }


        //launch(args);     //запустить граф. интерфейс.

        exit();     //завершение проги
    }

    /**
     * For GUI.
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("gui/main.fxml"));
        primaryStage.setTitle("Hahaha");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();

    }
}
