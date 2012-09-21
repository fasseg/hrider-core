package eu.scapeproject.hrider.servlet;

import java.io.IOException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import eu.scapeproject.hrider.service.storage.StorageService;

@Component
public class HRiderContextListener implements ServletContextListener {

	private static final Logger LOG = LoggerFactory.getLogger(HRiderContextListener.class);

	@Autowired
	@Qualifier("storageService")
	private StorageService storage;

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		LOG.debug("destroying HRider context at " + arg0.getServletContext().getServletContextName());
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		LOG.debug("initializing HRider context at " + arg0.getServletContext().getServletContextName());
	}
}
