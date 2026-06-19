package br.com.edward.dinosaur.ai;

import br.com.edward.dinosaur.enums.EnumTypeFunction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.SplittableRandom;

import static org.assertj.core.api.Assertions.assertThat;

class NeuronTests {

    private static final double WEIGHT_RANGE = 8.0;

    private final SplittableRandom random = new SplittableRandom();

    @Test
    @DisplayName("Constructor creates the requested number of weights with a zero bias")
    void testInitialization() {
        final var neuron = new Neuron(random, 7, EnumTypeFunction.RELU);

        assertThat(neuron.getWeights()).hasSize(7);
        assertThat(neuron.getBias()).isZero();
        assertThat(neuron.getEnumTypeFunction()).isEqualTo(EnumTypeFunction.RELU);
    }

    @Test
    @DisplayName("RELU never returns a negative value")
    void testReluActivation() {
        final var neuron = new Neuron(random, 3, EnumTypeFunction.RELU);

        assertThat(neuron.getOutput(new double[]{1.0, 1.0, 1.0})).isGreaterThanOrEqualTo(0.0);
    }

    @Test
    @DisplayName("SIGMOID keeps the output within the open interval (0, 1)")
    void testSigmoidActivation() {
        final var neuron = new Neuron(random, 3, EnumTypeFunction.SIGMOID);

        assertThat(neuron.getOutput(new double[]{0.5, 0.5, 0.5})).isStrictlyBetween(0.0, 1.0);
    }

    @Test
    @DisplayName("Exact copy replicates weights and bias unchanged (used by elitism)")
    void testExactCopy() {
        final var original = new Neuron(random, 5, EnumTypeFunction.RELU);

        final var copy = new Neuron(original);

        assertThat(copy.getWeights()).containsExactly(original.getWeights());
        assertThat(copy.getBias()).isEqualTo(original.getBias());
        assertThat(copy.getEnumTypeFunction()).isEqualTo(original.getEnumTypeFunction());
    }

    @Test
    @DisplayName("Mutated copy keeps the structure and respects the weight range")
    void testMutatedCopy() {
        final var original = new Neuron(random, 8, EnumTypeFunction.RELU);

        final var mutated = new Neuron(random, original);

        assertThat(mutated.getWeights()).hasSameSizeAs(original.getWeights());
        assertThat(mutated.getEnumTypeFunction()).isEqualTo(original.getEnumTypeFunction());
        for (final double weight : mutated.getWeights()) {
            assertThat(weight).isBetween(-WEIGHT_RANGE, WEIGHT_RANGE);
        }
    }

    @Test
    @DisplayName("Crossover produces a neuron with the same topology as its parents")
    void testCrossover() {
        final var father = new Neuron(random, 6, EnumTypeFunction.SIGMOID);
        final var mother = new Neuron(random, 6, EnumTypeFunction.SIGMOID);

        final var child = new Neuron(random, father, mother);

        assertThat(child.getWeights()).hasSize(6);
        assertThat(child.getEnumTypeFunction()).isEqualTo(EnumTypeFunction.SIGMOID);
    }
}
