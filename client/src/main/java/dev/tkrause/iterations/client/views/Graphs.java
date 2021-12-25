package dev.tkrause.iterations.client.views;

import dev.tkrause.iterations.algos.Algo;
import dev.tkrause.iterations.algos.AlgoConfig;
import dev.tkrause.iterations.algos.Algorithm;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.CheckBox;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

public class Graphs extends LineChart<Number, Number> {


    private final InputPane inputPane;

    public Graphs(final InputPane inputPane) {
        super(new NumberAxis("Step-count", 1, 10, 1),
                new NumberAxis("Error between steps", 0, 0.5, 0.05));
        getXAxis().setAutoRanging(true);
        getYAxis().setAutoRanging(true);
        this.inputPane = inputPane;
        init();
    }

    private void init() {
        Arrays.stream(Algo.values())
                .map(this::runAlgo)
                .forEach(getData()::add);
    }

    private XYChart.Series<Number, Number> runAlgo(Algo algo) {
        final List<double[]> steps = new LinkedList<>();
        Algorithm algorithm = algo.getInstance(new AlgoConfig(this.inputPane.getMatrix(), this.inputPane.getEpsilon(), steps::add));
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName(algorithm.getClass().getSimpleName());
        algorithm.solve();

        for (int i = 0; i < steps.size() - 1; i++) {
            double[] start = steps.get(i);
            double[] end = steps.get(i + 1);
            double v = IntStream.range(0, start.length)
                    .mapToDouble(j -> start[j] - end[j])
                    .map(Math::abs)
                    .max()
                    .orElseThrow();
            series.getData().add(new XYChart.Data<>(i + 1, v));
        }

        return series;
    }

    public void onCheckBoxAction(CheckBox checkBox) {
        Optional<Series<Number, Number>> optSeries = getData()
                .stream()
                .filter(s -> s.getName().equals(checkBox.getText()))
                .findFirst();

        if (optSeries.isPresent()) {
            optSeries.ifPresent(getData()::remove);
        } else {
            getData().add(runAlgo(Algo.valueOf(checkBox.getText())));
        }
    }
}
