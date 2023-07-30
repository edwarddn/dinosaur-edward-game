package br.com.edward.dinosaur.ai;

import lombok.Getter;

import java.io.Serializable;
import java.util.SplittableRandom;

@Getter
public class Neuron implements Serializable {

    private static final double MUTATION_SCALE = 60.0;

    private final double[] weights;
    private transient boolean active;

    private Neuron(final double[] weights) {
        this.weights = weights;
    }

    public Neuron(final SplittableRandom random, final int size) {
        // Generates initial random weights in the range -1000 to 1000
        this(random.doubles(size, -1000, 1000).toArray());
    }

    public Neuron(final SplittableRandom random, final Neuron neuron) {
        // Creates a new neuron with mutated weights based on the old neuron
        this(new double[neuron.getWeights().length]);
        for (int i = 0; i < neuron.getWeights().length; i++) {
            final var perturbation = (random.nextDouble() * 2 - 1) * MUTATION_SCALE;
            this.weights[i] = Math.max(-1000, Math.min(1000, neuron.getWeights()[i] + perturbation));
        }
    }

    public double getOutput(final double[] inputs) {
        // This method assumes that the length of `inputs` is equal to the length of `weights`
        if (inputs.length != weights.length) {
            throw new IllegalArgumentException("The size of the input array does not match the size of the weights array");
        }

        double sum = 0.0;

        // Sums the product of the inputs and the corresponding weights
        for (int i = 0; i < weights.length; i++) {
            sum += inputs[i] * weights[i];
        }

        // Applies the ReLU activation function
        final var output = Math.max(0, sum);
        this.active = output > 0;
        return output;
    }
}