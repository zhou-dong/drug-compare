package edu.troy.cs.bio;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

public class TsvToMongo {

	private static String path = "/home/hadoop/Downloads/chemicals.v4.0.tsv";

	static List<Document> mongoData = new ArrayList<Document>();
	static int writeCount = 0;
	static int flushSize = 50;

	public static void execute() throws IOException {
		BufferedReader tsvFile = new BufferedReader(new FileReader(path));
		String line = tsvFile.readLine();
		// ignore first line
		line = tsvFile.readLine();
		while (line != null) {
			String[] datas = line.split("\t");

			Document doc = new Document();
			doc.append("CID", datas[0]);
			doc.append("SMILES", datas[3]);

			mongoData.add(doc);
			bulkWrite();
			line = tsvFile.readLine();
		}
		closeIO(tsvFile);

		System.out.println("write from tsv to mongo done!");
	}

	private static void bulkWrite() {
		if (mongoData.size() >= flushSize) {
			System.out.println(writeCount * flushSize);
			MongoDB.sequenceCollection().insertMany(mongoData);
			mongoData = new ArrayList<Document>();
			writeCount++;
		}
	}

	private static void closeIO(Closeable closeable) {
		if (closeable != null) {
			try {
				closeable.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String args[]) throws IOException {
		execute();
	}

}
