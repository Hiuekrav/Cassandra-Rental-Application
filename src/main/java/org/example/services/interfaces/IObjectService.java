package org.example.services.interfaces;
import com.mongodb.client.MongoClient;

public interface IObjectService {

    void initDatabaseConnection();

    MongoClient getClient();
}
