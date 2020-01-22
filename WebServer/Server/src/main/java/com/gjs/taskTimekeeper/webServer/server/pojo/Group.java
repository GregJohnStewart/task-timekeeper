package com.gjs.taskTimekeeper.webServer.server.pojo;

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
public class Group extends MongoObject {
    private ObjectId _id;
    private String name;
    private ZonedDateTime creationTime;
    private ObjectId pointOfContact;
}
