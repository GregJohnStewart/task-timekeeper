package com.gjs.taskTimekeeper.webServer.server.validation;

import org.apache.commons.text.StringEscapeUtils;

public class StringValidator extends Validator<String> {

    @Override
    protected String sanitize(String object) {
        return StringEscapeUtils.escapeHtml4(object);
    }

    @Override
    protected void validate(String object) {

    }
}
