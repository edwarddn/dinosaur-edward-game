package br.com.edward.dinosaur.ai;

import br.com.edward.dinosaur.enums.EnumTypeFunction;
import br.com.edward.dinosaur.helper.ObjectUtil;
import lombok.Getter;

import java.io.Serializable;
import java.util.Optional;
import java.util.SplittableRandom;

@Getter
public class NeuralNetwork implements Serializable {

    private static final int INPUTS = 7;
    private static final int HIDDEN = 8;
    private static final int OUTPUTS = 2;

    private final int age;
    private final int generation;
    private final Layer inputLayer;
    private final Layer hiddenLayer;
    private final Layer outputLayer;

    public NeuralNetwork() {
        this(INPUTS, HIDDEN, OUTPUTS);
    }

    private NeuralNetwork(final int inputs, final int hidden, final int outputs) {
        final var random = new SplittableRandom();
        this.age = 1;
        this.generation = 1;
        this.inputLayer = new Layer(random, hidden, inputs, EnumTypeFunction.RELU);
        this.hiddenLayer = new Layer(random, hidden, hidden, EnumTypeFunction.RELU);
        this.outputLayer = new Layer(random, outputs, hidden, EnumTypeFunction.SIGMOID);
    }

    public NeuralNetwork(final NeuralNetwork parent) {
        final var random = new SplittableRandom();
        this.age = 1;
        this.generation = parent.generation + 1;
        this.inputLayer = new Layer(random, parent.inputLayer);
        this.hiddenLayer = new Layer(random, parent.hiddenLayer);
        this.outputLayer = new Layer(random, parent.outputLayer);
    }

    public NeuralNetwork(final NeuralNetwork father, final NeuralNetwork mother) {
        final var random = new SplittableRandom();
        this.age = 1;
        this.generation = Math.max(father.generation, mother.generation) + 1;
        this.inputLayer = new Layer(random, father.inputLayer, mother.inputLayer);
        this.hiddenLayer = new Layer(random, father.hiddenLayer, mother.hiddenLayer);
        this.outputLayer = new Layer(random, father.outputLayer, mother.outputLayer);
    }

    private NeuralNetwork(final NeuralNetwork parent, final int age) {
        this.age = age;
        this.generation = parent.generation;
        this.inputLayer = new Layer(parent.inputLayer);
        this.hiddenLayer = new Layer(parent.hiddenLayer);
        this.outputLayer = new Layer(parent.outputLayer);
    }

    public static NeuralNetwork elite(final NeuralNetwork parent) {
        return new NeuralNetwork(parent, parent.age + 1);
    }

    public static Optional<NeuralNetwork> get() {
        return ObjectUtil.<NeuralNetwork>readObjectFromFile(NeuralNetwork.class)
                .filter(NeuralNetwork::hasExpectedTopology);
    }

    private boolean hasExpectedTopology() {
        return matches(this.inputLayer, HIDDEN, INPUTS)
                && matches(this.hiddenLayer, HIDDEN, HIDDEN)
                && matches(this.outputLayer, OUTPUTS, HIDDEN);
    }

    private static boolean matches(final Layer layer, final int neurons, final int connections) {
        if (layer == null || layer.getNeurons().length != neurons) {
            return false;
        }
        for (final var neuron : layer.getNeurons()) {
            if (neuron == null || neuron.getWeights().length != connections) {
                return false;
            }
        }
        return true;
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
