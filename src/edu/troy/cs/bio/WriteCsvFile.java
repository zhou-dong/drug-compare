package edu.troy.cs.bio;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.mongodb.client.MongoCursor;

public class WriteCsvFile {

	static String filePath = "/home/hadoop/Downloads/";

	static List<Document> results = new ArrayList<Document>();

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
		wrireCsvFileOneLine("id", "smile1", "smile2");
		for (Document doc : results) {
			String id = doc.get("_id").toString();
			String seq1 = doc.getString("SEQ_1");
			String seq2 = doc.getString("SEQ_2");
			wrireCsvFileOneLine(id, seq1, seq2);
			writeIndex++;
			bulkWrite();
		}
		writer.close();
	}

	private static void wrireCsvFileOneLine(String id, String smile1, String smile2) throws IOException {
		writer.append(id);
		writer.append(',');
		writer.append(smile1);
		writer.append(',');
		writer.append(smile2);
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
	}
}
