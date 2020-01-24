package com.gjs.taskTimekeeper.webServer.server.mongoEntities.pojo;

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
}
