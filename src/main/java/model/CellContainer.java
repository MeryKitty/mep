package model;

import java.util.Arrays;

import algorithm.Config;

public final class CellContainer {
	private final int rowNumber, columnNumber;
	private float[] exposure;
	private int[][] vertexSet;
	private int[] vertexSetSize;
	private VertexContainer vertexContainer;
	
	@__primitive__
	public class VertexIterator {
		private int vertexSetSize;
		private int[] vertexIndices;
		private int cursor, index;
		private Vertex[] vertexData;
		
		private VertexIterator(int index, int cursor, int vertexSetSize, int[] vertexIndices, Vertex[] vertexData) {
			this.index = index;
			this.cursor = cursor;
			this.vertexSetSize = vertexSetSize;
			this.vertexIndices = vertexIndices;
			this.vertexData = vertexData;
		}
		
		private VertexIterator(int index) {
			this(index, 0, CellContainer.this.vertexSetSize[index], CellContainer.this.vertexSet[index], CellContainer.this.vertexContainer.data());
		}
		
		public boolean valid() {
			return this.cursor < this.vertexSetSize;
		}

		public Vertex get() {
			return this.vertexData[vertexIndices[this.cursor]];
		}
		
		public VertexIterator next() {
			return new VertexIterator(this.index, this.cursor + 1, this.vertexSetSize, this.vertexIndices, this.vertexData);
		}
		
	}
	
	CellContainer (float[][] exposureList) {
		this.rowNumber = exposureList.length;
		this.columnNumber = exposureList[0].length;
		int size = rowNumber * columnNumber;
		this.vertexSet = new int[size][];
		this.vertexSetSize = new int[size];
		this.exposure = new float[size];
		for (int i = 0; i < exposureList.length; i++) {
			for (int j = 0; j < exposureList[0].length; j++) {
				this.exposure[this.getIndex(i, j)] = exposureList[i][j];
			}
		}
	}
	
	public int rowNumber() {
		return this.rowNumber;
	}
	
	public int columnNumber() {
		return this.columnNumber;
	}
	
	public void initiate(ArrayListInt sourceCellData, int sinkNumber) {
		int size = this.columnNumber * this.rowNumber;
		int totalSize = size + 1 + sinkNumber;
		this.vertexSet = Arrays.copyOf(this.vertexSet, totalSize);
		this.vertexSetSize = Arrays.copyOf(this.vertexSetSize, totalSize);
		this.exposure = Arrays.copyOf(this.exposure, totalSize);
		
		this.vertexSet[size] = sourceCellData.toArray();
		this.vertexSetSize[size] = this.vertexSet[size].length;
		
		int[][] sinkCellData = new int[sinkNumber][];
		Arrays.fill(sinkCellData, new int[] {this.vertexContainer.getSink()});
		System.arraycopy(sinkCellData, 0, this.vertexSet, size + 1, sinkNumber);
		Arrays.fill(this.vertexSetSize, size + 1, totalSize, 1);
	}

	public float exposure(int index) {
		return this.exposure[index];
	}
	
	public float exposure(int row, int column) {
		return this.exposure[this.getIndex(row, column)];
	}
	
	public VertexIterator vertexIter(int index) {
		if (this.vertexSet[index] == null) {
			this.initiateCell(index);
		}
		return new VertexIterator(index);
	}
	
	public VertexIterator vertexIter(int row, int column) {
		return this.vertexIter(this.getIndex(row, column));
	}
	
	private void unsafeAdd(int cellIndex, int v) {
		this.vertexSet[cellIndex][this.vertexSetSize[cellIndex]] = v;
		this.vertexSetSize[cellIndex]++;
	}
	
	public void addVertex(int row, int column, Vertex v) {
		int cellIndex = this.getIndex(row, column);
		if (this.vertexSet[cellIndex] == null) {
			this.vertexSet[cellIndex] = new int[Config.M * 4];
		}
		int vertexIndex = this.vertexContainer.getIndex(v.row(), v.column());
		if (this.vertexSetSize[cellIndex] == this.vertexSet[cellIndex].length) {
			this.vertexSet[cellIndex] = Arrays.copyOf(this.vertexSet[cellIndex], this.vertexSetSize[cellIndex] * 2);
		}
		this.unsafeAdd(cellIndex, vertexIndex);
	}

	public void shrinkCell(int cellIndex) {
		this.vertexSet[cellIndex] = Arrays.copyOf(this.vertexSet[cellIndex], this.vertexSetSize[cellIndex]);
	}
	
	public boolean initiated(int row, int column) {
		return this.vertexSet[this.getIndex(row, column)] != null;
	}
	
	private final void initiateCell(int cellIndex) {
		if (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory() > 4e9) {
			this.vertexContainer.free();
		}
		int m = Config.M;
		int row = this.getRow(cellIndex);
		int column = this.getColumn(cellIndex);
		this.vertexSet[cellIndex] = new int[m * 4];
		for (int index = 0; index < m; index++) {
			int top = this.vertexContainer.getOrCreateIfAbsent(row * m, column * m + index);
			int right = this.vertexContainer.getOrCreateIfAbsent(row * m + index, (column + 1) * m);
			int bottom = this.vertexContainer.getOrCreateIfAbsent((row + 1) * m, (column + 1) * m - index);
			int left = this.vertexContainer.getOrCreateIfAbsent((row + 1) * m - index, column * m);
			this.unsafeAdd(cellIndex, top);
			this.unsafeAdd(cellIndex, right);
			this.unsafeAdd(cellIndex, bottom);
			this.unsafeAdd(cellIndex, left);
		}
	}
	
	final int getIndex(int row, int column) {
		if (row >= 0 && row < this.rowNumber && column >= 0 && column < this.columnNumber) {
			return row * this.columnNumber + column;
		} else {
			throw new IndexOutOfBoundsException();
		}
	}
	
	final int getRow(int index) {
		if (index >= 0 && index < this.rowNumber * this.columnNumber) {
			return index / this.columnNumber;
		} else {
			throw new IndexOutOfBoundsException();
		}
	}
	
	final int getColumn(int index) {
		if (index >= 0 && index < this.rowNumber * this.columnNumber) {
			return index % this.columnNumber;
		} else {
			throw new IndexOutOfBoundsException();
		}
	}
	
	public int getSourceCell() {
		return this.rowNumber * this.columnNumber;
	}

	public void setVertexContainer(VertexContainer vertexCon) {
		this.vertexContainer = vertexCon;
	}
}
