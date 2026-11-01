package com.missinghero;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;

/** Entry point; run without parameters to play. --screenshot is for project screenshots. */
public final class Main {

    private Main() {
    }

    public static void main(String[] args) throws Exception {

        if (args.length >= 2 && args[0].equals("--screenshot")) {
            System.setProperty("java.awt.headless", "true");

            GamePanel panel = new GamePanel();

            if (args.length >= 3) {
                panel.state.start();
                panel.state.closeDialog();

                String target = args[2].toLowerCase();

                if (target.equals("clue")) {
                    panel.state.previewScene(GameState.Scene.BEDROOM);
                    panel.state.placeForTest(5, 8);
                    panel.state.interact();

                } else if (target.equals("puzzle")) {

                    panel.state.previewScene(GameState.Scene.BEDROOM);

                    for (GameState.WorldObject o : panel.state.objects()) {
                        if (o.id().equals("collar")
                                || o.id().equals("door_scratch")
                                || o.id().equals("ball")) {

                            panel.state.placeForTest(o.x(), o.y());
                            panel.state.toggleFlashlight();

                            if (!panel.state.flashlight()) {
                                panel.state.toggleFlashlight();
                            }

                            panel.state.interact();
                            panel.state.closeDialog();
                        }
                    }

                    panel.state.placeForTest(18, 10);
                    panel.state.interact();

                } else if (target.equals("end")) {
                    panel.state.previewScene(GameState.Scene.TRUE_END);

                } else {
                    try {
                        panel.state.previewScene(
                                GameState.Scene.valueOf(target.toUpperCase())
                        );
                    } catch (IllegalArgumentException ignored) {
                    }
                }
            }

            panel.setSize(GamePanel.WIDTH, GamePanel.HEIGHT);

            BufferedImage img = new BufferedImage(
                    GamePanel.WIDTH,
                    GamePanel.HEIGHT,
                    BufferedImage.TYPE_INT_RGB
            );

            Graphics2D g = img.createGraphics();
            panel.paint(g);
            g.dispose();

            ImageIO.write(img, "png", new File(args[1]));
            panel.stop();
            return;
        }

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Missing Hero | Java OOP Mystery");
            GamePanel panel = new GamePanel();

            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.setResizable(false);
            frame.setContentPane(panel);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            panel.requestFocusInWindow();
        });
    }
}