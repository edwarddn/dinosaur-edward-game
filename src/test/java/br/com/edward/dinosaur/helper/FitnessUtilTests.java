package br.com.edward.dinosaur.helper;

import br.com.edward.dinosaur.entity.Dinosaur;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FitnessUtilTests {

    @Test
    @DisplayName("Higher score always wins, regardless of age")
    void testScoreDominates() {
        final var veteran = dino(500, 99, 0);
        final var rookie = dino(900, 1, 0);

        assertThat(Stream.of(veteran, rookie).max(FitnessUtil.COMPARATOR)).contains(rookie);
    }

    @Test
    @DisplayName("On a score tie the older veteran wins even when it moved more (protects the saved champion)")
    void testAgeBreaksTieOverMovements() {
        final var veteran = dino(0, 40, 50);
        final var freshChild = dino(0, 1, 0);

        assertThat(Stream.of(freshChild, veteran).max(FitnessUtil.COMPARATOR)).contains(veteran);
    }

    @Test
    @DisplayName("With equal score and age, the more efficient run (fewer movements) wins")
    void testMovementsBreakRemainingTie() {
        final var efficient = dino(0, 1, 10);
        final var wasteful = dino(0, 1, 99);

        assertThat(Stream.of(wasteful, efficient).max(FitnessUtil.COMPARATOR)).contains(efficient);
    }

    private Dinosaur dino(final long score, final int age, final long movementCount) {
        final var dinosaur = mock(Dinosaur.class);
        when(dinosaur.getScore()).thenReturn(score);
        when(dinosaur.getAge()).thenReturn(age);
        when(dinosaur.getMovementCount()).thenReturn(movementCount);
        return dinosaur;
    }
}
