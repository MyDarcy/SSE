package com.darcy.auxiliary;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

/*
 * author: darcy
 * date: 2017/12/24 10:45
 * description: 
*/
public class FileSplitting {

	public static final String SOURCE_DIR = "D:\\MrDarcy\\ForGraduationWorks\\Data\\cnn_stories\\cnn\\stories";
	public static final int NUMBER_TO_BE_SPLITTING = 16;
	public static final int NUMBER_LINE_PER_SUBDOCUMENT = 3;
	public static final String DEST_DIR = "D:\\MrDarcy\\ForGraduationWorks\\Data\\cnn_stories\\cnn_splitting"
			+ NUMBER_TO_BE_SPLITTING + "_" + NUMBER_LINE_PER_SUBDOCUMENT;

	public static void main(String[] args) throws IOException {
		if (!new File(DEST_DIR).exists()) {
			new File(DEST_DIR).mkdir();
		}
		File parentDir = new File(SOURCE_DIR);
		String[] filenames = parentDir.list();
		for (int i = 0; i < NUMBER_TO_BE_SPLITTING; i++) {
			File file = new File(SOURCE_DIR + "\\" + filenames[i]);
			List<String> allLines = Files.readAllLines(file.toPath());
			int subFileNumbers = allLines.size() / NUMBER_LINE_PER_SUBDOCUMENT;
			String nameWithoutSuffix = file.getName().substring(0, file.getName().lastIndexOf('.'));
			for (int j = 0; j < subFileNumbers; j++) {
				String k = j < 9 ? "0" + (j + 1) : "" + (j + 1);
				if (j < (subFileNumbers - 1)) {
					File subFile = new File(DEST_DIR + "\\" + nameWithoutSuffix + k + ".story");
					Files.write(subFile.toPath(), allLines.subList(j * NUMBER_LINE_PER_SUBDOCUMENT, (j + 1) * NUMBER_LINE_PER_SUBDOCUMENT));
				} else {
					File subFile = new File(DEST_DIR + "\\" + nameWithoutSuffix + k + ".story");
					Files.write(subFile.toPath(), allLines.subList(j * NUMBER_LINE_PER_SUBDOCUMENT, allLines.size()));
				}
			}
		}

	}

}
