package br.com.edward.dinosaur;

import br.com.edward.dinosaur.config.Config;
import br.com.edward.dinosaur.screen.GameWindow;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

import javax.swing.*;

@SpringBootApplication
public class DinosaurApplication implements CommandLineRunner {

    public static void main(final String[] args) {
        final var application = new SpringApplicationBuilder(DinosaurApplication.class);
        application.headless(false); // Configures the application to run with a graphical user interface
        application.run(args);
    }

    @Override
    public void run(final String... args) {
        SwingUtilities.invokeLater(() -> {

            final var config = Config.builder()
                    .title("Dinosaur Edward Game")
                    .resizable(true)
                    .width(1024)
                    .height(400)
                    .gameSpeed(60)
                    .fps(120)
                    .minSpeed(18)
                    .maxSpeed(38)
                    .gravity(-1.5)
                    .jumpSpeed(25)
                    .acceleration(0.001)
                    .enemyDistance(1000)
                    .populationSize(1500)
                    .showCollision(false)
                    .showStatistics(false)
                    .collision(true)
                    .build();

            final GameWindow game = new GameWindow(config);
            game.startGame();
        });
    }
}