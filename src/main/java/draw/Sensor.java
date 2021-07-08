package draw;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;

public class Sensor {
    private static final Path OUTPUT_FOLDER = Path.of("/mnt/e/lab/thesis/graphics");
    private static void omnidirectionalBinary() throws IOException {
        var image = new BufferedImage(2000, 2000, BufferedImage.TYPE_INT_ARGB);
        int inRanged = 0xff << 24 | 0xc0 << 16 | 0xc0 << 8 | 0xff;
        for (int i = 0; i < 2000; i++) {
            for (int j = 0; j < 2000; j++) {
                double dx = 1000 - i;
                double dy = 1000 - j;
                if (dx * dx + dy * dy <= 800 * 800) {
                    image.setRGB(i, j, inRanged);
                }
            }
        }
        ImageIO.write(image, "png", OUTPUT_FOLDER.resolve("ob.png").toFile());
    }

    private static void omnidirectionalAttenuated() throws IOException {
        var image = new BufferedImage(2000, 2000, BufferedImage.TYPE_INT_ARGB);
        for (int i = 0; i < 2000; i++) {
            for (int j = 0; j < 2000; j++) {
                double dx = 1000 - i;
                double dy = 1000 - j;
                double d = Math.sqrt(dx * dx + dy * dy);
                double intensity = Math.max(1 - d/800, 0);
                int red = 255 - (int)(127 * intensity);
                int colour = 0xff << 24 | red << 16 | red << 8 | 0xff;
                image.setRGB(i, j, colour);
            }
        }
        ImageIO.write(image, "png", OUTPUT_FOLDER.resolve("oa.png").toFile());
    }

    private static void omnidirectionalMixed() throws IOException {
        var image = new BufferedImage(2000, 2000, BufferedImage.TYPE_INT_ARGB);
        for (int i = 0; i < 2000; i++) {
            for (int j = 0; j < 2000; j++) {
                double dx = 1000 - i;
                double dy = 1000 - j;
                double d = Math.sqrt(dx * dx + dy * dy);
                double intensity;
                if (d <= 200) {
                    intensity = 1;
                } else if (d > 800) {
                    intensity = 0;
                } else {
                    intensity = 0.05 + 0.9 * (1 - (d - 200)/600);
                }
                int red = 255 - (int)(127 * intensity);
                int colour = 0xff << 24 | red << 16 | red << 8 | 0xff;
                image.setRGB(i, j, colour);
            }
        }
        ImageIO.write(image, "png", OUTPUT_FOLDER.resolve("om.png").toFile());
    }

    private static void directionalBinary() throws IOException {
        var image = new BufferedImage(2000, 2000, BufferedImage.TYPE_INT_ARGB);
        int inRanged = 0xff << 24 | 0xc0 << 16 | 0xc0 << 8 | 0xff;
        for (int i = 0; i < 2000; i++) {
            for (int j = 0; j < 2000; j++) {
                double dx = 1000 - i;
                double dy = 1000 - j;
                if (dx * dx + dy * dy <= 800 * 800) {
                    dx = Math.abs(dx);
                    if (dx == 0 && dy >= 0 || dy / dx >= Math.tan(55 * Math.PI / 180)) {
                        image.setRGB(i, j, inRanged);
                    }
                }
            }
        }
        ImageIO.write(image, "png", OUTPUT_FOLDER.resolve("db.png").toFile());
    }

    private static void directionalAttenuated() throws IOException {
        var image = new BufferedImage(2000, 2000, BufferedImage.TYPE_INT_ARGB);
        for (int i = 0; i < 2000; i++) {
            for (int j = 0; j < 2000; j++) {
                double dx = 1000 - i;
                double dy = 1000 - j;
                double d = Math.sqrt(dx * dx + dy * dy);
                double cos2 = dy / d;
                double cos = Math.sqrt((cos2 + 1) / 2);
                double intensity = Math.max(1 - d/(800 * cos), 0) * Math.pow(cos, 2);
                int red = 255 - (int)(127 * intensity);
                int colour = 0xff << 24 | red << 16 | red << 8 | 0xff;
                image.setRGB(i, j, colour);
            }
        }
        ImageIO.write(image, "png", OUTPUT_FOLDER.resolve("da.png").toFile());
    }

    private static void directionalMixed() throws IOException {
        var image = new BufferedImage(2000, 2000, BufferedImage.TYPE_INT_ARGB);
        for (int i = 0; i < 2000; i++) {
            for (int j = 0; j < 2000; j++) {
                double dx = 1000 - i;
                double dy = 1000 - j;
                if (dx * dx + dy * dy <= 800 * 800) {
                    dx = Math.abs(dx);
                    if (dx == 0 && dy >= 0 || dy / dx >= Math.tan(55 * Math.PI / 180)) {
                        double d = Math.sqrt(dx * dx + dy * dy);
                        double intensity;
                        if (d < 200) {
                            intensity = 1;
                        } else if (d > 800) {
                            intensity = 0;
                        } else {
                            intensity = 0.05 + 0.9 * (1 - (d - 200)/600);
                        }
                        int red = 255 - (int)(127 * intensity);
                        int colour = 0xff << 24 | red << 16 | red << 8 | 0xff;
                        image.setRGB(i, j, colour);
                    }
                }
            }
        }
        ImageIO.write(image, "png", OUTPUT_FOLDER.resolve("dm.png").toFile());
    }

    public static void main(String[] args) throws Exception {
        omnidirectionalMixed();
        directionalMixed();
    }
}
