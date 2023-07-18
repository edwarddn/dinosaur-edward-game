package br.com.edward.dinosaur.screen;

import br.com.edward.dinosaur.config.Config;

import javax.swing.*;

public class GameWindow extends JFrame {

    private final GameScreen gameScreen;

    public GameWindow(final Config config) {
        this.gameScreen = new GameScreen(config.setup(this));
        super.add(this.gameScreen);
    }

    public void startGame() {
        super.setVisible(true);
        this.gameScreen.startGame();
    }
}
