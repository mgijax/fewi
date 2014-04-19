package junit;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:applicationContext-ci.xml"})
@TransactionConfiguration
@Transactional
public class GxdResultSummaryIntegrationTest 
{
	@Test
	public void testNothing()
	{}
	// Now in concordion
//    @Autowired
//    private AnnotationMethodHandlerAdapter handler;
//
//    // The class being tested is autowired via spring's DI
//    @Autowired
//    private GXDController gxdController;
//
//    // ================================================================
//    // Helper Functions
//    // ================================================================
//    /*
//     * Does a nomenclature query through the gxd controller
//     */
//    public SearchResults<SolrAssayResult> mockNomenQuery(String query) throws Exception
//    {
//    	GxdQueryForm gqf = new GxdQueryForm();
//    	gqf.setNomenclature(query);
//    	MockHttpServletRequest request = new MockHttpServletRequest();
//    	request.setParameter("sort", "default");
//    	request.setParameter("dir","asc");
//    	Paginator page = new Paginator();
//    	page.setResults(10000);
//    	BindingResult br = new BeanPropertyBindingResult(new Object(),"mock");
//    	return gxdController.getGxdAssayResults(request, gqf, page, br);
//    	
//    }
//    /*
//     * Filters the results set by the specified assayID
//     * Returns only those results for that assayID
//     */
//    public List<SolrAssayResult> getResultsByAssayID(SearchResults<SolrAssayResult> results,String assayID)
//    {
//    	List<SolrAssayResult> foundResults = new ArrayList<SolrAssayResult>(0);
//    	assayID = assayID.toLowerCase();
//    	for(SolrAssayResult r : results.getResultObjects())
//    	{
//    		if(r.getAssayMgiid().toLowerCase().equals(assayID)) foundResults.add(r);
//    	}
//    	return foundResults;
//    }
//    public Integer mockCountQuery(String query,String url) throws Exception
//    {
//    	MockHttpServletRequest request = new MockHttpServletRequest();
//    	MockHttpServletResponse response = new MockHttpServletResponse();
//
//    	request.setRequestURI(url);
//    	request.addParameter("nomenclature", query);
//    	request.setMethod("GET");
//
//    	handler.handle(request, response, gxdController);
//
//    	ObjectMapper mapper = new ObjectMapper();
//    	Integer count = null; 
//    	count = mapper.readValue(response.getContentAsString(), 
//    			new TypeReference<Integer>() { });
//    	return count;
//    }
//    public JsonSummaryResponse<MockJSONGXDAssayResult> mockResultQuery(String query) throws Exception
//    {
//    	MockHttpServletRequest request = new MockHttpServletRequest();
//    	MockHttpServletResponse response = new MockHttpServletResponse();
//
//    	request.setRequestURI("/gxd/results/json");
//    	request.addParameter("nomenclature", query);
//    	request.setMethod("GET");
//
//    	handler.handle(request, response, gxdController);
//
//    	ObjectMapper mapper = new ObjectMapper();
//    	JsonSummaryResponse<MockJSONGXDAssayResult> results = null; 
//    	results = mapper.readValue(response.getContentAsString(), 
//    			new TypeReference<JsonSummaryResponse<MockJSONGXDAssayResult>>() { });
//    	return results;
//    }
//    
//    // ================================================================
//    // Additional assertions
//    // ================================================================
//    public void assertResultCount(String query,int count) throws Exception
//    {
//    	JsonSummaryResponse<MockJSONGXDAssayResult> results = mockResultQuery(query);
//    	assertTrue(results.getTotalCount()==count);
//    }
//    
//    
//    @Before
//    public void setUp()
//    {
//    }
//
//    // ================================================================
//    // Unit tests
//    // ================================================================
//    @Test
//    public void testUrlRouting () throws Exception
//    {
//    	String query = "Pax6";
//    	JsonSummaryResponse<MockJSONGXDAssayResult> results = mockResultQuery(query);
//    	assertTrue(results.getTotalCount()>0);
//    }
//    
//    @Test
//    public void testResultCountUrlRouting() throws Exception
//    {
//    	String query = "Pax6";
//    	Integer count = mockCountQuery(query,"/gxd/results/totalCount");
//    	assertTrue(count>0);
//    }
//    @Test
//    public void testGeneCountUrlRouting() throws Exception
//    {
//    	String query = "Pax6";
//    	Integer count = mockCountQuery(query,"/gxd/markers/totalCount");
//    	assertTrue(count>0);
//    }
//    @Test
//    public void testResultCount() throws Exception
//    {
//    	String query = "Pax6";
//    	int count = 1434;
//    	assertResultCount(query,count);
//    }
//    
//    @Test
//    public void testAssayType() throws Exception
//    {
//    	String query = "Pax6";
//    	String assayID = "MGI:1859758";
//    	String assayType = "Immunohistochemistry";
//    	//SearchResults<SolrAssayResult> sr = mockNomenQuery(query);
//    	List<SolrAssayResult> results = getResultsByAssayID(mockNomenQuery(query),assayID);
//    	assertTrue(results.size()>0);
//    	for(SolrAssayResult r : results)
//    	{
//    		assertTrue(r.getAssayType().equals(assayType));
//    	}
//    }
//    
//    @Test
//    public void testAnatomicalSystem() throws Exception
//    {
//    	String query = "Pax6";
//    	String assayID = "MGI:1859758";
//    	String system = "nervous system";
//    	String structure = "future forebrain";
//    	SearchResults<SolrAssayResult> sr = mockNomenQuery(query);
//    	List<SolrAssayResult> results = getResultsByAssayID(sr,assayID);
//    	boolean foundAny = false;
//    	for(SolrAssayResult r : results)
//    	{
//    		if(r.getPrintname().toLowerCase().equals(structure))
//    		{
//    			foundAny = true;
//    			assertTrue(r.getAnatomicalSystem().toLowerCase().equals(system));
//    		}
//    	}
//    	assertTrue(foundAny);
//
//    	// Try second assay
//    	assayID = "MGI:2157343";
//    	system = "sensory organs";
//    	structure = "lens placode";
//    	results = getResultsByAssayID(sr,assayID);
//    	foundAny = false;
//    	for(SolrAssayResult r : results)
//    	{
//    		if(r.getPrintname().toLowerCase().equals(structure))
//    		{
//    			foundAny = true;
//    			assertTrue(r.getAnatomicalSystem().toLowerCase().equals(system));
//    		}
//    	}
//    	assertTrue(foundAny);
//    }
//    
//    @Test
//    public void testAge() throws Exception
//    {
//    	String query = "Pax6";
//    	String assayID = "MGI:1859758";
//    	String age = "E9.5";
//    	String structure = "future forebrain";
//    	SearchResults<SolrAssayResult> sr = mockNomenQuery(query);
//    	List<SolrAssayResult> results = getResultsByAssayID(sr,assayID);
//    	boolean foundAny = false;
//    	for(SolrAssayResult r : results)
//    	{
//    		//need to make summary row object to test proper display value
//    		GxdAssayResultSummaryRow rSR = new GxdAssayResultSummaryRow(r);
//    		if(r.getPrintname().toLowerCase().equals(structure))
//    		{
//    			foundAny = true;
//    			assertTrue(rSR.getAge().equals(age));
//    		}
//    	}
//    	assertTrue(foundAny);
//    }
//    
//    @Test
//    public void testStructure() throws Exception
//    {
//    	String query="Pax6";
//    	String assayID = "MGI:1859758";
//    	// list of structures to find in the result set. Can include duplicates
//    	List<String> structuresToFind = new ArrayList<String>(Arrays.asList("TS12: embryo",
//    			"TS12: future prosencephalon floor plate",
//    			"TS12: future prosencephalon",
//    			"TS12: future prosencephalon floor plate",
//    			"TS13: future prosencephalon",
//    			"TS13: future prosencephalon floor plate",
//    			"TS13: future prosencephalon",
//    			"TS13: future prosencephalon floor plate",
//    			"TS15: future forebrain",
//    			"TS15: diencephalon floor plate",
//    			"TS15: telencephalon floor plate",
//    			"TS12: future prosencephalon")
//    	);
//    	List<SolrAssayResult> results = getResultsByAssayID(mockNomenQuery(query),assayID);
//    	for(SolrAssayResult r : results)
//    	{
//    		// convert to summary row to generate the correct structure string
//    		GxdAssayResultSummaryRow rSR = new GxdAssayResultSummaryRow(r);
//    		String rStructure = rSR.getStructure().trim();
//    		if(structuresToFind.contains(rStructure))
//    		{
//    			// remove the found structure.
//    			structuresToFind.remove(rStructure);
//    		}
//    	}
//    	// assert that we found (and removed) all the structures we wanted
//    	assertTrue(structuresToFind.size()==0);
//    }
}