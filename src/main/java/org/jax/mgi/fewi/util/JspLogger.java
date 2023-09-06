package org.jax.mgi.fewi.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
* wrapper - provides methods to log flow of JSP pages, which don't log
* normally, even in scriptlet
*/
public class JspLogger
{
    // logger for the class
    private static Logger logger = LoggerFactory.getLogger(JspLogger.class);

    public void log (String logString) {
		logger.debug(logString);
    }

} // end of class

