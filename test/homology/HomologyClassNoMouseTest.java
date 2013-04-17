package homology;

import mgi.frontend.datamodel.HomologyCluster;
import mgi.frontend.datamodel.OrganismOrtholog;

import org.jax.mgi.fewi.test.concordion.BaseConcordionTest;
import org.jax.mgi.fewi.test.mock.MockRequest;

public class HomologyClassNoMouseTest extends BaseConcordionTest {
	
    private String baseUrl = "/homology/";
    
    public HomologyCluster getHomologyCluster(String homologyClassID) throws Exception
    {
    	String url = baseUrl + homologyClassID;
    	MockRequest mr = mockRequest(url);
    	HomologyCluster hCluster = (HomologyCluster) mr.get("homology");
    	return hCluster;
    }
    
	/*
	 * The actual test functions
	 */   
    
    public int getMarkerCountByOrganism(HomologyCluster hCluster, String organism) {
    	OrganismOrtholog oo = hCluster.getOrganismOrtholog(organism);
    	if (oo != null){
    		return oo.getMarkerCount();
    	}
    	return 0;
    }
    
    public boolean hasOrganism(HomologyCluster hCluster, String organism) {
    	for (OrganismOrtholog oo: hCluster.getOrthologs()) {
    		if (oo.getOrganism().equalsIgnoreCase(organism)) {
    			return true;
    		}
    	}
    	return false;
    }
    
    public int getOrganismCount(HomologyCluster hCluster) {
    	int count = 0;  	
    	for (OrganismOrtholog oo: hCluster.getOrthologs()) {
    		if (oo.getMarkerCount() > 0) {
    			count++;
    		}
    	}
    	return count;
    }
    
    // Helper assert functions
    public boolean assertGreaterThan(int value1,int value2)
    {
    	return value1 > value2;
    }
    public boolean assertOneGreater(int value1, int value2)
    {
    	return value1 == (value2+1);
    }
}
