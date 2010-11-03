package org.jax.mgi.fewi.config;

import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;

import org.jax.mgi.fewi.template.WebTemplate;
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
    }
	
	public static Properties getConfigBean(){
		return properties;
	}
	
	public static Properties getExternalUrls(){
		return externalUrls;
	}

	public void setApplicationContext(ApplicationContext ac)
			throws BeansException {
		this.ac = ac;
		
	}

	public void setServletContext(ServletContext sc) {
		this.sc = sc;
	}
}
