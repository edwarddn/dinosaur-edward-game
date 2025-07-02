package br.com.edward.dinosaur.screen;

import br.com.edward.dinosaur.config.Config;
import br.com.edward.dinosaur.helper.ResourceUtil;
import br.com.edward.dinosaur.resource.AssetManager;

import javax.swing.*;

public class GameWindow extends JFrame {

    private final GameScreen gameScreen;

    public GameWindow(final Config config, final AssetManager assetManager) {
        this.setLocation(80, 10);
        this.setIconImage(ResourceUtil.getResourceImage("icons/icon.png"));
        this.setResizable(config.isResizable());
        this.setTitle(config.getTitle());
        this.setSize(config.getInitialWidth(), config.getInitialHeight());
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        this.gameScreen = new GameScreen(new GameState(config, this, assetManager));
        super.add(this.gameScreen);
    }

    public void startGame() {
        super.setVisible(true);
        this.gameScreen.startGame();
    }
}
