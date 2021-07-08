package d2;

public record Sensor(double x, double y, double phi) {
    public double coverageIntensity(double x, double y) {
        double dx = x - this.x;
        double dy = y - this.y;
        double d = Math.sqrt(dx * dx + dy * dy);
        double cos2 = (dx * Math.cos(this.phi) + dy * Math.sin(this.phi)) / d;
        double cos = Math.sqrt((cos2 + 1) / 2);
        return Math.min(cos / Math.pow(d, 1.5), 0.5);
    }
}
