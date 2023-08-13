package br.com.edward.dinosaur.helper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.awt.image.BufferedImage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class ResourceUtilTests {

    @Test
    @DisplayName("Check getResourceImage with valid file")
    void testGetNotNullResourceImage() {
        assertThat(ResourceUtil.getResourceImage("img/cloud.png")).isNotNull();
    }

    @Test
    @DisplayName("Check getResourceImage with non existing file")
    void testGetNullResourceImageForNonExistingResource() {
        assertThat(ResourceUtil.getResourceImage("non_existing_path.png")).isNull();
    }

    @Test
    @DisplayName("Check getResourceSound with valid file")
    void testGetNotNullResourceSound() {
        assertThat(ResourceUtil.getResourceSound("audio/reached.wav")).isNotNull();
    }

    @Test
    @DisplayName("Check getResourceSound with non existing file")
    void testGetNullResourceSoundForNonExistingResource() {
        assertThat(ResourceUtil.getResourceSound("non_existing_path.wav")).isNull();
    }

    @Test
    @DisplayName("Check playing sound from existing file")
    void testPlayExistingSound() {
        final var sound = ResourceUtil.getResourceSound("audio/reached.wav");
        assertThatCode(() -> ResourceUtil.playSound(sound)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Check playing sound from non existing file")
    void testNotPlayNonExistingSound() {
        assertThatCode(() -> ResourceUtil.playSound(null)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Check getResourceFont with valid file")
    void testGetNotNullResourceFont() {
        assertThat(ResourceUtil.getResourceFont("font-ttf/press-start2p-regular.ttf")).isNotNull();
    }

    @Test
    @DisplayName("Check getResourceFont with non existing file")
    void testGetNullResourceFontForNonExistingResource() {
        assertThat(ResourceUtil.getResourceFont("non_existing_path.ttf")).isNull();
    }

    @Test
    @DisplayName("Test that setColorToYellow method properly converts all pixels in the image to yellow")
    void testSetColorToYellow() {

        final var src = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
        final var g = src.getGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, 100, 100);

        final var result = ResourceUtil.setColorToYellow(src);

        for (int y = 0; y < 100; y++) {
            for (int x = 0; x < 100; x++) {
                final var pixel = new Color(result.getRGB(x, y), true);
                assertThat(pixel.getRed()).isEqualTo(255);
                assertThat(pixel.getGreen()).isEqualTo(255);
                assertThat(pixel.getBlue()).isEqualTo(0);
            }
        }
    }
}