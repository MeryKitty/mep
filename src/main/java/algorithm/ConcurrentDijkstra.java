package algorithm;

public class ConcurrentDijkstra {
	/*
	
	public static double stepWidth;
	private static final double EPSILON = 1e-8;
	
	private static final class Worker implements Runnable {
		private static int m;
		private static Graph graph;
		private static ConcurrentSkipListMap<Double, Vertex> queue;
		private static double scale;
		
		private Vertex vertex;
		
		Worker(Vertex vertex) {
			this.vertex = vertex;
		}
		
		@Override
		public void run() {
			for (var cellIter = this.vertex.cellIter(); cellIter.hasNext();) {
				int cellIndex = cellIter.next();
				double cellExposure = graph.cellContainer().exposure(cellIndex);
				for (var vertexIter = graph.cellContainer().vertexIter(cellIndex); vertexIter.hasNext();) {
					var v = vertexIter.next();
					if (!this.vertex.equals(v)) {
						int ARow = this.vertex.row(), AColumn = this.vertex.column();
						int BRow = v.row(), BColumn = v.column();
						boolean edgeHere = false;
						if (ARow != BRow && AColumn != BColumn) {
							edgeHere = true;
						} else if (ARow == BRow) {
							if (AColumn % m == 0 || BColumn % m == 0) {
								edgeHere = true;
							}
						} else if (AColumn == BColumn) {
							if (ARow % m == 0 || BRow % m == 0) {
								edgeHere = true;
							}
						} else {
							throw new AssertionError();
						}
						if (edgeHere) {
							int rowDiff = ARow - BRow;
							int columnDiff = AColumn - BColumn;
							double geoDistance = scale * Math.sqrt(rowDiff * rowDiff + columnDiff * columnDiff);
							double edgeLength = cellExposure * geoDistance;
							double tempTotalExposure = this.vertex.distance + edgeLength;
							synchronized (v) {
								if (v.distance > tempTotalExposure) {
									if (v.indexInQueue == 0) {
										queue.remove((double)v.distance);
									}
									v.distance = (float)tempTotalExposure;
									v.previous = this.vertex;
									queue.put((double)v.distance, v);
									v.indexInQueue = 0;
								}
							}
						}
					}
				}
			}
		}
	}
	
	public static Pair<List<Vertex>, Float> execute(Graph graph) {
		long start = System.currentTimeMillis();
		float result = Float.POSITIVE_INFINITY;
		var queue = new ConcurrentSkipListMap<Double, Vertex>();
		Worker.queue = queue; Worker.graph = graph; Worker.m = Config.M; Worker.scale = Config.DELTA / Config.M;
		graph.source().distance = 0;
		queue.put(0., graph.source());
		int cursor = 1;
		int concurrency = 0;
		while (!queue.isEmpty()) {
			while (queue.firstKey() > cursor * stepWidth - EPSILON) {
				cursor++;
			}
			var currentBucket = queue.headMap(cursor * stepWidth - EPSILON);
			if (currentBucket.size() > concurrency) {
				concurrency = currentBucket.size();
			}
			try (var exec = Executors.newVirtualThreadExecutor()) {
				var bucket = currentBucket.values();
				var tasks = new ArrayList<Worker>(bucket.size());
				for (var v : bucket) {
					queue.remove((double)v.distance);
					v.indexInQueue = -1;
					tasks.add(new Worker(v));
				}
				for (var t : tasks) {
					exec.execute(t);
				}
			}
			if (cursor * stepWidth - EPSILON > graph.sink().distance) {
				result = graph.sink().distance;
				break;
			}
		}
		long end = System.currentTimeMillis();
		System.out.println("Time       : " + (end - start));
		System.out.println("Concurrency: " + concurrency);
		
		if (result == Float.POSITIVE_INFINITY) {
			return new Pair<>(null, result);
		}
		var resultPath = new LinkedList<Vertex>();
		
		Vertex current = graph.sink();
		while (true) {
			resultPath.addFirst(current);
			current = current.previous;
			if (current == null) {
				break;
			}
		}
		return new Pair<>(resultPath, result);
	} */
}
