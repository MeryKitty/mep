package test;

import java.util.Arrays;
import java.util.stream.IntStream;


public class Test {

	public static void main(String[] args) {
		float[] a1 = {1, 2, 3, 4};
		double[] a2;
		a2 = IntStream.range(0, a1.length).mapToDouble(i -> a1[i]).toArray();
		System.out.println(Arrays.toString(a2));
	}
}
