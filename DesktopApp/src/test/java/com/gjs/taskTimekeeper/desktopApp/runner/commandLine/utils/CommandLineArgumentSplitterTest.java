package com.gjs.taskTimekeeper.desktopApp.runner.commandLine.utils;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collection;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class CommandLineArgumentSplitterTest {
    private final String toParse;
    private final String[] expected;

    public CommandLineArgumentSplitterTest(String toParse, String[] expected) {
        this.toParse = toParse;
        this.expected = expected;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(
                new Object[][] {
                    {"", new String[] {""}},
                    {"hello world", new String[] {"hello", "world"}},
                    {"hello\\ world", new String[] {"hello world"}},
                    {"hello\tworld", new String[] {"hello", "world"}},
                });
    }

    @Test
    public void split() {
        assertArrayEquals(this.expected, CommandLineArgumentSplitter.split(this.toParse));
    }
}
