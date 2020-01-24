package com.gjs.taskTimekeeper.webServer.server.mongoEntities;

import io.quarkus.mongodb.panache.MongoEntity;
import io.quarkus.mongodb.panache.PanacheMongoEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@MongoEntity(collection="Groups")
public class Group extends PanacheMongoEntity {
    private ObjectId _id;
    private String name;
    private ZonedDateTime creationTime;
    private ObjectId pointOfContact;
}
