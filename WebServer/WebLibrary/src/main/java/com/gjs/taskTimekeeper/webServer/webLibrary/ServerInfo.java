package com.gjs.taskTimekeeper.webServer.webLibrary;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServerInfo {
    private String organization;
    private String orgUrl;
    private String serverName;
    private ContactInfo contactInfo;
    //TODO:: hostname/port to use to connect to service
    private String hostname;
    private int port;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ContactInfo{
        public String name;
        public String email;
        public String phone;
    }
}
