package com.darcy.Scheme2016FineGrained.test;

import hep.aida.ref.Test;

import java.util.*;

/*
 * author: darcy
 * date: 2017/11/14 10:33
 * description: 
*/
public class HashMapTest {

	static class ValueComparator implements Comparator<Map.Entry<String, Integer>> {
		@Override
		public int compare(Map.Entry<String, Integer> e1, Map.Entry<String, Integer> e2) {
			return e1.getValue() - e2.getValue();
		}
	}

	public static void test1() {
		Map<String, Integer> map = new HashMap<>();
		map.put("a", 1);
		map.put("c", 3);
		map.put("b", 5);
		map.put("f", 7);
		map.put("e", 6);
		map.put("d", 8);
		List<Map.Entry<String, Integer>> list = new ArrayList<>();
		list.addAll(map.entrySet());
		ValueComparator vc = new ValueComparator();
		Collections.sort(list, vc);

		int sum = 1;
		Map<String, Integer> lhm = new LinkedHashMap<>();
		Random random = new Random(31);
		for (Iterator<Map.Entry<String, Integer>> it = list.iterator(); it.hasNext(); ) {
			Map.Entry<String, Integer> entry = it.next();
			lhm.put(entry.getKey(), 100 * (sum + (1 + random.nextInt(7))));
			sum += entry.getValue();
		}
		System.out.println(lhm);
	}

	public static void test2() {
		Map<String, Integer> map = new HashMap<>();
		map.put("a", 1);
		map.put("c", 3);
		map.put("b", 5);
		map.put("f", 7);
		map.put("e", 6);
		map.put("d", 8);
		map.put("h", 10);

		List<Map.Entry<String, Integer>> list = new ArrayList<>();
		list.addAll(map.entrySet());
		ValueComparator vc = new ValueComparator();
		Collections.sort(list, vc);
		Random random = new Random(31);
		System.out.println(list);

		Map<String, Integer> hyperIncresingSequence = new LinkedHashMap<>();
		int sum = 1;
		int i = 1;
		int upper = 10;
		for (Map.Entry<String, Integer> item : list) {
			/*Integer value = item.getValue();
			sum += value + 1 + random.nextInt(9);
			hyperIncresingSequence.put(item.getKey(), 100 * sum);*/



		}

		System.out.println(hyperIncresingSequence);

	}

	public static int[] generateHyperIncreasingSequence(int size, int upper) {
		int[] array = new int[size];
		int sum = 1;
		int value = 1;
		Random random = new Random(31);
		for (int i = 0; i < size; i++) {
			if (i != 0) {
				// 累加和上引入随机数.
				value = sum * upper + upper * random.nextInt(10);
			}
			array[i] = value;
			sum += value;
		}
		return array;
	}

	public static double[] generateHyperIncreasingSequence2(int size, int upper) {
		double[] array = new double[size];
		double sum = 1;
		double value = 1;
		Random random = new Random(31);
		for (int i = 0; i < size; i++) {
			if (i != 0) {
				// 累加和上引入随机数.
				value = sum * upper + upper * random.nextInt(10);
			}
			array[i] = value;
			sum += value;
		}
		return array;
	}


	public static void main(String[] args) {
		int size = 10;
		int upper = 5;
		int[] array = generateHyperIncreasingSequence(10, 5);
		System.out.println(Arrays.toString(array));
		int sum = array[0];
		for (int i = 1; i < array.length; i++) {
			if (upper * sum > array[i]) {
				System.out.println(i);
				System.out.println("not hyper increaing sequence.");
				break;
			}
			sum += array[i];
		}
		System.out.println("finish.");

		System.out.println();

		double[] array2 = generateHyperIncreasingSequence2(20, 100);
		System.out.println(Arrays.toString(array2));

		System.out.println();

		System.out.println(Double.MAX_VALUE);
	}


}
