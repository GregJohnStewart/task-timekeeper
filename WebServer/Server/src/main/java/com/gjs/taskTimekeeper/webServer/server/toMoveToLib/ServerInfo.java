package com.gjs.taskTimekeeper.webServer.server.toMoveToLib;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServerInfo {
    private String organization;
    private String serverName;
    private String url;
    private ContactInfo contactInfo;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ContactInfo{
        public String name;
        public String email;
        public String phone;
    }
}
