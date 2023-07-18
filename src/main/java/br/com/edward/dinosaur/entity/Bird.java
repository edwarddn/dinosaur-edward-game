package br.com.edward.dinosaur.entity;

import br.com.edward.dinosaur.config.Config;
import br.com.edward.dinosaur.enuns.EnumTypeOfEntity;
import br.com.edward.dinosaur.record.Sprite;

import java.awt.*;
import java.util.SplittableRandom;

public class Bird extends BaseEntity {

    public Bird(final Config config, final double positionX) {
        super(config, EnumTypeOfEntity.ENEMY);
        this.referencePositionX = positionX;
        this.speedX = super.getConfig().getSpeed() + 3;
        switch (new SplittableRandom().nextInt(0, 4)) {
            case 0 -> this.referencePositionY = 210;
            case 1 -> this.referencePositionY = 230;
            case 2 -> this.referencePositionY = 280;
            default -> this.referencePositionY = 320;
        }
    }

    @Override
    public void update(final double deltaTime) {
        super.update(deltaTime);
        this.updateFrame(120);
    }

    @Override
    public double getSpeedX() {
        if (this.getPositionX() > (super.getConfig().getEnemyDistance() + (super.getConfig().getEnemyDistance() / 2.0))) {
            return super.getConfig().getSpeed();
        }
        return super.speedX;
    }

    @Override
    public Rectangle getBound() {
        final var rectBound = super.getBound();

        rectBound.x += 6;
        rectBound.y += 15;
        rectBound.width -= 12;
        rectBound.height -= 30;
        return rectBound;
    }

    @Override
    protected Sprite getSprite() {
        return super.getConfig().getBirdsSprite();
    }

    @Override
    public double getPositionX() {
        return this.referencePositionX;
    }

    @Override
    public double getPositionY() {
        return super.getConfig().getHeight() - this.referencePositionY;
    }
}
