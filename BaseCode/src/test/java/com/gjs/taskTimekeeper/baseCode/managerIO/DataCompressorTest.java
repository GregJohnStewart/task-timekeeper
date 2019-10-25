package com.gjs.taskTimekeeper.baseCode.managerIO;

import static org.junit.Assert.assertArrayEquals;

import com.gjs.taskTimekeeper.baseCode.managerIO.exception.ManagerCompressionException;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataCompressorTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataCompressorTest.class);

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

    @Test(expected = ManagerCompressionException.class)
    public void decompressBadCompressDataTest() {
        byte[] returned = DataCompressor.compress("Hello World".getBytes());
        returned[returned.length - 2] = 5;
        returned[returned.length - 3] = 4;
        returned[returned.length - 4] = 3;
        DataCompressor.decompress(returned);
    }

    @Test
    public void compressEmptyArrayTest() {
        byte[] returned = DataCompressor.compress(new byte[0]);
        LOGGER.info("compressed empty array (length: {}): {}", returned.length, returned);
        assertCompressedData(new byte[0], returned);
    }

    @Test
    public void compressTest() {
        byte[] testData = "hello world".getBytes();
        byte[] returned = DataCompressor.compress(testData);
        LOGGER.info("data array uncompressed (length: {}): {}", testData.length, testData);
        LOGGER.info("  compressed data array (length: {}): {}", returned.length, returned);
        assertCompressedData("hello world".getBytes(), returned);
    }
}
