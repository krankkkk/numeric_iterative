package dev.tkrause.iterations.client.views;

import dev.tkrause.iterations.algos.Algo;
import dev.tkrause.iterations.algos.AlgoConfig;
import dev.tkrause.iterations.algos.Algorithm;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
                new NumberAxis("Error between steps", 1, 0.5, 0.05));
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
        algorithm.solve();

        ObservableList<Data<Number, Number>> data = IntStream.range(0, steps.size() - 1)
                .mapToObj(i -> toData(steps, i))
                .collect(FXCollections::observableArrayList, List::add, List::addAll);

        return new Series<>(algorithm.getClass().getSimpleName(), data);
    }

    private Data<Number, Number> toData(List<double[]> steps, int i) {
        return IntStream.range(0, steps.get(i).length)
                .mapToDouble(j -> steps.get(i)[j] - steps.get(i + 1)[j])
                .map(Math::abs)
                .max()
                .stream()
                .mapToObj(err -> new Data<Number, Number>(i + 1, err))
                .findFirst()
                .orElseThrow();
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
