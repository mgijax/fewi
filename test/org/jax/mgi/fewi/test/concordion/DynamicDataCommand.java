package org.jax.mgi.fewi.test.concordion;

import org.apache.commons.lang.StringUtils;
import org.concordion.api.AbstractCommand;
import org.concordion.api.CommandCall;
import org.concordion.api.Element;
import org.concordion.api.Evaluator;
import org.concordion.api.ResultRecorder;
import org.concordion.internal.util.Check;
import org.jax.mgi.fewi.test.data.DynamicTestData;
import org.springframework.beans.factory.annotation.Autowired;


/**
 *  A command that echos the value based on the input id for dynamic test data
 *  
 * @author kstone
 *
 */
public class DynamicDataCommand extends AbstractCommand {
	
    @Override
    public void verify(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder) {
    	Check.isFalse(commandCall.hasChildCommands(), "Nesting commands inside a 'dynamicData' is not supported");
        
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
						element.appendText(data);
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
    
    protected String[] preprocessRowMarkup(String data)
    {
    	// I'm not sure how to regex this, but for now I will split on "]," and re-add the ] bracket to the split rows
		String[] rows = data.split("],");
		for(int i=0;i<rows.length;i++)
		{
			// remove the ROW[ text
			rows[i] = rows[i].substring(4);
			if(i==(rows.length-1))
			{
				// last row
				// remove the last ]
				rows[i] = rows[i].substring(0,rows[i].length()-1);
			}
		}
		return rows;
    }
	/*
	 * Formats row/object data into html table rows
	 * @param data
	 * @return
	 */
	protected void formatHTMLTableRows(Element element,String[] rows)
	{
		Element currentEl = element;
		Element sibling = element;

		boolean first = true;
		for (String row : rows)
		{
			if (first) first = false;
			else
			{
				currentEl = new Element("tr");
				sibling.appendSister(currentEl);
			}
			
			//row = row.substring(4, row.length()-1);
			String[] cols = row.split(",");
			for (String col : cols)
			{
				Element newTd = new Element("td");
				newTd.appendText(col);
				currentEl.appendChild(newTd);
			}
		}
	}
	
    protected String getDynamicData(String id,Evaluator evaluator)
    {
    	// resolve the id into data by calling the get() method on BaseConcordionTest class
    	// I'm not sure how the evaluator scope works, but let's set it to an unlikely variable name just to be safe
    	evaluator.setVariable("#varDD1234", id.toString());
    	String data = (String) evaluator.evaluate("get(#varDD1234)");
    	return data;
    	//return dd.get(id);
    }
}
