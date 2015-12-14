package org.jax.mgi.fewi.test.mock;

import org.jax.mgi.fewi.controller.GOController;
import org.jax.mgi.fewi.forms.MarkerAnnotationQueryForm;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype") 
public class AbstractMockGOQuery implements MockQuery
{
	public String goUrl = "/go/summary/json";
	
	public int pageSize = 200;
	public String sort = "default";
	
    // The class being tested is autowired via spring's DI
    protected GOController goController;
    
    MarkerAnnotationQueryForm goqf = new MarkerAnnotationQueryForm();
	
	// set Marker key
	public void setMrkKey(String mrkKey)
	{
		goqf.setMrkKey(mrkKey);
	}
	
	// set Reference key
	public void setReferenceKey(String referenceKey)
	{
		goqf.setReferenceKey(referenceKey);
	}
	
	// set Term
	public void setTermId(String termId)
	{
		goqf.setGoID(termId);
	}
	
	// set Slim Grid Header
	public void setSlimHeaderTerm(String slimHeaderTerm)
	{
		goqf.setHeader(slimHeaderTerm);
	}
}
