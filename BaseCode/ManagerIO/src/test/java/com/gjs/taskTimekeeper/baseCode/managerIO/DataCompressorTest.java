package com.gjs.taskTimekeeper.baseCode.managerIO;

import com.gjs.taskTimekeeper.baseCode.managerIO.exception.ManagerCompressionException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public class DataCompressorTest {
	
	private static void assertCompressedData(byte[] expected, byte[] compressed) {
		assertArrayEquals(expected, DataCompressor.decompress(compressed));
	}
	
	@Test
	public void decompressEmptyArrayTest() {
		byte[] returned = DataCompressor.decompress(new byte[0]);
		assertArrayEquals(new byte[0], returned);
	}
	
	@Test
	public void decompressUncompressedTest() {
		byte[] returned = DataCompressor.decompress("Hello World".getBytes());
		assertArrayEquals("Hello World".getBytes(), returned);
	}
	
	@Test
	public void decompressBadCompressDataTest() {
		byte[] returned = DataCompressor.compress("Hello World".getBytes());
		returned[returned.length - 2] = 5;
		returned[returned.length - 3] = 4;
		returned[returned.length - 4] = 3;
		
		Assertions.assertThrows(ManagerCompressionException.class, ()->{
			DataCompressor.decompress(returned);
		});
	}
	
	@Test
	public void compressEmptyArrayTest() {
		byte[] returned = DataCompressor.compress(new byte[0]);
		log.info("compressed empty array (length: {}): {}", returned.length, returned);
		assertCompressedData(new byte[0], returned);
	}
	
	@Test
	public void compressTest() {
		byte[] testData = "hello world".getBytes();
		byte[] returned = DataCompressor.compress(testData);
		log.info("data array uncompressed (length: {}): {}", testData.length, testData);
		log.info("  compressed data array (length: {}): {}", returned.length, returned);
		assertCompressedData("hello world".getBytes(), returned);
	}
	
	@Test
	public void isCompressedTest() {
		assertFalse(DataCompressor.isDataCompressed("".getBytes()));
		assertFalse(DataCompressor.isDataCompressed("1".getBytes()));
		assertFalse(DataCompressor.isDataCompressed("12".getBytes()));
		assertFalse(DataCompressor.isDataCompressed("hello world".getBytes()));
		
		assertTrue(DataCompressor.isDataCompressed(DataCompressor.compress("".getBytes())));
		assertTrue(
			DataCompressor.isDataCompressed(DataCompressor.compress("hello world".getBytes())));
	}
}
