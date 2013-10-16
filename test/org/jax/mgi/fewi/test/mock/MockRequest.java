package org.jax.mgi.fewi.test.mock;

import java.util.Map;

import org.jax.mgi.fewi.controller.AlleleController;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.ui.Model;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

/**
 * A convenience class for mocking requests to Spring controllers.
 * 
 * Example Usage(s):
 * Either
 * MockRequest.handleRequest(handler,controller,url).get(parameter);
 * 
 * OR
 * MockRequest mr = new MockRequest(handler,controller,url);
 * Object value1 = mr.get(param1);
 * Object value2 = mr.get(param2);
 * 
 * OR
 * Object value = (new MockRequest(handler,controller)).setUrl(url).get(param);
 * 
 * @author kstone
 *
 */
public class MockRequest {

	private RequestMappingHandlerAdapter handler;
	private RequestMappingHandlerMapping handlerMapping;
	private String url = "";
	private Map<String,Object> model=null;
	
	public MockRequest(RequestMappingHandlerMapping handlerMapping,RequestMappingHandlerAdapter handler)
	{
		this.handlerMapping=handlerMapping;
		this.handler=handler;
	}
	
	public MockRequest(RequestMappingHandlerMapping handlerMapping,RequestMappingHandlerAdapter handler, String url) throws Exception
	{
		this.handlerMapping=handlerMapping;
		this.handler=handler;
		setUrl(url);
	}
	/*
	 * set the url for the request, perform the request, and cache the Spring model
	 */
	public MockRequest setUrl(String url) throws Exception
	{
		this.url=url;
		refresh();
		return this;
	}
	public String getUrl()
	{
		return url;
	}
	
	/*
	 * Returns the value stored inside Spring's model map
	 * for the requested parameter (i.e. key)
	 */
	public Object get(String parameter) throws Exception
	{
		if(model==null) refresh();
		return model.get(parameter);
	}
	
	/*
	 * Refreshes the model object by actually doing the request and caching it
	 */
	private void refresh() throws Exception
	{
		model = MockRequest.handleRequest(handlerMapping,handler, url);
	}
	
	/*
	 * Returns a mock servlet request for the requested url
	 */
	private static MockHttpServletRequest generateRequest(String url)
	{
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setRequestURI(url);
    	request.setMethod("GET");
		return request;
	}
	/*
	 * returns a Spring Model map for the requested url
	 */
	public static Map<String,Object> handleRequest(RequestMappingHandlerMapping handlerMapping,RequestMappingHandlerAdapter handler,String url) throws Exception
	{
		MockHttpServletRequest request = MockRequest.generateRequest(url);
    	MockHttpServletResponse response = new MockHttpServletResponse();
    	HandlerExecutionChain chain = handlerMapping.getHandler(request);
		ModelAndView mav = handler.handle(request, response, chain.getHandler());
		return mav.getModel();
	}
	/*
	 * returns a Spring Model map for the requested url
	 */
	public  Map<String,Object> handleRequest(MockHttpServletRequest request) throws Exception
	{
    	MockHttpServletResponse response = new MockHttpServletResponse();
    	HandlerExecutionChain chain = handlerMapping.getHandler(request);
    	return handler.handle(request, response, chain.getHandler()).getModel();
	}
	
	/*
	 * returns a filled out response object for the given request,
	 * 	using the Spring handler mapping functions
	 */
	public MockHttpServletResponse handle(MockHttpServletRequest request) throws Exception
	{
    	MockHttpServletResponse response = new MockHttpServletResponse();
    	HandlerExecutionChain chain = handlerMapping.getHandler(request);
    	handler.handle(request, response, chain.getHandler());
    	return response;
	}
}
