package p1;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import algorithm.Dijkstra;
import algorithm.DrawUtils;
import model.Graph;
import model.Pair;
import model.Vertex;

public class Operate {
	@SuppressWarnings("unused")
	private static final double[][] sensorData = {{1.56, 3.54},
			{5.7, 0.35},
			{9.55, 8.5},
			{4.85, 9.35},
			{6.75, 8},
			{1.4, 7.55},
			{5, 7},
			{9.15, 3.9},
			{5.9, 5.55},
			{9.6, 1.7},
			{4.15, 2},
			{3.2, 3.25},
			{7.35, 2.15},
			{1.5, 1.5},
			{6.65, 7.85},
			{4.8, 4.1},
			{6.5, 4.1},
			{5.8, 2.5},
			{8, 5},
			{2.8, 5.5},
			{4, 5.8},
			{0.8, 5.5},
			{9.9, 5.8},
			{3.5, 7.2},
			{7.8, 3.5},
			{2.6, 8.5},
			{0.2, 9.15},
			{2.5, 0.2},
			{8, 0.1},
			{0.05, 0},
			{4, 0},
			{7, 9}
	};

	public static void main(String[] args) throws IOException, InterruptedException {
		int fields = 100;
		double[] time = new double[5];
		for (int i = 0; i < fields; i++) {
			var field = new Field(100);
			for (int j = 0; j < time.length; j++) {
				long start = System.currentTimeMillis();
				var graph = Graph.construct(field);
				Pair<List<Vertex>, Float> result = null;
//				if (j == 0) {
				
				result = Dijkstra.execute(graph);
				
//				} else {
//					ConcurrentDijkstra.stepWidth = DELTA / Math.pow(2, j - 1);
//					result = ConcurrentDijkstra.execute(graph);
//				}
				System.out.println(result.second());
				System.out.println();
				long end = System.currentTimeMillis();
				Files.createDirectories(Paths.get("data/p1"));
				DrawUtils.draw("data/p1/data_" + i + ".png", result.second(), result.first(), field, graph, 10);
				if (result.second() > 0) {
					System.out.println(String.format("%-3d: ", i) + result.second());
					System.out.println("   : " + (end - start));
					System.out.println();
				}
				time[j] += end - start;
			}
		}
		System.out.println();
		for (int j = 0; j < time.length; j++) {
			System.out.println(j + " : " + (time[j] / fields));
		}
	}
}
