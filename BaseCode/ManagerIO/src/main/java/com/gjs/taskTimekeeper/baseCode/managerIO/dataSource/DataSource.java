package com.gjs.taskTimekeeper.baseCode.managerIO.dataSource;

import com.gjs.taskTimekeeper.baseCode.managerIO.dataSource.exception.DataSourceException;
import com.gjs.taskTimekeeper.baseCode.managerIO.dataSource.exception.DataSourceParsingException;
import com.gjs.taskTimekeeper.baseCode.managerIO.dataSource.exception.DataSourceReadOnlyException;
import lombok.extern.slf4j.Slf4j;

import java.net.MalformedURLException;
import java.net.URL;

@Slf4j
public abstract class DataSource {
	
	/**
	 * Parsed a data source from the string given. https://www.baeldung.com/java-url-vs-uri
	 *
	 * @param source The source url given
	 * @return The data source made given the url.
	 * @throws DataSourceParsingException If something went wrong parsing the exception
	 */
	public static DataSource fromString(String source) throws DataSourceParsingException {
		log.info("Parsing data source.");
		log.debug("String to parse data source from: {}", source);
		
		URL sourceUrl;
		try {
			sourceUrl = new URL(source);
		} catch(MalformedURLException e) {
			throw new DataSourceParsingException("Bad source string given.", e);
		}
		
		DataSource output;
		switch(sourceUrl.getProtocol()) {
		case "file":
			output = new FileDataSource(sourceUrl);
			break;
		case "ftp":
			output = new FtpDataSource(sourceUrl);
			break;
		default:
			throw new DataSourceParsingException("Unsupported source type given.");
		}
		
		return output;
	}
	
	/**
	 * Gets the input stream for retrieving a TimeManager
	 *
	 * @return The input stream for reading in a time manager.
	 * @throws DataSourceException If something went wrong in reading the data. Can differ between
	 *                             implementations.
	 */
	public abstract byte[] readDataIn() throws DataSourceException;
	
	/**
	 * Gets the output stream for saving a TimeManager that must be closed.
	 *
	 * @param bytes The set of bytes to save to the data source
	 * @throws DataSourceException         If something went wrong in writing the data out. Can differ
	 *                                     between implementations.
	 * @throws DataSourceReadOnlyException If the source was readonly when tried to write.
	 */
	public abstract void writeDataOut(byte[] bytes)
		throws DataSourceException, DataSourceReadOnlyException;
	
	/**
	 * Determines if the data source is read only or not.
	 *
	 * @return If the data source is read only or not
	 * @throws DataSourceException If something went wrong in determining writability. Can differ
	 *                             between implementations.
	 */
	public abstract boolean isReadOnly() throws DataSourceException;
	
	/**
	 * Ensures that the data source can properly read/write the manager data.
	 *
	 * @throws DataSourceReadOnlyException If the data source is read only.
	 * @throws DataSourceException         If the data source can't be read, or something went wrong in
	 *                                     determining the read/writability. Can differ between implementations.
	 */
	public void ensureReadWriteCapable() throws DataSourceException, DataSourceReadOnlyException {
		if(this.isReadOnly()) {
			throw new DataSourceReadOnlyException();
		}
	}
}
