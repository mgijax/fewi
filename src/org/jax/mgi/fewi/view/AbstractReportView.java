package org.jax.mgi.fewi.view;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.view.AbstractView;

/**
 * Super class for our report views
 * contains some config and logging methods
 */
public class AbstractReportView extends AbstractView
{
	// set timeout and log frequency for when using checkProgress(rownum) method
		protected long logFrequencySeconds = 1; // every second
		protected long timeoutSeconds = 1800; // a half hour
		private static final long SECOND_TO_NANO=1000000000;
		private Long timeoutPeriod=null;
		private Long loggingPeriod=null;
		private String simpleClassName;
		
		public AbstractReportView()
		{
			super();
			simpleClassName=this.getClass().getSimpleName();
		}
		
		@Override
		protected void renderMergedOutputModel(Map<String, Object> arg0,
				HttpServletRequest arg1, HttpServletResponse arg2)
				throws Exception {
			// TO BE OVERRIDDEN
		}
		
		/*
		 * init the timeout period for using checkProgress(rownum) method
		 */
		protected void startTimeoutPeriod(){ timeoutPeriod=System.nanoTime(); }
		/*
		 * logs progress and throws timeout exception if necessary
		 */
		protected void checkProgress() throws Exception
		{
			checkProgress(-1); // won't log any rows, but will still check for timeout
		}
		protected void checkProgress(int linesProcessed) throws Exception
		{
			// check if we need to log
			if(loggingPeriod==null) loggingPeriod=System.nanoTime();
			else if((System.nanoTime()-loggingPeriod) > (logFrequencySeconds*SECOND_TO_NANO))
			{
				loggingPeriod=System.nanoTime();
				logger.info("processed "+linesProcessed+" lines of "+simpleClassName+" report");
			}
			// check if we need to timeout
			if(timeoutPeriod!=null && ((System.nanoTime()-timeoutPeriod) > (timeoutSeconds*SECOND_TO_NANO)))
			{
				throw new TimeOutException("Exceeded time out value of "+timeoutSeconds+" seconds. Failed to generate "+simpleClassName+" report.");
			}
		}
		
		@SuppressWarnings("serial")
		protected class TimeOutException extends Exception
		{
			public TimeOutException(String message)
			{
				super(message);
			}
		}
		protected  final static String getCurrentDate()   {
			Date date = new Date();
			DateFormat df = new SimpleDateFormat( "yyyyMMdd_HHmmss" ) ;
			//df.setTimeZone( TimeZone.getTimeZone( "EST" )  ) ;
	        String formattedDate = df.format(date);
	        return (formattedDate);
		}
}
