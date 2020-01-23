package com.gjs.taskTimekeeper.webServer.server.mongoEntities.pojo;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor

public enum MembershipLevel {
    MEMBER(0),
    ADMIN_VIEW(1),
    ADMIN_EDIT(2),
    ADMIN_GROUP(3);

    @Getter
    private int levelValue;

    public static void configureObjectMapper(ObjectMapper mapper){
        //nothing to do
    }

    public static ObjectMapper getObjectMapper(){
        ObjectMapper mapper = new ObjectMapper();
        configureObjectMapper(new ObjectMapper());
        return mapper;
    }
}
