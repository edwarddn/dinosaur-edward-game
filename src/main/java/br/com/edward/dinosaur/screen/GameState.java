package br.com.edward.dinosaur.screen;

import br.com.edward.dinosaur.config.Config;
import br.com.edward.dinosaur.enuns.EnumGameStatus;
import br.com.edward.dinosaur.resource.AssetManager;
import lombok.Getter;

import java.time.ZonedDateTime;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
public class GameState {

    private EnumGameStatus gameStatus;
    private double currentSpeed;
    private ZonedDateTime startTime;
    private boolean isTraining;

    private final Config config;
    private final GameWindow gameWindow;
    private final AssetManager assetManager;
    private final AtomicInteger moonPhase;

    public GameState(final Config config, final GameWindow gameWindow, final AssetManager assetManager) {
        this.moonPhase = new AtomicInteger(0);
        this.config = config;
        this.gameWindow = gameWindow;
        this.assetManager = assetManager;
        this.reloadGame();
    }

    public void gameOver() {
        this.gameStatus = EnumGameStatus.GAME_OVER;
    }

    public void restartGame(final boolean isTraining) {
        this.startTime = ZonedDateTime.now();
        this.isTraining = isTraining;
        this.currentSpeed = this.config.getMinSpeed();
        this.gameStatus = EnumGameStatus.PLAYING;
    }

    public void reloadGame() {
        this.currentSpeed = this.config.getMinSpeed();
        this.gameStatus = EnumGameStatus.WAITING_TO_PLAY;
    }

    public void accelerate(double deltaTime) {
        if (this.currentSpeed < config.getMaxSpeed()) {
            double newSpeed = this.currentSpeed + ((config.getAcceleration() * config.getGameSpeed() / 1000.0) * deltaTime);
            this.currentSpeed = Math.min(newSpeed, config.getMaxSpeed());
        }
    }

    public boolean isGameOverTime() {
        return this.isTraining && this.startTime.isBefore(ZonedDateTime.now().minusHours(1));
    }

    public int getWidth() {
        return this.gameWindow.getWidth();
    }

    public int getHeight() {
        return this.gameWindow.getHeight();
    }
}
