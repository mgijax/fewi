package marker;

import java.util.ArrayList;
import java.util.List;

import org.jax.mgi.fewi.controller.MarkerController;
import org.jax.mgi.fewi.searchUtil.entities.SolrSummaryMarker;
import org.jax.mgi.fewi.test.base.BaseConcordionTest;
import org.jax.mgi.fewi.test.mock.MockMarkerControllerQuery;
import org.jax.mgi.fewi.util.QueryParser;
import org.springframework.beans.factory.annotation.Autowired;

public class MarkerSummaryFromQueryTest extends BaseConcordionTest 
{
	@Autowired
	MarkerController markerController;
	
	
	// Nomen Query
	public int getMarkerCountByNomen(String nomen) throws Exception
	{
		MockMarkerControllerQuery mq = this.getMockQuery().markerController(markerController);
		mq.setNomen(nomen);
		return mq.getMarkers().getTotalCount();
	}
	public List<String> getSymbolsByNomen(String nomen) throws Exception
	{
		MockMarkerControllerQuery mq = this.getMockQuery().markerController(markerController);
		mq.setNomen(nomen);
		return getSymbolsByMQ(mq);
	}
	
	// Feature type query
	public int getMarkerCountByMcv(String mcvKey) throws Exception
	{
		MockMarkerControllerQuery mq = this.getMockQuery().markerController(markerController);
		mq.setMcvKey(mcvKey);
		return mq.getMarkers().getTotalCount();
	}
	public List<String> getSymbolsByMcv(String mcvKey) throws Exception
	{
		MockMarkerControllerQuery mq = this.getMockQuery().markerController(markerController);
		mq.setMcvKey(mcvKey);
		return getSymbolsByMQ(mq);
	}
	
	// Chromosome Query
	public int getMarkerCountByChromosome(String chrString) throws Exception
	{
		MockMarkerControllerQuery mq = this.getMockQuery().markerController(markerController);
		mq.setChromosome(decodeChrs(chrString));
		return mq.getMarkers().getTotalCount();
	}
	public List<String> getSymbolsByChromosome(String chrString) throws Exception
	{
		MockMarkerControllerQuery mq = this.getMockQuery().markerController(markerController);
		mq.setChromosome(decodeChrs(chrString));
		return getSymbolsByMQ(mq);
	}
	
	// Coordinate Query
	public int getMarkerCountByCoordinate(String coordinate) throws Exception
	{
		MockMarkerControllerQuery mq = this.getMockQuery().markerController(markerController);
		mq.setCoordinate(coordinate);
		return mq.getMarkers().getTotalCount();
	}
	public List<String> getSymbolsByCoordinate(String coordinate) throws Exception
	{
		MockMarkerControllerQuery mq = this.getMockQuery().markerController(markerController);
		mq.setCoordinate(coordinate);
		return getSymbolsByMQ(mq);
	}
	
	// Cm Query
	public int getMarkerCountByCm(String cm) throws Exception
	{
		MockMarkerControllerQuery mq = this.getMockQuery().markerController(markerController);
		mq.setCm(cm);
		return mq.getMarkers().getTotalCount();
	}
	public List<String> getSymbolsByCm(String cm) throws Exception
	{
		MockMarkerControllerQuery mq = this.getMockQuery().markerController(markerController);
		mq.setCm(cm);
		return getSymbolsByMQ(mq);
	}
	
	public int getMarkerCountByRange(String startMarker,String endMarker) throws Exception
	{
		MockMarkerControllerQuery mq = this.getMockQuery().markerController(markerController);
		mq.setMarkerRange(startMarker,endMarker);
		return mq.getMarkers().getTotalCount();
	}
	public List<String> getSymbolsByRange(String startMarker,String endMarker) throws Exception
	{
		MockMarkerControllerQuery mq = this.getMockQuery().markerController(markerController);
		mq.setMarkerRange(startMarker,endMarker);
		return getSymbolsByMQ(mq);
	}
	
	// Go Query
	public int getMarkerCountByGo(String go) throws Exception
	{
		MockMarkerControllerQuery mq = this.getMockQuery().markerController(markerController);
		mq.setGo(go);
		return mq.getMarkers().getTotalCount();
	}
	public List<String> getSymbolsByGo(String go) throws Exception
	{
		MockMarkerControllerQuery mq = this.getMockQuery().markerController(markerController);
		mq.setGo(go);
		return getSymbolsByMQ(mq);
	}
	
	// Interpro Query
	public int getMarkerCountByInterpro(String interpro) throws Exception
	{
		MockMarkerControllerQuery mq = this.getMockQuery().markerController(markerController);
		mq.setInterpro(interpro);
		return mq.getMarkers().getTotalCount();
	}
	public List<String> getSymbolsByInterpro(String interpro) throws Exception
	{
		MockMarkerControllerQuery mq = this.getMockQuery().markerController(markerController);
		mq.setInterpro(interpro);
		return getSymbolsByMQ(mq);
	}
		
	// Phenotype Query
	public int getMarkerCountByPhenotype(String phenotype) throws Exception
	{
		MockMarkerControllerQuery mq = this.getMockQuery().markerController(markerController);
		mq.setPhenotype(phenotype);
		return mq.getMarkers().getTotalCount();
	}
	public List<String> getSymbolsByPhenotype(String phenotype) throws Exception
	{
		MockMarkerControllerQuery mq = this.getMockQuery().markerController(markerController);
		mq.setPhenotype(phenotype);
		return getSymbolsByMQ(mq);
	}
	
	
	/* Private helper methods */
	private List<String> getSymbolsByMQ(MockMarkerControllerQuery mq) throws Exception
	{
		List<String> symbols = new ArrayList<String>();
		
		for(SolrSummaryMarker marker : mq.getMarkers().getResultObjects())
		{
			symbols.add(marker.getSymbol());
		}
		return symbols;
	}
	
	private List<String> decodeChrs(String chrString)
	{
		return QueryParser.tokeniseOnWhitespaceAndComma(chrString);
	}
}
	
