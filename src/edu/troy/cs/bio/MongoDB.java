package edu.troy.cs.bio;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class MongoDB {

	private static final String DB_NAME = "drug";
	private static final String SCORE_COLLECTION = "score";
	private static final String SEQUENCE_COLLECTION = "sequence";
	private static final String RESULT_COLLECTION = "result";

	static MongoClient mongoClient = null;
	static MongoDatabase database = null;

	static {
		mongoClient = new MongoClient();
		database = mongoClient.getDatabase(DB_NAME);
	}

	public static void createIndexForSequence() {
		MongoDB.sequenceCollection().createIndex(new BasicDBObject("CID", 1));
	}

	public static MongoCollection<Document> resultCollection() {
		return database.getCollection(RESULT_COLLECTION);
	}

	public static MongoCollection<Document> scoreCollection() {
		return database.getCollection(SCORE_COLLECTION);
	}

	public static MongoCollection<Document> sequenceCollection() {
		return database.getCollection(SEQUENCE_COLLECTION);
	}

	public static void main(String args[]) {
		createIndexForSequence();
	}

}
