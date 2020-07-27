package com.gjs.taskTimekeeper.baseCode.managerIO.dataSource;

import com.gjs.taskTimekeeper.baseCode.managerIO.dataSource.exception.DataSourceConnectionException;
import com.gjs.taskTimekeeper.baseCode.managerIO.dataSource.exception.DataSourceCredentialException;
import com.gjs.taskTimekeeper.baseCode.managerIO.dataSource.exception.DataSourceException;
import com.gjs.taskTimekeeper.baseCode.managerIO.dataSource.exception.DataSourceNotFoundException;
import com.gjs.taskTimekeeper.baseCode.managerIO.dataSource.exception.DataSourceReadOnlyException;
import com.gjs.taskTimekeeper.baseCode.managerIO.exception.ManagerIOException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Data source for connecting to an ftp client.
 *
 * <p>TODO:: switch to use apache ftp client.
 *
 * <p>https://www.baeldung.com/java-ftp-client
 */
public class FtpDataSource extends DataSource {
	
	private final URL ftpUrl;
	
	public FtpDataSource(URL ftpUrl) {
		this.ftpUrl = ftpUrl;
	}
	
	private ManagerIOException handleException(Exception e) {
		if(e.getClass().getName().equals("sun.net.ftp.FtpLoginException")) {
			return new DataSourceCredentialException(e);
		} else if(e.getMessage().contains("The current user does not have")
			&& e.getMessage().contains("permission")) {
			return new DataSourceReadOnlyException(e);
		} else if(e.getClass().getName().equals("java.io.FileNotFoundException")) {
			return new DataSourceNotFoundException(e);
		}
		return new DataSourceException(e);
	}
	
	@Override
	public byte[] readDataIn() throws DataSourceException {
		URLConnection urlConnection = this.getUrlConnection();
		try(InputStream is = urlConnection.getInputStream();) {
			byte[] output = new byte[is.available()];
			is.read(output);
			return output;
		} catch(IOException e) {
			throw handleException(e);
		}
	}
	
	@Override
	public void writeDataOut(byte[] bytes) throws DataSourceException {
		URLConnection urlConnection = this.getUrlConnection();
		try(OutputStream outputStream = urlConnection.getOutputStream();) {
			outputStream.write(bytes);
		} catch(IOException e) {
			throw handleException(e);
		}
	}
	
	/**
	 * Determines if readonly by attempting to rewrite the data that exists. A lot of work but base
	 * FtpUrlConnection is really limited in functionality.
	 *
	 * @return
	 * @throws DataSourceReadOnlyException If the location is read only and does not exist.
	 * @throws DataSourceException         If anything else goes wrong with talking to the ftp server.
	 */
	@Override
	public boolean isReadOnly() throws DataSourceReadOnlyException, DataSourceException {
		byte[] bytes = new byte[0];
		try {
			bytes = this.readDataIn();
		} catch(DataSourceNotFoundException e) {
			// nothing to do
		}
		
		try {
			this.writeDataOut(bytes);
		} catch(DataSourceReadOnlyException e) {
			return true;
		} catch(DataSourceException e) {
			throw e;
		}
		return false;
	}
	
	private URLConnection getUrlConnection() throws DataSourceConnectionException {
		try {
			return this.ftpUrl.openConnection();
		} catch(IOException e) {
			throw new DataSourceConnectionException(e);
		}
	}
}
