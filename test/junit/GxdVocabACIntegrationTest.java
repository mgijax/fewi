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
public class GxdVocabACIntegrationTest 
{
	@Test
	public void testNothing()
	{}
	// Currently these have been transferred to concordion.
//    @Autowired
//    private AnnotationMethodHandlerAdapter handler;
//
//    // The class being tested is autowired via spring's DI
//    @Autowired
//    private AutoCompleteController autoCompleteController;
//
//    // ================================================================
//    // Additional assertions
//    // ================================================================
//    private void assertNoMatches(String query)
//    {
//        SearchResults<VocabACResult> results = 
//        		autoCompleteController.getVocabAutoCompleteResults(query); 
//    	assertTrue(
//    			"Bad objects:"+results.getResultObjects().toString(),
//    			results.getTotalCount() == 0);
//    }
//
//    private void assertTermInVocab(String query, String vocab)
//    {
//        SearchResults<VocabACResult> results = 
//        		autoCompleteController.getVocabAutoCompleteResults(query); 
//
//        boolean found = false;
//        for(VocabACResult result : results.getResultObjects())
//        {
//        	String resultTerm = result.getTerm().toLowerCase();
//        	String resultVocab = result.getRootVocab();
//        	if(resultTerm.equals(query.toLowerCase()) && 
//        		resultVocab.equals(vocab))
//        	{
//        		found = true;
//        		break;
//        	}
//        }
//        assertTrue(found);
//    }
//
//    private void assertTermFound(String query, String term)
//    {
//        SearchResults<VocabACResult> results = 
//        		autoCompleteController.getVocabAutoCompleteResults(query); 
//
//        boolean found = false;
//        for(VocabACResult result : results.getResultObjects())
//        {
//        	String resultTerm = result.getTerm().toLowerCase();
//        	if(resultTerm.equals(term));
//        	{
//        		found = true;
//        		break;
//        	}
//        }
//        assertTrue(found);
//    }
//
//    private void assertTermNotInVocab(String query, String vocab)
//    {
//    	 SearchResults<VocabACResult> results = 
//         		autoCompleteController.getVocabAutoCompleteResults(query); 
//
//         boolean found = false;
//         for(VocabACResult result : results.getResultObjects())
//         {
//         	String resultTerm = result.getTerm().toLowerCase();
//         	String resultVocab = result.getRootVocab();
//         	if(resultTerm.equals(query.toLowerCase()) && 
//         		resultVocab.equals(vocab))
//         	{
//         		found = true;
//         		break;
//         	}
//         }
//         assertTrue(!found);
//    }
//    
//    private void assertNoIncorrectTerm(String query, String incorrectTerm)
//    {
//    	SearchResults<VocabACResult> results = 
//    		autoCompleteController.getVocabAutoCompleteResults(query); 
//
//		for (VocabACResult result: results.getResultObjects())
//		{
//	    	assertTrue(
//	    		"Bad objects:"+results.getResultObjects().toString(),
//	    		!result.getTerm().equals(incorrectTerm));
//		}
//	}
//
//    /*
//     * a helper function for checking the value of markerCount for a given term
//     */
//    public void assertTermMarkerCount(String termToCheck,String vocab,int markerCount)
//    {
//    	SearchResults<VocabACResult> results = autoCompleteController.getVocabAutoCompleteResults(termToCheck); 
//        boolean foundTerm = false;
//        for(VocabACResult result : results.getResultObjects())
//        {
//        	if(result.getTerm().toLowerCase().equals(termToCheck.toLowerCase()) && result.getRootVocab().equals(vocab))
//        	{
//        		foundTerm=true;
//        		assertTrue(markerCount==result.getMarkerCount());
//        		break;
//        	}
//        }
//        assertTrue(foundTerm);
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
//    	String query = "fungal vacuole";
//
//    	MockHttpServletRequest request = new MockHttpServletRequest();
//    	MockHttpServletResponse response = new MockHttpServletResponse();
//
//    	request.setRequestURI("/autocomplete/vocabTerm");
//    	request.addParameter("query", query);
//    	request.setMethod("GET");
//
//    	handler.handle(request, response, autoCompleteController);
//
//    	
//    	ObjectMapper mapper = new ObjectMapper();
//    	JsonSummaryResponse<VocabACSummaryRow> results = null; 
//    	results = mapper.readValue(response.getContentAsString(), 
//    			new TypeReference<JsonSummaryResponse<VocabACSummaryRow>>() { });
//    	assertTrue(results.getTotalCount()>0);
//    }
//    
//    @Test
//    public void testVerifyOnlyOneTermSynonymPair() throws Exception
//    {
//    	// Verify that each term-synonym combination
//    	// appears in the results only once
//    	
//    	String query = "forebrain dorsal-ventral pattern formation";
//
//        SearchResults<VocabACResult> results = 
//        		autoCompleteController.getVocabAutoCompleteResults(query); 
//
//    	Set<String> uniques = new HashSet<String>();
//    	for (VocabACResult vResult: results.getResultObjects())
//    	{
//    		String combinedTerm = vResult.getTerm() + "--" + vResult.getOriginalTerm();
//    		assertTrue(!uniques.contains(combinedTerm));
//    		uniques.add(combinedTerm);    		
//    	}
//    }
//
//    @Test
//    public void testVerifyThreeGoVocabsDisplay() throws Exception
//    {
//    	Map<String, String> queries = new HashMap<String, String>();
//    	queries.put("Function", "SNARE binding");
//    	queries.put("Process", "saliva secretion");
//    	queries.put("Component", "clathrin coat");
//    	
//    	for (String vocab: queries.keySet()) {
//            SearchResults<VocabACResult> results = 
//            		autoCompleteController.getVocabAutoCompleteResults(queries.get(vocab));
//
//        	// More that zero results
//        	assertTrue(results.getTotalCount() > 0);
//
//        	VocabACResult vResult = results.getResultObjects().get(0);
//
//        	// The vocab should be GO and the displayed vocab should be
//        	// function, process or component, depending on term
//        	assertTrue(vResult.getRootVocab().equals("GO"));
//        	assertTrue(vResult.getDisplayVocab().equals(vocab));
//    	}
//    }
//    
//    @Test
//    public void testMPTermQuery() throws Exception
//    {
//    	String query = "absent mandible";
//    	String vocab = "Mammalian Phenotype";
//    	assertTermInVocab(query, vocab);
//    }
//    
//    @Test
//    public void testMPSynonymQuery() throws Exception
//    {
//    	String query = "agnathia";
//    	String vocab = "Mammalian Phenotype";
//    	assertTermInVocab(query, vocab);
//    }
//    
//    @Test
//    public void testOMIMTermQuery() throws Exception
//    {
//    	String query = "ACHOO Syndrome";
//    	String vocab = "OMIM";
//    	assertTermInVocab(query, vocab);
//    }
//
//    @Test
//    public void testOMIMSynonymQuery() throws Exception
//    {
//    	String query = "Arginase Deficiency";
//    	String vocab = "OMIM";
//    	assertTermInVocab(query, vocab);
//    }
//
//    @Test
//    public void testInterProTerm() throws Exception
//    {
//    	String query = "Kappa casein";
//    	String vocab = "InterPro Domains";
//    	assertTermInVocab(query, vocab);
//    }
//
//    @Test
//    public void testInterProMarkerCount() throws Exception
//    {
//    	String query = "Kappa casein";
//    	String vocab = "InterPro Domains";
//    	int markerCount = 1;
//    	
//    	assertTermMarkerCount(query,vocab,markerCount);
//    }
//    
//    @Test
//    public void testPIRSFTerm() throws Exception
//    {
//    	String query = "kappa-casein";
//    	String vocab = "PIR Superfamily";
//    	assertTermInVocab(query, vocab);
//    }
//
//    @Test
//    public void testPIRSFMarkerCount() throws Exception
//    {
//    	String query = "kappa-casein";
//    	String vocab = "PIR Superfamily";
//    	int markerCount = 1;
//    	
//    	assertTermMarkerCount(query,vocab,markerCount);
//    }
//    
//    @Test
//    public void testProteinOntologyTerm() throws Exception
//    {
//    	String query = "Kappa-casein";
//    	String vocab = "Protein Ontology";
//    	assertTermNotInVocab(query,vocab);
//    }
//
//    @Test
//    public void testCommaInTerm() throws Exception
//    {
//    	String query=",Guanine nucleotide binding protein alpha subunit";
//    	String correctTerm="Guanine nucleotide binding protein (G-protein), alpha subunit";
//    	assertTermFound(query,  correctTerm);
//
//    }
//
//    @Test
//    public void testParenthesisInTerm() throws Exception
//    {
//    	String query="Guanine nucleotide binding protein (G-protein), alpha subunit";
//    	assertTermFound(query, query);
//    }
//    
//    @Test
//    public void testUnderscoreInTerm() throws Exception
//    {
//    	String query="Golgi_SNARE";
//    	List<String> correctTerms = new ArrayList<String>(Arrays.asList("golgi_snare","snare associated golgi protein"));
//    	SearchResults<VocabACResult> results = autoCompleteController.getVocabAutoCompleteResults(query); 
//        for(VocabACResult result : results.getResultObjects())
//        {
//        	if(correctTerms.size()==0)
//        	{ break; }
//        	List<String> leftToSearch = new ArrayList<String>(correctTerms);
//        	for(String correctTerm : leftToSearch)
//        	{
//	        	if(result.getTerm().toLowerCase().equals(correctTerm));
//	        	{
//	        		correctTerms.remove(correctTerm);
//	        		break;
//	        	}
//        	}
//        }
//        assertTrue(correctTerms.size()==0);
//    }
//    
//    @Test
//    public void testWordOrder() throws Exception
//    {
//    	String query1="ceramide biosynthesis";
//    	String query2="biosynthesis ceramide";
//    	SearchResults<VocabACResult> results1 = autoCompleteController.getVocabAutoCompleteResults(query1);
//    	SearchResults<VocabACResult> results2 = autoCompleteController.getVocabAutoCompleteResults(query2);
//    	// Result sets should match
//    	Set<VocabACResult> resultSet1 = new HashSet<VocabACResult>(results1.getResultObjects());
//    	Set<VocabACResult> resultSet2 = new HashSet<VocabACResult>(results2.getResultObjects());
//    	assertTrue(resultSet1.containsAll(resultSet2));
//    	assertTrue(resultSet2.containsAll(resultSet1));
//    }
//
//    @Test
//    public void testVerifyThreeGoSynonymsAndTermsDisplay() throws Exception
//    {
//    	// Map of Synonym -> term
//    	Map<String, String> queries = new HashMap<String, String>();
//    	queries.put("SNAP receptor binding", "SNARE binding");
//    	queries.put("salivation", "saliva secretion");
//    	queries.put("clathrin cage", "clathrin coat");
//    	
//    	for (String q: queries.keySet()) {
//            SearchResults<VocabACResult> results = 
//            		autoCompleteController.getVocabAutoCompleteResults(q);
//
//        	// More that zero results
//        	assertTrue(results.getTotalCount()>0);
//
//        	VocabACResult vResult = results.getResultObjects().get(0);
//
//        	// The vocab should be GO and the displayed vocab should be
//        	// function, process or component, depending on term
//        	assertTrue(vResult.getIsSynonym());
//
//        	String term = queries.get(q);
//        	assertTrue(vResult.getOriginalTerm().equals(term));
//    	}
//    }
//
//    @Test
//    public void testVerifyNoObsoleteGoTerms() throws Exception
//    {
//    	String query = "acrosin activity";
//    	assertNoMatches(query);
//    }
//
//    @Test
//    public void testVerifyNoObsoleteGoSynonyms() throws Exception
//    {
//    	String query = "alpha-acrosin";
//    	assertNoMatches(query);
//    }
//
//    @Test
//    public void testVerifyOnlyMatchingTextInSynonym () throws Exception
//    {
//    	String query = "biosynthesis vacuole";
//    	assertNoMatches(query);
//    }
//    
//    @Test
//    public void testVerifyTwoWordSearchDoesntCrossTermSynonymBoundary () throws Exception
//    {
//    	String query = "fungal vacuole";
//
//        SearchResults<VocabACResult> results = 
//        		autoCompleteController.getVocabAutoCompleteResults(query); 
//
//    	assertTrue(results.getTotalCount() > 0);
//
//    	for (VocabACResult vResult: results.getResultObjects())
//    	{
//    		assertTrue(vResult.getIsSynonym()==false);
//    	}
//    }
//
//    @Test
//    public void testVerifyLeadingWhitspaceStripped () throws Exception
//    {
//    	// Get results without whitespace
//    	String query = "obese";
//
//        SearchResults<VocabACResult> resultsNoWhitespace = 
//        		autoCompleteController.getVocabAutoCompleteResults(query); 
//
//    	// Get results with whitespace
//    	query = "	obese";
//        SearchResults<VocabACResult> results = 
//        		autoCompleteController.getVocabAutoCompleteResults(query); 
//
//    	Set<VocabACResult> s1 = new HashSet<VocabACResult>(resultsNoWhitespace.getResultObjects());
//    	Set<VocabACResult> s2 = new HashSet<VocabACResult>(results.getResultObjects());
//        assertTrue(s1.containsAll(s2));
//        assertTrue(s2.containsAll(s1));    	
//    }
//    
//    @Test
//    public void testVerifySpecialCharsAreStripped () throws Exception
//    {
//    	List<String> queries = new ArrayList<String>();
//    	queries.add("`~!@#$%^&*()_+-=[{]}\\|;:'\"/?.>,<ceramide biosynthesis");
//    	queries.add("ceramide biosynthesis`~!@#$%^&*()_+-=[{]}\\|;:'\"/?.>,<");
//    	queries.add("ceramide,`~!@#$%^&*()_+-=[{]}\\|;:'\"/?.>,< biosynthesis`~!@#$%^&*()_+-=[{]}\\|;:'\"/?.>,< `~!@#$%^&*()_+-=[{]}\\|;:'\"/?.>,<");
//    	String correctTerm = "ceramide biosynthesis";
//
//    	for (String q: queries) {
//        	assertTermFound(q, correctTerm);
//       	}
//    }
//
//    @Test
//    public void testVerifyNonsenseEntry() throws Exception
//    {
//    	String query = "asdf";
//        assertNoMatches(query);
//    }
//
//    @Test
//    public void testVerifyLongestTermComesBack() throws Exception
//    {
//    	String query = "oxidoreductase activity, acting on paired donors, with incorporation or reduction of molecular oxygen, 2-oxoglutarate as one donor, and incorporation of one atom each of oxygen into both donors";
//    	assertTermFound(query, query);
//    }
//
//    @Test
//    public void testVerifyLongestSynonymComesBack() throws Exception
//    {
//    	String query = "oxidoreductase activity, acting on paired donors, with incorporation or reduction of molecular oxygen, reduced iron-sulfur protein as one donor, and incorporation of one atom of oxygen";
//    	assertTermFound(query, query);
//    }
//
//    @Test
//    public void testVerifyMultiplePartialTokens () throws Exception
//    {
//    	String query = "synd aper";
//    	String correctTerm = "Apert Syndrome";
//    	assertTermFound(query, correctTerm);
//    }
//
//    @Test
//    public void testVerifyEveryPartialTokenMatchesAtLeastOnce () throws Exception
//    {
//    	String query = "le lo";
//    	SearchResults<VocabACResult> results = 
//        		autoCompleteController.getVocabAutoCompleteResults(query); 
//
//    	// More that zero results
//    	assertTrue(results.getTotalCount() > 0);
//
//    	// For each result
//    	for (VocabACResult vResult: results.getResultObjects())
//    	{
//    		
//    		// For each word in the result
//    		String term = vResult.getTerm();
//    		List<String> words = new ArrayList<String>(Arrays.asList(query.trim().split("[^a-zA-Z0-9]+")));
//    		words.remove("");
//
//        	boolean foundLe = false;
//        	boolean foundLo = false;
//    		for (String word: words)
//    		{
//    			if (word.toLowerCase().startsWith("le"))
//    			{
//    				foundLe = true;
//    			}
//    			if (word.toLowerCase().startsWith("lo"))
//    			{
//    				foundLo = true;
//    			}
//    			// Short circuit if both are found
//    			if(foundLe && foundLo)
//    			{
//    				break;
//    			}
//    		}
//    		// If the token wasn't found, then found will be false
//        	assertTrue("Bad term (no le and lo): "+term, foundLe && foundLo);
//    	}
//    }
//
//    @Test
//    public void testVerifyADExcluded() throws Exception
//    {
//    	List<String> queries = new ArrayList<String>();
//    	queries.add("dorsal mesentery of heart");
//    	queries.add("liver blood vessel");
//
//    	for (String q: queries) {
//    		assertNoMatches(q);
//       	}
//    }
//
//
//    @Test
//    public void testVerifyNoAlphaNumericTokenization() throws Exception
//    {
//    	List<String> queries = new ArrayList<String>();
//    	queries.add("12 atg 5 16");
//
//    	for (String q: queries) {
//    		assertNoMatches(q);
//       	}
//    }
//    
//    
//    //SNARE binded
//    @Test
//    public void testVerifyNoStemming() throws Exception
//    {
//    	String query = "SNARE binded";
//    	assertNoMatches(query);
//    }
//    
//    @Test
//    public void testNoWordConcatenation() throws Exception
//    {
//    	String query = "factors";
//    	String incorrectTerm = "platelet-activating factor-synthesizing enzyme activity";
//    	assertNoIncorrectTerm(query, incorrectTerm);
//    	incorrectTerm = "Anti-lipopolysaccharide factor/Scygonadin";
//    	assertNoIncorrectTerm(query, incorrectTerm);
//    }
//
//
//    @Test
//    public void testNoWordCaseChangeTokenizin() throws Exception
//    {
//    	String query = "di geor";
//    	String incorrectTerm = "DiGeorge Syndrome; DGS";
//    	assertNoIncorrectTerm(query, incorrectTerm);
//    }
//    
//    @Test
//    public void testGOHighLevelTermMarkerCount() throws Exception
//    {
//    	String vocab = "GO";
//    	
//        assertTermMarkerCount("biological_process",vocab,14845);
//        assertTermMarkerCount("cellular_component",vocab,16290);
//        assertTermMarkerCount("molecular_function",vocab,14922);
//    }
//    
//    @Test
//    public void testGOTermMarkerCountExcludesNOT() throws Exception
//    {
//    	String vocab = "GO";
//    	String query = "ubiquitin-protein ligase activity";
//    	int markerCount = 194;
//    	assertTermMarkerCount(query,vocab,markerCount);
//    }
//    
//    @Test
//    public void testGOTermMarkerCountExcludesNOTInChildren() throws Exception
//    {
//    	String vocab = "GO";
//    	String query = "small conjugating protein ligase activity";
//    	int markerCount = 208;
//    	assertTermMarkerCount(query,vocab,markerCount);
//    }
//    
//    @Test
//    public void testGOProcessTermMarkerCount() throws Exception
//    {
//    	String vocab = "GO";
//    	String query = "amino acid transport";
//    	int markerCount = 110;
//    	assertTermMarkerCount(query,vocab,markerCount);
//    }
//    @Test
//    public void testGOFunctionTermMarkerCount() throws Exception
//    {
//    	String vocab = "GO";
//    	String query = "hyaluronic acid binding";
//    	int markerCount = 16;
//    	assertTermMarkerCount(query,vocab,markerCount);
//    }
//    @Test
//    public void testGOComponentTermMarkerCount() throws Exception
//    {
//    	String vocab = "GO";
//    	String query = "polysome";
//    	int markerCount = 23;
//    	assertTermMarkerCount(query,vocab,markerCount);
//    }
//    
//    @Test
//    public void testOMIMTermMarkerCountExcludesNOT() throws Exception
//    {
//    	String vocab = "OMIM";
//    	String query = "Sea-Blue Histiocyte Disease";
//    	int markerCount = 0;
//    	assertTermMarkerCount(query,vocab,markerCount);
//    }
//    
//    @Test
//    public void testMPTermMarkerCountExcludesNormal() throws Exception
//    {
//    	String vocab = "Mammalian Phenotype";
//    	String query = "decreased lateral semicircular canal size";
//    	int markerCount = 16;
//    	assertTermMarkerCount(query,vocab,markerCount);
//    }
}

