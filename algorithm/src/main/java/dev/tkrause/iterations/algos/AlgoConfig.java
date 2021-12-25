package dev.tkrause.iterations.algos;

import java.util.function.Consumer;

public record AlgoConfig(double[][] matrix, double epsilon, Consumer<double[]> onIteration) {
}
