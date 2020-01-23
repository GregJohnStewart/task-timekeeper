package com.gjs.taskTimekeeper.webServer.server.pojo;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationSettings {
    private boolean notifyByEmail;

    public static void configureObjectMapper(ObjectMapper mapper){
        //nothing needs done
    }

    public static ObjectMapper getObjectMapper(){
        ObjectMapper mapper = new ObjectMapper();
        configureObjectMapper(new ObjectMapper());
        return mapper;
    }
}
