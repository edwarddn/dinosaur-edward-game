package br.com.edward.dinosaur.entity;

import br.com.edward.dinosaur.config.Config;
import br.com.edward.dinosaur.enuns.EnumTypeOfEntity;
import br.com.edward.dinosaur.record.Sprite;

import java.util.concurrent.atomic.AtomicInteger;

public class Moon extends BaseEntity {

    public static final AtomicInteger MOON_PHASE = new AtomicInteger(0);

    public Moon(final Config config) {
        super(config, EnumTypeOfEntity.MOON);
        this.referencePositionX = config.getWidth();
        this.referencePositionY = super.getRandom().nextInt(3, 51);
        this.speedX = super.getConfig().getSpeed() / 50;
        this.framePosition = MOON_PHASE.getAndIncrement();
        if (this.framePosition >= this.getSprite().frames().size()) {
            this.framePosition = 0;
            MOON_PHASE.set(0);
        }
    }

    @Override
    protected Sprite getSprite() {
        return super.getConfig().getMoonSprite();
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
