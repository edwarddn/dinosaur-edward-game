package br.com.edward.dinosaur.ai;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.SplittableRandom;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Breeder {

    private static final double ELITE_RATE = 0.05;
    private static final double RANDOM_RATE = 0.10;

    public static List<NeuralNetwork> breed(final List<NeuralNetwork> parents, final int populationSize, final SplittableRandom random) {
        final var population = new ArrayList<NeuralNetwork>(populationSize);
        if (parents.isEmpty()) {
            for (int i = 0; i < populationSize; i++) {
                population.add(new NeuralNetwork());
            }
            return population;
        }

        final int eliteCount = Math.clamp((int) (populationSize * ELITE_RATE), 1, parents.size());
        final int randomCount = (int) (populationSize * RANDOM_RATE);
        final int offspringCount = populationSize - eliteCount - randomCount;

        parents.stream()
                .limit(eliteCount)
                .forEach(parent -> population.add(NeuralNetwork.elite(parent)));

        for (int i = 0; i < offspringCount; i++) {
            final var father = parents.get(random.nextInt(parents.size()));
            final var mother = parents.get(random.nextInt(parents.size()));
            population.add(father == mother ? new NeuralNetwork(father) : new NeuralNetwork(father, mother));
        }

        for (int i = 0; i < randomCount; i++) {
            population.add(new NeuralNetwork());
        }
        return population;
    }
}
