package com.gjs.taskTimekeeper.webServer.webLibrary;

import com.gjs.taskTimekeeper.webServer.webLibrary.pojo.user.UserLevel;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.InvocationTargetException;
import java.util.stream.Stream;

public class EnumTest {
    
    public static Stream<Arguments> getEnums() {
        return Stream.of(
            Arguments.of(
                UserLevel.class
            )
        );
    }
    
    @ParameterizedTest
    @MethodSource("getEnums")
    public void superficialEnumCodeCoverage(Class<? extends Enum<?>> enumClass)
        throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        for(Object o : (Object[])enumClass.getMethod("values").invoke(null)) {
            enumClass.getMethod("valueOf", String.class).invoke(null, o.toString());
        }
    }
}