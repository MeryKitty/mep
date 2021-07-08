package d3;

public record DiskSensor(double x, double y, double C, double R) implements Sensor {
    @Override
    public double coverageIntensity(double x, double y) {
        double dx = x - this.x;
        double dy = y - this.y;
        double d2 = dx * dx + dy * dy;
        if (d2 > R * R) {
            return 0;
        } else {
            return Math.min(C / Math.sqrt(d2), 0.5);
        }
    }
}
