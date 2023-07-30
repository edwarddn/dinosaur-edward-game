package br.com.edward.dinosaur.entity;

import br.com.edward.dinosaur.config.Config;
import br.com.edward.dinosaur.enuns.EnumTypeOfEntity;
import br.com.edward.dinosaur.record.Sprite;

public class ReplayButton extends BaseEntity {

    public ReplayButton(final Config config) {
        super(config, EnumTypeOfEntity.BUTTON);
        super.framePosition = 0;
    }

    @Override
    protected Sprite getSprite() {
        return super.getConfig().getReplayButtonsSprite();
    }

    @Override
    public double getPositionX() {
        return ((super.getConfig().getWidth() / 2.0) - (this.getSprite().getFrame(this.getFramePosition()).width() / 2.0));
    }

    @Override
    public double getPositionY() {
        return ((super.getConfig().getHeight() / 2.0) - (this.getSprite().getFrame(this.getFramePosition()).height() / 2.0));
    }
}
