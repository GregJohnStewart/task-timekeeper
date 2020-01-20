package com.gjs.taskTimekeeper.webServer.server.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Group implements MongoObject {
    private ObjectId id;
    private String name;
    private ZonedDateTime joinDateTime;
    private ZonedDateTime lastLogin;
}
