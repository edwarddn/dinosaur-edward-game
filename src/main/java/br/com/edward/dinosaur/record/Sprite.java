package br.com.edward.dinosaur.record;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public record Sprite(Image image, List<Frame> frames) {

    public Sprite(final Image image, final int width, final int height, final int x, final int y) {
        this(image, new ArrayList<>());
        this.addFrame(width, height, x, y);
    }

    public void draw(final Graphics2D g2d, final int telaX, final int telaY, final int p) {
        final var f = this.getFrame(p);
        g2d.drawImage(image, telaX, telaY, telaX + f.width(), telaY + f.height(), f.x(), f.y(), f.x() + f.width(), f.y() + f.height(), null);
    }

    public void addFrame(final int width, final int height, final int x, final int y) {
        this.frames.add(new Frame(width, height, x, y));
    }

    public Frame getFrame(final int p) {
        if (p >= this.frames.size()) {
            return this.frames.get(0);
        }
        return this.frames.get(p);
    }
}
