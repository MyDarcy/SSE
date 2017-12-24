package com.darcy.auxiliary;

import org.ujmp.core.doublematrix.calculation.entrywise.creators.Rand;

import java.util.*;

/*
 * author: darcy
 * date: 2017/12/19 16:39
 * description: 
*/
public class PriorityQueueTest {
	public static void main(String[] args) {
		Comparator<Integer> minComparator = new Comparator<Integer>() {
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
		};

		Comparator<Integer> maxComparator = new Comparator<Integer>() {
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
		};
		PriorityQueue<Integer> minHeap = new PriorityQueue<>(minComparator);
		PriorityQueue<Integer> maxHeap = new PriorityQueue<>(maxComparator);

		minHeap.addAll(Arrays.asList(1, 2, 3, 2, 3, 7, 11, 10, 4, 5, 9, 10, 11, 100, 200, 300, 10000));
		maxHeap.addAll(Arrays.asList(1, 2, 3, 2, 3, 7, 11, 10, 4, 5, 9, 10, 11, 100, 200, 300, 10000));
		System.out.println(minHeap);
		System.out.println(maxHeap);
		while (!minHeap.isEmpty()) {
			System.out.print(minHeap.poll() + "\t");
		}
		System.out.println();
		while (!maxHeap.isEmpty()) {
			System.out.print(maxHeap.poll() + "\t");
		}

		System.out.println();

		Map<Integer, Integer> treeMap = new TreeMap<>(maxComparator);
		Random random = new Random(System.currentTimeMillis());
		for (int i = 0; i < 10; i++) {
			treeMap.put(i, random.nextInt(3));
		}

		System.out.println(treeMap);
		System.out.println(treeMap.size());

		double v = Math.sqrt(1.0 / 6.0 * Math.E);
		System.out.println(v);
		System.out.println(0.02 / v);

	}
}
