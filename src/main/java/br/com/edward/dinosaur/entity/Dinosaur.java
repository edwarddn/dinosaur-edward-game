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

    private BaseEntity lastEnemy;
    private long score;
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
        this.score = 11;

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
            this.playSound(super.getConfig().getPress());
            this.score--;
        }
    }

    public void down(final boolean isDown) {
        if (EnumDinosaurActions.JUMPING.equals(this.state)) {
            this.jumpSpeed -= 10;
        }
        this.state = isDown ? EnumDinosaurActions.CROUCHING : EnumDinosaurActions.RUNNING;
        if (!isDown) {
            this.score--;
        }
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

            final var distance = enemy.getBound().getX() - (this.getBound().getX() + this.getBound().getWidth());
            final var intersect = this.getBound().intersects(enemy.getBound());

            if (!enemy.equals(this.lastEnemy) && distance < 0.0 && !intersect) {
                this.lastEnemy = enemy;
                if (enemy instanceof Bird) {
                    if (this.referencePositionY == this.defaultPositionY && EnumDinosaurActions.CROUCHING.equals(this.state)) {
                        this.score += 10;
                    } else if (this.referencePositionY == this.defaultPositionY && EnumDinosaurActions.RUNNING.equals(this.state)) {
                        this.score += 100;
                    } else {
                        this.score -= 200;
                    }
                } else {
                    this.score += 10;
                }
            }

            if (this.score <= 0.0 || (super.getConfig().isCollision() && intersect)) {
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

            if (Objects.nonNull(this.neuralNetwork)) {

                final double type;
                if (enemy instanceof Cactus) {
                    type = 1.0;
                } else if ((enemy.getReferencePositionY() - enemy.getHeight()) <= this.getDefaultPositionY()) {
                    type = 1000.0;
                } else {
                    type = -1000.0;
                }

                final var output = this.neuralNetwork.getOutput(new double[]{
                        (distance > 0) ? distance : 0,
                        type,
                        super.getConfig().getSpeed()
                });

                if (output[0] > 0 && output[1] <= 0) {
                    this.jump();
                } else if (output[0] <= 0 && output[1] > 0) {
                    this.down(true);
                } else if (EnumDinosaurActions.CROUCHING.equals(this.state)) {
                    this.down(false);
                }
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
        this.jumpSpeed = 0.0;
        this.score = 11;
    }
}
