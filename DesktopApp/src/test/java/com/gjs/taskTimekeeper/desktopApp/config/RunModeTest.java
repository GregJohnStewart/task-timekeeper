package com.gjs.taskTimekeeper.desktopApp.config;

import static org.junit.Assert.*;

import com.gjs.taskTimekeeper.baseCode.managerIO.dataSource.DataSourceTest;
import org.junit.Test;

public class RunModeTest extends DataSourceTest {
    @Test
    public void test() {
        RunMode.values();
    }
}
