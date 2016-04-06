package edu.troy.cs.bio;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.bson.Document;
import org.bson.types.ObjectId;

public class UpdateResult {

	private static String dirPath = "/home/hadoop/workspace/drug_result/Index-Score/";
	static int writeIndex = 0;
	static int bulkWriteSize = 500;

	private static File[] getFiles() {
		File dir = new File(dirPath);
		if (!dir.isDirectory()) {
			return null;
		}
		return dir.listFiles();
	}

	private static void updateScors(File file) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(file));

		String line = null;
		br.readLine();
		while ((line = br.readLine()) != null) {
			String[] cols = line.split(",");
			String id = cols[1];
			if (isEmpty(id)) {
				continue;
			}
			id = id.replace("\"", "");
			if (!ObjectId.isValid(id)) {
				continue;
			}
			double score = 0d;
			try {
				score = Double.parseDouble(cols[2]);
			} catch (Exception e) {
				System.out.println(e);
				continue;
			}
			MongoDB.resultCollection().updateOne(new Document("_id", new ObjectId(id)),
					new Document("$set", new Document("fpScore", score)));
		}

		br.close();
	}

	private static boolean isEmpty(String str) {
		return str == null || str.length() == 0 || str.trim().length() == 0;
	}

	public static void main(String[] args) throws IOException {
		int i = 0;
		File[] files = getFiles();
		for (File file : files) {
			i++;
			updateScors(file);
			System.out.println(i);
		}
	}

}
