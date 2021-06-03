package algorithm;

import java.util.LinkedList;
import java.util.List;

import jdk.incubator.vector.VectorSpecies;
import jdk.incubator.vector.IntVector;
import jdk.incubator.vector.VectorOperators;
import jdk.incubator.vector.FloatVector;

import model.CellContainer;
import model.Graph;
import model.Pair;
import model.Vertex;

public class Dijkstra {
	private static final VectorSpecies<Integer> INT_SPECIES = IntVector.SPECIES_PREFERRED;
	private static final VectorSpecies<Float> FLOAT_SPECIES = FloatVector.SPECIES_PREFERRED;
	private static final int LANE_NUMBER = FLOAT_SPECIES.length();
	private static final MinHeap QUEUE = new MinHeap(200000);
	private static final float SCALE = Config.DELTA / Config.M;
	private static final FloatVector ONE_V = FloatVector.broadcast(FLOAT_SPECIES, 1F);
	private static final FloatVector ZERO_V = FloatVector.broadcast(FLOAT_SPECIES, 1e-5F);
	private static final IntVector M_V = IntVector.broadcast(INT_SPECIES, Config.M - 1);

	private static long[] clock = new long[10];

	private static final class MinHeap {
		Vertex[] elementData;
		int size;

		MinHeap(int capacity) {
			this.elementData = new Vertex[capacity];
		}
		
		boolean isEmpty() {
			return this.size == 0;
		}
		
		void clear() {
			this.size = 0;
		}
		
		Vertex get(int index) {
			checkIndex(index);
			return this.elementData[index];
		}
		
		int size() {
			return this.size;
		}
		
		void set(int index, Vertex e) {
			checkIndex(index);
			this.elementData[index] = e;
		}
		
		Vertex pop() {
			this.size--;
			return this.elementData[this.size];
		}
		
		void add(Vertex e) {
			if (this.size == this.elementData.length) {
				this.grow();
			}
			this.elementData[this.size] = e;
			this.size++;
		}
		
		void grow() {
			var newData = new Vertex[this.elementData.length * 2];
			System.arraycopy(this.elementData, 0, newData, 0, this.size);
			this.elementData = newData;
			System.out.println("New capacity: " + this.elementData.length);
		}
		
		Vertex pollFirst() {
			if (this.isEmpty()) {
				return null;
			}
			var result = this.get(0);
			result.indexInQueue = -2;
			var last = this.pop();
			if (this.isEmpty()) {
				return result;
			}
			last.indexInQueue = 0;
			this.set(0, last);
			int currentIndex = 0;
			while (true) {
				int left = currentIndex * 2 + 1;
				int right = currentIndex * 2 + 2;
				if (this.size() > right) {
					if (this.get(left).distance() > this.get(right).distance()) {
						if (this.swap(currentIndex, right)) {
							currentIndex = right;
						} else {
							break;
						}
					} else {
						if (this.swap(currentIndex, left)) {
							currentIndex = left;
						} else {
							break;
						}
					}
				} else if (this.size() == right) {
					this.swap(currentIndex, left);
					break;
				} else {
					break;
				}
			}
			return result;
		}
		
		void addVertex(Vertex v) {
			assert (v.indexInQueue == -1);
			v.indexInQueue = this.size();
			this.add(v);
			this.decreaseKey(v, v.distance());
		}
		
		void decreaseKey(Vertex v, float newKey) {
			assert (v.distance() > newKey && v.indexInQueue >= 0 && this.get(v.indexInQueue) == v);
			v.setDistance(newKey);
			int currentIndex = v.indexInQueue;
			while (currentIndex > 1) {
				int parentIndex = (currentIndex - 1) / 2;
				if (this.swap(parentIndex, currentIndex)) {
					currentIndex = parentIndex;
				} else {
					break;
				}
			}
		}
		
		/**
		 * i1 is the parent of i2
		 * 
		 * @param i1
		 * @param i2
		 * @return
		 */
		boolean swap(int i1, int i2) {
			assert (i1 >= 0 && i2 > 0 && (i2 - 1) / 2 == i1);
			var v1 = this.get(i1);
			var v2 = this.get(i2);
			if (v1.distance() > v2.distance()) {
				this.set(i1, v2);
				this.set(i2, v1);
				v1.indexInQueue = i2;
				v2.indexInQueue = i1;
				return true;
			} else {
				return false;
			}
		}
		
		void checkIndex(int index) {
			assert (index < size && index >= 0);
		}
	}
	
	public static Pair<List<Vertex>, Float> execute(Graph graph) {
		QUEUE.clear();
		var cellCon = graph.cellContainer();
		var source = graph.source();
		var sink = graph.sink();
		QUEUE.addVertex(graph.source());
		float result = Float.POSITIVE_INFINITY;
	
		int maxSize = FLOAT_SPECIES.loopBound(Config.M * 12 - 4) + LANE_NUMBER;
		Vertex[] Bs = new Vertex[maxSize];
		float[] tempDistances = new float[maxSize];
		int[] BRows = new int[maxSize];
		int[] BColumns = new int[maxSize];
		
		while (true) {
			var current = QUEUE.pollFirst();
			if (current == null) {
				break;
			} else if (current == sink) {
				result = current.distance();
				break;
			} else if (current != source){
				current.previous().addBranch();
			}
			graph.vertexSet().addLeaf(current);
			
			if (current != source) {
				traverse(cellCon, current, Bs, BRows, BColumns, tempDistances);
			} else {
				sourceTraverse(graph);
			}
		}
		if (result == Float.POSITIVE_INFINITY) {
			return new Pair<>(null, result);
		}
		var resultPath = new LinkedList<Vertex>();
		
		Vertex current = graph.sink();
		while (true) {
			resultPath.addFirst(current);
			current = current.previous();
			if (current == null) {
				break;
			}
		}
		return new Pair<>(resultPath, result);
	}

	public static void restartClock() {
		for (int i = 0; i < clock.length; i++) {
			clock[i] = 0;
		}
	}

	public static long clock(int i) {
		return clock[i];
	}
	
	private static void sourceTraverse(Graph graph) {
		var current = graph.source();
		var cellCon = graph.cellContainer();
		for (var cellIter = current.cellIter(); cellIter.valid(); cellIter = cellIter.next()) {
			for (var vertexIter = cellCon.vertexIter(cellIter.get()); vertexIter.valid(); vertexIter = vertexIter.next()) {
				var vertex = vertexIter.get();
				vertex.setDistance(0);
				vertex.setPrevious(current);
				QUEUE.addVertex(vertex);
			}
		}
	}
	
	private static void traverse(CellContainer cellCon, Vertex current, Vertex[] Bs, int[] BRows, int[] BColumns, float[] tempDistances) {
		int size = 0;
		float averExpo = 0;
		long start = System.currentTimeMillis();
		for (var cellIter = current.cellIter(); cellIter.valid(); cellIter = cellIter.next()) {
			int cellIndex = cellIter.get();
			averExpo += cellCon.exposure(cellIndex);
			for (var vertexIter = cellCon.vertexIter(cellIndex); vertexIter.valid(); vertexIter = vertexIter.next()) {
				var v = vertexIter.get();
				if (v.indexInQueue > -2 && !v.addedToVector()) {
					v.addToVector();
					Bs[size] = v;
					BRows[size] = v.row();
					BColumns[size++] = v.column();
				}
			}
		}
		averExpo /= current.cellNumber();
		averExpo *= SCALE;
		long mid1 = System.currentTimeMillis();
		
		bulkOperations(current, averExpo, BRows, BColumns, tempDistances, size);

		long mid2 = System.currentTimeMillis();
		for (int i = 0; i < size; i++) {
			Vertex v = Bs[i];
			v.resetAddToVector();
			float tempTotalExposure = tempDistances[i];
			if (v.distance() > tempTotalExposure) {
				v.setPrevious(current);
				if (v.distance() == Float.POSITIVE_INFINITY) {
					v.setDistance(tempTotalExposure);
					QUEUE.addVertex(v);
				} else {
					QUEUE.decreaseKey(v, tempTotalExposure);
				}
			}
		}
		long end = System.currentTimeMillis();
		clock[0] += (mid1 - start);
		clock[1] += (mid2 - mid1);
		clock[2] += (end - mid2);
	}
	
	private static void bulkOperations(Vertex current, float averExpo, int[] BRows, int[] BColumns, float[] tempDistances, int size) {
		int ARow = current.row(), AColumn = current.column();
		var currentDistanceV = FloatVector.broadcast(FLOAT_SPECIES, current.distance());
		var ARowV = IntVector.broadcast(INT_SPECIES, ARow);
		var AColumnV = IntVector.broadcast(INT_SPECIES, AColumn);
		var averExpoV = FloatVector.broadcast(FLOAT_SPECIES, averExpo);

		int loopBound = FLOAT_SPECIES.loopBound(size - 1) + LANE_NUMBER;
		if (ARow % Config.M != 0) {
			for (int i = 0; i < loopBound; i += LANE_NUMBER) {
				var BRowV = IntVector.fromArray(INT_SPECIES, BRows, i);
				var BColumnV = IntVector.fromArray(INT_SPECIES, BColumns, i);
				var rowDiffV = ARowV.sub(BRowV);
				var columnDiffV = AColumnV.sub(BColumnV);
				var distanceV = currentDistanceV.add(
					averExpoV.mul(
						rowDiffV.mul(rowDiffV).add(columnDiffV.mul(columnDiffV)).castShape(FLOAT_SPECIES, 0).lanewise(VectorOperators.SQRT)
					).div(
						ZERO_V.blend(
							ONE_V,
							AColumnV.eq(BColumnV).not().or(
								BRowV.and(M_V).eq(0)
							).cast(FLOAT_SPECIES)
						)
					)
				);
				distanceV.intoArray(tempDistances, i);
			}
		} else if (AColumn % Config.M != 0) {
			for (int i = 0; i < loopBound; i += LANE_NUMBER) {
				var BRowV = IntVector.fromArray(INT_SPECIES, BRows, i);
				var BColumnV = IntVector.fromArray(INT_SPECIES, BColumns, i);
				var rowDiffV = ARowV.sub(BRowV);
				var columnDiffV = AColumnV.sub(BColumnV);
				var distanceV = currentDistanceV.add(
					averExpoV.mul(
						rowDiffV.mul(rowDiffV).add(columnDiffV.mul(columnDiffV)).castShape(FLOAT_SPECIES, 0).lanewise(VectorOperators.SQRT)
					).div(
						ZERO_V.blend(
							ONE_V,
							ARowV.eq(BRowV).not().or(
								BColumnV.and(M_V).eq(0)
							).cast(FLOAT_SPECIES)
						)
					)
				);
				distanceV.intoArray(tempDistances, i);
			}
		} else {
			for (int i = 0; i < loopBound; i += LANE_NUMBER) {
				var BRowV = IntVector.fromArray(INT_SPECIES, BRows, i);
				var BColumnV = IntVector.fromArray(INT_SPECIES, BColumns, i);
				var rowDiffV = ARowV.sub(BRowV);
				var columnDiffV = AColumnV.sub(BColumnV);
				var distanceV = currentDistanceV.add(
					averExpoV.mul(
						rowDiffV.mul(rowDiffV).add(columnDiffV.mul(columnDiffV)).castShape(FLOAT_SPECIES, 0).lanewise(VectorOperators.SQRT)
					)
				);
				distanceV.intoArray(tempDistances, i);
			}
		}
	}
}
