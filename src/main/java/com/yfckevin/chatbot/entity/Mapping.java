package com.yfckevin.chatbot.entity;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.neo4j.core.schema.Id;

@Document(collection = "mapping")
public class Mapping {
    @Id
    private String id;
    private String dbUri;
    private String collectionName;
    private String mappedId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDbUri() {
        return dbUri;
    }

    public void setDbUri(String dbUri) {
        this.dbUri = dbUri;
    }

    public String getCollectionName() {
        return collectionName;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }

    public String getMappedId() {
        return mappedId;
    }

    public void setMappedId(String mappedId) {
        this.mappedId = mappedId;
    }
}
