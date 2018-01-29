package com.frobisher.linux.accelerate.noextend;

import com.frobisher.linux.accelerate.DiagonalMatrixUtils;

import java.util.*;

/*
 * author: darcy
 * date: 2017/12/19 15:28
 * description:
 *
 * NCP-DFS 非候选深度优先搜索算法.
*/
public class SearchAlgorithm {

	double thresholdScore = Double.NEGATIVE_INFINITY;
	private Comparator<HACTreeNode> minComparator;
	private Comparator<HACTreeNode> maxComparator;
	private PriorityQueue<HACTreeNode> allDocumentScoreQueue;
	private int leafNodeCount = 0;
	private int containsCount = 0;
	private int computeCount = 0;
	private int pruneCount = 0;

	private Map<HACTreeNode, Double> nodeScoreMapForThreshold;

	/**
	 * 实现方式1: 采用堆的性性质.
	 *
	 * @param root
	 * @param trapdoor
	 * @param requestNumber
	 * @return
	 */
	public PriorityQueue<HACTreeNode> search(HACTreeNode root, Trapdoor trapdoor, int requestNumber) {
		System.out.println("SearchAlgorithm search start.");
		long start = System.currentTimeMillis();
		minComparator = new Comparator<HACTreeNode>() {
			@Override
			public int compare(HACTreeNode o1, HACTreeNode o2) {
				double s1 = scoreForPruning(o1, trapdoor);
				double s2 = scoreForPruning(o2, trapdoor);
				if (s1 > s2) {
					return 1;
				} else if (s1 < s2) {
					return -1;
				} else {
					return 0;
				}
			}

			/*@Override
			public int compare(HACTreeNode o1, HACTreeNode o2) {
				double score1 = scoreForPruning(o1, trapdoor);
				double score2 = scoreForPruning(o2, trapdoor);
				if (Double.compare(score1, score2) > 0) {
					return 1;
				} else if (Double.compare(score1, score2) == 0) {
					return 0;
				} else {
					return -1;
				}
			}*/
		};

		// 既然这个就是跟查询之间相关性评分最高的文档. 那么只需要利用此优先级队列或者相反的
		// 优先级队列就可以求出最不相关的文档.
		maxComparator = new Comparator<HACTreeNode>() {

			@Override
			public int compare(HACTreeNode o1, HACTreeNode o2) {
				double s1 = scoreForPruning(o1, trapdoor);
				double s2 = scoreForPruning(o2, trapdoor);
				if (s1 > s2) {
					return -1;
				} else if (s1 < s2) {
					return 1;
				} else {
					return 0;
				}
			}


			/*@Override
			public int compare(HACTreeNode node1, HACTreeNode node2) {
				double score1 = scoreForPruning(node1, trapdoor);
				double score2 = scoreForPruning(node2, trapdoor);
				if (Double.compare(score1, score2) > 0) {
					return -1;
				} else if (Double.compare(score1, score2) == 0) {
					return 0;
				} else {
					return 1;
				}
			}*/
		};

		nodeScoreMapForThreshold = new HashMap<>(requestNumber);

		allDocumentScoreQueue = new PriorityQueue<>(maxComparator);
		PriorityQueue<HACTreeNode> minHeap = new PriorityQueue<>(minComparator);
		dfs(root, trapdoor, requestNumber, minHeap);
		PriorityQueue<HACTreeNode> maxHeap = new PriorityQueue(maxComparator);
		maxHeap.addAll(minHeap);
		// 服务器端排序，然后返回top-K个最相关的文档.

		System.out.println("total time:" + (System.currentTimeMillis() - start) + "ms");
		System.out.println("SearchAlgorithm search end.");

		System.out.println("requestNumber:" + requestNumber);
		System.out.println("leafNodeCount:" + leafNodeCount);
		System.out.println("containsCount:" + containsCount);
		System.out.println("computeCount:" + computeCount);
		System.out.println("pruneCount:" + pruneCount);

		System.out.println("all document-score.");
		while (!allDocumentScoreQueue.isEmpty()) {
			HACTreeNode node = allDocumentScoreQueue.poll();
			System.out.printf("%-60s%.8f\n", node.fileDescriptor, scoreForPruning(node, trapdoor));
		}

		System.out.println("\nresult document-score.");

		PriorityQueue<HACTreeNode> result = new PriorityQueue<>(maxComparator);
		while (!maxHeap.isEmpty()) {
			HACTreeNode node = maxHeap.poll();
			result.add(node);
			System.out.printf("%-60s%.8f\n", node.fileDescriptor, scoreForPruning(node, trapdoor));
		}
		return result;
	}

	/**
	 * 这个剪枝基本没有效果，每次都需要计算999次。
	 */
	public static final double PRUNE_THRESHOLD_SCORE = 0.0004;

	private void dfs(HACTreeNode root, Trapdoor trapdoor, int requestNumber, PriorityQueue<HACTreeNode> minHeap) {
    // 是叶子结点.
		if (root.left == null && root.right == null) {
			leafNodeCount++;
			/*if (allDocumentSocreQueue.contains(root)) {
				containsCount++;
			}
			allDocumentSocreQueue.add(root);*/

			double scoreForPrune = scoreForPruning(root, trapdoor);
			computeCount++;
			if (!nodeScoreMapForThreshold.containsKey(root)) {
				nodeScoreMapForThreshold.put(root, scoreForPrune);
			}

			// 并且候选结果集合中没有top-K个元素.
			int size = minHeap.size();

			// 因为contains的关键词在文档向量中都是置1的, 而查询向量中相关位置处是超递增序列，至少是从1开始的。
			if (scoreForPrune >= PRUNE_THRESHOLD_SCORE) {
				if (size < requestNumber - 1) {
					System.out.println("< (N-1) add:" + root.fileDescriptor);
					minHeap.add(root);

					// 已经找到了 N-1个文档，然后将当前文档加入, 但是要更新现在的阈值评分.
				} else if (size == (requestNumber - 1)) {
					minHeap.add(root);
					System.out.println("= (N-1) add:" + root.fileDescriptor);
					HACTreeNode peekNode = minHeap.peek();
					if (nodeScoreMapForThreshold.containsKey(peekNode)) {
						thresholdScore = nodeScoreMapForThreshold.get(peekNode);
						containsCount++;
					} else {
						thresholdScore = scoreForPruning(peekNode, trapdoor);
						computeCount++;
					}
					System.out.println("new thresholdSocre:" + thresholdScore);

					// 仍然时叶子节点，但是候选结果集合中已经有了N个文档.
				} else {
					// 那么此时如果当前结点跟查询之间的相关性评分大于阈值，那么是需要更新
					// 候选结果集合的。
					if (/*scoreForPruning(root, trapdoor)*/ scoreForPrune > thresholdScore) {
						HACTreeNode minScoreNode = minHeap.poll();
						double score = scoreForPruning(minScoreNode, trapdoor);
						// System.out.println("== (N) remove:" + minScoreNode.fileDescriptor + " socre:" + score);
						minHeap.add(root);
						HACTreeNode peekNode = minHeap.peek();
						if (nodeScoreMapForThreshold.containsKey(peekNode)) {
							thresholdScore = nodeScoreMapForThreshold.get(peekNode);
							containsCount++;
						} else {
							thresholdScore = scoreForPruning(peekNode, trapdoor);
							computeCount++;
						}
						System.out.println("new thresholdSocre:" + thresholdScore);
					}
				}
			} else {
				System.out.println("leaf node not add for score < " + PRUNE_THRESHOLD_SCORE);
			}

		} else {
			double score = scoreForPruning(root, trapdoor);
			computeCount++;
			/*MatrixUitls.print(root.pruningVectorPart1);
			MatrixUitls.print(root.pruningVectorPart2);
			MatrixUitls.print(trapdoor.trapdoorPart1.transpose());
			MatrixUitls.print(trapdoor.trapdoorPart2.transpose());*/
			System.out.printf("%-10s\t%.8f\t%-20s\t%.8f\n", "score", score, "thresholdScore", thresholdScore);

			if (score >= PRUNE_THRESHOLD_SCORE && score >= thresholdScore) {
				if (root.left != null) {
					System.out.println("left");
					dfs(root.left, trapdoor, requestNumber, minHeap);
				}
				if (root.right != null) {
					System.out.println("right");
					dfs(root.right, trapdoor, requestNumber, minHeap);
				}
			} else {
				System.out.println("score:" + score + " no bigger than thresholdScore:" + thresholdScore);
				pruneCount++;
				System.out.println();
			}

		}
	}

	/**
	 * 计算跟查询向量之间最不相关的文档.
	 *
	 * @param resultList
	 * @param trapdoor
	 * @return
	 */
	private HACTreeNode getMinScoreNode(List<HACTreeNode> resultList, Trapdoor trapdoor) {
		HACTreeNode result = null;
		double min = Double.MAX_VALUE;
		for (int i = 0; i < resultList.size(); i++) {
			double currentScore = scoreForPruning(resultList.get(i), trapdoor);
			if (currentScore < min) {
				min = currentScore;
				result = resultList.get(i);
			}
		}
		return result;
	}

	/**
	 * 根节点和Trapdoor之间的相关性评分.
	 *
	 * @param root
	 * @param trapdoor
	 * @return
	 */
	private double scoreForPruning(HACTreeNode root, Trapdoor trapdoor) {
		/*return root.pruningVector.times(queryVector).get(0, 0);*/
		/*return root.pruningVectorPart1.transpose().times(trapdoor.trapdoorPart1).get(0, 0)
				+ root.pruningVectorPart2.transpose().times(trapdoor.trapdoorPart2).get(0, 0);*/
		return DiagonalMatrixUtils.score(root.pruningVectorPart1, trapdoor.trapdoorPart1) +
				DiagonalMatrixUtils.score(root.pruningVectorPart2, trapdoor.trapdoorPart2);
	}


	/**
	 * 搜索的时候用于更新最低阈值分数。
	 *
	 * @param resultList
	 * @param trapdoor
	 * @return
	 */
	private double updateThresholdScore(List<HACTreeNode> resultList, Trapdoor trapdoor) {

		double min = Double.MAX_VALUE;
		for (int i = 0; i < resultList.size(); i++) {
			/*double score = resultList.get(i).pruningVector.times(queryVector).get(0, 0);*/
			/*double score = resultList.get(i).pruningVectorPart1.transpose().times(trapdoor.trapdoorPart1).get(0, 0)
					+ resultList.get(i).pruningVectorPart2.transpose().times(trapdoor.trapdoorPart2).get(0, 0);*/

			// 构造的时候就以及认为pruningVectorPart1是1*M的矩阵。
			double score = DiagonalMatrixUtils.score(resultList.get(i).pruningVectorPart1, trapdoor.trapdoorPart1) +
					DiagonalMatrixUtils.score(resultList.get(i).pruningVectorPart2, trapdoor.trapdoorPart2);

			// 更新最小相关性评分.
			if (score < min) {
				min = score;
			}
		}
		return min;
	}
}