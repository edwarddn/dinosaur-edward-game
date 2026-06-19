package br.com.edward.dinosaur.entity;

import br.com.edward.dinosaur.enums.EnumTypeOfEntity;
import br.com.edward.dinosaur.record.Sprite;
import br.com.edward.dinosaur.screen.GameState;

import java.awt.*;
import java.util.SplittableRandom;

public class Bird extends BaseEntity {

    public Bird(final GameState gameState, final double positionX) {
        super(gameState, EnumTypeOfEntity.ENEMY);
        this.referencePositionX = positionX;
        this.speedX = gameState.getCurrentSpeed();
        switch (new SplittableRandom().nextInt(0, 4)) {
            case 0 -> this.referencePositionY = 210;
            case 1 -> this.referencePositionY = 230;
            case 2 -> this.referencePositionY = 280;
            default -> this.referencePositionY = 320;
        }
    }

    @Override
    public void update(final double deltaTime) {
        super.update(deltaTime);
        this.updateFrame(120);
    }

    @Override
    public Rectangle getBound() {
        final var rectBound = super.getBound();

        rectBound.x += 6;
        rectBound.y += 15;
        rectBound.width -= 12;
        rectBound.height -= 30;
        return rectBound;
    }

    @Override
    protected Sprite getSprite() {
        return super.getAssetManager().getBirdsSprite();
    }

    @Override
    public double getPositionX() {
        return this.referencePositionX;
    }

    @Override
    public double getPositionY() {
        return super.getGameState().getHeight() - this.referencePositionY;
    }
}
