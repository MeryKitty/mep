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

    public Sensor(double x, double y, double Viangle, double r, double alpha) {
        this.p = new Point(x, y);
        this.Viangle = Viangle;
		this.r = r;
		this.angle = alpha;
        this.pt = new Point(x + 2 * r * Math.sin(angle) * Math.cos(Viangle) / 3 / angle, y + 2 * r * Math.sin(angle) * Math.sin(Viangle) / 3 / angle);
    }

    public void draw(Graphics g) {
        g.fillArc((int)(this.p.x - this.r), (int)(this.p.y - this.r), (int)(2 * this.r), (int)(2 * this.r), (int)((-this.Viangle - this.angle) * 180 / Math.PI), (int)(this.angle * 360 / Math.PI));
    }

}
