package com.gjs.taskTimekeeper.webServer.server.mongoEntities.pojo;

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
}
