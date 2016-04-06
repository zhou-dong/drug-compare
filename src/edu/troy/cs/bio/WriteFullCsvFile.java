package edu.troy.cs.bio;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.mongodb.client.MongoCursor;

public class WriteFullCsvFile {

	static String filePath = "/home/hadoop/Downloads/full/";

	static List<Document> results = new ArrayList<Document>();

	static int countError = 0;

	static int fileSize = 20000;
	static int index = 0;

	static int writeIndex = 0;
	static int bulkWriteSize = 500;

	static FileWriter writer = null;

	public static void writeToFiles() throws IOException {
		MongoCursor<Document> cursor = MongoDB.resultCollection().find().iterator();
		try {
			while (cursor.hasNext()) {
				Document document = cursor.next();
				results.add(document);
				index++;
				if (results.size() >= fileSize) {
					write(index / fileSize);
					results = new ArrayList<Document>();
				}
			}
		} finally {
			cursor.close();
		}
	}

	private static void write(int filenumber) throws IOException {
		writer = new FileWriter(filePath + "drug-" + filenumber + ".csv");
		wrireCsvFileOneLine("id", "smile1", "smile2", "cid1", "cid2", "tmScore", "fpScore");
		for (Document doc : results) {
			String id = doc.get("_id").toString();
			String seq1 = doc.getString("SEQ_1");
			String seq2 = doc.getString("SEQ_2");
			String cid1 = doc.getString("CID_1");
			String cid2 = doc.getString("CID_2");
			double tmScore = 0d;
			double fpScore = 0d;
			try {
				tmScore = doc.getDouble("tmScore");
				fpScore = doc.getDouble("fpScore");
			} catch (Exception e) {
				System.out.println(e);
				System.out.println(doc);
				countError++;
				continue;
			}
			wrireCsvFileOneLine(id, seq1, seq2, cid1, cid2, tmScore + "", fpScore + "");
			writeIndex++;
			bulkWrite();
		}
		writer.close();
	}

	private static void wrireCsvFileOneLine(String id, String smile1, String smile2, String cid1, String cid2,
			String tmScore, String fpScore) throws IOException {
		writer.append(id);
		writer.append(',');
		writer.append(smile1);
		writer.append(',');
		writer.append(smile2);
		writer.append(',');
		writer.append(cid1);
		writer.append(',');
		writer.append(cid2);
		writer.append(',');
		writer.append(tmScore);
		writer.append(',');
		writer.append(fpScore);
		writer.append('\n');
	}

	private static void bulkWrite() throws IOException {
		if (writeIndex >= bulkWriteSize) {
			writer.flush();
			writeIndex = 0;
		}
	}

	public static void main(String args[]) throws IOException {
		writeToFiles();
		System.out.println(countError);
	}
}
