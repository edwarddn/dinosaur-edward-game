package br.com.edward.dinosaur.screen;

import br.com.edward.dinosaur.config.Config;
import br.com.edward.dinosaur.resource.AssetManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;

class GameWindowTests {

    @Test
    @DisplayName("GameWindow wires up the whole object graph without throwing")
    void testWiring() {
        final var config = Config.builder()
                .title("Test")
                .resizable(true)
                .initialWidth(100)
                .initialHeight(100)
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

        AssetManager.getInstance().loadAssets();

        assertThatCode(() -> new GameWindow(config, AssetManager.getInstance())).doesNotThrowAnyException();
    }
}
