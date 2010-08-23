package org.jax.mgi.fewi.config;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ServletContextAware;

@Component
public class ConfigLoader implements ServletContextAware {
    private ServletContext servletContext;
    
    @Autowired
    private ConfigWrapper configWrapper;

	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	@PostConstruct
    public void init() {
            servletContext.setAttribute("configBean", configWrapper);
    }
}
