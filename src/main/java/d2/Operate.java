package d2;

import algorithm.Config;
import algorithm.Dijkstra;
import algorithm.DrawUtils;
import model.Graph;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class Operate {
    private static final double MILI = 1e3;
    private static final Path INPUT_FOLDER = Path.of("data/d2/input");
    private static final Path OUTPUT_FOLDER = Path.of("./data/d2").resolve("m" + Config.M);

    public static void main(String[] args) throws Exception {
        for (int i = 0; i < 4; i++) {
            System.gc();
            var f = Field.read(INPUT_FOLDER.resolve("data_2_" + 0 + ".txt"));
            long start = System.currentTimeMillis();
            var g = Graph.construct(f);
            var result = Dijkstra.execute(g);
            long end = System.currentTimeMillis();
            System.out.println("--------------------------------------------------------");
            System.out.println("RESULT   : " + result.second() + "\nTIME     : " + ((end - start) / MILI));
            System.out.println("--------------------------------------------------------");
            System.out.println();
        }
        var resultString = new StringBuilder();
        double averRes = 0;
        double averTime = 0;
        for (int i = 0; i < 50; i++) {
            System.gc();
            String filename = "data_2_" + i;
            var f = Field.read(INPUT_FOLDER.resolve(filename + ".txt"));
            long start = System.currentTimeMillis();
            var g = Graph.construct(f);
            var result = Dijkstra.execute(g);
            long end = System.currentTimeMillis();
            double time = (end - start) / MILI;
            averRes += result.second();
            averTime += time;
            System.out.println();
            System.out.println("--------------------------------------------------------");
            System.out.println("FILE  : " + i + "\nRESULT: " + result.second() + "\nTIME  : " + time);
            System.out.println("--------------------------------------------------------");
            System.out.println();
            Files.createDirectories(OUTPUT_FOLDER);
            DrawUtils.draw(OUTPUT_FOLDER.resolve(filename + ".png"), result.second(), result.first(), f, 1);
            resultString.append(filename).append('\t').append(result.second()).append('\t').append(time).append("\n");
        }
        Files.writeString(OUTPUT_FOLDER.resolve("result_2_" + Config.M + ".txt"), "Result: " + (averRes / 50) + "\nTime  : " + (averTime / 50) + "\n");
        Files.writeString(OUTPUT_FOLDER.resolve("all_result_2_" + Config.M + ".txt"), resultString, StandardCharsets.UTF_8);
    }
}
