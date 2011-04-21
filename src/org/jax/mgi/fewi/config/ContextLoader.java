package org.jax.mgi.fewi.config;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.jax.mgi.fewi.template.WebTemplate;
import org.jax.mgi.fewi.util.IDLinker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ServletContextAware;

@Component
public class ContextLoader implements ApplicationContextAware, ServletContextAware {
	
	private ApplicationContext ac;
	private ServletContext sc;
	
	private static Properties properties = null;
	private static Properties externalUrls = null;

	private static Configuration propertiesConfig = null;
	
	private Logger logger = LoggerFactory.getLogger(ContextLoader.class);

    @Autowired
    private WebTemplate webTemplate;


	@PostConstruct
    public void init() {
        sc.setAttribute("templateBean", webTemplate);
        logger.debug("configs loaded");
		if(ac.containsBean("configBean")){
			properties =  (Properties)ac.getBean("configBean");
		}
		if(ac.containsBean("externalUrls")){
			externalUrls =  (Properties)ac.getBean("externalUrls");
		}

		try {
			propertiesConfig = new PropertiesConfiguration((File) ac.getResource("fewi.properties").getFile());
		} catch (ConfigurationException e) {
			logger.error("Error with the configuration file.");
		} catch (IOException e) {
			logger.error("File not found.");
		}

    }
	
	public static Properties getConfigBean(){
		return properties;
	}
	
	public static Properties getExternalUrls(){
		return externalUrls;
	}

	public static Configuration getPropertiesConfig() {
		return propertiesConfig;
	}
	
	public static IDLinker getIDLinker(){
		return IDLinker.getInstance(externalUrls);
	}
	
	public void setApplicationContext(ApplicationContext ac)
			throws BeansException {
		this.ac = ac;
		
	}

	public void setServletContext(ServletContext sc) {
		this.sc = sc;
	}
}
