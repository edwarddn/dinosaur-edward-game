package br.com.edward.dinosaur.entity;

import br.com.edward.dinosaur.config.Config;
import br.com.edward.dinosaur.helper.ResourceUtil;

import java.awt.*;

import static br.com.edward.dinosaur.helper.IntUtil.getInt;

public class ScoreLabel {

    private final Config config;

    private int highScore;
    private int score;
    private int youScore;
    private int aiScore;

    public ScoreLabel(final Config config) {
        this.config = config;
        this.highScore = 0;
        this.score = 0;
        this.youScore = 0;
        this.aiScore = 0;
    }

    public void update(final double score) {
        final var intScore = getInt(score);
        if (intScore > this.score) {
            this.score = intScore;
            if (this.score > this.highScore) {
                this.highScore = this.score;
            }

            if (this.score % 100 == 0) {
                ResourceUtil.playSound(this.config.getReached());
            }
        }
    }

    public void update(final double youScore, final double aiScore) {
        final var intYouScore = getInt(youScore);
        if (intYouScore > this.youScore) {
            this.youScore = intYouScore;
        }

        final var intAiScore = getInt(aiScore);
        if (intAiScore > this.aiScore) {
            this.aiScore = intAiScore;
        }
    }

    public void draw(final Graphics2D g2d) {
        g2d.setColor(new Color(91, 91, 91));
        g2d.setFont(config.getFont().deriveFont(23f));

        if (this.config.isTraining()) {
            g2d.drawString(String.format("HI %05d %05d", this.highScore, this.score), this.config.getWidth() - 336, 45);
        } else {
            g2d.drawString(String.format("AI %05d YOU %05d", this.aiScore, this.youScore), this.config.getWidth() - 430, 45);
        }
    }

    public void reset() {
        this.score = 0;
    }
}
