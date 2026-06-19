package br.com.edward.dinosaur.ai;

import br.com.edward.dinosaur.enums.EnumTypeFunction;
import lombok.Getter;

import java.io.Serializable;
import java.util.SplittableRandom;

@Getter
public class Layer implements Serializable {

    private final Neuron[] neurons;
    private final EnumTypeFunction typeFunction;

    public Layer(final SplittableRandom random, final int size, final int connections, final EnumTypeFunction typeFunction) {
        this.typeFunction = typeFunction;
        this.neurons = new Neuron[size];
        for (int i = 0; i < size; i++) {
            this.neurons[i] = new Neuron(random, connections, this.typeFunction);
        }
    }

    public Layer(final SplittableRandom random, final Layer layer) {
        this.typeFunction = layer.typeFunction;
        this.neurons = new Neuron[layer.neurons.length];
        for (int i = 0; i < layer.neurons.length; i++) {
            this.neurons[i] = new Neuron(random, layer.neurons[i]);
        }
    }

    public Layer(final SplittableRandom random, final Layer father, final Layer mother) {
        this.typeFunction = father.typeFunction;
        this.neurons = new Neuron[father.neurons.length];
        for (int i = 0; i < father.neurons.length; i++) {
            this.neurons[i] = new Neuron(random, father.neurons[i], mother.neurons[i]);
        }
    }

    public Layer(final Layer layer) {
        this.typeFunction = layer.typeFunction;
        this.neurons = new Neuron[layer.neurons.length];
        for (int i = 0; i < layer.neurons.length; i++) {
            this.neurons[i] = new Neuron(layer.neurons[i]);
        }
    }

    public double[] getOutput(final double[] inputs) {
        final double[] output = new double[neurons.length];
        for (int i = 0; i < neurons.length; i++) {
            output[i] = neurons[i].getOutput(inputs);
        }
        return output;
    }
}
