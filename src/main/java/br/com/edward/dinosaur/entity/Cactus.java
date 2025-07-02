package br.com.edward.dinosaur.entity;

import br.com.edward.dinosaur.enuns.EnumTypeOfEntity;
import br.com.edward.dinosaur.record.Sprite;
import br.com.edward.dinosaur.screen.GameState;

import java.awt.*;

public class Cactus extends BaseEntity {

    public Cactus(final GameState gameState, final double positionX) {
        super(gameState, EnumTypeOfEntity.ENEMY);
        this.referencePositionX = positionX;
        this.referencePositionY = (this.getRandomFrame().height() > 70) ? 175 : 150;
        this.speedX = gameState.getCurrentSpeed();
    }

    @Override
    public Rectangle getBound() {
        final var rectBound = super.getBound();

        rectBound.x += 3;
        rectBound.y += 3;
        rectBound.width -= 6;
        rectBound.height -= 6;
        return rectBound;
    }

    @Override
    protected Sprite getSprite() {
        return super.getAssetManager().getCactusSprite();
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
