package br.com.edward.dinosaur.entity;

import br.com.edward.dinosaur.config.Config;
import br.com.edward.dinosaur.enuns.EnumTypeOfEntity;
import br.com.edward.dinosaur.record.Frame;
import br.com.edward.dinosaur.record.Sprite;
import lombok.Getter;

import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.SplittableRandom;

import static br.com.edward.dinosaur.helper.IntUtil.getInt;

@Getter
public abstract class BaseEntity {

    private final Config config;
    private final SplittableRandom random;
    private final EnumTypeOfEntity type;

    protected double previousTime;
    protected double referencePositionX;
    protected double referencePositionY;

    protected int framePosition;
    protected double speedX;
    protected double speedY;

    protected BaseEntity(final Config config, final EnumTypeOfEntity type) {
        this.config = config;
        this.random = new SplittableRandom();
        this.type = type;

        this.referencePositionX = 0;
        this.referencePositionY = 0;
        this.framePosition = 0;
        this.speedX = 0;
        this.speedY = 0;
        this.previousTime = 0;
    }

    public Rectangle getBound() {
        final var rectangle = new Rectangle();
        rectangle.x = getInt(this.getPositionX());
        rectangle.y = getInt(this.getPositionY());
        rectangle.width = getInt(this.getSprite().getFrame(this.getFramePosition()).width());
        rectangle.height = getInt(this.getSprite().getFrame(this.getFramePosition()).height());
        return rectangle;
    }

    public boolean isOutOfScreen() {
        return this.getPositionX() < -this.getSprite().getFrame(this.getFramePosition()).width();
    }

    public void updateFrame(final double time) {
        if (System.currentTimeMillis() - this.getPreviousTime() >= time) {
            this.framePosition++;
            if (this.getFramePosition() >= this.getSprite().frames().size()) {
                this.framePosition = 0;
            }
            previousTime = System.currentTimeMillis();
        }
    }

    public Frame getRandomFrame() {
        final var sprite = this.getSprite();
        this.framePosition = getRandom().nextInt(0, sprite.frames().size());
        return sprite.getFrame(this.getFramePosition());
    }

    public boolean isEnemy() {
        return EnumTypeOfEntity.ENEMY.equals(this.getType());
    }

    public boolean isGround() {
        return EnumTypeOfEntity.GROUND.equals(this.getType());
    }

    public double getWidth() {
        return this.getSprite().getFrame(this.getFramePosition()).width();
    }

    public double getHeight() {
        return this.getSprite().getFrame(this.getFramePosition()).height();
    }

    public void draw(final Graphics2D g2d) {
        this.getSprite().draw(g2d, getInt(this.getPositionX()), getInt(this.getPositionY()), this.getFramePosition());
        if (this.getConfig().isShowCollision()) {
            g2d.setColor(Color.red);
            final var bound = getBound();
            g2d.drawRect(bound.x, bound.y, bound.width, bound.height);
        }
    }

    public void update(final double deltaTime) {
        this.referencePositionX -= new BigDecimal(((this.getSpeedX() * config.getGameSpeed()) / 1000.0) * deltaTime).setScale(6, RoundingMode.FLOOR).doubleValue();
    }

    protected abstract Sprite getSprite();
    public abstract double getPositionX();
    public abstract double getPositionY();
}
