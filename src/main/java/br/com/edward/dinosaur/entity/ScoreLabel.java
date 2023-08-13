package br.com.edward.dinosaur.entity;

import br.com.edward.dinosaur.config.Config;
import br.com.edward.dinosaur.helper.ResourceUtil;

import java.awt.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ScoreLabel {

    private final ExecutorService singleThreadExecutor;
    private final Config config;

    private long highScore;
    private long score;
    private long youScore;
    private long aiScore;

    public ScoreLabel(final Config config) {
        this.singleThreadExecutor = Executors.newSingleThreadExecutor();
        this.config = config;
        this.reset();
    }

    public void update(final long score) {
        CompletableFuture.runAsync(() -> {
            while (score > this.score) {
                this.score++;
                if (this.score > this.highScore) {
                    this.highScore = this.score;
                    if (this.highScore % 1000 == 0) {
                        ResourceUtil.playSound(this.config.getReached());
                    }
                }
            }
        }, singleThreadExecutor);
    }

    public void update(final long youScore, final long aiScore) {
        if (youScore != this.youScore) {
            this.youScore = youScore;
        }

        if (aiScore != this.aiScore) {
            this.aiScore = aiScore;
        }
    }

    public void draw(final Graphics2D g2d) {
        g2d.setColor(new Color(91, 91, 91));
        g2d.setFont(config.getFont().deriveFont(23f));

        final var text = this.getText();
        g2d.drawString(text, this.config.getWidth() - (text.length() * 25), 45);
    }

    private String getText() {
        if (this.config.isTraining()) {
            return String.format("HI %05d %05d", this.highScore, this.score);
        }
        return String.format("AI %05d YOU %05d", this.aiScore, this.youScore);
    }

    public void reset() {
        this.score = 0;
        this.youScore = 0;
        this.aiScore = 0;
    }
}
