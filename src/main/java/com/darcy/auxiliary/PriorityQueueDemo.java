package com.darcy.auxiliary;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Random;

/*
 * author: darcy
 * date: 2018/1/9 22:06
 * description: 
*/
public class PriorityQueueDemo {
	public static void main(String[] args) {
		PriorityQueue<Double> tfIdfMinHeap = new PriorityQueue<>(20, Double::compare);
		PriorityQueue<Double> tfIdfMaxHeap = new PriorityQueue<>(20, Comparator.reverseOrder());
		Random random = new Random(System.currentTimeMillis());
		int count = 100;
		for (int i = 0; i < count; i++) {
			tfIdfMinHeap.add(100 * random.nextDouble());
			tfIdfMaxHeap.add(100 * random.nextDouble());
		}

		while (!tfIdfMinHeap.isEmpty()) {
			System.out.print(tfIdfMinHeap.poll() + " ");
		}
		System.out.println();

		while (!tfIdfMaxHeap.isEmpty()) {
			System.out.print(tfIdfMaxHeap.poll() + " ");
		}
	}


}
