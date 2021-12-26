package dev.tkrause.iterations.algos;

import java.util.function.Function;

public enum Algo {
    SOR(config -> new SOR(config, 1.03)),
    GaussSeidel(GaussSeidel::new),
    Jacobi(Jacobi::new);

    private final Function<AlgoConfig, Algorithm> supplier;

    Algo(Function<AlgoConfig, Algorithm> supplier) {
        this.supplier = supplier;
    }

    public Algorithm getInstance(final AlgoConfig config) {
        return this.supplier.apply(config);
    }
}
