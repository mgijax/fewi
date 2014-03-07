package allele;

import java.util.ArrayList;
import java.util.List;

import mgi.frontend.datamodel.Allele;

import org.jax.mgi.fewi.controller.AlleleController;
import org.jax.mgi.fewi.test.base.BaseConcordionTest;
import org.jax.mgi.fewi.test.mock.MockAlleleControllerQuery;
import org.springframework.beans.factory.annotation.Autowired;


public class AlleleSummaryTest extends BaseConcordionTest 
{
	@Autowired
	AlleleController alleleController;
	
	public List<String> getAllelesByNomen(String nomen) throws Exception
	{
		MockAlleleControllerQuery mq = this.getMockQuery().alleleController(alleleController);
		mq.setNomen(nomen);
		
		List<String> alleleSymbols = new ArrayList<String>();
		for(Allele a : mq.getAlleles().getResultObjects())
		{
			alleleSymbols.add(a.getSymbol());
		}
		return alleleSymbols;
	}
	
	public int getAlleleCountByNomen(String nomen) throws Exception
	{
		MockAlleleControllerQuery mq = this.getMockQuery().alleleController(alleleController);
		mq.setNomen(nomen);
		
		return mq.getAlleles().getTotalCount();
	}
	
}
	
