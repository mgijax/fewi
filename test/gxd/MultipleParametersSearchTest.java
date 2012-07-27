package gxd;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.jax.mgi.fewi.controller.GXDController;
import org.jax.mgi.fewi.test.concordion.BaseConcordionTest;
import org.jax.mgi.fewi.test.mock.MockGxdHttpQuery;
import org.jax.mgi.fewi.test.mock.MockGxdQueryParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;


public class MultipleParametersSearchTest extends BaseConcordionTest {

    @Autowired
    protected GXDController gxdController;
    
    @Autowired
    protected AnnotationMethodHandlerAdapter handler;

	/*
	 * Helper functions for mocking queries
	 */

    private List<String> convertStringToAssayTypes(String assayTypeStr)
    {
		List<String> assayTypes = new ArrayList<String>();
		try {
	    	if (assayTypeStr.equals("All")) {
	    		assayTypes.add("Immunohistochemistry");
	    		assayTypes.add("In situ reporter (knock in)");
	    		assayTypes.add("Northern blot");
	    		assayTypes.add("Nuclease S1");
	    		assayTypes.add("RNA in situ");
	    		assayTypes.add("RNase protection");
	    		assayTypes.add("RT-PCR");
	    		assayTypes.add("Western blot");
	    	} else {
	    		for (String s : assayTypeStr.split(",")) {
	    			assayTypes.add(s);    			
	    		}
	    	}
		} catch (Exception e) {
			// Can't convert the passed in string to a list of 
			// assayTypes
    		fail("Call to convertStringToAssayTypes with an invalid list of assayTypes: ("+assayTypeStr+")");
    	}
		return assayTypes;
    }
    
    /*
     * Tests
     */

    /**
     * Tests for verifying
     *  - nomenclature
     *  - assay type
     */

    public Integer getGeneCountByNomenAndAssayType(String nomen, String assayTypeStr) throws Exception {
		List<String> assayTypes = convertStringToAssayTypes(assayTypeStr);
		MockGxdHttpQuery m = new MockGxdHttpQuery(handler, gxdController);
		m.setNomenclature(nomen);
    	m.setAssayType(assayTypes);
		return m.getGenes().getTotalCount();
	}
    public Integer getResultCountByNomenAndAssayType(String nomen, String assayTypeStr) throws Exception
    {
		List<String> assayTypes = convertStringToAssayTypes(assayTypeStr);
		MockGxdHttpQuery m = new MockGxdHttpQuery(handler, gxdController);
		m.setNomenclature(nomen);
    	m.setAssayType(assayTypes);
		return m.getAssayResults().getTotalCount();
    }

    /**
     * Tests for verifying
     *  - nomenclature
     *  - theiler stage
     */
    public Integer getGeneCountByNomenAndTS(String nomen, Integer ts) throws Exception {
		MockGxdHttpQuery m = new MockGxdHttpQuery(handler, gxdController);
		m.setNomenclature(nomen);
    	m.setTheilerStage(ts);
		return m.getGenes().getTotalCount();
	}
    public Integer getResultCountByNomenAndTS(String nomen, Integer ts) throws Exception {
		MockGxdHttpQuery m = new MockGxdHttpQuery(handler, gxdController);
		m.setNomenclature(nomen);
    	m.setTheilerStage(ts);
		return m.getAssayResults().getTotalCount();
	}

    /**
     * Tests for verifying 
     *  - theiler stage
     *  - assay type
     */
    public Integer getGeneCountByTSAndAssayType(Integer ts, String assayTypeStr) throws Exception {
		List<String> assayTypes = convertStringToAssayTypes(assayTypeStr);
		MockGxdHttpQuery m = new MockGxdHttpQuery(handler, gxdController);
		m.setTheilerStage(ts);
    	m.setAssayType(assayTypes);
		return m.getGenes().getTotalCount();
	}
    public Integer getResultCountByTSAndAssayType(Integer ts, String assayTypeStr) throws Exception
    {
		List<String> assayTypes = convertStringToAssayTypes(assayTypeStr);
		MockGxdHttpQuery m = new MockGxdHttpQuery(handler, gxdController);
		m.setTheilerStage(ts);
    	m.setAssayType(assayTypes);
		return m.getAssayResults().getTotalCount();
    }
    
    /**
     * Tests for verifying 
     *  - nomen
     *  - age
     *  - detection level
     */
    public Integer getGeneCountByNomenAndAgeAndDetection(String nomen, String ageStr, String detected) throws Exception {
		MockGxdHttpQuery m = new MockGxdHttpQuery(handler, gxdController);
		m.setNomenclature(nomen);
		m.setAge(MockGxdQueryParser.parseAgeString(ageStr));
		m.setDetected(detected);
		return m.getGenes().getTotalCount();
	}
    public Integer getResultCountByNomenAndAgeAndDetection(String nomen, String ageStr, String detected) throws Exception
    {
		MockGxdHttpQuery m = new MockGxdHttpQuery(handler, gxdController);
		m.setNomenclature(nomen);
		m.setAge(MockGxdQueryParser.parseAgeString(ageStr));
		m.setDetected(detected);
		return m.getAssayResults().getTotalCount();
    }

    /**
     * Tests for verifying 
     *  - nomenclature
     *  - theiler stage
     *  - assay type
     */
    public Integer getGeneCountByNomenAndTSAndAssayType(String nomen, Integer ts,String assayTypeStr) throws Exception {
		List<String> assayTypes = convertStringToAssayTypes(assayTypeStr);
		MockGxdHttpQuery m = new MockGxdHttpQuery(handler, gxdController);
		m.setNomenclature(nomen);
    	m.setTheilerStage(ts);
    	m.setAssayType(assayTypes);
		return m.getGenes().getTotalCount();
	}
    public Integer getResultCountByNomenAndTSAndAssayType(String nomen, Integer ts, String assayTypeStr) throws Exception
    {
		List<String> assayTypes = convertStringToAssayTypes(assayTypeStr);
		MockGxdHttpQuery m = new MockGxdHttpQuery(handler, gxdController);
		m.setNomenclature(nomen);
    	m.setTheilerStage(ts);
    	m.setAssayType(assayTypes);
		return m.getAssayResults().getTotalCount();
    }

    /**
     * Tests for verifying 
     *  - nomenclature
     *  - theiler stage
     *  - assay type
     *  - detected
     */
    public Integer getGeneCountByNomenAndTSAndAssayTypeAndDetected(String nomen, Integer ts, String assayTypeStr, String detected) throws Exception {
		List<String> assayTypes = convertStringToAssayTypes(assayTypeStr);
		MockGxdHttpQuery m = new MockGxdHttpQuery(handler, gxdController);
    	m.setNomenclature(nomen);
    	m.setTheilerStage(ts);
    	m.setAssayType(assayTypes);
		m.setDetected(detected);
		return m.getGenes().getTotalCount();
	}
    public Integer getResultCountByNomenAndTSAndAssayTypeAndDetected(String nomen, Integer ts, String assayTypeStr, String detected) throws Exception
    {
		List<String> assayTypes = convertStringToAssayTypes(assayTypeStr);
		MockGxdHttpQuery m = new MockGxdHttpQuery(handler, gxdController);
    	m.setNomenclature(nomen);
    	m.setTheilerStage(ts);
    	m.setAssayType(assayTypes);
		m.setDetected(detected);
		return m.getAssayResults().getTotalCount();
    }
    
    
    /**
     * Tests for verifying 
     *  - nomenclature
     *  - age
     *  - assay type
     *  - detected
     */
    public Integer getGeneCountByNomenAndAgeAndAssayTypeAndDetected(String nomen, String ageStr, String assayTypeStr, String detected) throws Exception {
		List<String> assayTypes = convertStringToAssayTypes(assayTypeStr);
		MockGxdHttpQuery m = new MockGxdHttpQuery(handler, gxdController);
    	m.setNomenclature(nomen);
    	m.setAge(MockGxdQueryParser.parseAgeString(ageStr));
    	m.setAssayType(assayTypes);
		m.setDetected(detected);
		return m.getGenes().getTotalCount();
	}
    public Integer getResultCountByNomenAndAgeAndAssayTypeAndDetected(String nomen, String ageStr, String assayTypeStr, String detected) throws Exception
    {
		List<String> assayTypes = convertStringToAssayTypes(assayTypeStr);
		MockGxdHttpQuery m = new MockGxdHttpQuery(handler, gxdController);
    	m.setNomenclature(nomen);
    	m.setAge(MockGxdQueryParser.parseAgeString(ageStr));
    	m.setAssayType(assayTypes);
		m.setDetected(detected);
		return m.getAssayResults().getTotalCount();
    }
    
    public Integer getWildTypeResultCountByNomenAndAgeAndAssayTypeAndDetected(String nomen, String ageStr, String assayTypeStr, String detected) throws Exception
    {
    	List<String> assayTypes = convertStringToAssayTypes(assayTypeStr);
		MockGxdHttpQuery m = new MockGxdHttpQuery(handler, gxdController);
		m.setIsWildType(true);
    	m.setNomenclature(nomen);
    	m.setAge(MockGxdQueryParser.parseAgeString(ageStr));
    	m.setAssayType(assayTypes);
		m.setDetected(detected);
		return m.getAssayResults().getTotalCount();
    }

}
	
