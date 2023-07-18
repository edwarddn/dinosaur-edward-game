package br.com.edward.dinosaur.entity;

import br.com.edward.dinosaur.config.Config;
import br.com.edward.dinosaur.enuns.EnumTypeOfEntity;
import br.com.edward.dinosaur.record.Sprite;

public class MenuButton extends BaseEntity {

    public MenuButton(final Config config, final int framePosition) {
        super(config, EnumTypeOfEntity.BUTTON);
        super.framePosition = framePosition;
    }

    @Override
    protected Sprite getSprite() {
        return super.getConfig().getMenuButtonsSprite();
    }

    @Override
    public double getPositionX() {
        return ((super.getConfig().getWidth() / 2.0) - (this.getSprite().getFrame(this.getFramePosition()).width() / 2.0));
    }

    @Override
    public double getPositionY() {
        if (this.framePosition > 0) {
            return ((super.getConfig().getHeight() / 2.0) - ((this.getHeightPosition() - 100.0) / 2.0));
        }
        return ((super.getConfig().getHeight() / 2.0) - ((this.getHeightPosition() + 100.0) / 2.0));
    }

    private double getHeightPosition() {
        return this.getSprite().getFrame(this.getFramePosition()).height() * 2.0;
    }
}
