package br.com.edward.dinosaur.entity;

import br.com.edward.dinosaur.config.Config;
import br.com.edward.dinosaur.enuns.EnumTypeOfEntity;
import br.com.edward.dinosaur.record.Sprite;

public class Ground extends BaseEntity {

    public Ground(final Config config, final double width) {
        super(config, EnumTypeOfEntity.GROUND);
        this.referencePositionX = width;
        this.referencePositionY = 100;
        this.speedX = super.getConfig().getSpeed();
        this.getRandomFrame();
    }

    @Override
    protected Sprite getSprite() {
        return super.getConfig().getGroundsSprite();
    }

    @Override
    public double getPositionX() {
        return this.referencePositionX;
    }

    @Override
    public double getPositionY() {
        return getConfig().getHeight() - this.referencePositionY;
    }
}
