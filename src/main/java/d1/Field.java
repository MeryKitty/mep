package d1;

import model.IField;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

public record Field(double W, double H, List<Sensor> sensorList) implements IField {
    public static Field read(Path input) throws IOException {
        var lines = Files.readAllLines(input, StandardCharsets.UTF_8);
        List<Double> size = Arrays.stream(lines.get(0).split(" ")).map(Double::parseDouble).toList();
        var sensorList = lines.stream().skip(1).map(line -> {
            var sensorData = Arrays.stream(line.split(" "))
                    .map(Double::parseDouble).toList();
            return new Sensor(sensorData.get(0), sensorData.get(1), sensorData.get(2));
        }).toList();
        return new Field(size.get(0), size.get(1), sensorList);
    }

    @Override
    public boolean inField(double x, double y) {
        return x >= 0 && x <= this.W && y >= 0 && y <= this.H;
    }

    @Override
    public double exposure(double x, double y) {
        double result = 0;
        for (var sensor : sensorList) {
            result += sensor.coverageIntensity(x, y);
        }
        return result < 1e-9 ? 1e-4 : result;
    }

    @Override
    public boolean isSource(double x, double y, double error) {
        return x < error;
    }

    @Override
    public boolean isSink(double x, double y, double error) {
        return x > this.W - error;
    }
}
