package br.com.edward.dinosaur.helper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.Objects;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ResourceUtil {

    public static Image getResourceSprite(final String path) {
        final var url = ResourceUtil.class.getClassLoader().getResource(path);
        if (Objects.isNull(url)) {
            return null;
        }
        return new ImageIcon(url).getImage();
    }

    public static Clip getResourceSound(final String path) {

        final var url = ResourceUtil.class.getClassLoader().getResource(path);
        if (Objects.isNull(url)) {
            return null;
        }

        try (final var audioStream = new BufferedInputStream(url.openStream())) {
            final var audioInputStream = AudioSystem.getAudioInputStream(audioStream);
            final var sound = AudioSystem.getClip();
            sound.open(audioInputStream);
            return sound;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    public static synchronized void playSound(final Clip clip) {
        if (Objects.nonNull(clip)) {
            clip.setFramePosition(0);
            clip.start();
        }
    }

    public static Font getResourceFont(final String path) {
        final var url = ResourceUtil.class.getClassLoader().getResource(path);
        if (Objects.isNull(url)) {
            return null;
        }
        try (InputStream inputStream = url.openStream()) {
            return Font.createFont(Font.TRUETYPE_FONT, inputStream);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }
}
