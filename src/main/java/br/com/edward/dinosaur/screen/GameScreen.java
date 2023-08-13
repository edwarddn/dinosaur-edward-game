package br.com.edward.dinosaur.screen;

import br.com.edward.dinosaur.config.Config;
import br.com.edward.dinosaur.entity.*;
import br.com.edward.dinosaur.enuns.EnumGameStatus;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;

@Slf4j
public class GameScreen extends JPanel implements Runnable {

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.00");

    private final transient Config config;
    private final transient ScreenManager screenManager;
    private final transient ScoreLabel scoreLabel;
    private final transient Dinosaur player;
    private final transient ReplayButton replayButton;
    private final transient MenuButton versusButton;
    private final transient MenuButton trainingButton;
    private final transient GameOverLabel gameOverLabel;
    private final transient Thread thread;

    public GameScreen(final Config config) {
        this.config = config;
        this.scoreLabel = new ScoreLabel(config);
        this.player = new Dinosaur(config, true, null);
        this.replayButton = new ReplayButton(config);
        this.versusButton = new MenuButton(config, 0);
        this.trainingButton = new MenuButton(config, 1);
        this.gameOverLabel = new GameOverLabel(config);
        this.screenManager = new ScreenManager(config, this.player);
        this.createListener();
        this.thread = new Thread(this);
    }

    public void startGame() {
        this.thread.start();
    }

    private void gameUpdate(final double deltaTime) {
        if (EnumGameStatus.WAITING_TO_PLAY.equals(this.config.getGameState())) {
            this.screenManager.update(deltaTime);
            this.player.update(deltaTime);
        } else if (EnumGameStatus.PLAYING.equals(this.config.getGameState())) {
            this.player.update(deltaTime);
            this.screenManager.update(deltaTime);
            final long score = this.screenManager.getBetterDinosaur().map(Dinosaur::getScore).orElse(0L);
            this.scoreLabel.update(score);
            this.scoreLabel.update(this.player.getScore(), score);
            if (this.screenManager.getDinosaurs().isEmpty() && this.player.isDeath()) {
                this.gameOver();
            }
        }
    }

    private void gameRender(final Graphics2D g2d) {
        g2d.setColor(Color.decode("#202124"));
        g2d.fillRect(0, 0, getWidth(), getHeight());
        if (EnumGameStatus.WAITING_TO_PLAY.equals(this.config.getGameState())) {
            this.screenManager.draw(g2d);
            this.player.draw(g2d);
            this.versusButton.draw(g2d);
            this.trainingButton.draw(g2d);
        } else if (EnumGameStatus.PLAYING.equals(this.config.getGameState()) || EnumGameStatus.GAME_OVER.equals(this.config.getGameState())) {
            this.screenManager.draw(g2d);
            this.scoreLabel.draw(g2d);
            this.player.draw(g2d);
            if (EnumGameStatus.GAME_OVER.equals(this.config.getGameState())) {
                this.gameOverLabel.draw(g2d);
                this.replayButton.draw(g2d);
            }
        }
    }


    private void gameDraw(final BufferedImage img) {
        Graphics g2 = this.getGraphics();
        g2.drawImage(img, 0, 0, null);
        g2.dispose();
    }

    @Override
    public void run() {
        final long msPerFrame = (long) Math.floor(1000.0 / config.getFps());
        long previousTime = System.currentTimeMillis();

        while (true) {

            final int width = config.getWidth();
            final int height = config.getHeight();

            final var image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            final var g2d = (Graphics2D) image.getGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            try {
                int fps = 0;
                int i = 0;
                long start = System.currentTimeMillis();

                do {
                    final var startTime = System.currentTimeMillis();
                    final var deltaTime = (startTime - previousTime);

                    i++;
                    final double elapsedSeconds = (startTime - start) / 1000.0;
                    if (elapsedSeconds >= 1.0) {
                        fps = i;
                        i = 0;
                        start = startTime;
                    }

                    this.gameRender(g2d);
                    this.statisticsRender(g2d, fps);
                    this.gameDraw(image);
                    this.gameUpdate(deltaTime);

                    final var waitTime = msPerFrame - (System.currentTimeMillis() - startTime);
                    TimeUnit.MILLISECONDS.sleep(waitTime > 0 ? waitTime : 0);
                    previousTime = startTime;
                } while (width == config.getWidth() && height == config.getHeight());
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    private void statisticsRender(final Graphics2D g2d, int fps) {
        g2d.setColor(Color.WHITE);
        g2d.setFont(config.getFont().deriveFont(12f));
        g2d.drawString("FPS.: " + fps, 5, 15);

        if (this.config.isShowStatistics()) {
            g2d.drawString("Speed.: " + DECIMAL_FORMAT.format(this.config.getSpeed()), 5, 35);
            g2d.drawString("Generation.: " + getGeneration(), 5, 55);
            g2d.drawString("Population.: " + this.screenManager.getDinosaurs().size(), 5, 75);
        }
    }

    private int getGeneration() {
        return this.screenManager.getBetterDinosaur()
                .map(dinosaur -> dinosaur.getNeuralNetwork().getGeneration())
                .orElse(0);
    }

    private void saveNeuralNetwork() {
        final var betterDinosaur = this.screenManager.getBetterDinosaur();
        if (betterDinosaur.isPresent() && this.config.isTraining()) {
            betterDinosaur.get().getNeuralNetwork().save();
        }
    }

    private void gameOver() {
        this.gameOver(this.config.isTraining());
    }

    private void gameOver(final boolean isTraining) {
        this.config.gameOver();
        this.saveNeuralNetwork();
        if (isTraining) {
            this.restartGame();
        }
    }

    private void restartGame() {
        this.restartGame(this.config.isTraining());
    }

    private void restartGame(final boolean isTraining) {
        this.config.restartGame(isTraining);
        this.screenManager.reset();
        this.scoreLabel.reset();
        if (!isTraining) {
            this.player.revive();
        }
    }

    private void reloadGame() {
        this.restartGame(false);
        this.config.reloadGame();
    }

    private void createListener() {
        this.setFocusable(true);
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(final KeyEvent e) {
                customKeyPressed(e);
            }

            @Override
            public void keyReleased(final KeyEvent e) {
                customKeyReleased(e);
            }
        });
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                customMouseClicked(e);
            }
        });
    }

    private void customKeyPressed(final KeyEvent e) {
        switch (this.config.getGameState()) {
            case PLAYING -> {
                if (e.getKeyCode() == KeyEvent.VK_SPACE || e.getKeyCode() == KeyEvent.VK_UP) {
                    this.player.jump();
                } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    this.player.down(true);
                } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    this.gameOver(false);
                }
            }
            case GAME_OVER -> {
                if (e.getKeyCode() == KeyEvent.VK_SPACE || e.getKeyCode() == KeyEvent.VK_ENTER) {
                    this.restartGame();
                } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    this.reloadGame();
                }
            }
        }
    }

    private void customKeyReleased(final KeyEvent e) {
        if (EnumGameStatus.PLAYING.equals(this.config.getGameState()) && e.getKeyCode() == KeyEvent.VK_DOWN) {
            this.player.down(false);
        }
    }

    private void customMouseClicked(final MouseEvent e) {
        if (EnumGameStatus.GAME_OVER.equals(this.config.getGameState()) && (this.replayButton.getBound().contains(e.getX(), e.getY()))) {
            this.restartGame();
        }
        if (EnumGameStatus.WAITING_TO_PLAY.equals(this.config.getGameState())) {
            if (this.versusButton.getBound().contains(e.getX(), e.getY())) {
                this.restartGame(false);
            }
            if (this.trainingButton.getBound().contains(e.getX(), e.getY())) {
                this.restartGame(true);
            }
        }
    }
}
