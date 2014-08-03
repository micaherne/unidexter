package uk.co.micaherne.unidexter;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TempTest {

/*	@Test
	public void test() {
		long[] arr = new long[10000];
		List<Long> list = new ArrayList<Long>(10000);
		for (int i = 0; i < arr.length; i++) {
			list.add(arr[i]);
		}
		long start = System.nanoTime();
		for (int i = 0; i < arr.length; i++) {
			long dummy1 = arr[i];
		}
		long tt1 = System.nanoTime() - start;
		System.out.println("Time taken: " + (tt1) + " nanoseconds");
		long start2 = System.nanoTime();
		for (long l : list) {
			long dummy2 = l;
		}
		long tt2 = System.nanoTime() - start2;
		System.out.println("Time taken: " + (tt2) + " nanoseconds");
		long start3 = System.nanoTime();
		for (int i = 0; i < arr.length; i++) {
			long dummy3 = arr[i];
		}
		long tt3 = System.nanoTime() - start3;
		System.out.println("Time taken: " + (tt3) + " nanoseconds");
		System.out.println("Difference: " + (tt2 / tt1) * 100 + "%");
	}*/
	
	@Test
	public void test2() {
		int[] test = new int[256];
		test[0] = 1;
		addToArray(test);
		assertEquals(100, test[1]);
	}
	
	public void addToArray(int[] test) {
		test[1] = 100;
	}

}
