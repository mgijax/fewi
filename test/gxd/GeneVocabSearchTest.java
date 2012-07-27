package gxd;

import java.util.ArrayList;
import java.util.List;

import org.jax.mgi.fewi.controller.GXDController;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.entities.SolrGxdMarker;
import org.jax.mgi.fewi.test.concordion.BaseConcordionTest;
import org.jax.mgi.fewi.test.mock.MockGxdControllerQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;

public class GeneVocabSearchTest extends BaseConcordionTest {

    @Autowired
    private AnnotationMethodHandlerAdapter handler;

    // The class being tested is autowired via spring's DI
    @Autowired
    private GXDController gxdController;
	
	public List<String> getGeneSymbolsByTermId(String termId) throws Exception
	{
		MockGxdControllerQuery mq = new MockGxdControllerQuery(gxdController);
		mq.setAnnotationId(termId);
		List<String> symbols = new ArrayList<String>();
		mq.pageSize=10000;
		SearchResults<SolrGxdMarker> sr = mq.getGenes();
		for(SolrGxdMarker m : sr.getResultObjects())
		{
			symbols.add(m.getSymbol());
		}
		return symbols;
	}
}
