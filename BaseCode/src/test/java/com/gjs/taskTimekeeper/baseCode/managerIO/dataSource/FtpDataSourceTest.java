package com.gjs.taskTimekeeper.baseCode.managerIO.dataSource;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

import com.gjs.taskTimekeeper.baseCode.managerIO.dataSource.exception.DataSourceCredentialException;
import com.gjs.taskTimekeeper.baseCode.managerIO.dataSource.exception.DataSourceReadOnlyException;
import java.net.MalformedURLException;
import java.net.URL;
import org.junit.Assert;
import org.junit.Test;
import org.mockftpserver.fake.FakeFtpServer;
import org.mockftpserver.fake.UserAccount;
import org.mockftpserver.fake.filesystem.DirectoryEntry;
import org.mockftpserver.fake.filesystem.FileEntry;
import org.mockftpserver.fake.filesystem.FileSystem;
import org.mockftpserver.fake.filesystem.Permissions;
import org.mockftpserver.fake.filesystem.UnixFakeFileSystem;

public class FtpDataSourceTest extends DataSourceTest<FtpDataSource> {
    private static final String USERNAME = "user";
    private static final String PASSWORD = "password";
    private static final byte[] INITIAL_SAVE_DATA = "abcdef 1234567890".getBytes();
    private static final byte[] INITIAL_SAVE_DATA_TWO =
            "abcdef whhhooooaoaaahhh 1234567890".getBytes();

    private final FakeFtpServer fakeFtpServer = new FakeFtpServer();

    {
        fakeFtpServer.addUserAccount(new UserAccount(USERNAME, PASSWORD, "/data"));

        Permissions readOnly = new Permissions("r--r--r--");

        FileSystem fileSystem = new UnixFakeFileSystem();
        fileSystem.add(new DirectoryEntry("/data"));

        fileSystem.add(new FileEntry("/data/save", new String(INITIAL_SAVE_DATA)));

        FileEntry readOnlyFile = new FileEntry("/data/readOnly", new String(INITIAL_SAVE_DATA_TWO));
        readOnlyFile.setPermissions(readOnly);
        fileSystem.add(readOnlyFile);

        fakeFtpServer.setFileSystem(fileSystem);

        fakeFtpServer.setServerControlPort(0);
        fakeFtpServer.start();
    }

    private URL getUrl(String username, String password, String path) throws MalformedURLException {
        return new URL(
                "ftp://"
                        + username
                        + ":"
                        + password
                        + "@localhost:"
                        + fakeFtpServer.getServerControlPort()
                        + "/"
                        + path);
    }

    private URL getUrl(String path) throws MalformedURLException {
        return this.getUrl(USERNAME, PASSWORD, path);
    }

    private URL getUrl() throws MalformedURLException {
        return this.getUrl(USERNAME, PASSWORD, "save");
    }

    @Test
    public void testRead() throws MalformedURLException {
        URL url = getUrl();
        this.testSource = new FtpDataSource(url);

        byte[] data = this.testSource.readDataIn();

        assertArrayEquals(INITIAL_SAVE_DATA, data);
    }

    @Test
    public void testSave() throws MalformedURLException {
        URL url = getUrl();
        this.testSource = new FtpDataSource(url);

        this.testSource.writeDataOut(this.testData);

        byte[] data = this.testSource.readDataIn();
        assertArrayEquals(this.testData, data);
    }

    @Test(expected = DataSourceCredentialException.class)
    public void testReadBadCreds() throws MalformedURLException {
        URL url = getUrl("some", "rando", "/data/save");
        this.testSource = new FtpDataSource(url);

        this.testSource.readDataIn();
    }

    @Test
    public void testFileReadOnly() throws MalformedURLException {
        URL url = getUrl("readOnly");
        this.testSource = new FtpDataSource(url);

        byte[] data = this.testSource.readDataIn();

        assertArrayEquals(INITIAL_SAVE_DATA_TWO, data);

        assertTrue(this.testSource.isReadOnly());

        try {
            this.testSource.writeDataOut(INITIAL_SAVE_DATA);
            Assert.fail();
        } catch (DataSourceReadOnlyException e) {
            // nothing to test
        }
    }

    public void testNoFileFound() {
        // TODO
    }

    public void testNoReadPerms() {
        // TODO
    }
}
