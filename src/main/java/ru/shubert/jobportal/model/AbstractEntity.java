package ru.shubert.jobportal.model;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

/**
 * User: user
 * Date: 06.07.12 20:56
 */
@Document
public class AbstractEntity  implements Serializable {
    public static int LONG_STRING = 150;
    public static int SHORT_STRING = 50;
    public static int SMALL_STRING = 50;

    @Id
    private ObjectId id;

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public boolean isPersisted(){
        return id != null;
    }
}
