package br.com.edward.dinosaur.screen;

import br.com.edward.dinosaur.ai.NeuralNetwork;
import br.com.edward.dinosaur.config.Config;
import br.com.edward.dinosaur.entity.*;
import br.com.edward.dinosaur.enuns.EnumGameStatus;
import br.com.edward.dinosaur.enuns.EnumTypeOfEntity;
import lombok.Getter;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.stream.Stream;

@Getter
public class ScreenManager {

    private final SplittableRandom random;
    private final List<BaseEntity> objects;
    private final List<Dinosaur> dinosaurs;
    private final List<Dinosaur> deadDinosaurs;
    private final Dinosaur player;
    private final NeuralNetworkDisplay neuralNetworkDisplay;
    private final Config config;

    public ScreenManager(final Config config, final Dinosaur player) {
        this.random = new SplittableRandom();
        this.objects = new ArrayList<>();
        this.dinosaurs = new ArrayList<>();
        this.deadDinosaurs = new ArrayList<>();
        this.config = config;
        this.player = player;
        this.neuralNetworkDisplay = new NeuralNetworkDisplay(config);
        this.reset();
    }

    public void update(final double deltaTime) {
        if (config.isTraining() && !this.player.isDeath()) {
            this.player.dead();
        }

        for (final var item : objects) {
            if (!EnumGameStatus.PLAYING.equals(this.config.getGameState())) {
                if (EnumTypeOfEntity.STAR.equals(item.getType())
                        || EnumTypeOfEntity.MOON.equals(item.getType())
                        || EnumTypeOfEntity.CLOUD.equals(item.getType())) {
                    item.update(deltaTime);
                }
            } else {
                item.update(deltaTime);
            }
        }
        for (final var item : this.dinosaurs) {
            item.update(deltaTime);
        }

        final var objs = objects.stream().filter(BaseEntity::isOutOfScreen).toList();
        for (BaseEntity obj : objs) {
            createGameEntity(false, obj.getType());
        }
        this.objects.removeAll(objs);

        final var outOfScreenDinosaurs = this.dinosaurs.stream().filter(BaseEntity::isOutOfScreen).toList();
        if (!outOfScreenDinosaurs.isEmpty()) {
            this.dinosaurs.removeAll(outOfScreenDinosaurs);
            this.deadDinosaurs.addAll(outOfScreenDinosaurs);
        }

        if (EnumGameStatus.PLAYING.equals(this.config.getGameState())) {
            this.config.accelerate(deltaTime);

            final var enemy = objects.stream().filter(BaseEntity::isEnemy).findFirst();
            if (enemy.isPresent()) {
                final var enemyEntity = enemy.get();
                this.dinosaurs.forEach(x -> x.think(enemyEntity));
                this.player.think(enemyEntity);
            }
        }
    }

    public synchronized void draw(final Graphics2D g2d) {
        for (final var item : this.objects) {
            item.draw(g2d);
        }
        int i = 0;
        for (final var item : this.dinosaurs) {
            item.declareAsNormal();
            item.draw(g2d);
            if (i++ > 50) {
                break;
            }
        }
        final var betterDinosaur = this.getBetterDinosaur();
        if (this.config.isShowStatistics() && betterDinosaur.isPresent()) {
            this.neuralNetworkDisplay.draw(g2d, betterDinosaur.get().getNeuralNetwork());
        }
        if (betterDinosaur.isPresent()) {
            betterDinosaur.get().declareAsBetter();
            betterDinosaur.get().draw(g2d);
        }
    }

    public synchronized void reset() {
        this.objects.clear();
        this.dinosaurs.clear();
        this.deadDinosaurs.clear();
        for (EnumTypeOfEntity type : EnumTypeOfEntity.values()) {
            this.createGameEntity(true, type);
        }
        this.createDinosaurs();
    }

    private void createGameEntity(final boolean beginning, final EnumTypeOfEntity type) {
        if (EnumTypeOfEntity.STAR.equals(Objects.requireNonNull(type))) {
            this.createStars(beginning);
        } else if (EnumTypeOfEntity.MOON.equals(type)) {
            this.createMoon();
        } else if (EnumTypeOfEntity.CLOUD.equals(type)) {
            this.createClouds(beginning);
        } else if (EnumTypeOfEntity.GROUND.equals(type)) {
            this.createGrounds();
        } else if (EnumTypeOfEntity.ENEMY.equals(type)) {
            this.createEnemy();
        }
    }

    private void createDinosaurs() {
        final var neuralNetwork = NeuralNetwork.get();
        if (this.config.isTraining()) {
            final var tenPercent = (int) (this.config.getPopulationSize() * 0.10);
            final var ninetyPercent = this.config.getPopulationSize() - tenPercent;

            neuralNetwork.ifPresent(network -> this.dinosaurs.add(new Dinosaur(this.config, false, network)));
            for (int i = 0; i < ninetyPercent; i++) {
                if (neuralNetwork.isPresent()) {
                    this.dinosaurs.add(new Dinosaur(this.config, false, new NeuralNetwork(neuralNetwork.get())));
                } else {
                    this.dinosaurs.add(new Dinosaur(this.config, false, new NeuralNetwork()));
                }
            }
            for (int i = 0; i < tenPercent; i++) {
                this.dinosaurs.add(new Dinosaur(this.config, false, new NeuralNetwork()));
            }
        } else {
            this.dinosaurs.add(new Dinosaur(this.config, false, neuralNetwork.orElse(new NeuralNetwork())));
        }
    }

    private void createMoon() {
        this.objects.add(new Moon(this.config));
    }

    private void createStars(final boolean beginning) {
        final var qtd = beginning ? this.random.nextInt(4, 10) : 1;
        for (double i = 0; i < qtd; i++) {
            this.objects.add(new Star(beginning, config));
        }
    }

    private void createClouds(final boolean beginning) {
        final var qtd = beginning ? this.random.nextInt(4, 10) : 1;
        for (double i = 0; i < qtd; i++) {
            this.objects.add(new Cloud(beginning, config));
        }
    }

    private void createGrounds() {
        final var width = config.getWidth() <= config.getEnemyDistance() ? config.getEnemyDistance() * 5 : config.getWidth() * 5;
        double groundWidth = getGroundWidth();
        while (width >= groundWidth) {
            final var ground = new Ground(config, groundWidth);
            this.objects.add(ground);
            groundWidth += ground.getWidth();
        }
    }

    private double getGroundWidth() {
        final var grounds = objects.stream().filter(BaseEntity::isGround).toList();
        if (grounds.isEmpty()) {
            return 0;
        }
        final var lastGround = grounds.get(grounds.size() - 1);
        return lastGround.getPositionX() + lastGround.getWidth();
    }

    private void createEnemy() {
        final var width = config.getWidth() <= config.getEnemyDistance() ? config.getEnemyDistance() * 4 : config.getWidth() * 4;
        final var enemies = objects.stream().filter(BaseEntity::isEnemy).toList();
        double enemyWidth = this.getEnemyWidth(enemies);
        while (width >= enemyWidth) {
            final var enemy = this.random.nextInt(2) == 0 ? new Bird(config, enemyWidth) : new Cactus(config, enemyWidth);
            this.objects.add(enemy);
            enemyWidth += enemy.getWidth() + config.getEnemyDistance();
        }
    }

    private double getEnemyWidth(final List<BaseEntity> enemies) {
        if (enemies.isEmpty()) {
            return config.getWidth() + (double) config.getEnemyDistance();
        }
        final var lastEnemy = enemies.get(enemies.size() - 1);
        return lastEnemy.getPositionX() + lastEnemy.getWidth() + this.random.nextInt(config.getEnemyDistance(), config.getEnemyDistance() * 2);
    }

    public Optional<Dinosaur> getBetterDinosaur() {
        return Stream.concat(this.dinosaurs.stream(), this.deadDinosaurs.stream())
                .max(Comparator.comparing(Dinosaur::getScore));
    }
}
