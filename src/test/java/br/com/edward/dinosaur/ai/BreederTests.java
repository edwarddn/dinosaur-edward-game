package br.com.edward.dinosaur.ai;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.SplittableRandom;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class BreederTests {

    private final SplittableRandom random = new SplittableRandom(42L);

    private List<NeuralNetwork> freshParents(final int amount) {
        return Stream.generate(NeuralNetwork::new).limit(amount).toList();
    }

    @Test
    @DisplayName("Without parents the whole population is random generation 1 networks")
    void testWithoutParents() {
        final var population = Breeder.breed(List.of(), 50, random);

        assertThat(population).hasSize(50);
        assertThat(population).allMatch(network -> network.getGeneration() == 1);
    }

    @Test
    @DisplayName("With parents the population matches the requested size")
    void testPopulationSize() {
        assertThat(Breeder.breed(freshParents(10), 100, random)).hasSize(100);
    }

    @Test
    @DisplayName("Composition is 5% elite, 10% fresh blood and the rest offspring")
    void testComposition() {
        final var parents = freshParents(10);

        final var population = Breeder.breed(parents, 100, random);

        final long elite = population.stream().filter(network -> network.getAge() == 2).count();
        final long freshBlood = population.stream().filter(network -> network.getGeneration() == 1 && network.getAge() == 1).count();
        final long offspring = population.stream().filter(network -> network.getGeneration() == 2).count();
        assertThat(elite).isEqualTo(5);
        assertThat(freshBlood).isEqualTo(10);
        assertThat(offspring).isEqualTo(85);
    }

    @Test
    @DisplayName("Elites keep the parent weights intact and lead the population")
    void testElitePreservation() {
        final var parents = freshParents(10);

        final var population = Breeder.breed(parents, 100, random);

        assertThat(population.getFirst().getGeneration()).isEqualTo(parents.getFirst().getGeneration());
        assertThat(population.getFirst().getOutputLayer().getNeurons()[0].getWeights())
                .containsExactly(parents.getFirst().getOutputLayer().getNeurons()[0].getWeights());
    }

    @Test
    @DisplayName("A tiny population still keeps at least one elite")
    void testKeepsAtLeastOneElite() {
        final var parents = freshParents(4);

        final var population = Breeder.breed(parents, 1, random);

        assertThat(population).hasSize(1);
        assertThat(population.getFirst().getOutputLayer().getNeurons()[0].getWeights())
                .containsExactly(parents.getFirst().getOutputLayer().getNeurons()[0].getWeights());
    }

    @Test
    @DisplayName("The leading elites advance their age so survivors gain seniority")
    void testElitesGainSeniority() {
        final var parents = freshParents(10);

        final var population = Breeder.breed(parents, 100, random);

        final int eliteCount = 5;
        assertThat(population.subList(0, eliteCount)).allMatch(network -> network.getAge() == 2);
    }
}
