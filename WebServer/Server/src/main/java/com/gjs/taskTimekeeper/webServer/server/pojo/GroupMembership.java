package com.gjs.taskTimekeeper.webServer.server.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupMembership {
    private ObjectId groupId;
    private MembershipLevel level;
}
