package br.com.edward.dinosaur.helper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.Objects;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ResourceUtil {

    public static BufferedImage getResourceImage(final String path) {
        final var url = ResourceUtil.class.getClassLoader().getResource(path);
        if (Objects.isNull(url)) {
            return null;
        }
        try {
            return ImageIO.read(url);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    public static BufferedImage setColorToYellow(final BufferedImage src) {
        final var width = src.getWidth();
        final var height = src.getHeight();

        final var dst = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        for (int y = 0; y < height; y++) {

            for (int x = 0; x < width; x++) {
                final var color = new Color(src.getRGB(x, y), true);
                final var yellow = new Color(color.getRed(), color.getGreen(), 0, color.getAlpha());
                dst.setRGB(x, y, yellow.getRGB());
            }
        }
        return dst;
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
