package dev.tkrause.iterations.client.views;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public class InputPane extends BorderPane {

    private final double epsilon;
    private final double[][] matrix;

    public InputPane(final double[][] matrix, final double epsilon) {
        this.epsilon = epsilon;
        this.matrix = matrix;

        setCenter(createCenter());
    }

    private Node createCenter() {
        Button input = new Button("Load input");
        input.setOnAction(this::onLoadInput);
        return input;
    }

    private void onLoadInput(ActionEvent ignored) {
        FileChooser chooser = new FileChooser();
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Text File", "*.txt");
        chooser.getExtensionFilters().add(filter);
        chooser.setSelectedExtensionFilter(filter);

        final File chosenFile = chooser.showOpenDialog(getScene().getWindow());
        if (chosenFile == null) {
            return;
        }

        try {
            List<String> input = Files.readAllLines(chosenFile.toPath());

            final double epsilon = Double.parseDouble(input.remove(0));

            double[][] matrix = input.stream()
                    .filter(Predicate.not(String::isBlank))
                    .map(line -> Arrays.stream(line.split("\\s+")).mapToDouble(Double::parseDouble).toArray())
                    .toArray(double[][]::new);
            Scene currentScene = getScene();
            ((Stage) currentScene.getWindow()).setScene(new Scene(new GraphView(matrix, epsilon), currentScene.getWidth(), currentScene.getHeight()));

        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (IllegalArgumentException e) {
            new Alert(Alert.AlertType.ERROR, "File contains illegal characters: " + e.getMessage(), ButtonType.CLOSE).show();
        }
    }

    public double getEpsilon() {
        return epsilon;
    }

    public double[][] getMatrix() {
        return matrix;
    }
}
