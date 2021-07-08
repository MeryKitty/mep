package algorithm;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import jdk.incubator.vector.VectorSpecies;
import jdk.incubator.vector.VectorOperators;
import jdk.incubator.vector.FloatVector;

import model.CellContainer;
import model.Graph;
import model.Pair;
import model.Vertex;

public class Dijkstra {
	private static final VectorSpecies<Float> FLOAT_SPECIES = FloatVector.SPECIES_PREFERRED;
	private static final int LANE_NUMBER = FLOAT_SPECIES.length();
	private static final float SCALE = Config.DELTA / Config.M;

	private static final class MinHeap {
		Vertex[] elementData;
		int size;

		MinHeap(int capacity) {
			this.elementData = new Vertex[capacity];
		}
		
		boolean isEmpty() {
			return this.size == 0;
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
		var queue = new MinHeap(400_000);
		var cellCon = graph.cellContainer();
		var source = graph.source();
		var sink = graph.sink();
		queue.addVertex(graph.source());
		float result = Float.POSITIVE_INFINITY;
	
		int maxSize = FLOAT_SPECIES.loopBound(Config.M * 12 - 4) + LANE_NUMBER;
		Vertex[] Bs = new Vertex[maxSize];
		float[] tempDistances = new float[maxSize];
		float[] BRows = new float[maxSize];
		float[] BColumns = new float[maxSize];
		
		while (true) {
			var current = queue.pollFirst();
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
				traverse(queue, cellCon, current, Bs, BRows, BColumns, tempDistances);
			} else {
				sourceTraverse(graph, queue);
			}
		}
		if (result == Float.POSITIVE_INFINITY) {
			return new Pair<>(null, result);
		}
		var resultPath = new LinkedList<Vertex>();
		
		Vertex current = graph.sink();
		while (current != null) {
			resultPath.addFirst(current);
			current = current.previous();
		}
		return new Pair<>(resultPath, result);
	}

	private static void sourceTraverse(Graph graph, MinHeap queue) {
		var current = graph.source();
		var cellCon = graph.cellContainer();
		for (var cellIter = current.cellIter(); cellIter.valid(); cellIter = cellIter.next()) {
			for (var vertexIter = cellCon.vertexIter(cellIter.get()); vertexIter.valid(); vertexIter = vertexIter.next()) {
				var vertex = vertexIter.get();
				vertex.setDistance(0);
				vertex.setPrevious(current);
				queue.addVertex(vertex);
			}
		}
	}
	
	private static void traverse(MinHeap queue, CellContainer cellCon, Vertex current, Vertex[] Bs, float[] BRows, float[] BColumns, float[] tempDistances) {
		int size = 0;
		float averExpo = 0;
		for (var cellIter = current.cellIter(); cellIter.valid(); cellIter = cellIter.next()) {
			int cellIndex = cellIter.get();
			averExpo += cellCon.exposure(cellIndex);
			for (var vertexIter = cellCon.vertexIter(cellIndex); vertexIter.valid(); vertexIter = vertexIter.next()) {
				var v = vertexIter.get();
				if (v.indexInQueue > -2 && !v.addedToVector()) {
					if (v.row() < 0) {
						v.setDistance(current.distance());
						v.setPrevious(current);
						queue.addVertex(v);
					} else {
						v.addToVector();
						Bs[size] = v;
						BRows[size] = v.row();
						BColumns[size] = v.column();
						size++;
					}
				}
			}
		}
		averExpo /= current.cellNumber();
		
		bulkOperations(current, averExpo, BRows, BColumns, tempDistances, size);

		for (int i = 0; i < size; i++) {
			Vertex v = Bs[i];
			v.resetAddToVector();
			float tempTotalExposure = tempDistances[i];
//			System.out.println(tempTotalExposure);
			if (v.distance() > tempTotalExposure) {
				v.setPrevious(current);
				if (v.distance() == Float.POSITIVE_INFINITY) {
					v.setDistance(tempTotalExposure);
					queue.addVertex(v);
				} else {
					queue.decreaseKey(v, tempTotalExposure);
				}
			}
		}
	}
	
	private static void bulkOperations(Vertex current, float averExpo, float[] BRows, float[] BColumns, float[] tempDistances, int size) {
		int ARow = current.row(), AColumn = current.column();
		var currentDistanceV = FloatVector.broadcast(FLOAT_SPECIES, current.distance());
		var ARowV = FloatVector.broadcast(FLOAT_SPECIES, ARow);
		var AColumnV = FloatVector.broadcast(FLOAT_SPECIES, AColumn);
		var averExpoScaledV = FloatVector.broadcast(FLOAT_SPECIES, averExpo * SCALE);

		int loopBound = FLOAT_SPECIES.loopBound(size - 1) + LANE_NUMBER;
		for (int i = 0; i < loopBound; i += LANE_NUMBER) {
			var BRowV = FloatVector.fromArray(FLOAT_SPECIES, BRows, i);
			var BColumnV = FloatVector.fromArray(FLOAT_SPECIES, BColumns, i);
			var rowDiffV = ARowV.sub(BRowV);
			var columnDiffV = AColumnV.sub(BColumnV);
			var distanceV = currentDistanceV.add(
					averExpoScaledV.mul(
							rowDiffV.mul(rowDiffV).add(columnDiffV.mul(columnDiffV)).lanewise(VectorOperators.SQRT)
					)
			);
			distanceV.intoArray(tempDistances, i);
		}
	}
}
