package com.gjs.taskTimekeeper.desktopApp.runner.commandLine.utils;

public class CommandLineArgumentSplitter {
    /**
     * Splits a spring by whitespace similar to how an argument string is parsed into an array form
     * a terminal
     *
     * <p>Ignores escaped spaces for the initial split and removes those escapes in resulting
     * entries.
     *
     * @param inputString The string to parse
     * @return The split string
     */
    public static String[] split(String inputString) {
        String[] inputs = inputString.split("(?<!\\\\)\\s+");
        
        for(int i = 0; i < inputs.length; i++) {
            inputs[i] = inputs[i].replace("\\ ", " ");
        }
        
        return inputs;
    }
}
