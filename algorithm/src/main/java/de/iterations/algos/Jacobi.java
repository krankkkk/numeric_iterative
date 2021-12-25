package de.iterations.algos;

import java.util.Arrays;

public class Jacobi extends AbstractAlgo {

    public Jacobi(final AlgoConfig config) {
        super(config);
    }

    @Override
    protected void iterate(double[] approx, double[] previous) {
        Arrays.setAll(approx, i -> calc(i, previous));
    }

    private double calc(int i, double[] previous) {
        double result = this.matrix[i][this.matrix[i].length - 1];
        for (int j = 0; j < previous.length; j++)
            if (j != i)
                result -= matrix[i][j] * previous[j];
        return result / this.matrix[i][i];
    }
}
