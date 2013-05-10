package org.jax.mgi.fewi.test.concordion;

import org.concordion.api.CommandCall;
import org.concordion.api.Element;
import org.concordion.api.Evaluator;
import org.concordion.api.ResultRecorder;
import org.concordion.internal.util.Check;

/*
 * This is a subclass of DynamicDataCommand that puts
 * all test data in the format of an HTML table, regardless of any
 * ROW[] markup in the test data
 */
public class DynamicDataTableCommand extends DynamicDataCommand
{

    @Override
    public void verify(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder) {
    	Check.isFalse(commandCall.hasChildCommands(), "Nesting commands inside a 'dynamicDataTable' is not supported");
        
        String id = commandCall.getExpression();
        
        Element element = commandCall.getElement();
        if (id != null) {
    		// redundancy check.
    		if(element.getAttributeValue("processed") != null) return;
    		element.addAttribute("processed", "true");
        	
    		String data = getDynamicData(id,evaluator);
        	if(data != null && !data.trim().equals(""))
        	{

        		if (!element.getText().equalsIgnoreCase(data))
        		{
					if (data.contains("ROW["))
					{
						formatHTMLTableRows(element,preprocessRowMarkup(data));
					}
					else
					{
						formatHTMLTableRows(element,preprocessDataWithoutMarkup(data));
					}
        		}
        	}
        	else
        	{
        		element.appendText("No data exists for: "+id);
        		//commandCall.setElement(new Element("i").appendText("No data exists for: "+id));
        	}
        } else {
            Element child = new Element("em");
            child.appendText("null");
            element.appendChild(child);
        }
    }
    
    protected String[] preprocessDataWithoutMarkup(String data)
    {
    	String[] rows = data.split(",");
    	return rows;
    }
}
