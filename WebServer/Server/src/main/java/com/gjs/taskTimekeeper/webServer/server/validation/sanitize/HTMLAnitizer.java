package com.gjs.taskTimekeeper.webServer.server.validation.sanitize;

import org.apache.commons.text.StringEscapeUtils;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class HTMLAnitizer extends Anitizer<String> {
    @Override
    public String deSanitize(String object) {
        return StringEscapeUtils.unescapeHtml4(object);
    }
    
    @Override
    public String sanitize(String object) {
        return StringEscapeUtils.escapeHtml4(object);
    }
}
