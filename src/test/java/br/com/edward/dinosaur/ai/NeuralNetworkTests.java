package br.com.edward.dinosaur.ai;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class NeuralNetworkTests {

    @Test
    void testNeuralNetwork() {

        final double distance = 171.11;
        final double width = 32.00;
        final double height = 15.00;
        final double length = 33.00;
        final double speed = 30.67;
        final double position = 15.0;

        final var network = new NeuralNetwork(6, 6, 1, 2);
        assertThat(network).isNotNull();

        final var output = network.getOutput(new double[] {distance, width, height, length, speed, position});
        assertThat(output).hasSize(2);

        assertThat(output[0]).isNotNegative();
        assertThat(output[1]).isNotNegative();
    }
}