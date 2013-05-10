package org.jax.mgi.fewi.test.mock;

import java.util.Arrays;
import java.util.List;

import org.jax.mgi.fewi.controller.GXDController;
import org.jax.mgi.fewi.forms.GxdQueryForm;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype") 
public class AbstractMockGxdQuery implements MockQuery
{
	public String markersUrl = "/gxd/markers/json";
	public String assaysUrl = "/gxd/assays/json";
	public String resultsUrl = "/gxd/results/json";
	
	public int pageSize = 1000;
	public String sort = "default";
	
    // The class being tested is autowired via spring's DI
    protected GXDController gxdController;
    
	GxdQueryForm gqf = new GxdQueryForm();
	
	// set nomenclature
	public void setNomenclature(String nomen)
	{
		gqf.setNomenclature(nomen);
	}
	
	//set vocab term ID
	public void setAnnotationId(String annotationId)
	{
		gqf.setAnnotationId(annotationId);
	}
	
	//set detected
	public void setDetected(String detected)
	{
		gqf.setDetected(detected);
	}
	
	//set theiler stage (s)
	public void setTheilerStage(Integer stage)
	{
		gqf.setTheilerStage(Arrays.asList(stage));
	}
	public void setTheilerStage(List<Integer> stages)
	{
		gqf.setTheilerStage(stages);
	}
	public void setDifTheilerStage(Integer stage)
	{
		gqf.setDifTheilerStage(Arrays.asList(stage));
	}
	public void setDifTheilerStage(List<Integer> stages)
	{
		gqf.setDifTheilerStage(stages);
	}
	
	public void setAge(String age)
	{
		gqf.setAge(Arrays.asList(age));
	}
	public void setAge(List<String> ages)
	{
		gqf.setAge(ages);
	}
	
	//set assay type (s)
	public void setAssayType(String assayType)
	{
		gqf.setAssayType(Arrays.asList(assayType));
	}
	public void setAssayType(List<String> assayTypes)
	{
		gqf.setAssayType(assayTypes);
	}
	public void setStructure(String structure)
	{
		gqf.setStructure(structure);
	}
	public void setDifStructure(String difStructure)
	{
		gqf.setDifStructure(difStructure);
	}
	public void setIsWildType(boolean isWildType)
	{
		if(isWildType) gqf.setIsWildType("true");
		//else gqf.setIsWildType("false");
	}
	public void setMutatedIn(String mutatedIn)
	{
		gqf.setMutatedIn(mutatedIn);
	}

	
}
