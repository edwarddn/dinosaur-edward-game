package br.com.edward.dinosaur.entity;

import br.com.edward.dinosaur.enuns.EnumTypeOfEntity;
import br.com.edward.dinosaur.record.Sprite;
import br.com.edward.dinosaur.screen.GameState;

public class ReplayButton extends BaseEntity {

    public ReplayButton(final GameState gameState) {
        super(gameState, EnumTypeOfEntity.BUTTON);
        super.framePosition = 0;
    }

    @Override
    protected Sprite getSprite() {
        return super.getAssetManager().getReplayButtonsSprite();
    }

    @Override
    public double getPositionX() {
        return ((super.getGameState().getWidth() / 2.0) - (this.getSprite().getFrame(this.getFramePosition()).width() / 2.0));
    }

    @Override
    public double getPositionY() {
        return ((super.getGameState().getHeight() / 2.0) - (this.getSprite().getFrame(this.getFramePosition()).height() / 2.0));
    }
}
