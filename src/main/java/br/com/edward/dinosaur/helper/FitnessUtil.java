package br.com.edward.dinosaur.helper;

import br.com.edward.dinosaur.entity.Dinosaur;
import lombok.experimental.UtilityClass;

import java.util.Comparator;

@UtilityClass
public final class FitnessUtil {

    public static final Comparator<Dinosaur> COMPARATOR = Comparator
            .comparingLong(Dinosaur::getScore)
            .thenComparingInt(Dinosaur::getAge)
            .thenComparing(Dinosaur::getMovementCount, Comparator.reverseOrder());
}
