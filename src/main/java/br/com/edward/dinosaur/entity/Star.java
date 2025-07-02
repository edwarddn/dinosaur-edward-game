package br.com.edward.dinosaur.entity;

import br.com.edward.dinosaur.enuns.EnumTypeOfEntity;
import br.com.edward.dinosaur.record.Sprite;
import br.com.edward.dinosaur.screen.GameState;

public class Star extends BaseEntity {

    public Star(final boolean beginning, final GameState gameState) {
        super(gameState, EnumTypeOfEntity.STAR);
        this.referencePositionX = beginning ? getRandom().nextInt(0, gameState.getWidth()) : getRandom().nextInt(gameState.getWidth(), gameState.getWidth() * 2);
        this.referencePositionY = super.getRandom().nextInt(3, 51);
        this.speedX = gameState.getCurrentSpeed() / super.getRandom().nextInt(16, 80);
        this.getRandomFrame();
    }

    @Override
    public void update(final double deltaTime) {
        super.update(deltaTime);
        this.updateFrame(100);
    }

    @Override
    protected Sprite getSprite() {
        return super.getAssetManager().getStarsSprite();
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
