package com.hypersocket.client.rmi;

import java.awt.Desktop;
import java.io.Serializable;
import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class BrowserLauncher implements ResourceLauncher, Serializable {

	private static Logger log = LoggerFactory.getLogger(BrowserLauncher.class);
	
	private static final long serialVersionUID = 7508038649288643559L;

	String launchUrl;
	
	public BrowserLauncher(String launchUrl) {
		this.launchUrl = launchUrl;
	}

	@Override
	public void launch() {
		
		if(log.isInfoEnabled()) {
			log.info("Launching website " + launchUrl);
		}
		
		Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
	    if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
	        try {
	            desktop.browse(new URI(launchUrl));
	        } catch (Exception e) {
	           log.error("Failed to launch website", e);
	        }
	    }
	}

}