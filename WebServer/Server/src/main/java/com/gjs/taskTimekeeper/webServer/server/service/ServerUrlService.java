package com.gjs.taskTimekeeper.webServer.server.service;

import com.gjs.taskTimekeeper.webServer.server.config.ServerInfoBean;

import javax.enterprise.context.ApplicationScoped;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * TODO:: test
 */
@ApplicationScoped
public class ServerUrlService {
	
	private final ServerInfoBean serverInfoBean;
	
	public ServerUrlService(
		ServerInfoBean serverInfoBean
	) {
		this.serverInfoBean = serverInfoBean;
	}
	
	/**
	 * TODO:: remove malformed url throws
	 *
	 * @return
	 * @throws MalformedURLException
	 */
	public URL getBaseServerUrl() throws MalformedURLException {
		//TODO:: http/s selection
		return new URL(
			"http://" +
				this.serverInfoBean.getHostname() +
				":" +
				this.serverInfoBean.getPort()
		);
	}
}
