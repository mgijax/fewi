package allele;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mgi.frontend.datamodel.Allele;

import org.jax.mgi.fewi.controller.AlleleController;
import org.jax.mgi.fewi.test.base.BaseConcordionTest;
import org.jax.mgi.fewi.test.mock.MockAlleleControllerQuery;
import org.springframework.beans.factory.annotation.Autowired;


public class QueryFormNomenclatureTest extends BaseConcordionTest {
	@Autowired
	AlleleController alleleController;
	
	public List<String> getAlleleSymbolsByNomen(String nomen) throws Exception
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
	
	public List<String> getMarkerSymbolsByNomen(String nomen) throws Exception
	{
		MockAlleleControllerQuery mq = this.getMockQuery().alleleController(alleleController);
		mq.setNomen(nomen);
		
		Set<String> symbols = new HashSet<String>();
		for(Allele a : mq.getAlleles().getResultObjects())
		{
			symbols.add(a.getMarker().getSymbol());
		}
		return new ArrayList<String>(symbols);
	}
	
	public String getFirstMarkerSymbolByNomen(String nomen) throws Exception
	{
		List<String> symbols = getMarkerSymbolsByNomen(nomen);
		if(symbols.size()>0) return symbols.get(0);
		return "";
	}
	
	public String getFirstAlleleSymbolByNomen(String nomen) throws Exception
	{
		List<String> symbols = getAlleleSymbolsByNomen(nomen);
		if(symbols.size()>0) return symbols.get(0);
		return "";
	}
	
	public int getAlleleCountByNomen(String nomen) throws Exception
	{
		MockAlleleControllerQuery mq = this.getMockQuery().alleleController(alleleController);
		mq.setNomen(nomen);
		
		return mq.getAlleles().getTotalCount();
	}
	
}
	
