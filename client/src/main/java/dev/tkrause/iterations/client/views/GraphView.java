package dev.tkrause.iterations.client.views;

import dev.tkrause.iterations.algos.Algo;
import dev.tkrause.iterations.algos.AlgoConfig;
import dev.tkrause.iterations.algos.Algorithm;
import dev.tkrause.iterations.algos.SOR;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

public class GraphView extends BorderPane {

    private static final double LOWER_BOUND = 0.1;
    private static final double UPPER_BOUND = 1.9;
    private static final double TICK_RATE = 0.1;

    private final InputPane inputPane;
    private final Graphs graphs;

    public GraphView(final double[][] matrix, final double epsilon) {
        this.inputPane = new InputPane(matrix, epsilon);
        this.graphs = new Graphs(this.inputPane);
        setCenter(this.graphs);
        setRight(getRightSide());
    }

    private Node getRightSide() {
        final List<Node> checkBoxes = new ArrayList<>();
        for (final Algo algo : Algo.values()) {
            final CheckBox checkBox = new CheckBox(algo.name());
            checkBox.setSelected(true);
            checkBox.setOnAction(ev -> this.graphs.onCheckBoxAction(checkBox));
            final Button button = new Button("Show Data");
            button.setOnAction(ev -> this.onShowData(algo));

            checkBoxes.addAll(Arrays.asList(checkBox, button, new Label("")));
        }
        final BorderPane rightBox = new BorderPane();
        rightBox.setTop(new VBox(checkBoxes.toArray(Node[]::new)));

        Button showMinimise = new Button("Show minimizing of SOR");
        showMinimise.setOnAction(ev -> this.onMinimize());
        rightBox.setCenter(showMinimise);

        rightBox.setBottom(this.inputPane);
        return rightBox;
    }

    private void onMinimize() {
        LineChart<Number, Number> chart = new LineChart<>(new NumberAxis("ω", LOWER_BOUND, UPPER_BOUND, TICK_RATE),
                new NumberAxis("Steps to Error-margin", 0, 50, 1));
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName("SOR-Steps");
        chart.getData().add(series);

        DoubleStream.iterate(LOWER_BOUND, d -> d < UPPER_BOUND, d -> d + TICK_RATE / 10)
                .mapToObj(d -> {
                    AtomicInteger i = new AtomicInteger();
                    SOR algo = new SOR(new AlgoConfig(this.inputPane.getMatrix().clone(), this.inputPane.getEpsilon(), e -> i.incrementAndGet()), d);
                    return new Minimize(algo, i);
                })
                .peek(m -> m.algo.solve())
                .forEach(a -> series.getData().add(new XYChart.Data<>(a.algo.getW(), a.i.get())));

        Dialog<Object> dialog = new Dialog<>();
        DialogPane pane = dialog.getDialogPane();
        pane.setContent(chart);
        pane.getButtonTypes().add(ButtonType.CLOSE);
        pane.setMinSize(800, 800);
        dialog.show();
    }

    private record Minimize(SOR algo, AtomicInteger i) {
    }

    private void onShowData(Algo algo) {
        final List<double[]> steps = new LinkedList<>();
        Algorithm instance = algo.getInstance(new AlgoConfig(this.inputPane.getMatrix(), this.inputPane.getEpsilon(), steps::add));
        instance.solve();
        ListView<Step> table = new ListView<>();


        IntStream.range(0, steps.size())
                .mapToObj(i -> new Step(i, steps.get(i)))
                .forEach(table.getItems()::add);

        Dialog<Object> dialog = new Dialog<>();
        dialog.setTitle("%s-Data".formatted(instance.getClass().getSimpleName()));
        DialogPane pane = dialog.getDialogPane();
        pane.setContent(table);
        pane.getButtonTypes().add(ButtonType.CLOSE);
        pane.setMinWidth(400);
        dialog.show();
    }

    private record Step(int i, double[] approx) {
        @Override
        public String toString() {
            NumberFormat instance = DecimalFormat.getInstance();
            instance.setMaximumFractionDigits(4);
            instance.setMinimumFractionDigits(4);

            return Arrays.stream(approx)
                    .mapToObj(v -> instance.format(v) + '\t')
                    .collect(Collectors.joining("", "Step " + (this.i + 1) + '\t', ""));
        }
    }
}
