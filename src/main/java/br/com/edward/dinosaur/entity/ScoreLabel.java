package br.com.edward.dinosaur.entity;

import br.com.edward.dinosaur.screen.GameState;

import java.awt.*;

public class ScoreLabel {

    private static final double SCORE_ANIMATION_SPEED = 200.0;

    private final GameState gameState;
    private long highScore;
    private double displayScore;
    private long youScore;
    private long aiScore;

    public ScoreLabel(final GameState gameState) {
        this.gameState = gameState;
        this.reset();
    }

    public void updateAnimation(final double deltaTime) {
        if (this.displayScore < this.aiScore) {
            final var increment = (SCORE_ANIMATION_SPEED / 1000.0) * deltaTime;
            this.displayScore = Math.min(this.displayScore + increment, this.aiScore);
        }
        if (this.getAnimatedScore() > this.highScore) {
            this.highScore = this.getAnimatedScore();
        }
    }

    public void setScores(final long youScore, final long aiScore) {
        this.youScore = youScore;
        this.aiScore = aiScore;
    }

    public void draw(final Graphics2D g2d) {
        g2d.setColor(new Color(91, 91, 91));
        g2d.setFont(this.gameState.getAssetManager().getFont().deriveFont(23f));

        final var text = this.getText();
        final var textWidth = g2d.getFontMetrics().stringWidth(text);
        g2d.drawString(text, this.gameState.getWidth() - textWidth - 25, 45);
    }

    private long getAnimatedScore() {
        return (long) Math.floor(this.displayScore);
    }

    private String getText() {
        if (this.gameState.isTraining()) {
            return String.format("HI %05d %05d", this.highScore, this.getAnimatedScore());
        }
        return String.format("AI %05d YOU %05d", this.aiScore, this.youScore);
    }

    public void reset() {
        this.displayScore = 0;
        this.youScore = 0;
        this.aiScore = 0;
    }
}