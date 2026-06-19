package br.com.edward.dinosaur;

import br.com.edward.dinosaur.config.Config;
import br.com.edward.dinosaur.resource.AssetManager;
import br.com.edward.dinosaur.screen.GameWindow;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

import javax.swing.*;

@SpringBootApplication
public class DinosaurApplication implements CommandLineRunner {

    static void main(final String[] args) {
        final var application = new SpringApplicationBuilder(DinosaurApplication.class);
        application.headless(false); // Configures the application to run with a graphical user interface
        application.run(args);
    }

    @Override
    public void run(final String @NonNull ... args) {
        SwingUtilities.invokeLater(() -> {

            final var config = Config.builder()
                    .title("Dinosaur Edward Game")
                    .resizable(true)
                    .initialWidth(1536)
                    .initialHeight(600)
                    .gameSpeed(60)
                    .fps(120)
                    .minSpeed(18)
                    .maxSpeed(38)
                    .gravity(-1.5)
                    .jumpSpeed(25)
                    .acceleration(0.001)
                    .enemyDistance(1000)
                    .populationSize(5000)
                    .showCollision(false)
                    .showStatistics(true)
                    .collision(true)
                    .build();

            AssetManager.getInstance().loadAssets();

            final var gameWindow = new GameWindow(config, AssetManager.getInstance());
            gameWindow.startGame();
        });
    }
}