package br.com.edward.dinosaur.config;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Config {
    private final String title;
    private final boolean resizable;
    private final int initialWidth;
    private final int initialHeight;

    private final boolean showCollision;
    private final boolean collision;
    private final boolean showStatistics;

    private final int populationSize;
    private final int enemyDistance;
    private final double minSpeed;
    private final double maxSpeed;
    private final double acceleration;
    private final double gravity;
    private final double jumpSpeed;
    private final double gameSpeed;
    private final double fps;
}
