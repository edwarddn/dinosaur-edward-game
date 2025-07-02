package br.com.edward.dinosaur.resource;

import br.com.edward.dinosaur.helper.ResourceUtil;
import br.com.edward.dinosaur.record.Sprite;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.sound.sampled.Clip;
import java.awt.*;
import java.awt.image.BufferedImage;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AssetManager {

    @Getter
    private static final AssetManager instance = new AssetManager();

    private Sprite cactusSprite;
    private Sprite groundsSprite;
    private Sprite cloudsSprite;
    private Sprite starsSprite;
    private Sprite moonSprite;
    private Sprite birdsSprite;
    private Sprite replayButtonsSprite;
    private Sprite menuButtonsSprite;
    private Sprite gameOverSprite;

    private Sprite dinosaurStandingSprite;
    private Sprite dinosaurRunSprite;
    private Sprite dinosaurJumpSprite;
    private Sprite dinosaurDownSprite;
    private Sprite dinosaurDeathSprite;

    private Sprite bestDinosaurStandingSprite;
    private Sprite bestDinosaurRunSprite;
    private Sprite bestDinosaurJumpSprite;
    private Sprite bestDinosaurDownSprite;
    private Sprite bestDinosaurDeathSprite;

    private Clip press;
    private Clip hit;
    private Clip reached;

    private Font font;

    public void loadAssets() {
        this.cactusSprite = this.createCactusSprite();
        this.groundsSprite = this.createGroundsSprite();
        this.cloudsSprite = this.createCloudsSprite();
        this.starsSprite = this.createStarsSprite();
        this.moonSprite = this.createMoonSprite();
        this.birdsSprite = this.createBirdsSprite();
        this.replayButtonsSprite = this.createReplayButtonsSprite();
        this.menuButtonsSprite = this.createMenuButtonsSprite();
        this.gameOverSprite = this.createGameOverSprite();

        final var dinoImage = ResourceUtil.getResourceImage("img/dinosaurs.png");
        this.dinosaurStandingSprite = this.createDinoStandingSprite(dinoImage);
        this.dinosaurRunSprite = this.createDinoRunSprite(dinoImage);
        this.dinosaurJumpSprite = this.createDinoJumpSprite(dinoImage);
        this.dinosaurDownSprite = this.createDinoDownSprite(dinoImage);
        this.dinosaurDeathSprite = this.createDinoDeathSprite(dinoImage);

        final var yellowDinoImage = ResourceUtil.setColorToYellow(dinoImage);
        this.bestDinosaurStandingSprite = this.createDinoStandingSprite(yellowDinoImage);
        this.bestDinosaurRunSprite = this.createDinoRunSprite(yellowDinoImage);
        this.bestDinosaurJumpSprite = this.createDinoJumpSprite(yellowDinoImage);
        this.bestDinosaurDownSprite = this.createDinoDownSprite(yellowDinoImage);
        this.bestDinosaurDeathSprite = this.createDinoDeathSprite(yellowDinoImage);

        this.press = ResourceUtil.getResourceSound("audio/press.wav");
        this.hit = ResourceUtil.getResourceSound("audio/hit.wav");
        this.reached = ResourceUtil.getResourceSound("audio/reached.wav");

        this.font = ResourceUtil.getResourceFont("font-ttf/press-start2p-regular.ttf");
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

    private Sprite createDinoStandingSprite(final BufferedImage image) {
        final var sprite = new Sprite(image, 88, 94, 0, 0);
        sprite.addFrame(88, 94, 88, 0);
        return sprite;
    }

    private Sprite createDinoRunSprite(final BufferedImage image) {
        final var sprite = new Sprite(image, 88, 94, 176, 0);
        sprite.addFrame(88, 94, 264, 0);
        return sprite;
    }

    private Sprite createDinoJumpSprite(final BufferedImage image) {
        return new Sprite(image, 88, 94, 0, 0);
    }

    private Sprite createDinoDownSprite(final BufferedImage image) {
        final var sprite = new Sprite(image, 118, 94, 528, 0);
        sprite.addFrame(118, 94, 646, 0);
        return sprite;
    }

    private Sprite createDinoDeathSprite(final BufferedImage image) {
        final var sprite = new Sprite(image, 88, 94, 440, 0);
        sprite.addFrame(88, 94, 352, 0);
        return sprite;
    }
}
