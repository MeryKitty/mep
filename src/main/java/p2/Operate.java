package p2;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import algorithm.Config;
import algorithm.Dijkstra;
import algorithm.DrawUtils;
import model.Graph;

public class Operate {
	private static final double MILI = 1e3;
	private static final Path TYPE = Path.of("attenuatedMultiple").resolve("m" + Config.M);
	private static final Path INPUT_FOLDER = Path.of("data/p2/input");
	private static final Path OUTPUT_FOLDER = Path.of("./data/p2").resolve(TYPE);
	
	public static void main(String[] args) throws Exception {
		for (int i = 0; i < 4; i++) {
			Field f = new Field(INPUT_FOLDER.resolve("data_" + 0 + ".txt"));
			long start = System.currentTimeMillis();
			Graph g = Graph.construct(f);
			var result = Dijkstra.execute(g);
			long end = System.currentTimeMillis();
			System.out.println("--------------------------------------------------------");
			System.out.println("RESULT   : " + result.second() + "\nTIME     : " + ((end - start) / MILI));
			System.out.println("--------------------------------------------------------");
			System.out.println();
		}
		var resultString = new StringBuilder();
		for (int i = 0; i < 8; i++) {
			String filename = "data_" + i;
			Field f = new Field(INPUT_FOLDER.resolve(filename + ".txt"));
			long start = System.currentTimeMillis();
			Graph g = Graph.construct(f);
			var result = Dijkstra.execute(g);
			long end = System.currentTimeMillis();
			double time = (end - start) / MILI;
			System.out.println();
			System.out.println("--------------------------------------------------------");
			System.out.println("RESULT: " + result.second() + "\nTIME  : " + time);
			System.out.println("--------------------------------------------------------");
			System.out.println();
			Files.createDirectories(OUTPUT_FOLDER);
			DrawUtils.draw(OUTPUT_FOLDER.resolve(filename + ".png"), result.second(), result.first(), f, 1);
			resultString.append("File name: ").append(filename).append("\nResult   : ").append(result.second()).append("\nTime     : ").append(time).append("\n\n");
		}
		Files.writeString(OUTPUT_FOLDER.resolve("result_" + Config.M + ".txt"), resultString, StandardCharsets.UTF_8);
	}
}
