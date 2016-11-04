/*
 * Copyright (C) 2016 MaiMemo Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.maimemo.text;
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
		if (position >= size) {
			throw new ArrayIndexOutOfBoundsException(size);
		}
		return array[position * 2];
	}

	public void setStart(int position, int start) {
		if (position >= size) {
			throw new ArrayIndexOutOfBoundsException(size);
		}
		array[position * 2] = start;
	}

	public int getEnd(int position) {
		if (position >= size) {
			throw new ArrayIndexOutOfBoundsException(size);
		}
		return array[position * 2 + 1];
	}

	public void setEnd(int position, int end) {
		if (position >= size) {
			throw new ArrayIndexOutOfBoundsException(size);
		}
		array[position * 2 + 1] = end;
	}

	public void clear() {
		size = 0;
	}

	public void remove(int index) {
		for (int i = index; i < size - 1; i++) {
			array[i * 2] = getStart(i + 1);
			array[i * 2 + 1] = getEnd(i + 1);
		}
		pop();
	}

	public void pop() {
		if (size == 0) {
			throw new IllegalStateException("list was empty!");
		}
		size -= 1;
	}

	public int size() {
		return size;
	}
}
