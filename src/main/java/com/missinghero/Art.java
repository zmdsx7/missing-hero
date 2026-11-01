package com.missinghero;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

/**
 * Abstract resource loading behind a tiny image cache
 * (assets are packaged inside the JAR).
 */
final class Art {

    private static final Map<String, BufferedImage> CACHE = new HashMap<>();

    private Art() {
    }

    static BufferedImage get(String name) {
        return CACHE.computeIfAbsent(name, key -> {
            try (var stream = Art.class.getResourceAsStream("/art/" + key + ".png")) {

                if (stream == null) {
                    throw new IllegalArgumentException(
                            "Missing embedded asset " + key
                    );
                }

                return ImageIO.read(stream);

            } catch (Exception e) {
                throw new IllegalStateException("Unable to read " + key, e);
            }
        });
    }

    static void image(Graphics2D g, String name, int x, int y, int w, int h) {
        Object old = g.getRenderingHint(RenderingHints.KEY_INTERPOLATION);

        g.setRenderingHint(
                RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR
        );

        g.drawImage(get(name), x, y, w, h, null);

        if (old != null) {
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, old);
        }
    }
}