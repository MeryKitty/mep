/**
 * @author ljnk975
 */
package p2;

import java.awt.Graphics;

public class Sensor {

	public Point p, pt;
	public double Viangle;
	public double r;
	public double angle;

    public Sensor(Point p, double Viangle, double r, double alpha) {
		this.p = p;
		this.Viangle = Viangle;
		this.r = r;
		this.angle = alpha;
        this.pt = new Point(p.x + 2 * r * Math.sin(angle) * Math.cos(Viangle) / 3 / angle, p.y + 2 * r * Math.sin(angle) * Math.sin(Viangle) / 3 / angle);
    }

}
