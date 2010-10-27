package org.jax.mgi.fewi.config;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;

import org.jax.mgi.fewi.template.WebTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ServletContextAware;

@Component
public class ContextLoader implements ServletContextAware {

	private Logger logger = LoggerFactory.getLogger(ContextLoader.class);
    private ServletContext servletContext;

    @Autowired
    private WebTemplate webTemplate;

	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	@PostConstruct
    public void init() {
            servletContext.setAttribute("templateBean", webTemplate);
            logger.debug("configs loaded");
    }
}
