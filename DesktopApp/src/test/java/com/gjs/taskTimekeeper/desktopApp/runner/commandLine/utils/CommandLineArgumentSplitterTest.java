package com.gjs.taskTimekeeper.desktopApp.runner.commandLine.utils;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class CommandLineArgumentSplitterTest {
    
    public static Collection<Object[]> data() {
        return Arrays.asList(
            new Object[][]{
                {"", new String[]{""}},
                {"hello world", new String[]{"hello", "world"}},
                {"hello\\ world", new String[]{"hello world"}},
                {"hello\tworld", new String[]{"hello", "world"}},
                });
    }
    
    @ParameterizedTest
    @MethodSource("data")
    public void split(String toParse, String[] expected) {
        assertArrayEquals(expected, CommandLineArgumentSplitter.split(toParse));
    }
}
