package br.com.edward.dinosaur.entity;

import br.com.edward.dinosaur.ai.Layer;
import br.com.edward.dinosaur.ai.NeuralNetwork;
import br.com.edward.dinosaur.ai.Neuron;
import br.com.edward.dinosaur.config.Config;

import java.awt.*;

public class NeuralNetworkDisplay {

    private final int w;
    private final int h;
    private final Config config;

    public NeuralNetworkDisplay(final Config config) {
        this.w = this.h = 25;
        this.config = config;
    }

    public void draw(final Graphics2D g2d, final NeuralNetwork neuralNetwork) {
        this.draw(g2d, neuralNetwork.getInputLayer(), 250, 25);
        this.draw(g2d, neuralNetwork.getHiddenLayer(), 150, 25);
        this.draw(g2d, neuralNetwork.getOutputLayer(), 50, 50);
        this.draw(g2d, neuralNetwork.getInputLayer(), 250, 25, neuralNetwork.getHiddenLayer(), 150, 25);
        this.draw(g2d, neuralNetwork.getHiddenLayer(), 150, 25, neuralNetwork.getOutputLayer(), 50, 50);
    }

    private void draw(final Graphics2D g2d, final Layer l, final int x, final int y) {
        int i = 0;
        for (final var n : l.getNeurons()) {
            this.draw(g2d, n, x, (y + (50 * ++i)));
        }
    }

    private void draw(final Graphics2D g2d, final Layer l1, final int x1, final int y1, final Layer l2, final int x2, final int y2) {
        int i1 = 0;
        for (final var n1 : l1.getNeurons()) {

            i1++;
            int i2 = 0;
            for (final var n2 : l2.getNeurons()) {

                g2d.setColor((n1.isActive() && n2.isActive()) ? Color.GREEN : Color.BLUE);
                g2d.drawLine(
                        ((this.config.getWidth() - x1) + this.w),
                        ((y1 + (50 * i1)) + (this.h / 2)),
                        (this.config.getWidth() - x2),
                        ((y2 + (50 * ++i2)) + (this.h / 2))
                );
            }
        }
    }

    public void draw(final Graphics2D g2d, final Neuron neuron, final int x, final int y) {
        if (neuron.isActive()) {
            g2d.setColor(Color.GREEN);
            g2d.fillOval(this.config.getWidth() - x, y, this.w, this.h);
        } else {
            g2d.setColor(Color.BLUE);
            g2d.drawOval(this.config.getWidth() - x, y, this.w, this.h);
        }
    }
}
