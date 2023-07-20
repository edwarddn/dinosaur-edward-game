package br.com.edward.dinosaur.config;

import br.com.edward.dinosaur.enuns.EnumGameStatus;
import br.com.edward.dinosaur.helper.ResourceUtil;
import br.com.edward.dinosaur.record.Sprite;
import br.com.edward.dinosaur.screen.GameWindow;
import lombok.Builder;
import lombok.Getter;

import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

@Getter
@Builder
public class Config {

    private final String title;
    private final boolean resizable;
    private final int width;
    private final int height;

    private final boolean showCollision;
    private final boolean collision;
    private final boolean showStatistics;

    private final int populationSize;
    private final int enemyDistance;
    private final double minSpeed;
    private final double maxSpeed;
    private final double acceleration;
    private final double gravity;
    private final double jumpSpeed;
    private final double gameSpeed;
    private final double fps;

    private EnumGameStatus gameState;
    private GameWindow window;

    private boolean isTraining;
    private double score;
    private double speed;

    private Sprite cactusSprite;
    private Sprite groundsSprite;
    private Sprite cloudsSprite;
    private Sprite starsSprite;
    private Sprite moonSprite;
    private Sprite birdsSprite;
    private Sprite replayButtonsSprite;
    private Sprite menuButtonsSprite;
    private Sprite gameOverSprite;

    private Sprite dinoStandingSprite;
    private Sprite dinoRunSprite;
    private Sprite dinoJumpSprite;
    private Sprite dinoDownSprite;
    private Sprite dinoDeathSprite;

    private Clip press;
    private Clip hit;
    private Clip reached;

    private Font font;

    public Config setup(final GameWindow window) {
        this.gameState = Objects.nonNull(this.gameState) ? gameState : EnumGameStatus.WAITING_TO_PLAY;
        this.window = window;
        this.speed = this.minSpeed;
        this.score = 0;

        this.window.setIconImage(ResourceUtil.getResourceImage("icons/icon.png"));
        this.window.setResizable(this.resizable);
        this.window.setTitle(this.title);
        this.window.setSize(this.width, this.height);
        this.window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        this.cactusSprite = this.createCactusSprite();
        this.groundsSprite = this.createGroundsSprite();
        this.cloudsSprite = this.createCloudsSprite();
        this.starsSprite = this.createStarsSprite();
        this.moonSprite = this.createMoonSprite();
        this.birdsSprite = this.createBirdsSprite();
        this.replayButtonsSprite = this.createReplayButtonsSprite();
        this.menuButtonsSprite = this.createMenuButtonsSprite();
        this.gameOverSprite = this.createGameOverSprite();

        final var image = ResourceUtil.getResourceImage("img/dinosaurs.png");
        this.dinoStandingSprite = this.createDinoStandingSprite(image);
        this.dinoRunSprite = this.createDinoRunSprite(image);
        this.dinoJumpSprite = this.createDinoJumpSprite(image);
        this.dinoDownSprite = this.createDinoDownSprite(image);
        this.dinoDeathSprite = this.createDinoDeathSprite(image);

        this.press = ResourceUtil.getResourceSound("audio/press.wav");
        this.hit = ResourceUtil.getResourceSound("audio/hit.wav");
        this.reached = ResourceUtil.getResourceSound("audio/reached.wav");

        this.font = ResourceUtil.getResourceFont("font-ttf/press-start2p-regular.ttf");

        return this;
    }

    public void gameOver() {
        this.gameState = EnumGameStatus.GAME_OVER;
    }

    public void restartGame(final boolean isTraining) {
        this.isTraining = isTraining;
        this.speed = this.minSpeed;
        this.score = 0;
        this.gameState = EnumGameStatus.PLAYING;
    }

    public void reloadGame() {
        this.speed = this.minSpeed;
        this.score = 0;
        this.gameState = EnumGameStatus.WAITING_TO_PLAY;
    }

    public void accelerate(double deltaTime) {
        if (this.speed >= this.maxSpeed) {
            this.speed = this.maxSpeed;
        } else {
            this.speed += new BigDecimal(((this.acceleration * this.getGameSpeed()) / 1000.0) * deltaTime).setScale(6, RoundingMode.FLOOR).doubleValue();
        }
    }

    private Sprite createCactusSprite() {
        final var image = ResourceUtil.getResourceImage("img/cactus.png");
        final var sprite = new Sprite(image, 34, 70, 0, 0);
        sprite.addFrame(34, 70, 68, 0);
        sprite.addFrame(34, 70, 136, 0);
        sprite.addFrame(50, 100, 206, 0);
        sprite.addFrame(68, 70, 0, 0);
        sprite.addFrame(68, 70, 68, 0);
        sprite.addFrame(68, 70, 136, 0);

        sprite.addFrame(98, 100, 206, 0);
        sprite.addFrame(98, 100, 306, 0);

        sprite.addFrame(102, 70, 0, 0);
        sprite.addFrame(102, 70, 34, 0);
        sprite.addFrame(102, 70, 68, 0);
        sprite.addFrame(102, 70, 102, 0);
        sprite.addFrame(102, 100, 404, 0);
        sprite.addFrame(150, 100, 356, 0);

        return sprite;
    }

    private Sprite createGroundsSprite() {
        final var image = ResourceUtil.getResourceImage("img/ground.png");
        final var sprite = new Sprite(image, 96, 26, 0, 0);
        sprite.addFrame(74, 26, 96, 0);
        sprite.addFrame(74, 26, 170, 0);
        sprite.addFrame(58, 26, 244, 0);
        sprite.addFrame(54, 26, 302, 0);
        sprite.addFrame(74, 26, 356, 0);
        sprite.addFrame(62, 26, 430, 0);
        sprite.addFrame(72, 26, 492, 0);
        sprite.addFrame(78, 26, 564, 0);
        sprite.addFrame(64, 26, 642, 0);
        sprite.addFrame(56, 26, 706, 0);
        sprite.addFrame(58, 26, 762, 0);
        sprite.addFrame(108, 26, 820, 0);
        sprite.addFrame(102, 26, 928, 0);
        sprite.addFrame(96, 26, 1030, 0);
        sprite.addFrame(156, 26, 1126, 0);
        sprite.addFrame(88, 26, 1282, 0);
        sprite.addFrame(88, 26, 1370, 0);
        sprite.addFrame(91, 26, 1458, 0);
        sprite.addFrame(103, 26, 1549, 0);
        sprite.addFrame(112, 26, 1652, 0);
        sprite.addFrame(106, 26, 1764, 0);
        sprite.addFrame(150, 26, 1870, 0);
        sprite.addFrame(80, 26, 2020, 0);
        sprite.addFrame(48, 26, 2100, 0);
        sprite.addFrame(29, 26, 2148, 0);
        sprite.addFrame(113, 26, 2177, 0);
        sprite.addFrame(110, 26, 2290, 0);
        return sprite;
    }

    private Sprite createCloudsSprite() {
        final var image = ResourceUtil.getResourceImage("img/cloud.png");
        return new Sprite(image, 92, 27, 0, 0);
    }

    private Sprite createStarsSprite() {
        final var image = ResourceUtil.getResourceImage("img/stars.png");
        final var sprite = new Sprite(image, 18, 18, 0, 36);
        sprite.addFrame(18, 18, 0, 18);
        sprite.addFrame(18, 18, 0, 0);
        return sprite;
    }

    private Sprite createMoonSprite() {
        final var image = ResourceUtil.getResourceImage("img/moons.png");
        final var sprite = new Sprite(image, 40, 80, 0, 0);
        sprite.addFrame(40, 80, 40, 0);
        sprite.addFrame(40, 80, 80, 0);
        sprite.addFrame(80, 80, 120, 0);
        sprite.addFrame(40, 80, 200, 0);
        sprite.addFrame(40, 80, 240, 0);
        sprite.addFrame(40, 80, 280, 0);
        sprite.addFrame(0, 0, 0, 0);
        return sprite;
    }

    private Sprite createBirdsSprite() {
        final var image = ResourceUtil.getResourceImage("img/birds.png");
        final var sprite = new Sprite(image, 92, 80, 92, 0);
        sprite.addFrame(92, 80, 0, 0);
        return sprite;
    }

    private Sprite createReplayButtonsSprite() {
        final var image = ResourceUtil.getResourceImage("img/replay-buttons.png");
        final var sprite = new Sprite(image, 72, 64, 0, 0);
        sprite.addFrame(72, 64, 72, 0);
        sprite.addFrame(72, 64, 144, 0);
        sprite.addFrame(72, 64, 216, 0);
        sprite.addFrame(72, 64, 288, 0);
        sprite.addFrame(72, 64, 360, 0);

        sprite.addFrame(72, 64, 360, 0);
        sprite.addFrame(72, 64, 288, 0);
        sprite.addFrame(72, 64, 216, 0);
        sprite.addFrame(72, 64, 144, 0);
        sprite.addFrame(72, 64, 72, 0);
        sprite.addFrame(72, 64, 0, 0);

        return sprite;
    }

    private Sprite createMenuButtonsSprite() {
        final var image = ResourceUtil.getResourceImage("img/menu-buttons.png");
        final var sprite = new Sprite(image, 432, 64, 0, 0);
        sprite.addFrame(432, 64, 0, 64);

        return sprite;
    }

    private Sprite createGameOverSprite() {
        final var image = ResourceUtil.getResourceImage("img/game-over.png");
        return new Sprite(image, 383, 23, 0, 0);
    }

    private Sprite createDinoStandingSprite(final Image image) {
        final var sprite = new Sprite(image, 88, 94, 0, 0);
        sprite.addFrame(88, 94, 88, 0);
        return sprite;
    }

    private Sprite createDinoRunSprite(final Image image) {
        final var sprite = new Sprite(image, 88, 94, 176, 0);
        sprite.addFrame(88, 94, 264, 0);
        return sprite;
    }

    private Sprite createDinoJumpSprite(final Image image) {
        return new Sprite(image, 88, 94, 0, 0);
    }

    private Sprite createDinoDownSprite(final Image image) {
        final var sprite = new Sprite(image, 118, 94, 528, 0);
        sprite.addFrame(118, 94, 646, 0);
        return sprite;
    }

    private Sprite createDinoDeathSprite(final Image image) {
        final var sprite = new Sprite(image, 88, 94, 440, 0);
        sprite.addFrame(88, 94, 352, 0);
        return sprite;
    }

    public int getWidth() {
        return window.getWidth();
    }

    public int getHeight() {
        return window.getHeight();
    }

    public void upScore(final double deltaTime) {
        this.score += BigDecimal.valueOf(((0.2 * this.gameSpeed) / 1000.0) * deltaTime).setScale(6, RoundingMode.FLOOR).doubleValue();
    }
}
