package model;

import java.util.LinkedList;

import algorithm.Config;

@__inline__
public final class VertexContainer {
	private final int majorRowNumber, majorColumnNumber;
	private final Vertex[] data;
	private final LinkedList<Vertex> leaves;
	
	private final CellContainer cellContainer;
	
	public VertexContainer(CellContainer cellCon, int majorRowNumber, int majorColumnNumber) {
		this.cellContainer = cellCon;
		this.majorRowNumber = majorRowNumber;
		this.majorColumnNumber = majorColumnNumber;
		this.data = new Vertex[staticGetIndex(majorRowNumber * Config.M, majorColumnNumber * Config.M, majorRowNumber, majorColumnNumber, Config.M) + 3];
		this.leaves = new LinkedList<>();
	}
	
	public int majorRowNumber() {
		return this.majorColumnNumber;
	}
	
	public int majorColumnNumber() {
		return this.majorColumnNumber;
	}
	
	public int length() {
		return this.data.length;
	}
	
	public final boolean put(int row, int column, Vertex v) {
		int index = this.getIndex(row, column);
		var pre = this.data[index];
		this.data[index] = v;
		if (pre == null) {
			return true;
		} else {
			return false;
		}
	}
	
	public final boolean put(Pair<Integer, Integer> index, Vertex v) {
		return this.put(index.first(), index.second(), v);
	}
	
	public final Vertex get(int index) {
		return this.data[index];
	}
	
	public void put(int index, Vertex ver) {
		this.data[index] = ver;
	}
	
	public final Vertex get(int row, int column) {
		int index = this.getIndex(row, column);
		return this.data[index];
	}
	
	public final Vertex get(Pair<Integer, Integer> index) {
		return this.get(index.first(), index.second());
	}
	
	public final int getSource() {
		return this.data.length - 2;
	}
	
	public final int getSink() {
		return this.data.length - 1;
	}
	
	public final boolean add(Vertex v) {
		int index = this.getIndex(v.row(), v.column());
		if (this.data[index] == null) {
			this.data[index] = v;
			return true;
		} else {
			return false;
		}
	}
	
	public final void addSource(Vertex v) {
		this.data[this.data.length - 2] = v;
	}
	
	public final void addSink(Vertex v) {
		this.data[this.data.length - 1] = v;
	}
	
	public final boolean contains(Vertex v) {
		try {
			return this.get(v.row(), v.column()) != null;
		} catch (IllegalArgumentException e) {
			return false;
		}
	}
	
	final int getOrCreateIfAbsent(int row, int column) {
		int index = this.getIndex(row, column);
		var temp = this.data[index];
		if (temp == null) {
			temp = Vertex.createVertex(row, column, this.cellContainer, Config.M);
			this.data[index] = temp;
		}
		return index;
	}
	
	
	public final void addLeaf(Vertex v) {
		this.leaves.add(v);
	}
	
	public final void free() {
		System.out.println("FREE");
		for (var iter = this.leaves.iterator(); iter.hasNext();) {
			var v = iter.next();
			if (!v.dead()) {
				iter.remove();
			}
		}
		for (var iter = this.leaves.iterator(); iter.hasNext();) {
			var v = iter.next();
			if (this.dead(v)) {
				iter.remove();
				this.freeVertex(v);
			}
		}
		System.gc();
	}
	
	private final void freeVertex(Vertex v) {
		int index = this.getIndex(v.row(), v.column());
		this.data[index] = null;
		v.previous().removeBranch();
		if (this.dead(v.previous())) {
			this.freeVertex(v.previous());
		}
	}
	
	private final boolean dead(Vertex v) {
		if (v.indexInQueue != - 2 || !v.dead()) {
			return false;
		}
		for (var iter = v.cellIter(); iter.valid(); iter = iter.next()) {
			var cellIndex = iter.get();
			for (var iterV2 = this.cellContainer.vertexIter(cellIndex); iterV2.valid(); iterV2 = iterV2.next()) {
				var v2 = iterV2.get();
				if (v2 != null && v2.indexInQueue != -2) {
					return false;
				}
			}
		}
		return true;
	}
	
	public final int getIndex(int row, int column) {
		assert (row >= 0 && column >= 0
				&& row <= this.majorRowNumber * Config.M
				&& column <= this.majorColumnNumber * Config.M
				&& (row % Config.M == 0 || column % Config.M == 0));
		return staticGetIndex(row, column, this.majorRowNumber, this.majorColumnNumber, Config.M);
	}

	Vertex[] data() {
		return this.data;
	}

	private final static int staticGetIndex(int row, int column, int majorRowNumber, int majorColumnNumber, int m) {
		int majorRow = row / m;
		int leftRow = row - m * majorRow;
		int result = majorRow * (majorColumnNumber * m + 1 + (m - 1) * (majorColumnNumber + 1));
		if (leftRow == 0) {
			result += column;
		} else {
			result += majorColumnNumber * m + 1;
			result += (leftRow - 1) * (majorColumnNumber + 1);
			result += column / m;
		}
		return result;
	}
}
