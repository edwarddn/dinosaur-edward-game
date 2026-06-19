package br.com.edward.dinosaur.ai;

import br.com.edward.dinosaur.enums.EnumTypeFunction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.SplittableRandom;

import static org.assertj.core.api.Assertions.assertThat;

class LayerTests {

    private final SplittableRandom random = new SplittableRandom();

    @Test
    @DisplayName("Constructor creates the requested number of neurons and connections")
    void testStructure() {
        final var layer = new Layer(random, 4, 7, EnumTypeFunction.RELU);

        assertThat(layer.getNeurons()).hasSize(4);
        assertThat(layer.getTypeFunction()).isEqualTo(EnumTypeFunction.RELU);
        for (final var neuron : layer.getNeurons()) {
            assertThat(neuron.getWeights()).hasSize(7);
        }
    }

    @Test
    @DisplayName("getOutput() returns one value per neuron")
    void testOutputSize() {
        final var layer = new Layer(random, 3, 2, EnumTypeFunction.SIGMOID);

        assertThat(layer.getOutput(new double[]{0.5, 0.5})).hasSize(3);
    }

    @Test
    @DisplayName("Exact copy replicates the topology and the weights of the layer")
    void testExactCopy() {
        final var original = new Layer(random, 3, 4, EnumTypeFunction.RELU);

        final var copy = new Layer(original);

        assertThat(copy.getNeurons()).hasSize(3);
        assertThat(copy.getTypeFunction()).isEqualTo(EnumTypeFunction.RELU);
        for (int i = 0; i < copy.getNeurons().length; i++) {
            assertThat(copy.getNeurons()[i].getWeights()).containsExactly(original.getNeurons()[i].getWeights());
        }
    }

    @Test
    @DisplayName("Crossover preserves the topology of the parent layers")
    void testCrossover() {
        final var father = new Layer(random, 5, 3, EnumTypeFunction.RELU);
        final var mother = new Layer(random, 5, 3, EnumTypeFunction.RELU);

        final var child = new Layer(random, father, mother);

        assertThat(child.getNeurons()).hasSize(5);
        for (final var neuron : child.getNeurons()) {
            assertThat(neuron.getWeights()).hasSize(3);
        }
    }
}
