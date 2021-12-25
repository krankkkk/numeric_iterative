package dev.tkrause.iterations.algos;

import java.util.Arrays;

public class GaussSeidel extends AbstractAlgo {

    public GaussSeidel(final AlgoConfig config) {
        super(config);
    }

    @Override
    protected void iterate(double[] approx, double[] previous) {
        Arrays.setAll(approx, i -> calc(i, approx, previous));
    }

    protected double calc(final int i,
                          final double[] current,
                          final double[] previous) {

        double result = this.matrix[i][this.matrix[i].length - 1];
        for (int j = 0; j < previous.length; j++)
            if (j != i)
                result -= this.matrix[i][j] * (j > i ? previous[j] : current[j]);
        return result / this.matrix[i][i];
    }
}
