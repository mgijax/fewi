package marker;

import java.util.ArrayList;
import java.util.List;

import org.jax.mgi.fewi.controller.MarkerController;
import org.jax.mgi.fewi.forms.MarkerQueryForm;
import org.jax.mgi.fewi.searchUtil.entities.SolrSummaryMarker;
import org.jax.mgi.fewi.test.base.BaseConcordionTest;
import org.jax.mgi.fewi.test.mock.MockMarkerControllerQuery;
import org.jax.mgi.fewi.util.QueryParser;
import org.springframework.beans.factory.annotation.Autowired;


/*
 * TIP: 
 * Feature types must be mapped to term keys
 * 
 *              term                 | term_key 
--------------------------------------+----------
 all feature types                    |  6238159
 BAC end                              |  6442608
 BAC/YAC end                          |  6238177
 chromosomal deletion                 |  7196768
 chromosomal duplication              |  7196774
 chromosomal inversion                |  7196770
 chromosomal translocation            |  7196773
 chromosomal transposition            |  7196775
 complex/cluster/region               |  6238175
 cytogenetic marker                   |  6238176
 DNA segment                          |  6238179
 endogenous retroviral region         |  9272146
 gene                                 |  6238160
 gene segment                         |  6238171
 heritable phenotypic marker          |  6238170
 insertion                            |  7196769
 lincRNA gene                         |  6238169
 minisatellite                        |  7648968
 miRNA gene                           |  6238167
 non-coding RNA gene                  |  6238162
 other feature type                   |  6238185
 other genome feature                 |  6238178
 PAC end                              |  6442610
 polymorphic pseudogene               |  7288449
 protein coding gene                  |  6238161
 pseudogene                           |  7313348
 pseudogenic gene segment             |  6967235
 pseudogenic region                   |  7288448
 QTL                                  |  6238173
 reciprocal chromosomal translocation |  7196772
 retrotransposon                      |  7648966
 RNase MRP RNA gene                   |  6238182
 RNase P RNA gene                     |  6238181
 Robertsonian fusion                  |  7196771
 rRNA gene                            |  6238163
 scRNA gene                           |  6238168
 snoRNA gene                          |  6238166
 snRNA gene                           |  6238165
 SRP RNA gene                         |  6238180
 telomerase RNA gene                  |  6238183
 telomere                             |  7648967
 transgene                            |  6238174
 tRNA gene                            |  6238164
 unclassified cytogenetic marker      |  7222413
 unclassified gene                    |  6238184
 unclassified non-coding RNA gene     |  6238186
 unclassified other genome feature    |  7648969
 YAC end                              |  6442609
 */

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
	public int getMarkerCountByMcv(String mcvKeys) throws Exception
	{
		MockMarkerControllerQuery mq = this.getMockQuery().markerController(markerController);
		mq.setMcvKey(decodeCommaValues(mcvKeys));
		return mq.getMarkers().getTotalCount();
	}
	public int getMarkerCountAllMcv() throws Exception
	{
		MockMarkerControllerQuery mq = this.getMockQuery().markerController(markerController);
		mq.setMcvKey(this.getAllMcvKeys());
		return mq.getMarkers().getTotalCount();
	}
	public List<String> getSymbolsByMcv(String mcvKeys) throws Exception
	{
		MockMarkerControllerQuery mq = this.getMockQuery().markerController(markerController);
		mq.setMcvKey(decodeCommaValues(mcvKeys));
		return getSymbolsByMQ(mq);
	}
	
	// Chromosome Query
	public int getMarkerCountByChromosome(String chrString) throws Exception
	{
		MockMarkerControllerQuery mq = this.getMockQuery().markerController(markerController);
		mq.setChromosome(decodeCommaValues(chrString));
		return mq.getMarkers().getTotalCount();
	}
	public List<String> getSymbolsByChromosome(String chrString) throws Exception
	{
		MockMarkerControllerQuery mq = this.getMockQuery().markerController(markerController);
		mq.setChromosome(decodeCommaValues(chrString));
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
	
	// Range Query
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
	
	
	/*
	 * Multiple Query Parameters
	 * 
	 *  Note: we only add combinations when we need to. This is not exhaustive, obviously.
	 */
	public List<String> getSymbolsByChrCm(String chr, String cm) throws Exception
	{
		MockMarkerControllerQuery mq = this.getMockQuery().markerController(markerController);
		mq.setChromosome(decodeCommaValues(chr));
		mq.setCm(cm);
		return getSymbolsByMQ(mq);
	}
	public List<String> getSymbolsByChrCoord(String chr, String coord) throws Exception
	{
		MockMarkerControllerQuery mq = this.getMockQuery().markerController(markerController);
		mq.setChromosome(decodeCommaValues(chr));
		mq.setCoordinate(coord);
		return getSymbolsByMQ(mq);
	}
	public List<String> getSymbolsByChrCoordMbp(String chr, String coord) throws Exception
	{
		MockMarkerControllerQuery mq = this.getMockQuery().markerController(markerController);
		mq.setChromosome(decodeCommaValues(chr));
		mq.setCoordUnit(MarkerQueryForm.COORD_UNIT_MBP);
		mq.setCoordinate(coord);
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
	
	private List<String> decodeCommaValues(String input)
	{
		return QueryParser.tokeniseOnWhitespaceAndComma(input);
	}
	private List<String> getAllMcvKeys()
	{
		markerController.initQueryForm();
		return new ArrayList<String>(MarkerQueryForm.markerTypeKeyToDisplayMap.keySet());
	}
}
	
