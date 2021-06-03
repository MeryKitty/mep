package p1;

import java.util.ArrayList;
import java.util.List;

import model.IField;
import model.Pair;

public class Field implements IField {
	List<Pair<Double, Double>> sensorList;
	
	public Field(int sensorNumber) {
		this.sensorList = new ArrayList<>();
		for (int i = 0; i < sensorNumber; i++) {
			double x = Math.random() * this.W();
			double y = Math.random() * this.H();
			this.sensorList.add(new Pair<>(x, y));
		}
	}
	
	public Field(double[][] sensorData) {
		this.sensorList = new ArrayList<>();
		for (double[] s : sensorData) {
			this.sensorList.add(new Pair<>(s[0], s[1]));
		}
	}

	@Override
	public double W() {
		return 100;
	}

	@Override
	public double H() {
		return 100;
	}

	@Override
	public boolean inField(double x, double y) {
		return x > 0 && x < this.W() && y > 0 && y < this.H();
	}

	@Override
	public double exposure(double x, double y) {
		int minIndex = -1;
		double minValue = Double.POSITIVE_INFINITY;
		for (int i = 0; i < this.sensorList.size(); i++) {
			var s = sensorList.get(i);
			double temp = this.distance(s.first(), s.second(), x, y);
			if (temp < minValue) {
				minIndex = i;
				minValue = temp;
			}
		}
		var minSensor = sensorList.get(minIndex);
		double distance = this.distance(minSensor.first(), minSensor.second(), x, y);
		return 100 / (distance * distance);
	}

	@Override
	public boolean isSource(double x, double y, double error) {
		return Math.abs(x) < error && Math.abs(y - 40) < error;
	}

	@Override
	public boolean isSink(double x, double y, double error) {
		return Math.abs(x - this.W()) < error && Math.abs(y - 65) < error;
	}
	
	private double distance(double x1, double y1, double x2, double y2) {
		return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
	}

}
