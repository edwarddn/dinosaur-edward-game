package br.com.edward.dinosaur.entity;

import br.com.edward.dinosaur.enuns.EnumTypeOfEntity;
import br.com.edward.dinosaur.record.Sprite;
import br.com.edward.dinosaur.screen.GameState;

public class Moon extends BaseEntity {

    public Moon(final GameState gameState) {
        super(gameState, EnumTypeOfEntity.MOON);
        this.referencePositionX = gameState.getWidth();
        this.referencePositionY = super.getRandom().nextInt(3, 51);
        this.speedX = gameState.getCurrentSpeed() / 50;
        this.framePosition = gameState.getMoonPhase().getAndIncrement();
        if (this.framePosition >= this.getSprite().frames().size()) {
            this.framePosition = 0;
            gameState.getMoonPhase().set(0);
        }
    }

    @Override
    protected Sprite getSprite() {
        return super.getAssetManager().getMoonSprite();
    }

    @Override
    public double getPositionX() {
        return this.referencePositionX;
    }

    @Override
    public double getPositionY() {
        return (this.referencePositionY / 100F) * super.getGameState().getHeight();
    }
}
