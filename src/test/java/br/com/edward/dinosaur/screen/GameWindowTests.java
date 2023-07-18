package br.com.edward.dinosaur.screen;

import br.com.edward.dinosaur.config.Config;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;

class GameWindowTests {

    @Test
    @DisplayName("Test start game")
    void testStartGame() {

        final var config = Config.builder()
                .title("Test")
                .resizable(true)
                .width(100)
                .height(100)
                .gameSpeed(60)
                .fps(120)
                .minSpeed(18)
                .maxSpeed(38)
                .gravity(-1.5)
                .jumpSpeed(25)
                .acceleration(0.001)
                .enemyDistance(1000)
                .populationSize(1000)
                .showCollision(false)
                .showStatistics(true)
                .collision(true)
                .build();

        assertThatCode(() -> new GameWindow(config).startGame()).doesNotThrowAnyException();
    }
}