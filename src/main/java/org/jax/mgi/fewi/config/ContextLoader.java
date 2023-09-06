package org.jax.mgi.fewi.config;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
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
    
    private static String webInfPath = null;

    private final Logger logger = LoggerFactory.getLogger(ContextLoader.class);
    
	@Autowired
	ServletContext servletContext;

    @PostConstruct
    public void init() {
        System.out.println("---ContextLoader.init() PostConstruct");
        logger.debug("configs loaded");
        if(ac.containsBean("configBean")){
            properties =  (Properties)ac.getBean("configBean");
        }
        if(ac.containsBean("externalUrls")){
            externalUrls =  (Properties)ac.getBean("externalUrls");
        }

        try {
        	Configuration propertiesConfig = new CompositeConfiguration();
        	((CompositeConfiguration)propertiesConfig).addConfiguration(new PropertiesConfiguration(ac.getResource("fewi.properties").getFile()));
        	((CompositeConfiguration)propertiesConfig).addConfiguration(new PropertiesConfiguration(ac.getResource("common.fewi.properties").getFile()));
        	((CompositeConfiguration)propertiesConfig).addConfiguration(new PropertiesConfiguration(ac.getResource("common.solr.properties").getFile()));
        	((CompositeConfiguration)propertiesConfig).addConfiguration(new PropertiesConfiguration(ac.getResource("common.es.properties").getFile()));
        	
        	for (@SuppressWarnings("unchecked") Iterator<String> i = propertiesConfig.getKeys(); i.hasNext();)
        	{
                String key = i.next();
                String value = (String) propertiesConfig.getProperty(key);
                properties.setProperty(key,value);
        	}
        } catch (ConfigurationException e) {
            logger.error("Error with the configuration file.",e);
        } catch (IOException e) {
            logger.error("File not found.",e);
        }
        
        /*
         *  Find the absolute path to the web-inf directory during deployment
         */
        String webInfRealPath = servletContext.getRealPath("/WEB-INF/");
        if(webInfRealPath!=null)
        {
        	File webInfDir = new File( webInfRealPath );
        	webInfPath = webInfDir.getAbsolutePath();
        }
    }

    public static Properties getConfigBean(){

        // defensive programming to avoid call before init() PostConstruct-ed
        if (properties==null) {
            System.out.println("---ContextLoader Error: Uninitialized getConfigBean");
            return new Properties();
        }

        return properties;
    }

    public static Properties getExternalUrls(){
        return externalUrls;
    }
    
    public static String getWebInfPath()
    {
    	return webInfPath;
    }

    public void setApplicationContext(ApplicationContext ac)
            throws BeansException {
        this.ac = ac;

    }

    public void setServletContext(ServletContext sc) {
        this.sc = sc;
    }
}
