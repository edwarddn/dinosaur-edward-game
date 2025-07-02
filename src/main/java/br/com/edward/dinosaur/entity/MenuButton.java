package br.com.edward.dinosaur.entity;

import br.com.edward.dinosaur.enuns.EnumTypeOfEntity;
import br.com.edward.dinosaur.record.Sprite;
import br.com.edward.dinosaur.screen.GameState;

public class MenuButton extends BaseEntity {

    public MenuButton(final GameState gameState, final int framePosition) {
        super(gameState, EnumTypeOfEntity.BUTTON);
        super.framePosition = framePosition;
    }

    @Override
    protected Sprite getSprite() {
        return super.getAssetManager().getMenuButtonsSprite();
    }

    @Override
    public double getPositionX() {
        return ((super.getGameState().getWidth() / 2.0) - (this.getSprite().getFrame(this.getFramePosition()).width() / 2.0));
    }

    @Override
    public double getPositionY() {
        if (this.framePosition > 0) {
            return ((super.getGameState().getHeight() / 2.0) - ((this.getHeightPosition() - 100.0) / 2.0));
        }
        return ((super.getGameState().getHeight() / 2.0) - ((this.getHeightPosition() + 100.0) / 2.0));
    }

    private double getHeightPosition() {
        return this.getSprite().getFrame(this.getFramePosition()).height() * 2.0;
    }
}
