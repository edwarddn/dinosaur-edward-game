package br.com.edward.dinosaur.helper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class ResourceUtilTests {

    @Test
    @DisplayName("Check getResourceSprite with valid file")
    void testGetNotNullResourceSprite() {
        assertThat(ResourceUtil.getResourceSprite("img/cloud.png")).isNotNull();
    }

    @Test
    @DisplayName("Check getResourceSprite with non existing file")
    void testGetNullResourceSpriteForNonExistingResource() {
        assertThat(ResourceUtil.getResourceSprite("non_existing_path.png")).isNull();
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
}