package br.com.edward.dinosaur.entity;

import br.com.edward.dinosaur.enuns.EnumTypeOfEntity;
import br.com.edward.dinosaur.record.Sprite;
import br.com.edward.dinosaur.screen.GameState;

public class GameOverLabel extends BaseEntity {

    public GameOverLabel(final GameState gameState) {
        super(gameState, EnumTypeOfEntity.LABEL);
    }

    @Override
    protected Sprite getSprite() {
        return super.getAssetManager().getGameOverSprite();
    }

    @Override
    public double getPositionX() {
        return ((super.getGameState().getWidth() / 2.0) - (this.getSprite().getFrame(this.getFramePosition()).width() / 2.0));
    }

    @Override
    public double getPositionY() {
        return ((super.getGameState().getHeight() / 3.0) - (this.getSprite().getFrame(this.getFramePosition()).height() / 2.0));
    }
}
