package p2;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import model.IField;

public class Field implements IField{
	private int W, H;
	private List<Sensor> sensorList;
	
	public Field(Path filename) throws Exception {
        this.sensorList = new ArrayList<>();
        BufferedReader input = new BufferedReader(new FileReader(filename.toString()));
		String s = input.readLine();
		String[] s1 = s.split(" ");
		this.W = Integer.parseInt(s1[0]);
		this.H = Integer.parseInt(s1[1]);
		String temp;
        while ((temp = input.readLine()) != null) {
			String[] t = temp.split(" ");
			Point p = new Point(Double.parseDouble(t[0]), Double.parseDouble(t[1]));
			double vangle = Double.parseDouble(t[2]);
			double r = Double.parseDouble(t[3]);
			double angle = Double.parseDouble(t[4]);
			this.sensorList.add(new Sensor(p, vangle, r, angle));
		}
        input.close();
    }

	@Override
	public double W() {
		return this.W;
	}

	@Override
	public double H() {
		return this.H;
	}

	@Override
	public boolean inField(double x, double y) {
		return x > 0 && x < this.W && y > 0 && y < this.H;
	}

	@Override
	public double exposure(double x, double y) {
		double value = 0;
        for (int i = 0; i < this.sensorList.size(); i++)
        	value += Sensor_Point(this.sensorList.get(i), x, y);
        return Math.max(value, 1e-4);
	}

	@Override
	public boolean isSource(double x, double y, double error) {
		if (Math.abs(x - 0) < error && y > 100 && y < this.H - 100) { // && Math.abs(y - 150) < error) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean isSink(double x, double y, double error) {
		if (Math.abs(x - this.W) < error && y > 100 && y < this.H - 100) { // && Math.abs(y - 350) < error) {
			return true;
		} else {
			return false;
		}
	}
	
	private static double Sensor_Point(Sensor s, double x, double y) {
		double dx = x - s.p.x;
		double dy = y - s.p.y;
		double d = Math.sqrt(dx * dx + dy * dy);
		double cos2 = (dx * Math.cos(s.Viangle) + dy * Math.sin(s.Viangle)) / d;
		double cos = Math.sqrt((cos2 + 1) / 2);
		return Math.min(cos / d, 0.5);
//        double dx = x - s.p.x;
//        double dy = y - s.p.y;
//        double tvh = dx * Math.cos(s.Viangle) + dy * Math.sin(s.Viangle);
//        double d = Math.sqrt(dx * dx + dy * dy);
//        return ((d > s.r) || (tvh < d * Math.cos(s.angle))) ? 0 : 1;
    }
}
