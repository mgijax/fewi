package org.jax.mgi.fewi.util;

import javax.servlet.http.HttpServletResponse;

/**
 * 
 * Initially created to factor out code that prepares a jsp response to comply with
 * a cross browser Http AJAX request.
 * 
 * @author kstone
 *
 */
public class AjaxUtils 
{

	/*
	 * Adds the appropriate headers to the response object to make it comply with
	 * a cross browser HTTP AJAX request.
	 */
	public static void prepareAjaxHeaders(HttpServletResponse response)
	{
	       response.addHeader("Access-Control-Allow-Origin", "*");
	       // This header is only required for IE, because IE is "special"
	       response.addHeader("Access-Control-Allow-Headers", "x-requested-with");
	}
}
