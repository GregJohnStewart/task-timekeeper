package com.gjs.taskTimekeeper.webServer.server.mongoEntities;

import io.quarkus.mongodb.panache.MongoEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@MongoEntity(collection="Users")
public class ManagerEntity extends OurMongoEntity {
    private static final Logger LOGGER = LoggerFactory.getLogger(ManagerEntity.class);

    private ObjectId userId;
    private byte[] timeManagerData;

    private Date lastUpdate;
}
