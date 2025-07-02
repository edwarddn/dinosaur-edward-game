package br.com.edward.dinosaur.ai;

import br.com.edward.dinosaur.enuns.EnumTypeFunction;
import br.com.edward.dinosaur.helper.ObjectUtil;
import lombok.Getter;

import java.io.Serializable;
import java.util.Optional;
import java.util.SplittableRandom;

@Getter
public class NeuralNetwork implements Serializable {

    private final int generation;
    private final Layer inputLayer;
    private final Layer hiddenLayer;
    private final Layer outputLayer;

    public NeuralNetwork() {
        this(8, 8, 2);
    }

    private NeuralNetwork(final int inputs, final int hidden, final int outputs) {
        final var random = new SplittableRandom();
        this.generation = 1;
        this.inputLayer = new Layer(random, inputs, inputs, EnumTypeFunction.RELU);
        this.hiddenLayer = new Layer(random, hidden, inputs, EnumTypeFunction.RELU);
        this.outputLayer = new Layer(random, outputs, hidden, EnumTypeFunction.SIGMOID);
    }

    public NeuralNetwork(final NeuralNetwork neuralNetwork) {
        final var random = new SplittableRandom();
        this.generation = neuralNetwork.generation + 1;

        this.inputLayer = new Layer(random, neuralNetwork.getInputLayer());
        this.hiddenLayer = new Layer(random, neuralNetwork.getHiddenLayer());
        this.outputLayer = new Layer(random, neuralNetwork.getOutputLayer());
    }

    public static Optional<NeuralNetwork> get() {
        return ObjectUtil.readObjectFromFile(NeuralNetwork.class);
    }

    public double[] getOutput(final double[] inputs) {
        var layerOutputs = this.inputLayer.getOutput(inputs);
        layerOutputs = hiddenLayer.getOutput(layerOutputs);
        return this.outputLayer.getOutput(layerOutputs);
    }

    public void save() {
        ObjectUtil.writeObjectToFile(this);
    }
}