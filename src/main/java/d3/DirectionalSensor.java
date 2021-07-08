package d3;

public record DirectionalSensor(double x, double y, double phi, double C, double R, double alpha) implements Sensor{
    @Override
    public double coverageIntensity(double x, double y) {
        double dx = x - this.x;
        double dy = y - this.y;
        double d2 = dx * dx + dy * dy;
        if (d2 > R * R) {
            return 0;
        } else {
            double d = Math.sqrt(d2);
            double cos = (dx * Math.cos(this.phi) + dy * Math.sin(this.phi)) / d;
            if (cos < Math.cos(alpha / 2)) {
                return 0;
            } else {
                return Math.min(C / d, 0.5);
            }
        }
    }
}
