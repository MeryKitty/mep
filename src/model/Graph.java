package model;

import algorithm.Config;

public final class Graph {
	private int sourceAdjacent, sinkAdjacent;

	private final VertexContainer vertexSet;
	private CellContainer cellContainer;
	
	@SuppressWarnings("unused")
	private static class CellRowConstruct implements Runnable {
		int row;
		float delta;
		float[][] exposureList;
		IField field;
		
		public CellRowConstruct(int row, float delta, float[][] exposureList, IField field) {
			this.row = row;
			this.delta = delta;
			this.exposureList = exposureList;
			this.field = field;
		}
		
		public void run() {
			double y = (this.row + 0.5) * delta;
			float[] tempRow = exposureList[row];
			for (int j = 0;; j++) {
				double x = (j + 0.5) * delta;
				if (x > field.W()) {
					break;
				}
				if (field.inField(x, y)) {
					var temp = field.exposure(x, y);
					tempRow[j] = (float)temp;
				} else {
					tempRow[j] = -1;
				}
			}
		}
	}
	
	private Graph(VertexContainer verCon, CellContainer cellCon) {
		this.vertexSet = verCon;
		this.cellContainer = cellCon;
	}
	
	public Vertex source() {
		return vertexSet.get(vertexSet.getSource());
	}
	
	public Vertex sink() {
		return vertexSet.get(vertexSet.getSink());
	}
	
	public VertexContainer vertexSet() {
		return this.vertexSet;
	}
	
	public CellContainer cellContainer() {
		return this.cellContainer;
	}
	
	public int vertexNumber() {
		return this.vertexSet.length() + 2;
	}
	
	public int sourceAdjacent() {
		return this.sourceAdjacent;
	}
	
	public int sinkAdjacent() {
		return this.sinkAdjacent;
	}

	public static Graph construct(IField field) throws InterruptedException {
		float[][] exposureList = exposureList(field, Config.DELTA, Config.M);
		int rowNumber = exposureList.length;
		int columnNumber = exposureList[0].length;
		var cellContainer = new CellContainer(exposureList);
		var vertexContainer = new VertexContainer(cellContainer, rowNumber, columnNumber);
		cellContainer.setVertexContainer(vertexContainer);
		var result = new Graph(vertexContainer, cellContainer);
		vertexContainer.addSource(Vertex.createSource()); vertexContainer.addSink(Vertex.createSink());
		var sourceCellData = new ArrayListInt();
		
		int sinkNumber = 0;
		for (int i = 0; i < rowNumber; i++) {
			for (int j = 0; j < columnNumber; j++) {
				var tempCell = exposureList[i][j];
				if (tempCell != -1) {
					if (field.isSource((j + 0.5) * Config.DELTA, (i + 0.5) * Config.DELTA, Config.DELTA * 3 / 1.999)) {
						for (var iter = cellContainer.vertexIter(i, j); iter.valid(); iter = iter.next()) {
							var ver = iter.get();
							if (field.isSource(ver.column() * Config.DELTA / Config.M, ver.row() * Config.DELTA / Config.M, Config.DELTA / 1.99)) {
								sourceCellData.add(vertexContainer.getIndex(ver.row(), ver.column()));
							}
						}
					}
					if (field.isSink((j + 0.5) * Config.DELTA, (i + 0.5) * Config.DELTA, Config.DELTA * 3 / 1.999)) {
						for (var iter = cellContainer.vertexIter(i, j); iter.valid(); iter = iter.next()) {
							var ver = iter.get();
							if (field.isSink(ver.column() * Config.DELTA / Config.M, ver.row() * Config.DELTA / Config.M, Config.DELTA / 1.99)) {
								ver.addCell(rowNumber * columnNumber + sinkNumber + 1);
								sinkNumber++;
							}
						}
					}
				}
			}
		}
		result.sourceAdjacent = sourceCellData.size();
		result.sinkAdjacent = sinkNumber;
		cellContainer.initiate(sourceCellData, sinkNumber);
		result.source().addCell(cellContainer.getSourceCell());
		cellContainer.shrinkCell(cellContainer.getSourceCell());
		return result;
	}
	
	public static float[][] exposureList(IField field, double delta, int m) {
		int rows = 0, columns = 0;
		for (int i = 0;; i++) {
			if ((i + 0.5) * delta > field.H()) {
				rows = i;
				break;
			}
		}
		for (int j = 0;; j++) {
			if ((j + 0.5) * delta > field.W()) {
				columns = j;
				break;
			}
		}
		var exposure = new float[rows][columns];
		for (int i = 0; i < rows; i++) {
			double y = (i + 0.5) * delta;
			if (y > field.H()) {
				break;
			}
			var tempRow = exposure[i];
			for (int j = 0;; j++) {
				double x = (j + 0.5) * delta;
				if (x > field.W()) {
					break;
				}
				if (field.inField(x, y)) {
					var temp = field.exposure(x, y);
					tempRow[j] = (float)temp;
				} else {
					tempRow[j] = -1;
				}
			}
		}
		
		return exposure;
	}
	
	/*
	public static float[][] concurrentExposureList(IField field, float delta, int m) throws InterruptedException {
		int rows = 0, columns = 0;
		for (int i = 0;; i++) {
			if ((i + 0.5) * delta > field.H()) {
				rows = i;
				break;
			}
		}
		for (int j = 0;; j++) {
			if ((j + 0.5) * delta > field.W()) {
				columns = j;
				break;
			}
		}
		float[][] exposure = new float[rows][columns];
		
		try (var exec = Executors.newVirtualThreadExecutor()) {
			for (int i = 0; i < rows; i++) {
				exec.execute(new CellRowConstruct(i, delta, exposure, field));
			}
		}
		
		return exposure;
	}
	*/
}
