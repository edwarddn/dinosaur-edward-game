package br.com.edward.dinosaur.entity;

import br.com.edward.dinosaur.ai.NeuralNetwork;
import br.com.edward.dinosaur.config.Config;
import br.com.edward.dinosaur.enuns.EnumDinosaurActions;
import br.com.edward.dinosaur.enuns.EnumGameStatus;
import br.com.edward.dinosaur.enuns.EnumTypeOfEntity;
import br.com.edward.dinosaur.helper.ResourceUtil;
import br.com.edward.dinosaur.record.Sprite;
import lombok.Getter;

import javax.sound.sampled.Clip;
import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

import static br.com.edward.dinosaur.helper.IntUtil.getInt;

@Getter
public class Dinosaur extends BaseEntity {

    private double score;
    private final NeuralNetwork neuralNetwork;

    private final Polygon polygon;
    private final double defaultPositionX;
    private final double defaultPositionY;
    private EnumDinosaurActions state;
    private double jumpSpeed;
    private boolean death;

    public Dinosaur(final Config config, final boolean isPlayer, final NeuralNetwork neuralNetwork) {
        super(config, EnumTypeOfEntity.PLAYER);
        this.polygon = new Polygon();
        this.score = 0.0;

        if (isPlayer) {
            this.neuralNetwork = null;
            this.referencePositionX = 100;
        } else {
            if (config.isTraining()) {
                this.referencePositionX = super.getRandom().nextInt(10, 100);
            } else {
                this.referencePositionX = 20;
            }
            this.neuralNetwork = neuralNetwork;
        }

        this.death = false;
        this.state = EnumDinosaurActions.STANDING;
        this.jumpSpeed = 0;
        this.referencePositionY = 172;
        this.defaultPositionX = this.referencePositionX;
        this.defaultPositionY = this.referencePositionY;
        this.speedX = super.getConfig().getSpeed();
    }

    @Override
    protected Sprite getSprite() {
        return switch (this.state) {
            case STANDING -> super.getConfig().getDinoStandingSprite();
            case JUMPING -> super.getConfig().getDinoJumpSprite();
            case CROUCHING -> super.getConfig().getDinoDownSprite();
            case DEAD -> super.getConfig().getDinoDeathSprite();
            default -> super.getConfig().getDinoRunSprite();
        };
    }

    @Override
    public double getPositionX() {
        return this.referencePositionX;
    }

    @Override
    public double getPositionY() {
        return getConfig().getHeight() - this.referencePositionY;
    }

    @Override
    public Rectangle getBound() {
        final var rectBound = super.getBound();

        if (EnumDinosaurActions.CROUCHING.equals(this.state)) {
            rectBound.y += 34;
            rectBound.height -= 34;
        }
        rectBound.x += 4;
        rectBound.y += 4;
        rectBound.width -= 8;
        rectBound.height -= 8;
        return rectBound;
    }

    @Override
    public void update(final double deltaTime) {
        if (this.isDeath()) {
            super.update(deltaTime);
        } else if (EnumGameStatus.WAITING_TO_PLAY.equals(super.getConfig().getGameState())) {
            this.blink();
        } else {
            this.updateFrame(80);
            if (EnumDinosaurActions.JUMPING.equals(this.state) || this.referencePositionY > this.defaultPositionY) {
                this.referencePositionY += BigDecimal.valueOf(((this.jumpSpeed * super.getConfig().getGameSpeed()) / 1000.0) * deltaTime).setScale(6, RoundingMode.FLOOR).doubleValue();
                this.jumpSpeed += BigDecimal.valueOf(((this.getConfig().getGravity() * super.getConfig().getGameSpeed()) / 1000.0) * deltaTime).setScale(6, RoundingMode.FLOOR).doubleValue();
                if (this.referencePositionY <= this.defaultPositionY) {
                    this.referencePositionY = this.defaultPositionY;
                    this.state = EnumDinosaurActions.JUMPING.equals(this.state) ? EnumDinosaurActions.RUNNING : this.state;
                    this.jumpSpeed = 0;
                }
            } else if (!EnumDinosaurActions.CROUCHING.equals(this.state)) {
                this.state = EnumDinosaurActions.RUNNING;
            }
        }

    }

    private void blink() {
        if (this.framePosition == 0) {
            this.updateFrame(super.getRandom().nextInt(2000, 3000));
        } else {
            this.updateFrame(super.getRandom().nextInt(100, 300));
        }
    }

    public void jump() {
        if (!EnumDinosaurActions.JUMPING.equals(this.state) && this.referencePositionY == this.defaultPositionY) {
            this.state = EnumDinosaurActions.JUMPING;
            this.jumpSpeed = this.getConfig().getJumpSpeed();
            playSound(super.getConfig().getPress());
        }
    }

    public void down(final boolean isDown) {
        if (EnumDinosaurActions.JUMPING.equals(this.state)) {
            this.jumpSpeed -= 10;
        }
        this.state = isDown ? EnumDinosaurActions.CROUCHING : EnumDinosaurActions.RUNNING;
    }

    public void dead() {
        this.state = EnumDinosaurActions.DEAD;
        this.death = true;
    }

    public void playDeadSound() {
        this.playSound(super.getConfig().getHit());
    }

    protected void playSound(final Clip clip) {
        if (Objects.isNull(this.neuralNetwork)) {
            ResourceUtil.playSound(clip);
        }
    }

    public void think(final BaseEntity enemy) {
        if (!this.isDeath()) {
            this.score = super.getConfig().getScore();
        }

        if (super.getConfig().isCollision() && this.getBound().intersects(enemy.getBound())) {
            this.playDeadSound();
            this.dead();
        }

        if (this.getConfig().isShowCollision()) {

            this.polygon.reset();
            this.polygon.addPoint(
                    getInt(this.getBound().getX() + this.getBound().getWidth()),
                    getInt(this.getBound().getY())
            );
            this.polygon.addPoint(
                    getInt(enemy.getBound().getX()),
                    getInt((enemy instanceof Bird) ? enemy.getBound().getY() + enemy.getBound().getHeight() : enemy.getBound().getY())
            );
        }

        if (!this.isDeath() && Objects.nonNull(this.neuralNetwork)) {

            final var distance = enemy.getBound().getX() - (this.getBound().getX() + this.getBound().getWidth());
            final var output = neuralNetwork.getOutput(new double[]{
                    (distance > 0) ? distance : 0,
                    enemy.getBound().getWidth(),
                    enemy.getReferencePositionY(),
                    enemy.getBound().getHeight(),
                    super.getConfig().getSpeed(),
                    (this.referencePositionY + this.getBound().getHeight())
            });

            if (output[0] > 0) {
                this.jump();
            }
            if (output[1] > 0) {
                this.down(true);
            }
        }
    }

    @Override
    public void draw(final Graphics2D g2d) {
        super.draw(g2d);
        if (this.getConfig().isShowCollision() && !this.isOutOfScreen()) {
            g2d.setColor(Color.GREEN);
            g2d.drawPolygon(this.polygon);
        }
    }

    public void revive() {
        this.referencePositionX = this.defaultPositionX;
        this.death = false;
        this.state = EnumDinosaurActions.STANDING;
        this.referencePositionY = this.defaultPositionY;
        this.jumpSpeed = 0;
    }
}
