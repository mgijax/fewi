package org.jax.mgi.fewi.test.mock;

import org.jax.mgi.fewi.controller.DiseasePortalController;
import org.jax.mgi.fewi.forms.DiseasePortalQueryForm;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype") 
public class AbstractMockHdpQuery implements MockQuery
{
	public String markersUrl = "/diseasePortal/markers/json";
	public String diseaseUrl = "/diseasePortal/diseases/json";
	public String gridUrl = "/diseasePortal/grid";
	public String systemPopupUrl = "/diseasePortal/gridSystemCell";
	public String diseasePopupUrl = "/diseasePortal/gridDiseaseCell";

	
	public int pageSize = 10000;
	public String sort = "default";
	
    // The class being tested is autowired via spring's DI
    protected DiseasePortalController hdpController;
    
	DiseasePortalQueryForm qf = new DiseasePortalQueryForm();
	
	// set phenotypes or diseass
	public void setPhenotypes(String pheno)
	{
		qf.setPhenotypes(pheno);
	}
	
	public void setGenes(String genes)
	{
		qf.setGenes(genes);
	}
	
	// set location
	public void setMouseLocations(String locations)
	{
		qf.setLocations(locations);
		qf.setOrganism(DiseasePortalQueryForm.MOUSE);
	}
	
	public void setHumanLocations(String locations)
	{
		qf.setLocations(locations);
		qf.setOrganism(DiseasePortalQueryForm.HUMAN);
	}
}
