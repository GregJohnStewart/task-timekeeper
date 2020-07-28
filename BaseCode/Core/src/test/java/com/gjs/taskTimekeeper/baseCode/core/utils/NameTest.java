package com.gjs.taskTimekeeper.baseCode.core.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NameTest {
	
	// <editor-fold desc="Validator Tests">
	//TODO:: parameteritize these bad name tests
	@Test
	public void validateNameStringTestNull() {
		Assertions.assertThrows(IllegalArgumentException.class, ()->{
			Name.validateName(null);
		});
	}
	
	@Test
	public void validateNameStringTestEmpty() {
		Assertions.assertThrows(IllegalArgumentException.class, ()->{
			Name.validateName("");
		});
	}
	
	@Test
	public void validateNameStringTestWhitespace() {
		Assertions.assertThrows(IllegalArgumentException.class, ()->{
			Name.validateName("     \n\r");
		});
	}
	
	@Test
	public void validateNameStringTest() {
		assertEquals("n", Name.validateName("n"));
		assertEquals("n", Name.validateName(" n \t\n\r"));
		assertEquals("n a", Name.validateName(" n a \t\n\r"));
	}
	
	// </editor-fold>
	// <editor-fold desc="Serialization Tests">
	@Test
	public void serializationTest() throws IOException {
		Name testName = new Name("testName");
		
		String nameJson = ObjectMapperUtilities.getDefaultMapper().writeValueAsString(testName);
		
		Name otherTestName =
			ObjectMapperUtilities.getDefaultMapper().readValue(nameJson, Name.class);
		assertEquals(testName, otherTestName);
	}
	// </editor-fold>
}
