package org.jax.mgi.fewi.test.mock;

import org.jax.mgi.fewi.controller.DiseasePortalController;
import org.jax.mgi.fewi.controller.GXDController;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

/*
 * A useful way to encapsulate the inner workings of how mock queries get created
 * 
 * 		Usage is generally 
 * 			1) Initialize: MockQuery mq = gxdController();
 * 			2) Set Query params: mq.setNomen() or mq.setTheilerStages(), etc
 * 			3) Get results of query: mq.getGenes(), or mq.getAssays(), etc
 */
public class MockQueryFactory 
{
	protected MockRequest mr;
	
	public MockQueryFactory(MockRequest mr)
	{
		this.mr=mr;
	}
	
	/*
	 * GXD Mock Queries
	 */
	public MockGxdControllerQuery gxdController(GXDController controller) throws Exception
	{
		return new MockGxdControllerQuery(controller);
	}
	
	public MockGxdHttpQuery gxdHttp() throws Exception
	{
		return new MockGxdHttpQuery(mr);
	}
	
	/*
	 * Disease Portal Mock Queries
	 */
	public MockHdpControllerQuery diseasePortalController(DiseasePortalController controller) throws Exception
	{
		return new MockHdpControllerQuery(controller);
	}
	public MockHdpHttpQuery diseasePortalHttp() throws Exception
	{
		return new MockHdpHttpQuery(mr);
	}
	
	/*
	 * Cre Mock Queries
	 */
	public MockCreHttpQuery creHttp() throws Exception
	{
		return new MockCreHttpQuery(mr);
	}
}
