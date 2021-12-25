package de.iterations.algos;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.stream.IntStream;

abstract class AbstractAlgo implements Algorithm {

    protected double[][] matrix;
    private final double epsilon;
    private final Consumer<double[]> onIteration;

    protected AbstractAlgo(final AlgoConfig config) {
        this.matrix = config.matrix();
        this.epsilon = config.epsilon();
        this.onIteration = config.onIteration();
    }

    @Override
    public void solve() {
        if (!makeDominant()) {
            throw new IllegalArgumentException("Cannot translate Matrix to diagonally dominant");
        }

        double error = Double.MAX_VALUE;
        final double[] approx = new double[this.matrix.length];

        while (error > this.epsilon) {
            final double[] previous = Arrays.copyOf(approx, approx.length);
            iterate(approx, previous);

            this.onIteration.accept(Arrays.copyOf(approx, approx.length));
            error = IntStream.range(0, approx.length)
                    .mapToDouble(i -> approx[i] - previous[i])
                    .map(Math::abs)
                    .max()
                    .orElseThrow();
        }
    }

    private boolean makeDominant() {
        return transformToDominant(0, new boolean[matrix.length], new int[matrix.length]);
    }

    private boolean transformToDominant(int recursionDepth,
                                        boolean[] visited,
                                        int[] arr) {
        int rows = matrix.length;
        if (recursionDepth == matrix.length) {
            double[][] T = new double[rows][rows + 1];
            for (int i = 0; i < arr.length; i++) {
                System.arraycopy(matrix[arr[i]], 0, T[i], 0, rows + 1);
            }
            matrix = T;
            return true;
        }
        for (int i = 0; i < rows; i++) {
            if (visited[i])
                continue;
            double sum = 0;
            for (int j = 0; j < rows; j++)
                sum += Math.abs(matrix[i][j]);
            if (2 * Math.abs(matrix[i][recursionDepth]) > sum) {
                // diagonally dominant?
                visited[i] = true;
                arr[recursionDepth] = i;
                if (transformToDominant(recursionDepth + 1, visited, arr))
                    return true;
                visited[i] = false;
            }
        }
        return false;
    }

    protected abstract void iterate(final double[] approx, final double[] previous);
}
