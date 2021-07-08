package d1;

public record Sensor(double x, double y, double R) {
    public double coverageIntensity(double x, double y) {
        double dx = this.x - x;
        double dy = this.y - y;
        return dx * dx + dy * dy > R * R ? 0 : 1;
    }
}
