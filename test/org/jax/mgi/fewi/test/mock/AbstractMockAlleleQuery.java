package org.jax.mgi.fewi.test.mock;

import org.jax.mgi.fewi.controller.AlleleController;
import org.jax.mgi.fewi.forms.AlleleQueryForm;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype") 
public class AbstractMockAlleleQuery implements MockQuery
{
	public String allelesUrl = "/allele/summary/json";
	
	public int pageSize = 200;
	public String sort = "default";
	
    // The class being tested is autowired via spring's DI
    protected AlleleController alleleController;
    
	AlleleQueryForm aqf = new AlleleQueryForm();
	
	// set nomenclature
	public void setNomen(String nomen)
	{
		aqf.setNomen(nomen);
	}
}
