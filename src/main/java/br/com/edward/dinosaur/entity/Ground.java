package br.com.edward.dinosaur.entity;

import br.com.edward.dinosaur.enums.EnumTypeOfEntity;
import br.com.edward.dinosaur.record.Sprite;
import br.com.edward.dinosaur.screen.GameState;

public class Ground extends BaseEntity {

    public Ground(final GameState gameState, final double width) {
        super(gameState, EnumTypeOfEntity.GROUND);
        this.referencePositionX = width;
        this.referencePositionY = 100;
        this.speedX = gameState.getCurrentSpeed();
        this.getRandomFrame();
    }

    @Override
    protected Sprite getSprite() {
        return super.getAssetManager().getGroundsSprite();
    }

    @Override
    public double getPositionX() {
        return this.referencePositionX;
    }

    @Override
    public double getPositionY() {
        return getGameState().getHeight() - this.referencePositionY;
    }
}
