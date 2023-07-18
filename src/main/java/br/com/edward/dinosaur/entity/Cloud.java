package br.com.edward.dinosaur.entity;

import br.com.edward.dinosaur.config.Config;
import br.com.edward.dinosaur.enuns.EnumTypeOfEntity;
import br.com.edward.dinosaur.record.Sprite;

public class Cloud extends BaseEntity {

    public Cloud(final boolean beginning, final Config config) {
        super(config, EnumTypeOfEntity.CLOUD);
        this.referencePositionX = beginning ? getRandom().nextInt(0, config.getWidth()) : getRandom().nextInt(config.getWidth(), config.getWidth() * 2);
        this.referencePositionY = super.getRandom().nextInt(3, 51);
        this.speedX = super.getConfig().getSpeed() / super.getRandom().nextInt(8, 40);
    }

    @Override
    protected Sprite getSprite() {
        return super.getConfig().getCloudsSprite();
    }

    @Override
    public double getPositionX() {
        return this.referencePositionX;
    }

    @Override
    public double getPositionY() {
        return (this.referencePositionY / 100F) * super.getConfig().getHeight();
    }
}
