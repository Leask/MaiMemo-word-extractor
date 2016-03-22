package com.maimemo;
/**
 * 相当于 List<Integer[2]>, 避免了 auto boxing
 * @author TJT
 *
 */
public class FastIntPairArray {

	private int[] array;
	private int size = 0;
	
	public FastIntPairArray(int initSize) {
		array = new int[initSize * 2];
	}
	
	public void add(int start, int end) {
		if ((size + 1) * 2 > array.length) {
			int[] tempArray = new int[size * 4];
			System.arraycopy(array, 0, tempArray, 0, size * 2);
			array = tempArray;
		}
		array[size * 2] = start;
		array[size * 2 + 1] = end;
		size++;
	}
	
	public int getStart(int position) {
		return array[position * 2];
	}
	
	public int getEnd(int position) {
		return array[position * 2 + 1];
	}

	public void clear() {
		size = 0;
	}

	public int size() {
		return size;
	}
}
