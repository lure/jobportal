package ru.shubert.jobportal.dao;

import com.mongodb.BasicDBList;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.data.mongodb.core.MongoTemplate;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Set;


public class MongoDataSeeder {

    private static final Logger LOGGER = LoggerFactory.getLogger(MongoDataSeeder.class);

    private MongoTemplate template;

    private String data;

    @Required
    public void setMongoTemplate(MongoTemplate template) {
        this.template = template;
    }

    @Required
    public void setData(String data) {
        this.data = data;
    }

    @PostConstruct
    public void init() throws IOException {
        deleteAllElements();
        DBObject dbObject = parseData(data);
        insertParsedData(dbObject);
    }


    private void deleteAllElements() {
        Set<String> collectionaNames = template.getCollectionNames();

        for (String collectionName : collectionaNames) {
            if (isNotASystemCollection(collectionName)) {
                LOGGER.debug("Dropping Collection {}.", collectionName);
                DBCollection dbCollection = template.getCollection(collectionName);
                dbCollection.drop();
            }
        }
    }

    private boolean isNotASystemCollection(String collectionName) {
        return !collectionName.startsWith("system");
    }

    private DBObject parseData(String jsonData) throws IOException {
        return (DBObject) JSON.parse(jsonData);
    }

    private void insertParsedData(DBObject parsedData) {
        Set<String> collectionaNames = parsedData.keySet();

        for (String collectionName : collectionaNames) {
            BasicDBList dataObjects = (BasicDBList) parsedData.get(collectionName);
            DBCollection dbCollection = template.getCollection(collectionName);
            for (Object dataObject : dataObjects) {
                LOGGER.debug("Inserting {} To {}.", dataObject, dbCollection.getName());
                dbCollection.insert((DBObject) dataObject);
            }

        }
    }

}
