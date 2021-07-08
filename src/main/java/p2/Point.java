/**
 * @author ljnk975
 */
package p2;

public class Point implements Comparable<Point> {

	public double x;
	public double y;
	
	// For scliping
	public static final int LEFT = 1;
	public static final int RIGHT = 2;
	public static final int TOP = 4;
	public static final int BOTTOM = 8;

	/*
	 * Create a point with x, y coordinates
	 */
	public Point(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public int encode(double width, double height) {
		int encode = 0;
		if(x < 0) encode |= LEFT;
		if(x > width) encode |= RIGHT;
		if(y < 0) encode |= TOP;
		if(y > height) encode |= BOTTOM;
		return encode;
	}

	public double distance(Point p) {
		return Math.sqrt((this.x-p.x)*(this.x-p.x)+(this.y-p.y)*(this.y-p.y));
	}

	@Override
    public int compareTo(Point other) {
        if (this.y == other.y) {
            if (this.x == other.x)
                return 0;
            if (this.x > other.x)
                return 1;
            return -1;
        }
        if (this.y > other.y)
        	return 1;
        return -1;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    @Override
    public boolean equals(Object other) {
    	if(other instanceof Point) {
    		Point op = (Point)other;
    		return this.x == op.x && this.y == op.y;
    	}
		return false;
    }
    
    @Override
    public Object clone() {
    	return new Point(x, y);
    }

}
