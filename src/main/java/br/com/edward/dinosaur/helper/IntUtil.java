package br.com.edward.dinosaur.helper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class IntUtil {

    public static int getInt(final double value) {
        return (int) Math.floor(value);
    }
}
