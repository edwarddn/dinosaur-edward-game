package br.com.edward.dinosaur.helper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class IntUtilTests {

    @Test
    @DisplayName("Test fetching integer portion from a float number")
    void testGetInt() {
        assertThat(IntUtil.getInt(10.6)).isEqualTo(10);
        assertThat(IntUtil.getInt(-10.7)).isEqualTo(-11);
        assertThat(IntUtil.getInt(0)).isZero();
    }
}