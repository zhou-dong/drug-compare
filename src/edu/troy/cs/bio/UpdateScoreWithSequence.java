package edu.troy.cs.bio;

import static com.mongodb.client.model.Filters.eq;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.mongodb.client.MongoCursor;

public class UpdateScoreWithSequence {

	static List<Document> scores = new ArrayList<Document>();
	static List<Document> blukWriteScores = new ArrayList<Document>();
	static int flushSize = 50;
	static int writeCount = 0;
	static int fetchCount = 0;

	public static void execute() {
		MongoCursor<Document> cursor = MongoDB.scoreCollection().find().iterator();
		try {
			while (cursor.hasNext()) {
				Document document = cursor.next();
				scores.add(document);
				printFetchCount();
			}
		} finally {
			cursor.close();
		}
	}

	private static void printFetchCount() {
		fetchCount++;
		if (fetchCount % 1000 == 0) {
			System.out.println(fetchCount);
		}
	}

	private static void updateScores() {
		for (Document document : scores) {

			String cid1 = document.getString("CID_1");
			String cid2 = document.getString("CID_2");

			String seq1 = getSequence(cid1);
			String seq2 = getSequence(cid2);

			document.append("SEQ_1", seq1);
			document.append("SEQ_2", seq2);

			blukWriteScores.add(document);
			bulkWrite();
		}
	}

	private static void bulkWrite() {
		if (blukWriteScores.size() >= flushSize) {
			System.out.println(writeCount * flushSize);
			MongoDB.resultCollection().insertMany(blukWriteScores);
			blukWriteScores = new ArrayList<Document>();
			writeCount++;
		}
	}

	private static String getSequence(String cid) {
		Document doc = MongoDB.sequenceCollection().find(eq("CID", cid)).first();
		if (doc == null) {
			return "";
		} else {
			return doc.getString("SMILES");
		}

	}

	public static void main(String[] args) {
		execute();
		System.out.print("loading all scores data finish");
		updateScores();
		System.out.print("update score finish");
	}
}
