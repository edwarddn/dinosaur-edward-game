package br.com.edward.dinosaur.helper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class IntUtilTests {

    @Test
    @DisplayName("getInt() truncates positive values downwards")
    void testTruncatePositiveValues() {
        assertThat(IntUtil.getInt(10.6)).isEqualTo(10);
        assertThat(IntUtil.getInt(10.0)).isEqualTo(10);
    }

    @Test
    @DisplayName("getInt() applies floor to negative values")
    void testTruncateNegativeValues() {
        assertThat(IntUtil.getInt(-10.7)).isEqualTo(-11);
        assertThat(IntUtil.getInt(-0.1)).isEqualTo(-1);
    }

    @Test
    @DisplayName("getInt() of zero is zero")
    void testZero() {
        assertThat(IntUtil.getInt(0.0)).isZero();
    }
}
