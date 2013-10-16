package gxd;

import java.util.ArrayList;
import java.util.List;

import org.jax.mgi.fewi.controller.GXDController;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.entities.SolrAssayResult;
import org.jax.mgi.fewi.test.base.BaseConcordionTest;
import org.jax.mgi.fewi.test.mock.MockGxdControllerQuery;
import org.springframework.beans.factory.annotation.Autowired;

public class AssayResultsTest extends BaseConcordionTest {

    @Autowired
    private GXDController gxdController;
    
    public List<String> getAssayResultStrengths(String nomen, String assayType, String assayMgiid) throws Exception
    {
    	return getAssayResultStrengths(nomen,assayType,assayMgiid,null);
    }
    public List<String> getAssayResultStrengths(String nomen, String assayType, String assayMgiid, Integer theilerStage) throws Exception
    {
    	return getAssayResultStrengths(nomen,assayType,assayMgiid,theilerStage,null);
    }
    
    public List<String> getAssayResultStrengths(String nomen, String assayType, String assayMgiid,Integer theilerStage,String structure) throws Exception
    {
    	List<String> strengths = new ArrayList<String>();
    	MockGxdControllerQuery mq = new MockGxdControllerQuery(gxdController);
		mq.setNomenclature(nomen);
		mq.setAssayType(assayType);
		if(theilerStage != null) mq.setTheilerStage(theilerStage);
		
		SearchResults<SolrAssayResult> results = mq.getAssayResults();
		mq.pageSize = 1000;
		
		for(SolrAssayResult r : results.getResultObjects())
		{
			if(r.getAssayMgiid().equalsIgnoreCase(assayMgiid))
			{
				if(structure == null || r.getPrintname().toLowerCase().contains(structure.toLowerCase()))
				{
					strengths.add(r.getDetectionLevel());
				}
			}
		}
		return strengths;
    }
}
