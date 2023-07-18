package br.com.edward.dinosaur.ai;

import br.com.edward.dinosaur.helper.ObjectUtil;
import lombok.Getter;

import java.io.Serializable;
import java.util.Optional;
import java.util.SplittableRandom;

@Getter
public class NeuralNetwork implements Serializable {

    private static final int BIAS = 1;

    private final int generation;
    private final Layer inputLayer;
    private final Layer[] hiddenLayers;
    private final Layer outputLayer;

    public NeuralNetwork(final int inputs, final int hidden, int hiddenLayers, final int outputs) {
        final var random = new SplittableRandom();
        this.generation = 1;

        this.inputLayer = new Layer(random, inputs + BIAS, inputs + BIAS);
        this.hiddenLayers = new Layer[hiddenLayers];
        for (int i = 0; i < hiddenLayers; i++) {
            this.hiddenLayers[i] = new Layer(random, hidden + BIAS, inputs + BIAS);
        }
        this.outputLayer = new Layer(random, outputs, hidden + BIAS);
    }

    public NeuralNetwork(final NeuralNetwork neuralNetwork) {
        final var random = new SplittableRandom();
        this.generation = neuralNetwork.generation + 1;

        this.inputLayer = new Layer(random, neuralNetwork.getInputLayer());
        this.hiddenLayers = new Layer[neuralNetwork.getHiddenLayers().length];
        for (int i = 0; i < neuralNetwork.getHiddenLayers().length; i++) {
            this.hiddenLayers[i] = new Layer(random, neuralNetwork.getHiddenLayers()[i]);
        }
        this.outputLayer = new Layer(random, neuralNetwork.getOutputLayer());
    }

    public double[] getOutput(final double[] inputs) {
        final var layerInputs = new double[inputs.length + BIAS];
        System.arraycopy(inputs, 0, layerInputs, 0, inputs.length);
        layerInputs[inputs.length] = BIAS;

        var layerOutputs = this.inputLayer.getOutput(layerInputs);
        for (Layer hiddenLayer : hiddenLayers) {
            layerOutputs = hiddenLayer.getOutput(layerOutputs);
        }
        return this.outputLayer.getOutput(layerOutputs);
    }

    public void save() {
        ObjectUtil.writeObjectToFile(this);
    }

    public static Optional<NeuralNetwork> get() {
        return ObjectUtil.readObjectFromFile(NeuralNetwork.class);
    }
}
