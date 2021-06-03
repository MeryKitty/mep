package algorithm;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

import model.Graph;
import model.IField;
import model.Vertex;

public class DrawUtils {
	public static void drawField(Graphics2D graphic, Graph graph, double scale) {
		var cellList = graph.cellContainer();
		double realScale = Config.DELTA * scale;
		int rowNumber = cellList.rowNumber();
		int columnNumber = cellList.columnNumber();
		for (int i = 0; i < rowNumber; i++) {
			for (int j = 0; j < columnNumber; j++) {
				if (i == 0 || cellList.exposure(i - 1, j) == -1) {
					graphic.drawLine((int)(j * realScale), (int)(i * realScale), (int)((j + 1) * realScale), (int)(i * realScale));
				}
				if (i == rowNumber - 1 || cellList.exposure(i + 1, j) == -1) {
					graphic.drawLine((int)(j * realScale), (int)((i + 1) * realScale), (int)((j + 1) * realScale), (int)((i + 1) * realScale));
				}
				if (j == 0 || cellList.exposure(i, j - 1) == -1) {
					graphic.drawLine((int)(j * realScale), (int)(i * realScale), (int)(j * realScale), (int)((i + 1) * realScale));
				}
				if (j == columnNumber - 1 || cellList.exposure(i, j + 1) == -1) {
					graphic.drawLine((int)((j + 1) * realScale), (int)(i * realScale), (int)((j + 1) * realScale), (int)((i + 1) * realScale));
				}
				if (!cellList.initiated(i, j)) {
					graphic.fillRect((int)(j * realScale), (int)(i * realScale), (int)realScale + 1, (int)realScale + 1);
				}
			}
		}
	}
	
	public static void drawPath(Graphics2D graphic, List<Vertex> path, double scale) {
		double realScale = (Config.DELTA / Config.M) * scale;
		Vertex previous = null;
		for (var iter = path.listIterator(); iter.hasNext();) {
			var current = iter.next();
			if (previous != null && Math.abs(current.row() - previous.row()) <= Config.M && Math.abs(current.column() - previous.column()) <= Config.M) {
				graphic.drawLine((int)(previous.column() * realScale), (int)(previous.row() * realScale), (int)(current.column() * realScale), (int)(current.row() * realScale));
			}
			previous = current;
		}
	}
	
	public static void draw(String file, double result, List<Vertex> path, IField field, Graph graph, double scale) throws IOException {
		var pic = new BufferedImage((int)(field.W() * scale + 1), (int)(field.H() * scale * 1.5 + 1), BufferedImage.TYPE_INT_ARGB);
		var graphic = pic.createGraphics();
		graphic.setColor(Color.WHITE);
		graphic.fillRect(0, 0, pic.getWidth(), pic.getHeight());
		graphic.setColor(Color.BLACK);
		drawField(graphic, graph, scale);
		drawPath(graphic, path, scale);
		graphic.drawString(String.format("Exposure: %.2f", result), 0, (int)(field.H() * scale * 1.4));
		var output = new File(file);
		ImageIO.write(pic, "png", output);
	}
}