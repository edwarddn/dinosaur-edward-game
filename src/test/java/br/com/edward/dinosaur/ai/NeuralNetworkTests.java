package br.com.edward.dinosaur.ai;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class NeuralNetworkTests {

    @Test
    void testNeuralNetwork() {

        final double distance = 600.0;
        final double height = 100.0;
        final double speed = 30.67;

        final var network = new NeuralNetwork();
        assertThat(network).isNotNull();

        final var output = network.getOutput(new double[] {distance, height, speed});
        assertThat(output).hasSize(2);

        assertThat(output[0]).isNotNegative();
        assertThat(output[1]).isNotNegative();
    }
}