package br.com.edward.dinosaur.screen;

import br.com.edward.dinosaur.ai.Breeder;
import br.com.edward.dinosaur.ai.NeuralNetwork;
import br.com.edward.dinosaur.entity.*;
import br.com.edward.dinosaur.enums.EnumGameStatus;
import br.com.edward.dinosaur.enums.EnumTypeOfEntity;
import lombok.Getter;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Stream;

@Getter
public class ScreenManager {

    private static final Comparator<Dinosaur> FITNESS = Comparator
            .comparingLong(Dinosaur::getScore)
            .thenComparing(Dinosaur::getMovementCount, Comparator.reverseOrder());

    private final SplittableRandom random;
    private final List<BaseEntity> objects;
    private final List<Dinosaur> dinosaurs;
    private final List<Dinosaur> deadDinosaurs;
    private final Dinosaur player;
    private final NeuralNetworkDisplay neuralNetworkDisplay;
    private final GameState gameState;
    private boolean trainedGeneration;

    public ScreenManager(final GameState gameState, final Dinosaur player) {
        this.random = new SplittableRandom();
        this.objects = new CopyOnWriteArrayList<>();
        this.dinosaurs = new CopyOnWriteArrayList<>();
        this.deadDinosaurs = new CopyOnWriteArrayList<>();
        this.gameState = gameState;
        this.player = player;
        this.neuralNetworkDisplay = new NeuralNetworkDisplay(gameState);
        this.reset();
    }

    public void update(final double deltaTime) {
        if (gameState.isTraining() && !this.player.isDeath()) {
            this.player.dead();
        }

        for (final var item : objects) {
            if (!EnumGameStatus.PLAYING.equals(this.gameState.getGameStatus())) {
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

        final var dinosaursOutOfScreen = this.dinosaurs.stream().filter(BaseEntity::isOutOfScreen).toList();
        if (!dinosaursOutOfScreen.isEmpty()) {
            this.dinosaurs.removeAll(dinosaursOutOfScreen);
            this.deadDinosaurs.addAll(dinosaursOutOfScreen);
        }


        if (EnumGameStatus.PLAYING.equals(this.gameState.getGameStatus())) {
            this.gameState.accelerate(deltaTime);
            this.findNextEnemy().ifPresent(enemy -> {
                this.dinosaurs.forEach(dino -> dino.think(enemy));
                this.player.think(enemy);
            });
        }
    }

    public void draw(final Graphics2D g2d) {
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
        if (this.gameState.getConfig().isShowStatistics() && betterDinosaur.isPresent() && betterDinosaur.get().getNeuralNetwork() != null) {
            this.neuralNetworkDisplay.draw(g2d, betterDinosaur.get().getNeuralNetwork());
        }

        betterDinosaur.ifPresent(dino -> {
            dino.declareAsBetter();
            dino.draw(g2d);
        });
    }

    public synchronized void reset() {
        final var parents = this.trainedGeneration ? this.selectParents() : new ArrayList<NeuralNetwork>();
        this.objects.clear();
        this.dinosaurs.clear();
        this.deadDinosaurs.clear();
        for (EnumTypeOfEntity type : EnumTypeOfEntity.values()) {
            this.createGameEntity(true, type);
        }
        this.createDinosaurs(parents);
        this.trainedGeneration = this.gameState.isTraining();
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

    private void createDinosaurs(final List<NeuralNetwork> parents) {
        if (!this.gameState.isTraining()) {
            final var champion = NeuralNetwork.get()
                    .or(() -> parents.stream().findFirst())
                    .orElseGet(NeuralNetwork::new);
            this.dinosaurs.add(new Dinosaur(this.gameState, false, champion));
            return;
        }

        final var pool = parents.isEmpty()
                ? NeuralNetwork.get().map(List::of).orElseGet(List::of)
                : parents;

        final int populationSize = this.gameState.getConfig().getPopulationSize();
        final var population = Breeder.breed(pool, populationSize, this.random).stream()
                .map(network -> new Dinosaur(this.gameState, false, network))
                .toList();
        this.dinosaurs.addAll(population);
    }

    private List<NeuralNetwork> selectParents() {
        final var parentCount = Math.max(2, (int) (this.gameState.getConfig().getPopulationSize() * 0.10));
        return Stream.concat(this.dinosaurs.stream(), this.deadDinosaurs.stream())
                .filter(Objects::nonNull)
                .filter(dino -> dino.getNeuralNetwork() != null)
                .sorted(FITNESS.reversed())
                .limit(parentCount)
                .map(Dinosaur::getNeuralNetwork)
                .toList();
    }

    private Optional<BaseEntity> findNextEnemy() {
        final double frontX = Stream.concat(this.dinosaurs.stream(), Stream.of(this.player))
                .filter(dino -> !dino.isDeath())
                .mapToDouble(Dinosaur::getPositionX)
                .max()
                .orElse(0.0);
        return this.objects.stream()
                .filter(BaseEntity::isEnemy)
                .filter(enemy -> enemy.getPositionX() + enemy.getWidth() >= frontX)
                .min(Comparator.comparingDouble(BaseEntity::getPositionX));
    }

    private void createMoon() {
        this.objects.add(new Moon(this.gameState));
    }

    private void createStars(final boolean beginning) {
        final var qtd = beginning ? this.random.nextInt(4, 10) : 1;
        for (double i = 0; i < qtd; i++) {
            this.objects.add(new Star(beginning, gameState));
        }
    }

    private void createClouds(final boolean beginning) {
        final var qtd = beginning ? this.random.nextInt(4, 10) : 1;
        for (double i = 0; i < qtd; i++) {
            this.objects.add(new Cloud(beginning, gameState));
        }
    }

    private void createGrounds() {
        final var width = gameState.getWidth() <= gameState.getConfig().getEnemyDistance() ? gameState.getConfig().getEnemyDistance() * 5 : gameState.getWidth() * 5;
        double groundWidth = getGroundWidth();
        while (width >= groundWidth) {
            final var ground = new Ground(gameState, groundWidth);
            this.objects.add(ground);
            groundWidth += ground.getWidth();
        }
    }

    private double getGroundWidth() {
        return objects.stream()
                .filter(BaseEntity::isGround)
                .max(Comparator.comparingDouble(BaseEntity::getPositionX))
                .map(lastGround -> lastGround.getPositionX() + lastGround.getWidth())
                .orElse(0.0);
    }

    private void createEnemy() {
        final var width = gameState.getWidth() <= gameState.getConfig().getEnemyDistance() ? gameState.getConfig().getEnemyDistance() * 4 : gameState.getWidth() * 4;
        double enemyWidth = this.getEnemyWidth();
        while (width >= enemyWidth) {
            final var enemy = this.random.nextInt(2) == 0 ? new Bird(gameState, enemyWidth) : new Cactus(gameState, enemyWidth);
            this.objects.add(enemy);
            enemyWidth += enemy.getWidth() + gameState.getConfig().getEnemyDistance();
        }
    }

    private double getEnemyWidth() {
        return objects.stream()
                .filter(BaseEntity::isEnemy)
                .max(Comparator.comparingDouble(BaseEntity::getPositionX))
                .map(lastEnemy -> lastEnemy.getPositionX() + lastEnemy.getWidth() + this.random.nextInt(gameState.getConfig().getEnemyDistance(), gameState.getConfig().getEnemyDistance() * 2))
                .orElse((double) (gameState.getWidth() + gameState.getConfig().getEnemyDistance()));
    }

    public Optional<Dinosaur> getBetterDinosaur() {
        return Stream.concat(this.dinosaurs.stream(), this.deadDinosaurs.stream())
                .filter(Objects::nonNull)
                .max(FITNESS);
    }
}