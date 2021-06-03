package model;

import java.util.Arrays;

public class Vertex {
	private final short row, column;
	private float distance = Float.POSITIVE_INFINITY;
	private Vertex previous;
	private int[] cellContains;
	private int branch;
	public int indexInQueue = -1;
	private boolean addedToVector;
	
	@__inline__
	public class CellIterator {
		private int cursor;
		private int length;
		private int[] cellData;
		
		private CellIterator(int cursor, int length, int[] cellData) {
			this.cursor = cursor;
			this.length = length;
			this.cellData = cellData;
		}
		
		private CellIterator() {
			this(0, Vertex.this.cellContains.length, Vertex.this.cellContains);
		}
		
		public boolean valid() {
			return this.cursor < this.length;
		}

		public int get() {
			return this.cellData[this.cursor];
		}
		
		public CellIterator next() {
			return new CellIterator(this.cursor + 1, this.length, this.cellData);
		}
		
	}
	
	private Vertex(short row, short column, float distance, int indexInQueue, Vertex previous, int[] cellContains) {
		this.row = row; this.column = column;
		this.distance = distance;
		this.indexInQueue = indexInQueue;
		this.previous = previous;
		this.cellContains = cellContains;
	}
	
	public static Vertex createSource() {
		return new Vertex((short)-1, (short)-1, 0, 0, null, null);
	}
	
	public static Vertex createSink() {
		return new Vertex((short)-2, (short)-2, Float.POSITIVE_INFINITY, -1, null, null);
	}
	
	public static Vertex createVertex(int row, int column, CellContainer cellContainer, int m) {
		var cellContainList = new ArrayListInt(5);
		int i = row / m; int j = column / m;
		if (row % m == 0 && column % m == 0) {
			addCell(cellContainer, cellContainList, i, j);
			addCell(cellContainer, cellContainList, i, j - 1);
			addCell(cellContainer, cellContainList, i - 1, j);
			addCell(cellContainer, cellContainList, i - 1, j - 1);
		} else if (row % m == 0) {
			addCell(cellContainer, cellContainList, i, j);
			addCell(cellContainer, cellContainList, i - 1, j);
		} else if (column % m == 0) {
			addCell(cellContainer, cellContainList, i, j);
			addCell(cellContainer, cellContainList, i, j - 1);
		}
		var cellContains = cellContainList.toArray();
		var result = new Vertex((short)row, (short)column, Float.POSITIVE_INFINITY, -1, null, cellContains);
		return result;
	}
	
	public int row() {
		return this.row;
	}
	
	public int column() {
		return this.column;
	}

	public float distance() {
		return this.distance;
	}

	public void setDistance(float distance) {
		this.distance = distance;
	}

	public Vertex previous() {
		return this.previous;
	}

	public void setPrevious(Vertex previous) {
		this.previous = previous;
	}

	public boolean dead() {
		return this.branch == 0;
	}

	public void addBranch() {
		this.branch++;
	}

	public void removeBranch() {
		this.branch--;
	}
	
	public boolean addedToVector() {
		return this.addedToVector;
	}

	public void addToVector() {
		assert !this.addedToVector;
		this.addedToVector = true;
	}

	public void resetAddToVector() {
		assert this.addedToVector;
		this.addedToVector = false;
	}

	public CellIterator cellIter() {
		return new CellIterator();
	}
	
	public int cellNumber() {
		return cellContains.length;
	}

	public void addCell(int c) {
		int[] cellContains;
		if (this.cellContains == null) {
			cellContains = new int[] {c};
		} else {
			int length = this.cellContains.length;
			cellContains = Arrays.copyOf(this.cellContains, length + 1);
			cellContains[length] = c;
		}
		this.cellContains = cellContains;
	}
	
	private static boolean addCell(CellContainer cellContainer, ArrayListInt cellContainList, int row, int column) {
		try {
			var cellIndex = cellContainer.getIndex(row, column);
			if (cellContainer.exposure(cellIndex) != -1) {
				cellContainList.add(cellIndex);
				return true;
			} else {
				return false;
			}
		} catch (IndexOutOfBoundsException e) {
			return false;
		}
	}
	
	@Override
	public String toString() {
		return "[" + this.row() + ", " + this.column() + "]";
	}
}
