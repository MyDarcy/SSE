package com.darcy.auxiliary;

import com.darcy.Scheme2018PLVMSE.accelerate.DiagonalMatrixUtils;
import org.ujmp.core.doublematrix.calculation.entrywise.creators.Rand;

import java.util.*;
import java.util.stream.Collectors;

/*
 * author: darcy
 * date: 2018/1/4 20:46
 * description: 
*/

class PointScore {
	double[] dot1;
	double[] dot2;
	double score;

	public PointScore(double[] dot1, double[] dot2, double score) {
		this.dot1 = dot1;
		this.dot2 = dot2;
		this.score = score;
	}
}

public class AccelerateCorrespongdingScore {



	public Comparator<PointScore> maxComparator;

	public void test() {
		maxComparator = new Comparator<PointScore>() {
			@Override
			public int compare(PointScore ps1, PointScore ps2) {
				if (Double.compare(ps1.score, ps2.score) > 0) {
					return -1;
				} else if (Double.compare(ps1.score, ps2.score) < 0) {
					return 1;
				} else {
					return 0;
				}
			}
		};

		int dotNumber = 1001;
		int matrixSize = 5000;
		List<double[]> list1 = new ArrayList<>(dotNumber);
		List<double[]> list2 = new ArrayList<>(dotNumber);
		Random random = new Random(System.currentTimeMillis());
		for (int i = 0; i < dotNumber; i++) {
			double[] array = new double[matrixSize];
			for (int j = 0; j < array.length; j++) {
				array[j] = random.nextDouble();
			}
			list1.add(array);
			list2.add(array);
		}
		List<Double> scores1 = heapTest(list1);
		System.out.println();
		List<Double> scores2 = simpleTest(list2);

		for (int i = 0; i < scores1.size(); i++) {
			if (Double.compare(scores1.get(i), scores2.get(i)) != 0) {
				System.out.println("not equal.");
				break;
			}
		}
		System.out.println("total finish.");

	}

	private List<Double> simpleTest(List<double[]> list) {
		List<Double> scores = new ArrayList<>();
		System.out.println("simpleTest1 start.");
		long start = System.currentTimeMillis();
		int count = 0;
		while (list.size() > 1) {
			PointScore ps = getMostSimilar(list);
			list.remove(ps.dot2);
			list.remove(ps.dot1);
			count++;
			System.out.println(count + ":" + ps.score);
			scores.add(ps.score);
		}
		System.out.println("count:" + count);
		System.out.println("time:" + (System.currentTimeMillis() - start) + "ms");
		return scores;
	}

	private PointScore getMostSimilar(List<double[]> list) {
		double max = Double.NEGATIVE_INFINITY;
		int maxIndex1 = 0;
		int maxIndex2 = 1;
		for (int i = 0; i < list.size(); i++) {
			for (int j = i + 1; j < list.size(); j++) {
				double s = score(list.get(i), list.get(j));
				if (s > max) {
					max = s;
					maxIndex1 = i;
					maxIndex2 = j;
				}
			}
		}
		return new PointScore(list.get(maxIndex1), list.get(maxIndex2), max);
	}

	private List<Double> heapTest(List<double[]> list) {
		List<Double> scores = new ArrayList<>();
		System.out.println("heapTest.");
		long start = System.currentTimeMillis();
		PriorityQueue<PointScore> maxQueue = getPriorityQueue(list);
		int count = 0;
		Set<double[]> set = new HashSet<>();
		if (list.size() % 2 == 0) {
			while (maxQueue.size() > 0) {
				PointScore ps = maxQueue.poll();
				if (set.contains(ps.dot1)) {
					continue;
				}

				if (set.contains(ps.dot2)) {
					continue;
				}

				scores.add(ps.score);
				count++;
				System.out.println(count + ":" + ps.score);
				set.add(ps.dot1);
				set.add(ps.dot2);
			}
		} else {
			while (maxQueue.size() > 0) {
				PointScore ps = maxQueue.poll();
				if (set.contains(ps.dot1)) {
					continue;
				}

				if (set.contains(ps.dot2)) {
					continue;
				}

				//System.out.println(ps.score);
				scores.add(ps.score);
				count++;
				set.add(ps.dot1);
				set.add(ps.dot2);
			}
			Set<double[]> initSet = list.stream().collect(Collectors.toSet());
			boolean x = initSet.removeAll(set);
			System.out.println("odd number, initSet.size():" + initSet.size());
		}

		System.out.println(count);
		System.out.println("time:" + (System.currentTimeMillis() - start) + "ms");
		System.out.println("testHeap finish.");
		return scores;
	}

	private PriorityQueue<PointScore> getPriorityQueue(List<double[]> list) {
		System.out.println("getPriorityQueue start.");
		PriorityQueue<PointScore> maxHeap = new PriorityQueue<>(maxComparator);
		for (int i = 0; i < list.size(); i++) {
			for (int j = i + 1; j < list.size(); j++) {
				maxHeap.add(new PointScore(list.get(i), list.get(j), score(list.get(i), list.get(j))));
			}
		}
		System.out.println("getPriorityQueue end.");
		return maxHeap;
	}

	private double score(double[] dot1, double[] dot2) {
		double sum = 0;
		for (int i = 0; i < dot1.length; i++) {
			sum += dot1[i] * dot2[i];
		}
		return sum;
	}

	public static void main(String[] args) {
		AccelerateCorrespongdingScore demo = new AccelerateCorrespongdingScore();
		demo.test();
	}

}
