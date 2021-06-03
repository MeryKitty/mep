package model;

public interface IField {
	public double W();
	
	public double H();
	
	public boolean inField(double x, double y);
	
	public double exposure(double x, double y);
	
	public boolean isSource(double x, double y, double error);
	
	public boolean isSink(double x, double y, double error);
	
}
