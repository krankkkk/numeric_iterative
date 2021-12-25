package de.iterations.algos;

import java.util.Arrays;

public class SOR extends GaussSeidel {

    private final double w;

    public SOR(final AlgoConfig config,
               final double w) {
        super(config);
        this.w = w;
    }

    public double getW() {
        return w;
    }

    @Override
    protected void iterate(double[] approx, double[] previous) {
        Arrays.setAll(approx, i -> (1 - w) * approx[i] + w * calc(i, approx, previous));
    }
}
