package com.darcy.auxiliary;

import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;

/*
 * author: darcy
 * date: 2017/12/19 16:39
 * description: 
*/
public class PriorityQueueTest {
	public static void main(String[] args) {
		PriorityQueue<Integer> minHeap = new PriorityQueue<>(new Comparator<Integer>() {
			@Override
			public int compare(Integer o1, Integer o2) {
				if (Integer.compare(o1, o2) > 0) {
					return 1;
				} else if (Integer.compare(o1, o2) == 0) {
					return 0;
				} else {
					return -1;
				}
			}
		});

		PriorityQueue<Integer> maxHeap = new PriorityQueue<>(new Comparator<Integer>() {
			@Override
			public int compare(Integer o1, Integer o2) {
				if (Integer.compare(o1, o2) > 0) {
					return -1;
				} else if (Integer.compare(o1, o2) == 0) {
					return 0;
				} else {
					return 1;
				}
			}
		});

		minHeap.addAll(Arrays.asList(1, 2, 3, 4, 5, 9, 10, 11, 100, 200, 300, 10000));
		maxHeap.addAll(Arrays.asList(1, 2, 3, 4, 5, 9, 10, 11, 100, 200, 300, 10000));
		System.out.println(minHeap);
		System.out.println(maxHeap);
		while (!minHeap.isEmpty()) {
			System.out.println(minHeap.poll());
		}
	}
}
