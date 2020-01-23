package com.gjs.taskTimekeeper.webServer.server.mongoEntities.pojo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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

    public static void configureObjectMapper(ObjectMapper mapper){
        mapper.registerModule(new JavaTimeModule());
    }

    public static ObjectMapper getObjectMapper(){
        ObjectMapper mapper = new ObjectMapper();
        configureObjectMapper(new ObjectMapper());
        return mapper;
    }
}
