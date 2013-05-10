package gxd;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jax.mgi.fewi.controller.GXDController;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.entities.SolrGxdAssay;
import org.jax.mgi.fewi.searchUtil.entities.SolrGxdImage;
import org.jax.mgi.fewi.test.concordion.BaseConcordionTest;
import org.jax.mgi.fewi.test.mock.MockGxdControllerQuery;
import org.springframework.beans.factory.annotation.Autowired;

public class ImageSummaryTest extends BaseConcordionTest {

    @Autowired
    private GXDController gxdController;
	
    public Integer getAssayCountByNomen(String nomen) throws Exception
    {
		MockGxdControllerQuery mq = new MockGxdControllerQuery(gxdController);
		mq.setNomenclature(nomen);

		SearchResults<SolrGxdAssay> sr = mq.getAssays();

		return sr.getTotalCount();
    }
    public int getImagesCountByNomen(String nomen) throws Exception
    {
    	MockGxdControllerQuery mq = new MockGxdControllerQuery(gxdController);
		mq.setNomenclature(nomen);
		
		SearchResults<SolrGxdImage> sr = mq.getImages();
		return sr.getTotalCount();
    }

    public Integer getAssayCountByTypeAndNomen(String nomen, String assayType) throws Exception
	{
		MockGxdControllerQuery mq = new MockGxdControllerQuery(gxdController);
		mq.setNomenclature(nomen);

		mq.pageSize=10000;
		SearchResults<SolrGxdAssay> sr = mq.getAssays();
		Integer cnt = new Integer(0);

		for(SolrGxdAssay r : sr.getResultObjects())
		{
			String t = r.getAssayType();
			
			if (t.equals(assayType)) {
				cnt += 1;
			}
			
		}
		return cnt;
	}
	
	public boolean getAssaysInOrder(String nomen, String orderedAssays) throws Exception
	{
		
		// orderedAssays should be a comman sep string of MGI IDs
		// of assays.
		List<String> passedIn = Arrays.asList(orderedAssays.replaceAll("\\s+", "").split(","));
		List<String> queried = new ArrayList<String>();
		
		MockGxdControllerQuery mq = new MockGxdControllerQuery(gxdController);
		mq.setNomenclature(nomen);

		mq.pageSize=10000;
		SearchResults<SolrGxdAssay> sr = mq.getAssays();

		for (SolrGxdAssay r : sr.getResultObjects()) {
			queried.add(r.getAssayMgiid());
		}

		return queried.equals(passedIn);
	}
}
