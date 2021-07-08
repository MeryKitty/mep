package d3;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;

public class DataGenerator {
    public static void main(String[] args) throws IOException {
        var random = new Random();
        var inputFolder = Path.of("data").resolve("d3").resolve("input");
        Files.createDirectories(inputFolder);
        for (int n = 0; n < 50; n++) {
            var data = new StringBuilder();
            data.append(500).append(' ').append(500).append('\n');
            for (int i = 0; i < 250; i++) {
                double x = random.nextDouble() * 700 - 100;
                double y = random.nextDouble() * 700 - 100;
                double phi = random.nextDouble() * 2 * Math.PI;
                double C = random.nextDouble() * 2 + 1;
                double R = random.nextDouble() * 20 + 60;
                double alpha = random.nextDouble() * Math.PI / 3 + Math.PI / 3;
                data.append(x).append(' ').append(y).append(' ').append(phi).append(' ').append(C).append(' ').append(R).append(' ').append(alpha).append('\n');
            }
            var tempFile = inputFolder.resolve("data_3_" + n + ".txt");
            Files.writeString(tempFile, data, StandardCharsets.UTF_8);
        }
    }
}
