package com.gjs.taskTimekeeper.baseCode.managerIO;

import com.gjs.taskTimekeeper.baseCode.managerIO.exception.ManagerCompressionException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DataCompressor {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataCompressor.class);

    /**
     * Attempts to decompress the data compressed by {@link #compress(byte[])}. Does not decompress
     * uncompressed data.
     *
     * @param bytes The bytes to decompress
     * @return The decompressed data
     * @throws ManagerCompressionException If something went wrong in the decompression.
     */
    public static byte[] decompress(byte[] bytes) throws ManagerCompressionException {
        // TODO: use the following to use GZIP_MAGIC:
        // https://stackoverflow.com/questions/2383265/convert-4-bytes-to-int
        // GZIPInputStream.GZIP_MAGIC != Ints.fromBytes((byte) 0, (byte) 0, signature[1],
        // signature[0])
        // don't decompress uncompressed data
        if (bytes.length == 0
                || bytes[0] != (byte) 0x1f
                || bytes[1] != (byte) 0x8b) { // check if matches standard gzip magic number
            return bytes;
        }

        byte[] buffer = new byte[1024];
        try (InputStream inputStream = new ByteArrayInputStream(bytes);
                GZIPInputStream gzipper = new GZIPInputStream(inputStream);
                ByteArrayOutputStream out = new ByteArrayOutputStream(); ) {
            int len;
            while ((len = gzipper.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }

            gzipper.close();
            out.close();
            return out.toByteArray();
        } catch (IOException e) {
            LOGGER.error("Error occurred while decompressing data: ", e);
            throw new ManagerCompressionException(e);
        }
    }

    public static byte[] compress(byte[] bytes) throws ManagerCompressionException {
        byte[] compressed;
        try (ByteArrayOutputStream byteStream = new ByteArrayOutputStream(bytes.length);
                GZIPOutputStream zipStream = new GZIPOutputStream(byteStream); ) {
            zipStream.write(bytes);
            byteStream.close();
            zipStream.close();
            compressed = byteStream.toByteArray();
        } catch (IOException e) {
            LOGGER.error("Error occurred while compressing data: ", e);
            throw new ManagerCompressionException(e);
        }

        return compressed;
    }
}
