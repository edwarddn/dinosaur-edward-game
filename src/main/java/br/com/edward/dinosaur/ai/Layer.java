package br.com.edward.dinosaur.ai;

import lombok.Getter;

import java.io.Serializable;
import java.util.SplittableRandom;

@Getter
public class Layer implements Serializable {

    private final Neuron[] neurons;

    public Layer(final SplittableRandom random, final int size, final int connections) {
        this.neurons = new Neuron[size];
        for (int i = 0; i < size; i++) {
            this.neurons[i] = new Neuron(random, connections);
        }
    }

    public Layer(final SplittableRandom random, final Layer layer) {
        this.neurons = new Neuron[layer.neurons.length];
        for (int i = 0; i < layer.neurons.length; i++) {
            this.neurons[i] = new Neuron(random, layer.getNeurons()[i]);
        }
    }

    public double[] getOutput(final double[] inputs) {
        final double[] output = new double[neurons.length];
        for (int i = 0; i < neurons.length; i++) {
            output[i] = inputs[i] * neurons[i].getOutput(inputs);
        }
        return output;
    }
}