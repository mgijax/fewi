package org.jax.mgi.fewi.test.mock;

import org.jax.mgi.fewi.controller.GXDController;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

/*
 * A useful way to encapsulate the inner workings of how mock queries get created
 */
public class MockQueryFactory 
{
	protected MockRequest mr;
	
	public MockQueryFactory(MockRequest mr)
	{
		this.mr=mr;
	}
	
	public MockGxdControllerQuery gxdController(GXDController controller) throws Exception
	{
		return new MockGxdControllerQuery(controller);
	}
	
	public MockGxdHttpQuery gxdHttp() throws Exception
	{
		return new MockGxdHttpQuery(mr);
	}
}
