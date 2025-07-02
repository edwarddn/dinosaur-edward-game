package br.com.edward.dinosaur.ai;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class NeuralNetworkTests {

    private NeuralNetwork network;

    @BeforeEach
    void setUp() {
        this.network = new NeuralNetwork();
    }

    @Test
    void shouldProduceValidOutputForCactusScenario() {

        final double distance = 500.0;
        final double enemyPositionY = 450.0;
        final double enemyWidth = 50.0;
        final double enemyHeight = 100.0;
        final double dinoPositionY = 420.0;
        final double speed = 25.0;

        final double maxGameSpeed = 38.0;
        final double maxGameHeight = 600.0;
        final double maxGameWidth = 1536.0;

        double normalizedDistance = distance / maxGameWidth;
        double normalizedEnemyY = enemyPositionY / maxGameHeight;
        double normalizedEnemyWidth = enemyWidth / 150.0;
        double normalizedEnemyHeight = enemyHeight / 100.0;
        double normalizedDinoY = dinoPositionY / maxGameHeight;
        double normalizedSpeed = speed / maxGameSpeed;

        double isCactus = 1.0;
        double isBird = 0.0;

        final double[] inputs = new double[] {
                Math.max(0, Math.min(1, normalizedDistance)),
                Math.max(0, Math.min(1, normalizedEnemyY)),
                Math.max(0, Math.min(1, normalizedEnemyWidth)),
                Math.max(0, Math.min(1, normalizedEnemyHeight)),
                Math.max(0, Math.min(1, normalizedDinoY)),
                Math.max(0, Math.min(1, normalizedSpeed)),
                isCactus,
                isBird
        };

        final var output = network.getOutput(inputs);

        assertThat(output).isNotNull().hasSize(2);

        assertThat(output[0]).isBetween(0.0, 1.0);
        assertThat(output[1]).isBetween(0.0, 1.0);
    }
}