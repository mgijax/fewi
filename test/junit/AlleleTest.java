package junit;

import java.util.List;

import mgi.frontend.datamodel.Allele;

import org.jax.mgi.fewi.finder.AlleleFinder;
import org.jax.mgi.fewi.searchUtil.Filter;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:applicationContext-ci.xml"})
@TransactionConfiguration
@Transactional
public class AlleleTest {

	@Autowired
    private AnnotationMethodHandlerAdapter handler;

    // The class being tested is autowired via spring's DI
    @Autowired
    private AlleleFinder alleleFinder;
	    
	@Test 
	public void setSomeAlleleBiz()
	{
		String alleleID = "MGI:1857259";
		List<Allele> alleles = alleleFinder.getAlleleByID(alleleID);
		for(Allele a : alleles)
		{
			System.out.println(a);
		}
		
		SearchParams params = new SearchParams();
		params.setFilter(new Filter(SearchConstants.ALL_ID,alleleID));
		SearchResults<Allele> sr = new SearchResults<Allele>();
		sr = alleleFinder.getAlleleByID(params);
		for(Allele a : sr.getResultObjects())
		{
			System.out.println(a);
		}
	}
}
