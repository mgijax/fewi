package org.jax.mgi.fewi.test.concordion;

import org.apache.commons.lang.StringUtils;
import org.concordion.api.AbstractCommand;
import org.concordion.api.CommandCall;
import org.concordion.api.Element;
import org.concordion.api.Evaluator;
import org.concordion.api.ResultRecorder;
import org.concordion.internal.util.Check;
import org.jax.mgi.fewi.test.data.DynamicTestData;


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
        	// resolve the id into data.
        	String data = getDynamicData(id.toString());
        	if(data != null && !data.equals(""))
        	{
        		if(element.getAttributeValue("processed") != null) return;
        		element.addAttribute("processed", "true");
        		// redundancy check.
        		if (!element.getText().equalsIgnoreCase(data))
        		{
					if (data.contains("ROW["))
					{
						formatHTMLTableRows(element,data);
					}
					else
					{
						element.appendText(data);
					}
        		}
        	}
        	else
        	{
        		commandCall.setElement(new Element("i").appendText("No data exists for: "+id));
        	}
        } else {
            Element child = new Element("em");
            child.appendText("null");
            element.appendChild(child);
        }
    }
    
	/*
	 * Formats row/object data into html table rows
	 * @param data
	 * @return
	 */
	private void formatHTMLTableRows(Element element,String data)
	{
		Element currentEl = element;
		Element sibling = element;
		
		// I'm not sure how to regex this, but for now I will split on "]," and re-add the ] bracket to the split rows
		String[] rows = data.split("],");
		if (rows.length > 1)
		{
			for(int i=0;i<rows.length-1;i++)
			rows[i] += "]";
		}

		boolean first = true;
		for (String row : rows)
		{
			if (first) first = false;
			else
			{
				currentEl = new Element("tr");
				sibling.appendSister(currentEl);
			}
			
			row = row.substring(4, row.length()-1);
			String[] cols = row.split(",");
			for (String col : cols)
			{
				Element newTd = new Element("td");
				newTd.appendText(col);
				currentEl.appendChild(newTd);
			}
		}
	}
	
    private String getDynamicData(String id)
    {
    	return DynamicTestData.get(id);
    }
}
