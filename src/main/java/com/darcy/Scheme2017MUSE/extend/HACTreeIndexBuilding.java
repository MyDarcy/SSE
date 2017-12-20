package com.darcy.Scheme2017MUSE.extend;

import Jama.Matrix;
import org.apache.commons.math3.distribution.RealDistribution;
import org.apache.commons.math3.distribution.UniformRealDistribution;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import static java.util.stream.Collectors.toList;

/*
 * author: darcy
 * date: 2017/12/18 22:22
 * description: 
*/
public class HACTreeIndexBuilding {

	// 秘密钥
	public MySecretKey mySecretKey;
	public Map<String, byte[]> fileBytesMap = new HashMap<>();
	public HACTreeIndexBuilding(MySecretKey mySecretKey) {
		this.mySecretKey = mySecretKey;
	}

	public RealDistribution distribution = new UniformRealDistribution(-0.01, 0.01);
	public Random random = new Random(31);

	/**
	 * 求MySecretKey中两个矩阵的转置矩阵和逆矩阵, 因为在构造索引阶段要用。
	 */
	public void generateAuxiliaryMatrix() {
		System.out.println("HACTreeIndexBuilding generateAuxiliaryMatirx start.");
		long start = System.currentTimeMillis();
		long nstart = start;

		AuxiliaryMatrix.M1Transpose = mySecretKey.M1.transpose();
		AuxiliaryMatrix.M2Transpose = mySecretKey.M2.transpose();
		System.out.println("two transpose:" + (System.currentTimeMillis() - start) + "ms");

		start = System.currentTimeMillis();
		AuxiliaryMatrix.M2Inverse = mySecretKey.M2.inverse();
		AuxiliaryMatrix.M2Inverse = mySecretKey.M2.inverse();
		System.out.println("two inverse:" + (System.currentTimeMillis() - start) + "ms");

		System.out.println("total time:" + (System.currentTimeMillis() - nstart) + "ms");
		System.out.println("HACTreeIndexBuilding generateAuxiliaryMatrix finished.");
	}

	/**
	 * 加密文档，
	 * 同时生成name -> fileBytes的映射, 因为要生成消息摘要的使用要用到文档的内容，
	 * 为了避免两次IO读取操作，所以牺牲了内存的性能。
	 *
	 * @throws IOException
	 * @throws BadPaddingException
	 * @throws InvalidKeyException
	 * @throws IllegalBlockSizeException
	 */
	public void encryptFiles() throws IOException, BadPaddingException, InvalidKeyException, IllegalBlockSizeException {
		System.out.println("HACTreeIndexBuilding encryptFiles start.");
		long start = System.currentTimeMillis();
		File parentDir = new File(Initialization.PLAIN_DIR);
		if (parentDir.exists()) {
			File[] files = parentDir.listFiles();
			for (int i = 0; i < files.length; i++) {
				Path path = files[i].toPath();
				byte[] bytes = Files.readAllBytes(path);
				// 先缓存加密文件.
				fileBytesMap.put(files[i].getName(), bytes);
				// 获取加密后的bytes.
				byte[] encrypt = EncryptionUtils.encrypt(bytes);

				/*System.out.println(path.getFileName());*/
				String encryptedFileName = Initialization.ENCRYPTED_DIR + "\\encrypted_" + path.getFileName().toString();
				// 二进制文件的后缀是.dat
				encryptedFileName = encryptedFileName.substring(0, encryptedFileName.lastIndexOf('.')) + ".dat";

				/*System.out.println(encryptedFileName);*/

				Files.write(new File(encryptedFileName).toPath(), encrypt);

				// 显示文件加密后的内容.
				/*byte[] decrypt = EncryptionUtils.decrypt(encrypt);
				String text = new String(decrypt);
				System.out.println(text);
				System.out.println();*/
			}
		}

		System.out.println("total time:" + (System.currentTimeMillis() - start) + "ms");
		System.out.println("HACTreeIndexBuilding encryptFiles finish.");
	}

	/**
	 *  *  HAC-tree中节点u是一个五元组〈VM,PL,PR,FD,sig〉, 其中，u.VM是是一个剪枝向量，u.PL和u.PR分别是指向节点u的左右孩子节点。
	 *  u.FD代表的是文档额唯一的ID。u.sig代表的是u.FD文档的消息摘要。此外，u.VC是聚类C_u的聚类中心向量，u.N表示聚类C_u中文档的数目，
	 *  聚类C_u代表的是以u为根节点的子树中所有的叶子结点代表的文档，同时注意u.VC和u.N仅仅存在于HAC-Tree的构造阶段，
	 *  不需要存储在HAC-Tree中。根据节点u的类型，我们详细的描述HAC-Tree节点如下
	 *      如果u是叶子结点，那么u.PL= u.PR= ϕ , u.FD 存储的是文档的id，u.VM和u.VC都存储的是当前文档的向量，u.N=1, u.sig
	 *  存储的是当前文档的消息摘要，消息摘要主要用于后续的搜搜结果的验证。
	 *      如果u是一个内部的中间节点，那么u.FD= ϕ, u.sig= ϕ, u.PL和u.PR代表节点u的左右孩子节点。u.N= u.PL.N + u.PR.N,
	 *  而u.VM则是从聚类C_u中提取的最大向量。u.VC则是聚类C_u的聚类中心向量。
	 *  u.VM= (max) ⃗{u.PL.VM,u.PR.VM}    (9)
	 *  u.VC=  (u.PL.N × u.PL.VC+u.PR.N+u.PR.VC)/(u.PL.N+u.PR.N)    (10)
	 * @return
	 */
	public HACTreeNode buildHACTreeIndex() throws NoSuchAlgorithmException {
		System.out.println("HACTreeIndexBuilding buildHACTreeIndex:");
		Set<HACTreeNode> currentProcessingHACTreeNodeSet = new HashSet<>();
	  Set<HACTreeNode> newGeneratedHACTreeNodeSet = new HashSet<>();

		File parentFile = new File(Initialization.PLAIN_DIR);
		File[] files = parentFile.listFiles();

		for (int i = 0; i < files.length; i++) {
			System.out.println(files[i].getName());

			Matrix P = new Matrix(1, Initialization.DICTIONARY_SIZE + Initialization.DUMMY_KEYWORD_NUMBER);

			// 当前文档的长度.
			int lengthOfFile = Initialization.fileLength.get(files[i].getName());

			Map<String, Integer> keywordFrequencyInCurrentDocument =
					Initialization.keywordFrequencyInDocument.get(files[i].getName());

			double denominator = tfDenominator(keywordFrequencyInCurrentDocument, lengthOfFile);
			double molecule = 0;

			for (String key : keywordFrequencyInCurrentDocument.keySet()) {
				int index = Initialization.dict.indexOf(key);
				if (index != -1) {
					/*int score = (int)Math.ceil(upper * score(lengthOfFile, keywordFrequencyInCurrentDocument.get(key),
							Initialization.numberOfDocumentContainsKeyword.get(key), files.length));
					P.set(0, index, score);*/

					// 本方案中， 文档向量中存储的是归一化的TF值.
					molecule = (1 + Math.log(keywordFrequencyInCurrentDocument.get(key))) / lengthOfFile;
					double tfValue = molecule / denominator;
					/*System.out.printf("%-20s %10s  %-10s %-15s %-10s\n", "key", "freq", "molecule", "denominator", "tfValue");
					System.out.printf("%-20s %10d  %-10f %-15f %-10f\n", key, keywordFrequencyInCurrentDocument.get(key)
							,molecule, denominator, tfValue);*/
					P.set(0, index, tfValue);
				}
			}

			double[] sample = distribution.sample(Initialization.DUMMY_KEYWORD_NUMBER);
			for (int j = 0; j < (Initialization.DUMMY_KEYWORD_NUMBER); j++) {
				P.set(0, Initialization.DICTIONARY_SIZE + j, sample[j]);
			}

			// 获取可逆矩阵加密后的Matrix.
			Matrix pa = new Matrix(1, Initialization.DICTIONARY_SIZE + Initialization.DUMMY_KEYWORD_NUMBER);
			Matrix pb = new Matrix(1, Initialization.DICTIONARY_SIZE + Initialization.DUMMY_KEYWORD_NUMBER);

			/**
			 * S[i] = 1, pa[i] + pb[i] = P[i]
			 * S[i] = 0, pa[i] = pb[i] = P[i]
			 */
			for (int j = 0; j < Initialization.DICTIONARY_SIZE + Initialization.DUMMY_KEYWORD_NUMBER; j++) {
				if (mySecretKey.S.get(j)) {
					double v1 = random.nextDouble();
					pa.set(0, j, v1);
					pb.set(0, j, P.get(0, j) - v1);
				} else {
					pa.set(0, j, P.get(0, j));
					pb.set(0, j, P.get(0, j));
				}
			}

			// 获取消息摘要.
			MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
			byte[] keyBytes = mySecretKey.secretKey.getEncoded();

			byte[] fileBytes = fileBytesMap.get(files[i].getName());
			byte[] bytes = new byte[keyBytes.length + fileBytes.length];
			System.arraycopy(keyBytes, 0, bytes, 0, keyBytes.length);
			System.arraycopy(fileBytes, 0, bytes, keyBytes.length, fileBytes.length);
			messageDigest.update(bytes);

			Matrix paEncrypted = pa.times(AuxiliaryMatrix.M1Transpose);
			Matrix pbEncrypted = pb.times(AuxiliaryMatrix.M2Transpose);

			// 存疑, 中心向量向上构造的过程中.最开始的中心向量的选择，因为原来的
			// 剪枝向量已经用两个转置矩阵加密了。
			HACTreeNode currentNode = new HACTreeNode(paEncrypted, pbEncrypted, P, 1,
					null, null, files[i].getName(), messageDigest);

			/*HACTreeNode currentNode = new HACTreeNode(P, P, 1,
					null, null, files[i].getName(), files[i].getName());*/
			/*System.out.println(currentNode);*/

			currentProcessingHACTreeNodeSet.add(currentNode);
		}

		System.out.println("start construct hac-tree.");
		int round = 1;
		while (currentProcessingHACTreeNodeSet.size() > 1) {
			System.out.println("the " + (round++) + " reduce to build tree.");
			/*System.out.println();*/
			while (currentProcessingHACTreeNodeSet.size() > 1) {
				HACTreeNodePair nodePair = findMostCorrespondNodePair(currentProcessingHACTreeNodeSet);
				List<Matrix> parentNodePruningVectors = getParentNodePruningVector(nodePair);
				Matrix parentNodeCenterVector = getParentNodeCenterVector(nodePair);
				int parentNumberOfNodeInCurrentCluster = nodePair.node1.numberOfNodeInCurrentCluster + nodePair.node2.numberOfNodeInCurrentCluster;
				// 存疑，这样构造出来的剪枝向量有效吗？
				HACTreeNode parentNode = new HACTreeNode(parentNodePruningVectors.get(0), parentNodePruningVectors.get(1),
						parentNodeCenterVector, parentNumberOfNodeInCurrentCluster,
						nodePair.node1, nodePair.node2, null, null);
				currentProcessingHACTreeNodeSet.remove(nodePair.node1);
				currentProcessingHACTreeNodeSet.remove(nodePair.node2);
				newGeneratedHACTreeNodeSet.add(parentNode);
			}
			if (newGeneratedHACTreeNodeSet.size() > 0) {
				currentProcessingHACTreeNodeSet.addAll(newGeneratedHACTreeNodeSet);
				newGeneratedHACTreeNodeSet.clear();
			}
		}

		System.out.println("currentProcessingHACTreeNodeSet.size():" + currentProcessingHACTreeNodeSet.size());
		// currentProcessingHACTreeNodeSet中一定是有一个节点的.
		HACTreeNode root = currentProcessingHACTreeNodeSet.iterator().next();
		return root;
	}

	/**
	 * 获取两个聚类的中心向量.
	 * @param nodePair
	 * @return
	 */
	private Matrix getParentNodeCenterVector(HACTreeNodePair nodePair) {
		int newNumberOfNode = nodePair.node1.numberOfNodeInCurrentCluster + nodePair.node2.numberOfNodeInCurrentCluster;
		Matrix parentCenterVector = new Matrix(1, Initialization.DICTIONARY_SIZE + Initialization.DUMMY_KEYWORD_NUMBER);
		for (int i = 0; i < Initialization.DICTIONARY_SIZE + Initialization.DUMMY_KEYWORD_NUMBER; i++) {
			double sum = nodePair.node1.clusterCenterVector.get(0, i) * nodePair.node1.numberOfNodeInCurrentCluster
					+ nodePair.node2.clusterCenterVector.get(0, i) + nodePair.node2.numberOfNodeInCurrentCluster;
			parentCenterVector.set(0, i, sum / newNumberOfNode);
		}
		return parentCenterVector;
	}

	/**
	 * 一堆HACTreeNode中找最相关的文档。即相关性评分最高的文档.
	 * <p>
	 * version-1: 暴力的方法, n/2 * n * n * (向量维度的平方);
	 * version-2: 网上的凸包问题的解法, n^2 -> nlogn, 但是那个是2维平面的点，用到了2维的特性，拓展到n维，效率有没有提升，多大的提升都是未知的.
	 *
	 * @param currentProcessingHACTreeNodeSet
	 * @return
	 */
	private HACTreeNodePair findMostCorrespondNodePair(Set<HACTreeNode> currentProcessingHACTreeNodeSet) {
		System.out.println("findMostCorrespondNodePair start.");
		long start = System.currentTimeMillis();
		int maxIndex1 = 0;
		int maxIndex2 = 0;
		double max = Double.MIN_VALUE;
		List<HACTreeNode> list = currentProcessingHACTreeNodeSet.stream().collect(toList());
		for (int i = 0; i < list.size(); i++) {
			for (int j = i + 1; j < list.size(); j++) {
				double score = correspondingScore(list.get(i), list.get(j));
				if (score > max) {
					maxIndex1 = i;
					maxIndex2 = j;
					max = score;
				}
				/*System.out.println(list.get(i) + "\t" + list.get(j) + "\tscore:" +score );*/
			}
		}
		/*System.out.println(list.get(maxIndex1) + "\t" + list.get(maxIndex2) + "\t max score:" + max);
		System.out.println();*/
		System.out.println("total time:" + (System.currentTimeMillis() - start) + "ms");
		System.out.println("findMostCorrespondNodePair finished.");
		return new HACTreeNodePair(list.get(maxIndex1), list.get(maxIndex2));
	}

	/**
	 * 获取两个子节点剪枝向量对应位置max值组成的父节点的剪枝向量.
	 * 但是此时的剪枝向量是经过
	 * @param pair
	 * @return
	 */
	public List<Matrix> getParentNodePruningVector(HACTreeNodePair pair) {
		Matrix parent1 = new Matrix(1, Initialization.DICTIONARY_SIZE + Initialization.DUMMY_KEYWORD_NUMBER);
		Matrix parent2 = new Matrix(1, Initialization.DICTIONARY_SIZE + Initialization.DUMMY_KEYWORD_NUMBER);
		for (int i = 0; i < Initialization.DICTIONARY_SIZE + Initialization.DUMMY_KEYWORD_NUMBER; i++) {
			parent1.set(0, i, Double.max(pair.node1.pruningVectorPart1.get(0, i), pair.node2.pruningVectorPart1.get(0, i)));
			parent1.set(0, i, Double.max(pair.node1.pruningVectorPart2.get(0, i), pair.node2.pruningVectorPart2.get(0, i)));
		}
		return Arrays.asList(parent1, parent2);
	}

	private double correspondingScore(HACTreeNode node1, HACTreeNode node2) {
		Matrix matrix = node1.pruningVector.times(node2.pruningVector.transpose());
		/*System.out.println(matrix.getRowDimension() + "\t" + matrix.getColumnDimension());*/
		return matrix.get(0, 0);
	}

	/**
	 *
	 * @param keywordFrequencyInCurrentDocument
	 * @param lengthOfFile
	 * @return
	 */
	private double tfDenominator(Map<String, Integer> keywordFrequencyInCurrentDocument, int lengthOfFile) {
		double denominator = 0;
		for (String keyword : keywordFrequencyInCurrentDocument.keySet()) {
			// 所有单个关键词按此公式计算出来的TF值平方.
			denominator += Math.pow((1 + Math.log(keywordFrequencyInCurrentDocument.get(keyword))) / lengthOfFile, 2);
		}
		// 返回sqrt的数字.
		return Math.sqrt(denominator);
	}

	/**
	 *
	 * @param lengthOfFile 文件i的长度.
	 * @param frequency 当前关键词在文档i中出现的频率.
	 * @param numberOfDocumentContainsKeyword 有多少个文档包含关键词.
	 * @param filesNumber 总的文档的数目.
	 * @return
	 */
	private double score(int lengthOfFile, Integer frequency, Integer numberOfDocumentContainsKeyword,int filesNumber) {
		return ((1 + Math.log(frequency)) / lengthOfFile)
				* Math.log(1 + filesNumber / numberOfDocumentContainsKeyword);
	}

	public static void main(String[] args) throws IOException, BadPaddingException, InvalidKeyException, IllegalBlockSizeException, NoSuchAlgorithmException {
		MySecretKey mySecretKey = Initialization.getMySecretKey();
		HACTreeIndexBuilding hacTreeIndexBuilding = new HACTreeIndexBuilding(mySecretKey);

		// 在加密文件之前，需要先加密文档和
		hacTreeIndexBuilding.encryptFiles();
		hacTreeIndexBuilding.generateAuxiliaryMatrix();

		// test2
		long start = System.currentTimeMillis();
		System.out.println("hac tree index building:");
		hacTreeIndexBuilding.buildHACTreeIndex();
		System.out.println("finish hac tree index building:" + (System.currentTimeMillis() - start) + "ms");
		System.out.println((1 + Math.log(16)) / (1 + Math.log(1)));
		System.out.println(0.156131 / 0.041386);
	}
}
