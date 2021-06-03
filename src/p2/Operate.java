package p2;

import java.nio.file.Files;
import java.nio.file.Paths;

import algorithm.Config;
import algorithm.Dijkstra;
import algorithm.DrawUtils;
import model.Graph;

public class Operate {
	private static final double NANO = 1e9;
	private static final double MILI = 1e3;
	private static final String INPUT_FOLDER;
	static {
		String tail = "/LabWorkspace/SOURCE CODE ASOC-D-18-02515/MEPDirecCode/Data/Data/";
		if (System.getProperty("os.name").toLowerCase().contains("win")) {
			INPUT_FOLDER = "E:" + tail;
		} else {
			INPUT_FOLDER = "/mnt/e" + tail;
		}
	}
	
	public static void main(String[] args) throws Exception {
		for (int i = 0; i < 4; i++) {
			System.gc();
			Field f = new Field(INPUT_FOLDER + "data_" + 0 + ".txt");
			long start = System.currentTimeMillis(); //Config.THREAD_MX_BEAN.getCurrentThreadCpuTime();
			Graph g = Graph.construct(f);
			var result = Dijkstra.execute(g);
			long end = System.currentTimeMillis(); //Config.THREAD_MX_BEAN.getCurrentThreadCpuTime();
			System.out.println();
			System.out.println("--------------------------------------------------------");
			System.out.println("RESULT   : " + result.second() + "\nTIME     : " + ((end - start) / MILI));
			for (int clock = 0; clock < 10; clock++) {
				System.out.println("TIME " + clock + "   : " + (Dijkstra.clock(clock) / MILI));
			}
			System.out.println("--------------------------------------------------------");
			System.out.println();
			Dijkstra.restartClock();
		}
		for (int i = 0; i < 8; i++) {
			System.gc();
			Field f = new Field(INPUT_FOLDER + "data_" + i + ".txt");
			long start = Config.THREAD_MX_BEAN.getCurrentThreadCpuTime();
			Graph g = Graph.construct(f);
			var result = Dijkstra.execute(g);
			long end = Config.THREAD_MX_BEAN.getCurrentThreadCpuTime();
			System.out.println();
			System.out.println("--------------------------------------------------------");
			System.out.println("RESULT: " + result.second() + "\nTIME  : " + ((end - start) / NANO));
			System.out.println("--------------------------------------------------------");
			System.out.println();
			Files.createDirectories(Paths.get("data/p2"));
			DrawUtils.draw("data/p2/data_" + i + ".png", result.second(), result.first(), f, g, 1);
		}
	}
}
