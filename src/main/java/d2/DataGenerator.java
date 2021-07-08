package d2;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;

public class DataGenerator {
    public static void main(String[] args) throws IOException {
        var random = new Random();
        var inputFolder = Path.of("data").resolve("d2").resolve("input");
        Files.createDirectories(inputFolder);
        for (int n = 0; n < 50; n++) {
            var data = new StringBuilder();
            data.append(500).append(' ').append(500).append('\n');
            for (int i = 0; i < 250; i++) {
                double x = random.nextDouble() * 700 - 100;
                double y = random.nextDouble() * 700 - 100;
                double phi = random.nextDouble() * 2 * Math.PI;
                data.append(x).append(' ').append(y).append(' ').append(phi).append('\n');
            }
            var tempFile = inputFolder.resolve("data_2_" + n + ".txt");
            Files.writeString(tempFile, data, StandardCharsets.UTF_8);
        }
    }
}
