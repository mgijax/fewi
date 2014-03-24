package org.jax.mgi.fewi.test.mock;

import java.util.Arrays;
import java.util.List;

import org.jax.mgi.fewi.controller.MarkerController;
import org.jax.mgi.fewi.forms.MarkerQueryForm;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype") 
public class AbstractMockMarkerQuery implements MockQuery
{
	public String markersUrl = "/marker/json";
	
	public int pageSize = 2000;
	public String sort = "default";
	
    // The class being tested is autowired via spring's DI
    protected MarkerController markerController;
    
	MarkerQueryForm mqf = new MarkerQueryForm();
	
	// set nomenclature
	public void setNomen(String nomen)
	{
		mqf.setNomen(nomen);
	}
	
	//set feature type
	public void setMcvKey(String key)
	{
		setMcvKey(Arrays.asList(key));
	}
	public void setMcvKey(List<String> keys)
	{
		mqf.setMcv(keys);
	}
	
	//set chromosome
	public void setChromosome(String chr)
	{
		setChromosome(Arrays.asList(chr));
	}
	public void setChromosome(List<String> chr)
	{
		mqf.setChromosome(chr);
	}
	
	//set cm
	public void setCm(String cm)
	{
		mqf.setCm(cm);
	}
	
	//set coordinate
	public void setCoordinate(String coord)
	{
		mqf.setCoordinate(coord);
	}
	
	// set coordinate unit
	public void setCoordUnit(String coordUnit)
	{
		mqf.setCoordUnit(coordUnit);
	}
	
	//set marker range
	public void setMarkerRange(String startMarker,String endMarker)
	{
		mqf.setStartMarker(startMarker);
		mqf.setEndMarker(endMarker);
	}
	
	// set go
	public void setGo(String go)
	{
		mqf.setGo(go);
	}
	
	public void setGoVocab(List<String> goVocab)
	{
		mqf.setGoVocab(goVocab);
	}
	
	// set interpro
	public void setInterpro(String interpro)
	{
		mqf.setInterpro(interpro);
	}
	
	//set phenotype
	public void setPhenotype(String phenotype)
	{
		mqf.setPhenotype(phenotype);
	}	
}
