package br.com.edward.dinosaur.ai;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class NeuralNetworkTests {

    private static final double[] CACTUS_SCENARIO = {
            500.0 / 1536.0,
            450.0 / 600.0,
            50.0 / 150.0,
            100.0 / 100.0,
            420.0 / 600.0,
            25.0 / 38.0,
            0.0
    };

    private String originalUserHome;

    @BeforeEach
    void redirectUserHome(@TempDir final Path tempHome) {
        this.originalUserHome = System.getProperty("user.home");
        System.setProperty("user.home", tempHome.toString());
    }

    @AfterEach
    void restoreUserHome() {
        System.setProperty("user.home", this.originalUserHome);
    }

    @Test
    @DisplayName("The default topology is 7 inputs, two hidden layers of 8 and 2 outputs")
    void testTopology() {
        final var network = new NeuralNetwork();

        assertThat(network.getInputLayer().getNeurons()).hasSize(8);
        assertThat(network.getInputLayer().getNeurons()[0].getWeights()).hasSize(7);
        assertThat(network.getHiddenLayer().getNeurons()).hasSize(8);
        assertThat(network.getHiddenLayer().getNeurons()[0].getWeights()).hasSize(8);
        assertThat(network.getOutputLayer().getNeurons()).hasSize(2);
        assertThat(network.getOutputLayer().getNeurons()[0].getWeights()).hasSize(8);
    }

    @Test
    @DisplayName("getOutput() returns two outputs, both within the [0, 1] range")
    void testValidOutput() {
        final var output = new NeuralNetwork().getOutput(CACTUS_SCENARIO);

        assertThat(output).hasSize(2);
        assertThat(output[0]).isBetween(0.0, 1.0);
        assertThat(output[1]).isBetween(0.0, 1.0);
    }

    @Test
    @DisplayName("Freshly created networks do not saturate the SIGMOID at 0.0/1.0 (He/Xavier init)")
    void testNoSaturation() {
        final var samples = new double[200];
        for (int i = 0; i < samples.length; i++) {
            final var output = new NeuralNetwork().getOutput(CACTUS_SCENARIO);
            assertThat(output[0]).isStrictlyBetween(0.0, 1.0);
            assertThat(output[1]).isStrictlyBetween(0.0, 1.0);
            samples[i] = output[0];
        }
        assertThat(Arrays.stream(samples).distinct().count()).isGreaterThan(1L);
    }

    @Test
    @DisplayName("A brand new network starts at generation 1")
    void testInitialGeneration() {
        assertThat(new NeuralNetwork().getGeneration()).isEqualTo(1);
    }

    @Test
    @DisplayName("A mutated copy of a parent advances one generation")
    void testChildGeneration() {
        final var parent = new NeuralNetwork();

        assertThat(new NeuralNetwork(parent).getGeneration()).isEqualTo(parent.getGeneration() + 1);
    }

    @Test
    @DisplayName("Crossover takes the highest generation among the parents and adds one")
    void testCrossoverGeneration() {
        final var father = new NeuralNetwork();
        final var mother = new NeuralNetwork(new NeuralNetwork());

        final var child = new NeuralNetwork(father, mother);

        assertThat(child.getGeneration()).isEqualTo(3);
    }

    @Test
    @DisplayName("elite() keeps the weights intact and only advances the generation")
    void testElite() {
        final var parent = new NeuralNetwork();

        final var elite = NeuralNetwork.elite(parent);

        assertThat(elite.getGeneration()).isEqualTo(parent.getGeneration() + 1);
        assertThat(elite.getOutputLayer().getNeurons()[0].getWeights())
                .containsExactly(parent.getOutputLayer().getNeurons()[0].getWeights());
        assertThat(elite.getOutput(CACTUS_SCENARIO)).containsExactly(parent.getOutput(CACTUS_SCENARIO));
    }

    @Test
    @DisplayName("save() persists the network and get() recovers a topology-valid network")
    void testPersistAndRecover() {
        final var original = new NeuralNetwork();
        original.save();

        final var loaded = NeuralNetwork.get();

        assertThat(loaded).isPresent();
        assertThat(loaded.get().getGeneration()).isEqualTo(original.getGeneration());
        assertThat(loaded.get().getOutput(CACTUS_SCENARIO)).containsExactly(original.getOutput(CACTUS_SCENARIO));
    }

    @Test
    @DisplayName("get() returns empty when there is no saved network")
    void testGetWithoutFile() {
        assertThat(NeuralNetwork.get()).isEmpty();
    }
}
