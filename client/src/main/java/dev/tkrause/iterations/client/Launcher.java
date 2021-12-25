package dev.tkrause.iterations.client;

import dev.tkrause.iterations.client.views.GraphView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public final class Launcher extends Application {
    private static double[][] MATRIX;
    private static double EPSILON;


    public static void main(String[] args) {
        MATRIX = new double[][]{
                {10, -1, 2, 0, 6},
                {-1, 11, -1, 3, 25},
                {2, -1, 10, -1, -11},
                {0, 3, -1, 8, 15}
        };

        EPSILON = 1e-3;

        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setScene(new Scene(new GraphView(MATRIX, EPSILON), 900, 900));

        primaryStage.setTitle("Lösung linearer Gleichungssysteme mit iterativen Methoden");
        primaryStage.show();
    }

}
