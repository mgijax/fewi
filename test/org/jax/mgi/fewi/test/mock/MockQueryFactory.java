package org.jax.mgi.fewi.test.mock;

import org.jax.mgi.fewi.controller.AlleleController;
import org.jax.mgi.fewi.controller.DiseasePortalController;
import org.jax.mgi.fewi.controller.GOController;
import org.jax.mgi.fewi.controller.GXDController;
import org.jax.mgi.fewi.controller.MarkerController;

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
	 * Allele Mock Queries
	 */
	public MockAlleleControllerQuery alleleController(AlleleController controller) throws Exception
	{
		return new MockAlleleControllerQuery(controller);
	}
	
	
	/*
	 * Cre Mock Queries
	 */
	public MockCreHttpQuery creHttp() throws Exception
	{
		return new MockCreHttpQuery(mr);
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
	 * GO Mock Queries
	 */
	public MockGOControllerQuery goController(GOController controller) throws Exception
	{
		return new MockGOControllerQuery(controller);
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
	 * Marker Mock Queries
	 */
	public MockMarkerControllerQuery markerController(MarkerController controller) throws Exception
	{
		return new MockMarkerControllerQuery(controller);
	}


}
