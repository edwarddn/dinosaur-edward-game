package br.com.edward.dinosaur.entity;

import br.com.edward.dinosaur.ai.NeuralNetwork;
import br.com.edward.dinosaur.enums.EnumDinosaurActions;
import br.com.edward.dinosaur.enums.EnumGameStatus;
import br.com.edward.dinosaur.enums.EnumTypeOfEntity;
import br.com.edward.dinosaur.helper.ResourceUtil;
import br.com.edward.dinosaur.record.Sprite;
import br.com.edward.dinosaur.screen.GameState;
import lombok.Getter;

import javax.sound.sampled.Clip;
import java.awt.*;
import java.util.Objects;

import static br.com.edward.dinosaur.helper.IntUtil.getInt;

@Getter
public class Dinosaur extends BaseEntity {

    private final NeuralNetwork neuralNetwork;
    private final Polygon polygon;
    private final double defaultPositionX;
    private final double defaultPositionY;
    private boolean better;
    private BaseEntity lastEnemy;
    private long score;
    private long movementCount;
    private EnumDinosaurActions state;
    private double jumpSpeed;
    private boolean death;

    public Dinosaur(final GameState gameState, final boolean isPlayer, final NeuralNetwork neuralNetwork) {
        super(gameState, EnumTypeOfEntity.PLAYER);
        this.better = false;
        this.polygon = new Polygon();
        this.score = 1;
        this.movementCount = 0;

        if (isPlayer) {
            this.neuralNetwork = null;
            this.referencePositionX = 100;
        } else {
            if (gameState.isTraining()) {
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
        this.speedX = gameState.getCurrentSpeed();
    }

    @Override
    protected Sprite getSprite() {
        if (this.better) {
            return switch (this.state) {
                case STANDING -> super.getAssetManager().getBestDinosaurStandingSprite();
                case JUMPING -> super.getAssetManager().getBestDinosaurJumpSprite();
                case CROUCHING -> super.getAssetManager().getBestDinosaurDownSprite();
                case DEAD -> super.getAssetManager().getBestDinosaurDeathSprite();
                default -> super.getAssetManager().getBestDinosaurRunSprite();
            };
        }
        return switch (this.state) {
            case STANDING -> super.getAssetManager().getDinosaurStandingSprite();
            case JUMPING -> super.getAssetManager().getDinosaurJumpSprite();
            case CROUCHING -> super.getAssetManager().getDinosaurDownSprite();
            case DEAD -> super.getAssetManager().getDinosaurDeathSprite();
            default -> super.getAssetManager().getDinosaurRunSprite();
        };
    }

    @Override
    public double getPositionX() {
        return this.referencePositionX;
    }

    @Override
    public double getPositionY() {
        return getGameState().getHeight() - this.referencePositionY;
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
        } else if (EnumGameStatus.WAITING_TO_PLAY.equals(super.getGameState().getGameStatus())) {
            this.blink();
        } else {
            this.updateFrame(80);
            if (EnumDinosaurActions.JUMPING.equals(this.state) || this.referencePositionY > this.defaultPositionY) {
                this.referencePositionY += ((this.jumpSpeed * super.getConfig().getGameSpeed()) / 1000.0) * deltaTime;
                this.jumpSpeed += ((this.getConfig().getGravity() * super.getConfig().getGameSpeed()) / 1000.0) * deltaTime;
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
            this.playSound(super.getAssetManager().getPress());
            this.movementCount++;
        }
    }

    public void down(final boolean isDown) {
        if (isDown && EnumDinosaurActions.JUMPING.equals(this.state)) {
            this.jumpSpeed -= 10;
        }
        if (isDown && !EnumDinosaurActions.CROUCHING.equals(this.state)) {
            this.movementCount++;
        }
        this.state = isDown ? EnumDinosaurActions.CROUCHING : EnumDinosaurActions.RUNNING;
    }

    public void dead() {
        this.state = EnumDinosaurActions.DEAD;
        this.death = true;
    }

    public void playDeadSound() {
        this.playSound(super.getAssetManager().getHit());
    }

    protected void playSound(final Clip clip) {
        if (Objects.isNull(this.neuralNetwork)) {
            ResourceUtil.playSound(clip);
        }
    }

    public void think(final BaseEntity enemy) {
        if (this.isDeath()) {
            return;
        }

        final var distance = enemy.getBound().getX() - (this.getBound().getX() + this.getBound().getWidth());
        final var intersect = this.getBound().intersects(enemy.getBound());

        if (!enemy.equals(this.lastEnemy) && distance < 0.0 && !intersect) {
            this.lastEnemy = enemy;
            this.score++;
            if (this.score > 0 && this.score % 100 == 0) {
                this.playSound(super.getAssetManager().getReached());
            }
        }

        if (super.getConfig().isCollision() && intersect) {
            this.playDeadSound();
            this.dead();
            return;
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

            final var output = this.neuralNetwork.getOutput(getInputs(enemy, distance));

            if (output[0] > 0.5 && output[1] <= 0.5) {
                this.jump();
            } else if (output[0] <= 0.5 && output[1] > 0.5) {
                this.down(true);
            } else if (EnumDinosaurActions.CROUCHING.equals(this.state)) {
                this.down(false);
            }
        }
    }

    private double[] getInputs(final BaseEntity enemy, final double distance) {

        final double normalizedDistance = (distance > 0) ? (distance / getGameState().getConfig().getEnemyDistance()) : 0.0;
        final double normalizedEnemyY = enemy.getReferencePositionY() / 350.0;
        final double normalizedEnemyWidth = enemy.getBound().getWidth() / 150.0;
        final double normalizedEnemyHeight = enemy.getBound().getHeight() / 100.0;
        final double normalizedDinoY = this.getReferencePositionY() / 350.0;
        final double normalizedSpeed = getGameState().getCurrentSpeed() / getConfig().getMaxSpeed();
        final double enemyType = (enemy instanceof Bird) ? 1.0 : 0.0;

        return new double[]{
                Math.clamp(normalizedDistance, 0.0, 1.0),
                Math.clamp(normalizedEnemyY, 0.0, 1.0),
                Math.clamp(normalizedEnemyWidth, 0.0, 1.0),
                Math.clamp(normalizedEnemyHeight, 0.0, 1.0),
                Math.clamp(normalizedDinoY, 0.0, 1.0),
                Math.clamp(normalizedSpeed, 0.0, 1.0),
                enemyType
        };
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
        this.score = 1;
        this.movementCount = 0;
        this.better = false;
    }

    public void declareAsNormal() {
        this.better = false;
    }

    public void declareAsBetter() {
        this.better = true;
    }

    public int getAge() {
        return Objects.nonNull(this.neuralNetwork) ? this.neuralNetwork.getAge() : -1;
    }
}
