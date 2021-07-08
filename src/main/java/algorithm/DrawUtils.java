package algorithm;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import javax.imageio.ImageIO;

import model.Graph;
import model.IField;
import model.Vertex;

public class DrawUtils {
	private static final int STROKE = 3;

	public static void drawField(BufferedImage image, IField field, double scale) {
		var graphics = (Graphics2D)image.getGraphics();
		graphics.setStroke(new BasicStroke(STROKE));
		graphics.setColor(Color.BLACK);
		int width = (int)(field.W() * scale);
		int height = (int)(field.H() * scale);
		graphics.drawRect(0, 0, width + STROKE, height + STROKE);
		double[][] intensity = new double[height][width];
		double maxIntensity = 0;
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				double current = field.exposure(j / scale, i / scale);
				intensity[j][i] = current;
				maxIntensity = Math.max(current, maxIntensity);
			}
		}
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				int red = (int)(255 * (1 - intensity[j][i] / maxIntensity)) & 0xff;
				image.setRGB(j + STROKE, i + STROKE, 0xff << 24 | red << 16 | red << 8 | 0xff);
			}
		}
	}
	
	public static void drawPath(Graphics2D graphic, List<Vertex> path, double scale) {
		double realScale = (Config.DELTA / Config.M) * scale;
		graphic.setStroke(new BasicStroke(1));
		Vertex previous = null;
		for (var iter = path.listIterator(); iter.hasNext();) {
			var current = iter.next();
			if (previous != null && Math.abs(current.row() - previous.row()) <= Config.M && Math.abs(current.column() - previous.column()) <= Config.M) {
				graphic.drawLine((int)(previous.column() * realScale) + STROKE, (int)(previous.row() * realScale) + STROKE, (int)(current.column() * realScale) + STROKE, (int)(current.row() * realScale) + STROKE);
			}
			previous = current;
		}
	}
	
	public static void draw(Path file, double result, List<Vertex> path, IField field, double scale) throws IOException {
		var pic = new BufferedImage((int)(field.W() * scale + STROKE * 2), (int)(field.H() * scale + STROKE * 2 + 10), BufferedImage.TYPE_INT_ARGB);
		var graphic = pic.createGraphics();
		graphic.setColor(Color.WHITE);
		graphic.fillRect(0, 0, pic.getWidth(), pic.getHeight());
		graphic.setColor(Color.BLACK);
		drawField(pic, field, scale);
		drawPath(graphic, path, scale);
		graphic.drawString(String.format("MinE: %.2f", result), 0, (int)(field.H() * scale + STROKE * 2 + 10));
		var output = file.toFile();
		ImageIO.write(pic, "png", output);
	}
}