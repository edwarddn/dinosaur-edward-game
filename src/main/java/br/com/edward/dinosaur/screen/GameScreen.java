package br.com.edward.dinosaur.screen;

import br.com.edward.dinosaur.entity.*;
import br.com.edward.dinosaur.enums.EnumGameStatus;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

@Slf4j
public class GameScreen extends JPanel implements Runnable {

    private static final int MAX_UPDATES_PER_FRAME = 5;
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.00");

    private final transient GameState gameState;
    private final transient ScreenManager screenManager;
    private final transient ScoreLabel scoreLabel;
    private final transient Dinosaur player;
    private final transient ReplayButton replayButton;
    private final transient MenuButton versusButton;
    private final transient MenuButton trainingButton;
    private final transient GameOverLabel gameOverLabel;
    private final transient Thread gameThread;
    private final transient Queue<Runnable> actionQueue;

    private transient volatile BufferedImage frontBuffer;
    private transient BufferedImage backBuffer;
    private volatile int currentFps;
    private volatile boolean running;

    public GameScreen(final GameState gameState) {
        this.gameState = gameState;
        this.scoreLabel = new ScoreLabel(this.gameState);
        this.player = new Dinosaur(this.gameState, true, null);
        this.replayButton = new ReplayButton(this.gameState);
        this.versusButton = new MenuButton(this.gameState, 0);
        this.trainingButton = new MenuButton(this.gameState, 1);
        this.gameOverLabel = new GameOverLabel(this.gameState);
        this.screenManager = new ScreenManager(this.gameState, this.player);
        this.actionQueue = new ConcurrentLinkedQueue<>();
        this.createListeners();
        this.gameThread = new Thread(this);
    }

    public void startGame() {
        this.running = true;
        this.gameThread.start();
    }

    private void processActions() {
        Runnable action;
        while ((action = actionQueue.poll()) != null) {
            action.run();
        }
    }

    private void gameUpdate(final double deltaTime) {
        if (EnumGameStatus.WAITING_TO_PLAY.equals(this.gameState.getGameStatus())) {
            this.screenManager.update(deltaTime);
            this.player.update(deltaTime);
        } else if (EnumGameStatus.PLAYING.equals(this.gameState.getGameStatus())) {
            this.player.update(deltaTime);
            this.screenManager.update(deltaTime);
            final long score = this.screenManager.getBetterDinosaur().map(Dinosaur::getScore).orElse(0L);
            this.scoreLabel.updateAnimation(score);
            this.scoreLabel.setScores(this.player.getScore(), score);
            if ((this.screenManager.getDinosaurs().isEmpty() && this.player.isDeath()) || this.gameState.isGameOverTime()) {
                this.gameOver();
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        final var image = this.frontBuffer;
        if (Objects.nonNull(image)) {
            g.drawImage(image, 0, 0, null);
        }
    }

    private void renderFrame() {
        final var configuration = getGraphicsConfiguration();
        final int width = getWidth();
        final int height = getHeight();
        if (Objects.isNull(configuration) || width <= 0 || height <= 0) {
            return;
        }

        if (Objects.isNull(this.backBuffer) || this.backBuffer.getWidth() != width || this.backBuffer.getHeight() != height) {
            this.backBuffer = configuration.createCompatibleImage(width, height);
        }

        final var g2d = this.backBuffer.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        gameRender(g2d);
        statisticsRender(g2d);

        g2d.dispose();

        final var rendered = this.backBuffer;
        this.backBuffer = this.frontBuffer;
        this.frontBuffer = rendered;
    }

    private void gameRender(final Graphics2D g2d) {
        g2d.setColor(Color.decode("#202124"));
        g2d.fillRect(0, 0, getWidth(), getHeight());

        if (EnumGameStatus.WAITING_TO_PLAY.equals(this.gameState.getGameStatus())) {
            this.screenManager.draw(g2d);
            this.player.draw(g2d);
            this.versusButton.draw(g2d);
            this.trainingButton.draw(g2d);
        } else if (EnumGameStatus.PLAYING.equals(this.gameState.getGameStatus()) || EnumGameStatus.GAME_OVER.equals(this.gameState.getGameStatus())) {
            this.screenManager.draw(g2d);
            this.scoreLabel.draw(g2d);
            this.player.draw(g2d);
            if (EnumGameStatus.GAME_OVER.equals(this.gameState.getGameStatus())) {
                this.gameOverLabel.draw(g2d);
                this.replayButton.draw(g2d);
            }
        }
    }

    private void statisticsRender(final Graphics2D g2d) {
        g2d.setColor(Color.WHITE);
        g2d.setFont(gameState.getAssetManager().getFont().deriveFont(12f));
        g2d.drawString("FPS: " + this.currentFps, 5, 15);

        if (this.gameState.getConfig().isShowStatistics()) {
            g2d.drawString("Speed: " + DECIMAL_FORMAT.format(this.gameState.getCurrentSpeed()), 5, 35);
            g2d.drawString("Age: " + getAge(), 5, 55);
            g2d.drawString("Generation: " + getGeneration(), 5, 75);
            g2d.drawString("Population: " + this.screenManager.getDinosaurs().size(), 5, 95);
        }
    }

    @Override
    public void run() {
        final double fixedDeltaMs = 1000.0 / gameState.getConfig().getFps();
        final long fixedDeltaNanos = (long) (1_000_000_000.0 / gameState.getConfig().getFps());
        final long maxAccumulatedNanos = fixedDeltaNanos * MAX_UPDATES_PER_FRAME;

        long previousTime = System.nanoTime();
        long fpsTimer = previousTime;
        long accumulator = 0;
        int frameCount = 0;

        while (this.running) {
            try {
                final long now = System.nanoTime();
                accumulator += Math.min(now - previousTime, maxAccumulatedNanos);
                previousTime = now;

                processActions();
                while (accumulator >= fixedDeltaNanos) {
                    gameUpdate(fixedDeltaMs);
                    accumulator -= fixedDeltaNanos;
                }
                renderFrame();
                repaint();

                frameCount++;
                if (now - fpsTimer >= 1_000_000_000L) {
                    this.currentFps = frameCount;
                    frameCount = 0;
                    fpsTimer = now;
                }

                final long sleepNanos = fixedDeltaNanos - (System.nanoTime() - now);
                if (sleepNanos > 0) {
                    TimeUnit.NANOSECONDS.sleep(sleepNanos);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                this.running = false;
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    private int getAge() {
        return this.screenManager.getBetterDinosaur()
                .map(dinosaur -> dinosaur.getNeuralNetwork().getAge())
                .orElse(0);
    }

    private int getGeneration() {
        return this.screenManager.getBetterDinosaur()
                .map(dinosaur -> dinosaur.getNeuralNetwork().getGeneration())
                .orElse(0);
    }

    private void saveNeuralNetwork() {
        if (this.gameState.isTraining()) {
            this.screenManager.getBetterDinosaur()
                    .ifPresent(dinosaur -> dinosaur.getNeuralNetwork().save());
        }
    }

    private void gameOver() {
        this.gameOver(this.gameState.isTraining());
    }

    private void gameOver(final boolean isTraining) {
        this.gameState.gameOver();
        this.saveNeuralNetwork();
        if (isTraining) {
            this.restartGame();
        }
    }

    private void restartGame() {
        this.restartGame(this.gameState.isTraining());
    }

    private void restartGame(final boolean isTraining) {
        this.gameState.restartGame(isTraining);
        this.screenManager.reset();
        this.scoreLabel.reset();
        if (!isTraining) {
            this.player.revive();
        }
    }

    private void reloadGame() {
        this.restartGame(false);
        this.gameState.reloadGame();
    }

    private void createListeners() {
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
        switch (this.gameState.getGameStatus()) {
            case PLAYING -> {
                if (e.getKeyCode() == KeyEvent.VK_SPACE || e.getKeyCode() == KeyEvent.VK_UP) {
                    actionQueue.add(player::jump);
                } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    actionQueue.add(() -> player.down(true));
                } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    actionQueue.add(() -> gameOver(false));
                }
            }
            case GAME_OVER -> {
                if (e.getKeyCode() == KeyEvent.VK_SPACE || e.getKeyCode() == KeyEvent.VK_ENTER) {
                    actionQueue.add(this::restartGame);
                } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    actionQueue.add(this::reloadGame);
                }
            }
            default -> {}
        }
    }

    private void customKeyReleased(final KeyEvent e) {
        if (EnumGameStatus.PLAYING.equals(this.gameState.getGameStatus()) && e.getKeyCode() == KeyEvent.VK_DOWN) {
            actionQueue.add(() -> player.down(false));
        }
    }

    private void customMouseClicked(final MouseEvent e) {
        if (EnumGameStatus.GAME_OVER.equals(this.gameState.getGameStatus()) && (this.replayButton.getBound().contains(e.getX(), e.getY()))) {
            actionQueue.add(this::restartGame);
        } else if (EnumGameStatus.WAITING_TO_PLAY.equals(this.gameState.getGameStatus())) {
            if (this.versusButton.getBound().contains(e.getX(), e.getY())) {
                actionQueue.add(() -> restartGame(false));
            } else if (this.trainingButton.getBound().contains(e.getX(), e.getY())) {
                actionQueue.add(() -> restartGame(true));
            }
        }
    }
}