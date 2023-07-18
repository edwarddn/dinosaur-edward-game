package br.com.edward.dinosaur.entity;

import br.com.edward.dinosaur.config.Config;
import br.com.edward.dinosaur.enuns.EnumTypeOfEntity;
import br.com.edward.dinosaur.record.Sprite;

import java.awt.*;

public class Cactus extends BaseEntity {

    public Cactus(final Config config, final double positionX) {
        super(config, EnumTypeOfEntity.ENEMY);
        this.referencePositionX = positionX;
        this.referencePositionY = (this.getRandomFrame().height() > 70) ? 175 : 150;
        this.speedX = super.getConfig().getSpeed();
    }

    @Override
    public Rectangle getBound() {
        final var rectBound = super.getBound();

        rectBound.x += 3;
        rectBound.y += 3;
        rectBound.width -= 6;
        rectBound.height -= 6;
        return rectBound;
    }

    @Override
    protected Sprite getSprite() {
        return super.getConfig().getCactusSprite();
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
