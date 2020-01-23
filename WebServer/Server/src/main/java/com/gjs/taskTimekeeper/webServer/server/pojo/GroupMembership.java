package com.gjs.taskTimekeeper.webServer.server.pojo;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupMembership {
    private ObjectId groupId;
    private List<MembershipLevel> level;
    private NotificationSettings notificationSettings;

    public static void configureObjectMapper(ObjectMapper mapper){
        MembershipLevel.configureObjectMapper(mapper);
        NotificationSettings.configureObjectMapper(mapper);
    }

    public static ObjectMapper getObjectMapper(){
        ObjectMapper mapper = new ObjectMapper();
        configureObjectMapper(new ObjectMapper());
        return mapper;
    }
}
