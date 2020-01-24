package com.gjs.taskTimekeeper.webServer.server.mongoEntities.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationSettings {
    private boolean notifyByEmail;
}
