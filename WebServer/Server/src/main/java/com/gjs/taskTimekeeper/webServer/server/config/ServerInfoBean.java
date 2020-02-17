package com.gjs.taskTimekeeper.webServer.server.config;

import com.gjs.taskTimekeeper.webServer.server.toMoveToLib.ServerInfo;
import io.quarkus.arc.config.ConfigProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.net.URL;
import java.util.Optional;

// TODO:: add contact info
@Data
@AllArgsConstructor
@NoArgsConstructor
@ConfigProperties(prefix = "runningInfo")
public class ServerInfoBean {
    @ConfigProperty(name = "organization")
    private Optional<String> organization;
    @ConfigProperty(name = "serverName")
    private Optional<String> serverName;
    @ConfigProperty(name = "url")
    private Optional<URL> url;
    @ConfigProperty(name = "contactInfo")
    private ContactInfo contactInfo;

    public ServerInfo toServerInfo() {
        ServerInfo info = new ServerInfo();

        info.setOrganization(this.getOrganization().orElse(""));
        info.setServerName(this.getServerName().orElse(""));
        URL url = this.getUrl().orElse(null);
        info.setUrl(url == null ? "" : url.toExternalForm());

        ServerInfo.ContactInfo contactInfo = new ServerInfo.ContactInfo();

        contactInfo.setName(this.contactInfo.getName().orElse(""));
        contactInfo.setEmail(this.contactInfo.getEmail().orElse(""));
        contactInfo.setPhone(this.contactInfo.getPhone().orElse(""));

        info.setContactInfo(contactInfo);

        return info;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ContactInfo {
        public Optional<String> name;
        public Optional<String> email;
        public Optional<String> phone;
    }
}
