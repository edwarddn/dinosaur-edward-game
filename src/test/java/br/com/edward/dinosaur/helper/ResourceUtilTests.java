package br.com.edward.dinosaur.helper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.awt.image.BufferedImage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class ResourceUtilTests {

    @Test
    @DisplayName("getResourceImage() loads an existing image from the classpath")
    void testLoadExistingImage() {
        assertThat(ResourceUtil.getResourceImage("img/cloud.png")).isNotNull();
    }

    @Test
    @DisplayName("getResourceImage() returns null for a non-existing path")
    void testNonExistingImage() {
        assertThat(ResourceUtil.getResourceImage("img/non_existing.png")).isNull();
    }

    @Test
    @DisplayName("getResourceSound() loads an existing audio from the classpath")
    void testLoadExistingSound() {
        assertThat(ResourceUtil.getResourceSound("audio/reached.wav")).isNotNull();
    }

    @Test
    @DisplayName("getResourceSound() returns null for a non-existing path")
    void testNonExistingSound() {
        assertThat(ResourceUtil.getResourceSound("audio/non_existing.wav")).isNull();
    }

    @Test
    @DisplayName("getResourceFont() loads the existing font from the classpath")
    void testLoadExistingFont() {
        assertThat(ResourceUtil.getResourceFont("font-ttf/press-start2p-regular.ttf")).isNotNull();
    }

    @Test
    @DisplayName("getResourceFont() returns null for a non-existing path")
    void testNonExistingFont() {
        assertThat(ResourceUtil.getResourceFont("font-ttf/non_existing.ttf")).isNull();
    }

    @Test
    @DisplayName("playSound() ignores a null clip without throwing")
    void testPlayNullSound() {
        assertThatCode(() -> ResourceUtil.playSound(null)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("setColorToYellow() zeroes the blue channel keeping red, green and alpha")
    void testSetColorToYellow() {
        final var src = new BufferedImage(2, 2, BufferedImage.TYPE_INT_ARGB);
        src.setRGB(0, 0, new Color(10, 20, 30, 200).getRGB());

        final var pixel = new Color(ResourceUtil.setColorToYellow(src).getRGB(0, 0), true);

        assertThat(pixel.getRed()).isEqualTo(10);
        assertThat(pixel.getGreen()).isEqualTo(20);
        assertThat(pixel.getBlue()).isZero();
        assertThat(pixel.getAlpha()).isEqualTo(200);
    }
}
