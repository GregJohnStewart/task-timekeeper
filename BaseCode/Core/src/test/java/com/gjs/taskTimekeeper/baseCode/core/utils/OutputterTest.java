package com.gjs.taskTimekeeper.baseCode.core.utils;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class OutputterTest {
	private ByteArrayOutputStream testNormStream = new ByteArrayOutputStream();
	private ByteArrayOutputStream testErrorStream = new ByteArrayOutputStream();
	
	// <editor-fold desc="Stream setting tests">
	@Test
	public void testSetNormStream() {
		Outputter outputter = new Outputter(this.testNormStream, this.testErrorStream);
		
		outputter.normPrint(OutputLevel.DEFAULT, "hello World");
		
		assertEquals("hello World", testNormStream.toString());
		assertEquals("", testErrorStream.toString());
	}
	
	@Test
	public void testSetErrStream() {
		Outputter outputter = new Outputter(this.testNormStream, this.testErrorStream);
		
		outputter.errorPrint("hello World");
		
		assertEquals("hello World", testErrorStream.toString());
		assertEquals("", testNormStream.toString());
	}
	
	@Test
	public void testSetNullNormStream() {
		Outputter outputter = new Outputter(null, this.testErrorStream);
		
		outputter.normPrint(OutputLevel.DEFAULT, "hello World");
		
		assertEquals("", testNormStream.toString());
		assertEquals("", testErrorStream.toString());
	}
	
	@Test
	public void testSetNullErrStream() {
		Outputter outputter = new Outputter(this.testNormStream, null);
		
		outputter.errorPrint("hello World");
		
		assertEquals("", testNormStream.toString());
		assertEquals("", testErrorStream.toString());
	}
	// </editor-fold>
	
	// <editor-fold desc="Can Output tests">
	@Test
	public void cantSetNullOutputLevel() {
		Assertions.assertThrows(IllegalArgumentException.class, ()->{
			new Outputter(null, null).setOutputLevelThreshold(null);
		});
	}
	
	@Test
	public void canOutputNormTest() {
		Outputter outputter =
			new Outputter(OutputLevel.DEFAULT, this.testNormStream, this.testErrorStream);
		assertTrue(outputter.canOutput(OutputLevel.DEFAULT));
		assertFalse(outputter.canOutput(OutputLevel.VERBOSE));
		
		outputter = new Outputter(OutputLevel.VERBOSE, this.testNormStream, this.testErrorStream);
		assertTrue(outputter.canOutput(OutputLevel.DEFAULT));
		assertTrue(outputter.canOutput(OutputLevel.VERBOSE));
		
		outputter = new Outputter(OutputLevel.NONE, this.testNormStream, this.testErrorStream);
		assertFalse(outputter.canOutput(OutputLevel.DEFAULT));
		assertFalse(outputter.canOutput(OutputLevel.VERBOSE));
		
		outputter = new Outputter(OutputLevel.VERBOSE, null, this.testErrorStream);
		assertFalse(outputter.canOutput(OutputLevel.DEFAULT));
		assertFalse(outputter.canOutput(OutputLevel.VERBOSE));
	}
	
	@Test
	public void canOutputErrTest() {
		Outputter outputter =
			new Outputter(OutputLevel.DEFAULT, this.testNormStream, this.testErrorStream);
		assertTrue(outputter.canOutputErr());
		
		outputter = new Outputter(OutputLevel.NONE, this.testNormStream, this.testErrorStream);
		assertFalse(outputter.canOutputErr());
		
		outputter = new Outputter(OutputLevel.DEFAULT, this.testNormStream, null);
		assertFalse(outputter.canOutputErr());
	}
	// </editor-fold>
	
	// <editor-fold desc="Can Output tests">
	@Test
	public void canGetDefault() {
		Outputter def = Outputter.getDefaultOutputter();
		assertNotNull(def);
	}
	// </editor-fold>
}
