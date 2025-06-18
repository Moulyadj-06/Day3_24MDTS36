package org.example.student;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class MongoDBConnection {
    private static final String URI = "mongodb://localhost:27017";

    public static MongoDatabase getDatabase(String dbName) {
        MongoClient mongoClient = MongoClients.create(URI);
        return mongoClient.getDatabase(dbName);
    }
}
