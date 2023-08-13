package br.com.edward.dinosaur.ai;

import lombok.Getter;

import java.io.Serializable;
import java.util.SplittableRandom;

@Getter
public class Neuron implements Serializable {

    private static final double MUTATION_SCALE = 60.0;
    private static final double WEIGHT_RANGE = 1000.0;

    private final double[] weights;
    private final double bias;
    private transient boolean active;

    public Neuron(final SplittableRandom random, final int size) {
        this.weights = random.doubles(size, -WEIGHT_RANGE, WEIGHT_RANGE).toArray();
        this.bias = random.nextDouble(-WEIGHT_RANGE, WEIGHT_RANGE);
    }

    public Neuron(final SplittableRandom random, final Neuron neuron) {
        this.weights = new double[neuron.getWeights().length];
        for (int i = 0; i < neuron.getWeights().length; i++) {
            final var perturbation = (random.nextDouble() * 2 - 1) * MUTATION_SCALE;
            this.weights[i] = Math.max(-WEIGHT_RANGE, Math.min(WEIGHT_RANGE, neuron.getWeights()[i] + perturbation));
        }

        final var biasPerturbation = (random.nextDouble() * 2 - 1) * MUTATION_SCALE;
        this.bias = Math.max(-WEIGHT_RANGE, Math.min(WEIGHT_RANGE, neuron.bias + biasPerturbation));
    }

    public double getOutput(final double[] inputs) {
        double sum = bias;
        for (int i = 0; i < weights.length; i++) {
            sum += inputs[i] * weights[i];
        }

        // Applying the ReLU activation function
        final var output = Math.max(0, sum);
        this.active = output > 0.0;
        return output;
    }
}