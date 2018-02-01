package com.darcy.Scheme2018PLVMSE.example.pv_base_2;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

/*
 * author: darcy
 * date: 2017/12/19 17:05
 * description: 
*/
public class Query2 {

	public static void test2() {
		try {
			Initialization initialization = new Initialization();
			MySecretKey mySecretKey = initialization.getMySecretKey();
			HACTreeIndexBuilding hacTreeIndexBuilding = new HACTreeIndexBuilding(mySecretKey, initialization);
			hacTreeIndexBuilding.encryptFiles();
			hacTreeIndexBuilding.generateAuxiliaryMatrix();
			HACTreeNode root = hacTreeIndexBuilding.buildHACTreeIndex();
			hacTreeIndexBuilding.encryptHACTreeIndex(root);
			// System.out.println(root);

			// for-16
			// String query = "church China hospital performance British interview Democratic citizenship broadcasting voice";

			// for-40
      // String query = "clinton broadcasting voice Francis honorary citizenship Democratic Revolution church president conferences";
			String query = "java python go";

			System.out.println("Query2 start generating trapdoor.");
			TrapdoorGenerating trapdoorGenerating = new TrapdoorGenerating(mySecretKey, initialization);
			Trapdoor trapdoor = trapdoorGenerating.generateTrapdoor(query);
			SearchAlgorithm searchAlgorithm = new SearchAlgorithm();

       int requestNumber = 2;
			// int requestNumber = 6;
			PriorityQueue<HACTreeNode> priorityQueue = searchAlgorithm.search(root, trapdoor, requestNumber);
			System.out.println("Query2 priorityQueue.size():" + priorityQueue.size());
			for (HACTreeNode node : priorityQueue) {
				System.out.println(node.fileDescriptor);
			}

			List<String> filenameList = priorityQueue.stream().map((node) -> node.fileDescriptor).collect(toList());

			String keywordPatternStr = getQueryPattern(query);

			// 验证搜索结果是否包含特定的文档。
			searchResultVerify(initialization, filenameList, keywordPatternStr);


		} catch (IOException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}
	}

	public static void testMultiRounds() {
		int round = 100;
		List<List<String>> list = new ArrayList<>(round);
		try {
			for (int i = 0; i < round; i++) {
				Initialization initialization = new Initialization();
				MySecretKey mySecretKey = initialization.getMySecretKey();
				HACTreeIndexBuilding hacTreeIndexBuilding = new HACTreeIndexBuilding(mySecretKey, initialization);
				hacTreeIndexBuilding.encryptFiles();
				hacTreeIndexBuilding.generateAuxiliaryMatrix();
				HACTreeNode root = hacTreeIndexBuilding.buildHACTreeIndex();
				hacTreeIndexBuilding.encryptHACTreeIndex(root);
				String query = "java python go";

				System.out.println("Query2 start generating trapdoor.");
				TrapdoorGenerating trapdoorGenerating = new TrapdoorGenerating(mySecretKey, initialization);
				Trapdoor trapdoor = trapdoorGenerating.generateTrapdoor2(query);
				SearchAlgorithm searchAlgorithm = new SearchAlgorithm();

				int requestNumber = 2;
				// int requestNumber = 6;
				PriorityQueue<HACTreeNode> priorityQueue = searchAlgorithm.search(root, trapdoor, requestNumber);
				System.out.println("Query2 priorityQueue.size():" + priorityQueue.size());
				for (HACTreeNode node : priorityQueue) {
					System.out.println(node.fileDescriptor);
				}

				List<String> filenameList = priorityQueue.stream().map((node) -> node.fileDescriptor).collect(toList());
				list.add(filenameList);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}

		for (List<String> item : list) {
			System.out.println(item);
		}
	}

	private static void searchResultVerify(Initialization initialization, List<String> filenameList, String keywordPatternStr) throws IOException {
		System.out.println();

		Pattern keywordPattern = Pattern.compile(keywordPatternStr);
		for (int i = 0; i < filenameList.size(); i++) {
			System.out.println(filenameList.get(i));
			List<String> allLines = Files.readAllLines(new File(initialization.PLAIN_DIR + "\\" + filenameList.get(i)).toPath());
			String passage = allLines.stream().map(String::toLowerCase).collect(joining("\n"));

			Matcher matcher = keywordPattern.matcher(passage);
			int count = 0;
			while (matcher.find()) {
				String keyword = matcher.group().toLowerCase();
				/*System.out.println(filenameArray[i] + "\t" + keyword + "\t" + Initialization.keywordFrequencyInDocument.get(filenameArray[i]).get(keyword) + "\t" + "documentNumber\t" + Initialization.numberOfDocumentContainsKeyword.get(keyword));*/
				System.out.printf("%-15s\t%-10s%-15s\t%10s\n", keyword,
						initialization.keywordFrequencyInDocument.get(filenameList.get(i)).get(keyword),
						"docsNumber", initialization.numberOfDocumentContainsKeyword.get(keyword));
				count++;
			}
			System.out.println("count:" + count);
			System.out.println();
		}
	}

	public static String getQueryPattern(String str) {
		Matcher matcher = Initialization.WORD_PATTERN.matcher(str);
		String result = "";
		while (matcher.find()) {
			result += matcher.group().toLowerCase() + "|";
		}
		return "(" + result.substring(0, result.lastIndexOf('|')) + ")";
	}

	public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
		System.out.println("tf_idf_base_1 search.");
//		test2();
    testMultiRounds();
	}
}

/*
tf_idf_base_1 search.
fileLength:{f4.txt=1, f1.txt=2, f2.txt=2, f3.txt=2, f5.txt=1, f8.txt=3, f6.txt=1, f7.txt=3}
keywordFrequencyInDocument:{f4.txt={cpp=1}, f1.txt={python=1, java=1}, f2.txt={java=1, go=1}, f3.txt={python=1, go=1}, f5.txt={c=1}, f8.txt={python=1, java=1, go=1}, f6.txt={javascript=1}, f7.txt={python=1, cpp=1, c=1}}
numberOfDocumentContainsKeyword:{python=4, cpp=2, java=3, c=2, go=3, javascript=1}
keyword - documentNumber
[python=4, java=3, go=3, cpp=2, c=2, javascript=1]
testInfo:true
initialization dict.size():6
[c, cpp, go, java, javascript, python]
[c, cpp, go, java, javascript, mugvnxze, python, pzfv]
add dummy keywords dict.size():8
bitSet.length:9
HACTreeIndexBuilding encryptFiles start.
total time:42ms
HACTreeIndexBuilding encryptFiles finish.
HACTreeIndexBuilding generateAuxiliaryMatirx start.
two transpose:1ms
two inverse:1ms
total time:2ms
HACTreeIndexBuilding generateAuxiliaryMatrix finished.
HACTreeIndexBuilding buildHACTreeIndex start.
f1.txt
python:0.5493061443340549
java:0.6496414920651304
[0.000000,0.000000,0.000000,0.649641,0.000000,0.004890,0.549306,-0.007111]

[0.710597,0.351116,0.267831,0.612492,0.318433,0.406480,0.960571,0.860208]

[0.904695,0.929540,0.556766,0.501247,1.012358,0.644691,0.690743,0.593906]

f2.txt
java:0.6496414920651304
go:0.6496414920651304
[0.000000,0.000000,0.649641,0.649641,0.000000,-0.000721,0.000000,-0.001086]

[0.268566,0.316981,0.274527,0.567193,0.207123,0.075689,0.505094,0.519958]

[0.632846,1.067858,0.586140,0.899125,1.046357,0.796309,0.999645,0.786583]

f3.txt
python:0.5493061443340549
go:0.6496414920651304
[0.000000,0.000000,0.649641,0.000000,0.000000,0.008257,0.549306,-0.006661]

[0.478376,0.056435,0.030951,0.077238,0.149502,0.368828,0.478290,0.382224]

[0.563328,0.753007,0.241920,0.622034,0.970844,0.598021,0.573319,0.689668]

f4.txt
cpp:1.6094379124341003
[0.000000,1.609438,0.000000,0.000000,0.000000,0.009712,0.000000,-0.003317]

[1.092249,0.743222,0.202450,0.988670,1.116584,1.423572,1.015512,1.054173]

[0.789408,0.361803,1.607412,0.078768,0.118730,0.719180,0.893992,0.637908]

f5.txt
c:1.6094379124341003
[1.609438,0.000000,0.000000,0.000000,0.000000,-0.007421,0.000000,-0.008924]

[0.707721,0.699190,0.004883,0.141833,0.413656,0.014063,0.004845,0.108697]

[0.479368,0.713029,0.055847,0.439376,0.525900,0.447234,0.022872,0.628901]

f6.txt
javascript:2.1972245773362196
[0.000000,0.000000,0.000000,0.000000,2.197225,-0.009298,0.000000,0.009818]

[1.959071,0.241791,1.670379,2.060262,0.026523,1.759356,1.168795,2.185747]

[2.134782,2.006016,0.145486,0.145029,1.811837,0.173349,0.293845,1.824967]

f7.txt
python:0.3662040962227032
cpp:0.5364793041447
c:0.5364793041447
[0.536479,0.536479,0.000000,0.000000,0.000000,-0.005273,0.366204,0.004225]

[0.751454,0.362962,0.068761,0.382860,0.503192,0.701115,0.644883,0.592765]

[0.823137,0.742482,0.638902,0.363306,0.657187,0.651656,0.406169,0.745810]

f8.txt
python:0.3662040962227032
java:0.4330943280434203
go:0.4330943280434203
[0.000000,0.000000,0.433094,0.433094,0.000000,0.005761,0.366204,-0.005600]

[0.623847,0.325280,0.334215,0.540060,0.370859,0.430040,0.736026,0.744728]

[0.640758,0.733142,0.406108,0.463775,0.802379,0.525984,0.571970,0.508492]

start construct hac-tree.
the 1's round to build tree.
getPriorityQueue start.
time:2
getPriorityQueue end.
(f5.txt,f7.txt)
(f8.txt,f2.txt)
(f3.txt,f1.txt)
(f4.txt,f6.txt)
the 2's round to build tree.
getPriorityQueue start.
time:0
getPriorityQueue end.
(f4f6.txt,f5f7.txt)
(f8f2.txt,f3f1.txt)
the 3's round to build tree.
getPriorityQueue start.
time:0
getPriorityQueue end.
(f8f2f3f1.txt,f4f6f5f7.txt)
currentProcessingHACTreeNodeSet.size():1
build hac tree index total time:32ms
HACTreeIndexBuilding buildHACTreeIndex finished.
Query2 start generating trapdoor.
TrapdoorGenerating trapdoorGenerating start.
java                7              1.00000000
python              8              22.50498918
go                  10             264.89085156

java                preference     1.00000000
python              preference     22.50498918
go                  preference     264.89085156
Q Qa Qb transponse.
[0.000000,0.000000,264.890852,1.000000,0.000000,0.000000,22.504989,0.000000]

[1044.125748,-2081.366816,240.015484,790.527193,1710.351335,-1063.049439,49.649173,-825.795678]

[458.166406,-1141.633034,948.335197,2077.675807,517.163624,-1738.928014,-670.254498,237.885311]

generate trapdoor total time:5
TrapdoorGenerating trapdoorGenerating finished.
SearchAlgorithm search start.
score     	2217.53965734	thresholdScore      	-Infinity
left
score     	382.89948291	thresholdScore      	-Infinity
left
score     	273.57446001	thresholdScore      	-Infinity
left
f8.txt:123.39723891113687
< (N-1) add:f8.txt

right
f2.txt:172.7337295330123
= (N-1) add:f2.txt

right
score     	286.74782351	thresholdScore      	123.39723891
left
== (N) remove:f8.txt socre:123.39723891113687

right
f1.txt:13.011770325757652
== (N) but the node cannot reach the point.
right
score     	1467.26727718	thresholdScore      	172.73372953
left
score     	855.70914892	thresholdScore      	172.73372953
left
f4.txt:1.0231815394945443E-12
== (N) but the node cannot reach the point.
right
f6.txt:9.094947017729282E-13
== (N) but the node cannot reach the point.
right
score     	-533.52502033	thresholdScore      	172.73372953
score:-533.525020328894 no bigger than thresholdScore:172.7337295330123

total time:4ms
SearchAlgorithm search end.
leafNodeCount:6
containsCount:0
all document-size:6
all document-score.
f3.txt                                                      184.44621687
f2.txt                                                      172.73372953
f8.txt                                                      123.39723891
f1.txt                                                      13.01177033
f4.txt                                                      0.00000000
f6.txt                                                      0.00000000

result document-score.
f3.txt                                                      184.44621687
f2.txt                                                      172.73372953
Query2 priorityQueue.size():2
f3.txt
f2.txt

f3.txt
python         	1         docsNumber     	         4
go             	1         docsNumber     	         3
count:2

f2.txt
java           	1         docsNumber     	         3
go             	1         docsNumber     	         3
count:2


Process finished with exit code 0

 */

/*
tf_idf_base_1 search.
fileLength:{f4.txt=1, f1.txt=2, f2.txt=2, f3.txt=2, f5.txt=1, f8.txt=3, f6.txt=1, f7.txt=3}
keywordFrequencyInDocument:{f4.txt={cpp=1}, f1.txt={python=1, java=1}, f2.txt={java=1, go=1}, f3.txt={python=1, go=1}, f5.txt={c=1}, f8.txt={python=1, java=1, go=1}, f6.txt={javascript=1}, f7.txt={python=1, cpp=1, c=1}}
numberOfDocumentContainsKeyword:{python=4, cpp=2, java=3, c=2, go=3, javascript=1}
keyword - documentNumber
[python=4, java=3, go=3, cpp=2, c=2, javascript=1]
testInfo:true
initialization dict.size():6
[c, cpp, go, java, javascript, python]
[c, cpp, go, java, javascript, mugvnxze, python, pzfv]
add dummy keywords dict.size():8
bitSet.length:9
HACTreeIndexBuilding encryptFiles start.
total time:39ms
HACTreeIndexBuilding encryptFiles finish.
HACTreeIndexBuilding generateAuxiliaryMatirx start.
two transpose:0ms
two inverse:1ms
total time:2ms
HACTreeIndexBuilding generateAuxiliaryMatrix finished.
HACTreeIndexBuilding buildHACTreeIndex start.
f1.txt
python:0.5493061443340549
java:0.6496414920651304
[0.000000,0.000000,0.000000,0.649641,0.000000,0.005758,0.549306,0.003968]
[0.590975,0.413183,0.668292,0.297977,0.486014,0.459484,0.684286,0.404093]
[0.337097,0.537452,0.367698,0.581989,0.361990,0.830213,0.115401,0.076255]
f2.txt
java:0.6496414920651304
go:0.6496414920651304
[0.000000,0.000000,0.649641,0.649641,0.000000,-0.006469,0.000000,0.009831]
[0.776719,0.896893,0.751235,0.311925,0.352044,0.868057,0.786450,0.235680]
[0.218394,0.060296,0.586684,0.352979,0.489742,0.575681,0.511915,0.054523]
f3.txt
python:0.5493061443340549
go:0.6496414920651304
[0.000000,0.000000,0.649641,0.000000,0.000000,-0.002380,0.549306,-0.004752]
[1.026488,0.807117,1.020005,0.435351,0.543633,0.869373,0.947345,0.319927]
[0.314409,0.553875,0.663889,0.496234,0.450829,0.864803,0.533321,0.109379]
f4.txt
cpp:1.6094379124341003
[0.000000,1.609438,0.000000,0.000000,0.000000,0.000370,0.000000,-0.007739]
[0.790454,1.001667,0.427346,0.549612,0.699534,0.224407,0.261095,0.570755]
[0.311682,0.476056,0.079035,0.348685,0.046930,0.037924,0.028400,0.270605]
f5.txt
c:1.6094379124341003
[1.609438,0.000000,0.000000,0.000000,0.000000,0.000550,0.000000,-0.009232]
[0.614095,0.271520,0.115895,0.005753,0.106702,0.654902,0.002094,0.101380]
[0.570047,0.472428,0.142770,0.665347,0.259304,0.828225,0.001187,0.412837]
f6.txt
javascript:2.1972245773362196
[0.000000,0.000000,0.000000,0.000000,2.197225,-0.000264,0.000000,-0.005425]
[0.445169,0.261364,0.478532,0.133911,0.284548,0.448171,0.153643,0.521183]
[1.266416,1.473230,0.283566,1.294162,0.585067,0.666664,1.262621,0.374829]
f7.txt
python:0.3662040962227032
cpp:0.5364793041447
c:0.5364793041447
[0.536479,0.536479,0.000000,0.000000,0.000000,-0.001775,0.366204,0.009954]
[0.779155,0.499270,0.485393,0.289471,0.462664,0.535540,0.361874,0.361428]
[0.414590,0.673672,0.222347,0.545093,0.182530,0.554651,0.065007,0.269173]
f8.txt
python:0.3662040962227032
java:0.4330943280434203
go:0.4330943280434203
[0.000000,0.000000,0.433094,0.433094,0.000000,-0.007521,0.366204,0.005836]
[0.760893,0.651204,0.768649,0.330486,0.427708,0.684305,0.748167,0.284976]
[0.323399,0.386931,0.584326,0.539029,0.491484,0.835561,0.397818,0.082484]
start construct hac-tree.
the 1's round to build tree.
getPriorityQueue start.
time:1
getPriorityQueue end.
(f4.txt,f7.txt)
parentPruning1
[0.790454,1.001667,0.427346,0.549612,0.699534,0.224407,0.261095,0.570755]
[0.779155,0.499270,0.485393,0.289471,0.462664,0.535540,0.361874,0.361428]
[0.790454,1.001667,0.485393,0.549612,0.699534,0.535540,0.361874,0.570755]
parentPruning2
[0.311682,0.476056,0.079035,0.348685,0.046930,0.037924,0.028400,0.270605]
[0.414590,0.673672,0.222347,0.545093,0.182530,0.554651,0.065007,0.269173]
[0.414590,0.673672,0.222347,0.545093,0.182530,0.554651,0.065007,0.270605]
(f8.txt,f2.txt)
parentPruning1
[0.760893,0.651204,0.768649,0.330486,0.427708,0.684305,0.748167,0.284976]
[0.776719,0.896893,0.751235,0.311925,0.352044,0.868057,0.786450,0.235680]
[0.776719,0.896893,0.768649,0.330486,0.427708,0.868057,0.786450,0.284976]
parentPruning2
[0.323399,0.386931,0.584326,0.539029,0.491484,0.835561,0.397818,0.082484]
[0.218394,0.060296,0.586684,0.352979,0.489742,0.575681,0.511915,0.054523]
[0.323399,0.386931,0.586684,0.539029,0.491484,0.835561,0.511915,0.082484]
(f3.txt,f1.txt)
parentPruning1
[1.026488,0.807117,1.020005,0.435351,0.543633,0.869373,0.947345,0.319927]
[0.590975,0.413183,0.668292,0.297977,0.486014,0.459484,0.684286,0.404093]
[1.026488,0.807117,1.020005,0.435351,0.543633,0.869373,0.947345,0.404093]
parentPruning2
[0.314409,0.553875,0.663889,0.496234,0.450829,0.864803,0.533321,0.109379]
[0.337097,0.537452,0.367698,0.581989,0.361990,0.830213,0.115401,0.076255]
[0.337097,0.553875,0.663889,0.581989,0.450829,0.864803,0.533321,0.109379]
(f5.txt,f6.txt)
parentPruning1
[0.614095,0.271520,0.115895,0.005753,0.106702,0.654902,0.002094,0.101380]
[0.445169,0.261364,0.478532,0.133911,0.284548,0.448171,0.153643,0.521183]
[0.614095,0.271520,0.478532,0.133911,0.284548,0.654902,0.153643,0.521183]
parentPruning2
[0.570047,0.472428,0.142770,0.665347,0.259304,0.828225,0.001187,0.412837]
[1.266416,1.473230,0.283566,1.294162,0.585067,0.666664,1.262621,0.374829]
[1.266416,1.473230,0.283566,1.294162,0.585067,0.828225,1.262621,0.412837]
the 2's round to build tree.
getPriorityQueue start.
time:0
getPriorityQueue end.
(f8f2.txt,f3f1.txt)
parentPruning1
[0.776719,0.896893,0.768649,0.330486,0.427708,0.868057,0.786450,0.284976]
[1.026488,0.807117,1.020005,0.435351,0.543633,0.869373,0.947345,0.404093]
[1.026488,0.896893,1.020005,0.435351,0.543633,0.869373,0.947345,0.404093]
parentPruning2
[0.323399,0.386931,0.586684,0.539029,0.491484,0.835561,0.511915,0.082484]
[0.337097,0.553875,0.663889,0.581989,0.450829,0.864803,0.533321,0.109379]
[0.337097,0.553875,0.663889,0.581989,0.491484,0.864803,0.533321,0.109379]
(f5f6.txt,f4f7.txt)
parentPruning1
[0.614095,0.271520,0.478532,0.133911,0.284548,0.654902,0.153643,0.521183]
[0.790454,1.001667,0.485393,0.549612,0.699534,0.535540,0.361874,0.570755]
[0.790454,1.001667,0.485393,0.549612,0.699534,0.654902,0.361874,0.570755]
parentPruning2
[1.266416,1.473230,0.283566,1.294162,0.585067,0.828225,1.262621,0.412837]
[0.414590,0.673672,0.222347,0.545093,0.182530,0.554651,0.065007,0.270605]
[1.266416,1.473230,0.283566,1.294162,0.585067,0.828225,1.262621,0.412837]
the 3's round to build tree.
getPriorityQueue start.
time:0
getPriorityQueue end.
(f5f6f4f7.txt,f8f2f3f1.txt)
parentPruning1
[0.790454,1.001667,0.485393,0.549612,0.699534,0.654902,0.361874,0.570755]
[1.026488,0.896893,1.020005,0.435351,0.543633,0.869373,0.947345,0.404093]
[1.026488,1.001667,1.020005,0.549612,0.699534,0.869373,0.947345,0.570755]
parentPruning2
[1.266416,1.473230,0.283566,1.294162,0.585067,0.828225,1.262621,0.412837]
[0.337097,0.553875,0.663889,0.581989,0.491484,0.864803,0.533321,0.109379]
[1.266416,1.473230,0.663889,1.294162,0.585067,0.864803,1.262621,0.412837]
currentProcessingHACTreeNodeSet.size():1
build hac tree index total time:62ms
HACTreeIndexBuilding buildHACTreeIndex finished.
Query2 start generating trapdoor.
TrapdoorGenerating trapdoorGenerating start.
java                7              1.00000000
python              8              46.74818858
go                  10             511.02833757

java                preference     1.00000000
python              preference     46.74818858
go                  preference     511.02833757
Q Qa Qb transponse.
[0.000000,0.000000,511.028338,1.000000,0.000000,0.000000,46.748189,0.000000]
[-91.319714,222.153777,657.427722,139.880679,-262.739060,-12.600298,-274.678755,-437.717570]
[-2247.685038,-643.620840,49.629807,2612.317715,-1895.446514,124.683201,965.754531,550.466690]
generate trapdoor total time:5
TrapdoorGenerating trapdoorGenerating finished.
SearchAlgorithm search start.
score     	235.95312088	thresholdScore      	-Infinity
left
score     	46.12190293	thresholdScore      	-Infinity
left
score     	25.29115510	thresholdScore      	-Infinity
left
f5.txt:1.9895196601282805E-13
< (N-1) add:f5.txt

right
f6.txt:-3.694822225952521E-13
= (N-1) add:f6.txt

right
score     	11.01268098	thresholdScore      	-0.00000000
left
== (N) remove:f6.txt socre:-3.694822225952521E-13

f4.txt:3.694822225952521E-13
right
== (N) remove:f5.txt socre:1.9895196601282805E-13

f7.txt:17.1193781481498
right
score     	436.73249356	thresholdScore      	0.00000000
left
score     	389.48709796	thresholdScore      	0.00000000
left
== (N) remove:f4.txt socre:3.694822225952521E-13

f8.txt:238.875946948679
right
== (N) remove:f7.txt socre:17.1193781481498

f2.txt:332.6348532007946
right
score     	493.84876700	thresholdScore      	238.87594695
left
== (N) remove:f8.txt socre:238.875946948679

f3.txt:357.6642789309536
right
f1.txt:26.32870871428983
== (N) but the node cannot reach the point.
total time:5ms
SearchAlgorithm search end.
leafNodeCount:8
containsCount:0
all document-size:8
all document-score.
f3.txt                                                      357.66427893
f2.txt                                                      332.63485320
f8.txt                                                      238.87594695
f1.txt                                                      26.32870871
f7.txt                                                      17.11937815
f4.txt                                                      0.00000000
f5.txt                                                      0.00000000
f6.txt                                                      -0.00000000

result document-score.
f3.txt                                                      357.66427893
f2.txt                                                      332.63485320
Query2 priorityQueue.size():2
f3.txt
f2.txt

f3.txt
python         	1         docsNumber     	         4
go             	1         docsNumber     	         3
count:2

f2.txt
java           	1         docsNumber     	         3
go             	1         docsNumber     	         3
count:2


Process finished with exit code 0

 */