package br.com.edward.dinosaur.entity;

import br.com.edward.dinosaur.config.Config;
import br.com.edward.dinosaur.enuns.EnumTypeOfEntity;
import br.com.edward.dinosaur.record.Sprite;

public class Star extends BaseEntity {

    public Star(final boolean beginning, final Config config) {
        super(config, EnumTypeOfEntity.STAR);
        this.referencePositionX = beginning ? getRandom().nextInt(0, config.getWidth()) : getRandom().nextInt(config.getWidth(), config.getWidth() * 2);
        this.referencePositionY = super.getRandom().nextInt(3, 51);
        this.speedX = super.getConfig().getSpeed() / super.getRandom().nextInt(16, 80);
        this.getRandomFrame();
    }

    @Override
    public void update(final double deltaTime) {
        super.update(deltaTime);
        this.updateFrame(100);
    }

    @Override
    protected Sprite getSprite() {
        return super.getConfig().getStarsSprite();
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
