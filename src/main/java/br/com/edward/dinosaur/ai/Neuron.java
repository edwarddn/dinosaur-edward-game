package br.com.edward.dinosaur.ai;

import br.com.edward.dinosaur.enums.EnumTypeFunction;
import lombok.Getter;

import java.io.Serializable;
import java.util.SplittableRandom;

@Getter
public class Neuron implements Serializable {

    private static final double WEIGHT_RANGE = 8.0;
    private static final double MUTATION_RATE = 0.2;
    private static final double MUTATION_SCALE = 0.5;
    private static final double MUTATION_RESET_CHANCE = 0.05;

    private final double[] weights;
    private final double bias;
    private final EnumTypeFunction enumTypeFunction;
    private transient boolean active;

    public Neuron(final SplittableRandom random, final int connections, final EnumTypeFunction typeFunction) {
        this.enumTypeFunction = typeFunction;
        this.weights = new double[connections];

        final var standardDeviation = EnumTypeFunction.SIGMOID.equals(typeFunction)
                ? Math.sqrt(1.0 / connections)
                : Math.sqrt(2.0 / connections);
        for (int i = 0; i < connections; i++) {
            this.weights[i] = random.nextGaussian() * standardDeviation;
        }
        this.bias = 0.0;
    }

    public Neuron(final SplittableRandom random, final Neuron neuron) {
        this.enumTypeFunction = neuron.enumTypeFunction;
        this.weights = new double[neuron.weights.length];
        for (int i = 0; i < this.weights.length; i++) {
            this.weights[i] = mutate(random, neuron.weights[i]);
        }
        this.bias = mutate(random, neuron.bias);
    }

    public Neuron(final SplittableRandom random, final Neuron father, final Neuron mother) {
        this.enumTypeFunction = father.enumTypeFunction;
        this.weights = new double[father.weights.length];
        for (int i = 0; i < this.weights.length; i++) {
            this.weights[i] = mutate(random, random.nextBoolean() ? father.weights[i] : mother.weights[i]);
        }
        this.bias = mutate(random, random.nextBoolean() ? father.bias : mother.bias);
    }

    public Neuron(final Neuron neuron) {
        this.enumTypeFunction = neuron.enumTypeFunction;
        this.weights = neuron.weights.clone();
        this.bias = neuron.bias;
    }

    private static double mutate(final SplittableRandom random, final double value) {
        if (random.nextDouble() >= MUTATION_RATE) {
            return value;
        }
        final double mutated = (random.nextDouble() < MUTATION_RESET_CHANCE)
                ? random.nextGaussian()
                : value + random.nextGaussian() * MUTATION_SCALE;
        return Math.clamp(mutated, -WEIGHT_RANGE, WEIGHT_RANGE);
    }

    public double getOutput(final double[] inputs) {
        double sum = bias;
        for (int i = 0; i < weights.length; i++) {
            sum += inputs[i] * weights[i];
        }

        double output = EnumTypeFunction.SIGMOID.equals(this.enumTypeFunction)
                ? 1 / (1 + Math.exp(-sum))
                : Math.max(0, sum);

        this.active = output > 0.0;
        return output;
    }
}
