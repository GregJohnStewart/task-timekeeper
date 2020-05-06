package com.gjs.taskTimekeeper.webServer.webLibrary;

import com.gjs.taskTimekeeper.webServer.webLibrary.user.UserLevel;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

public class EnumTest {

    public static Stream<Arguments> getEnums(){
        return Stream.of(
                Arguments.of(
                        UserLevel.class
                )
        );
    }

    @ParameterizedTest
    @MethodSource("getEnums")
    public static void superficialEnumCodeCoverage(Class<? extends Enum<?>> enumClass) {
        try {
            for (Object o : (Object[])enumClass.getMethod("values").invoke(null)) {
                enumClass.getMethod("valueOf", String.class).invoke(null, o.toString());
            }
        }
        catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}