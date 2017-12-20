package com.darcy.Scheme2017MUSE.extend;

import Jama.Matrix;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

/*
 * author: darcy
 * date: 2017/12/19 15:28
 * description:
 *
 * NCP-DFS 非候选深度优先搜索算法.
*/
public class SearchAlgorithm {

	double thresholdScore = 0;

	/**
	 * 实现方式1: 采用堆的性性质.
	 * @param root
	 * @param trapdoor
	 * @param requestNumber
	 * @return
	 */
	public PriorityQueue<HACTreeNode> search(HACTreeNode root, Trapdoor trapdoor, int requestNumber) {
		PriorityQueue<HACTreeNode> minHeap = new PriorityQueue<>(new Comparator<HACTreeNode>() {
			@Override
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
			}
		});

		dfs(root, trapdoor, requestNumber, minHeap);

		// 既然这个就是跟查询之间相关性评分最高的文档. 那么只需要利用此优先级队列或者相反的
		// 优先级队列就可以求出最不相关的文档.
		PriorityQueue maxHeap = new PriorityQueue(new Comparator<HACTreeNode>() {
			@Override
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
			}
		});
		maxHeap.addAll(minHeap);
		// 服务器端排序，然后返回top-K个最相关的文档.
		return maxHeap;
	}

	private void dfs(HACTreeNode root, Trapdoor trapdoor, int requestNumber, PriorityQueue<HACTreeNode> minHeap) {
		// 是叶子结点.
		if (root.left == null && root.right == null) {
			// 并且候选结果集合中没有top-K个元素.
			if (minHeap.size() < requestNumber - 1) {
				minHeap.add(root);

				// 已经找到了 N-1个文档，然后将当前文档加入, 但是要更新现在的阈值评分.
			} else if (minHeap.size() == (requestNumber - 1)) {
				minHeap.add(root);
				thresholdScore = scoreForPruning(minHeap.peek(), trapdoor);

				// 仍然时叶子节点，但是候选结果集合中已经有了N个文档.
			} else {
				// 那么此时如果当前结点跟查询之间的相关性评分大于阈值，那么是需要更新
				// 候选结果集合的。
				if (scoreForPruning(root, trapdoor) > thresholdScore) {
					HACTreeNode minScoreNode = minHeap.poll();
					minHeap.add(root);
					thresholdScore = scoreForPruning(minHeap.peek(), trapdoor);
				}
			}
		} else {
			if (scoreForPruning(root, trapdoor) > thresholdScore) {
				if (root.left != null) {
					dfs(root.left, trapdoor, requestNumber, minHeap);
				}
				if (root.right != null) {
					dfs(root.right, trapdoor, requestNumber, minHeap);
				}
			}
		}
	}

	/**
	 * 计算跟查询向量之间最不相关的文档.
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
	 * @param root
	 * @param trapdoor
	 * @return
	 */
	private double scoreForPruning(HACTreeNode root, Trapdoor trapdoor) {
		/*return root.pruningVector.times(queryVector).get(0, 0);*/
		return root.pruningVectorPart1.times(trapdoor.trapdoorPart1).get(0, 0)
				+ root.pruningVectorPart2.times(trapdoor.trapdoorPart2).get(0, 0);
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
			double score = resultList.get(i).pruningVectorPart1.times(trapdoor.trapdoorPart1).get(0, 0)
					+ resultList.get(i).pruningVectorPart2.times(trapdoor.trapdoorPart2).get(0, 0);
			// 更新最小相关性评分.
			if (score < min) {
				min = score;
			}
		}
		return min;
	}
}
