package org.jax.mgi.fewi.test.concordion;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.concordion.api.AbstractCommand;
import org.concordion.api.CommandCall;
import org.concordion.api.Element;
import org.concordion.api.Evaluator;
import org.concordion.api.ResultRecorder;
import org.concordion.internal.util.Check;

public class EchoCommand extends AbstractCommand {

	private Map<String,Object> trackingMap= new HashMap<String,Object>();
	private Set<String> trackingSet = new HashSet<String>();
	
    @Override
    public void verify(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder) {
        Check.isFalse(commandCall.hasChildCommands(), "Nesting commands inside an 'echo' is not supported");
        
        Object result = evaluator.evaluate(commandCall.getExpression());
        
        Element element = commandCall.getElement();
        if (result != null) {
            // set up a failsafe so that this instance of echo command can only execute once
            if(trackedCommand(commandCall)) return;
            element.appendText(result.toString());
        } else {
//            Element child = new Element("em");
//            child.appendText("nullY");
//            element.appendChild(child);
        	// Don't even do anything if it's null. If it's null, that is the user's own fault.
        }
    }
    
    private boolean trackedCommand(CommandCall cc)
    {
    	String trackingID = cc.toString();
    	if(trackingSet.contains(trackingID)) return true;
    	trackingSet.add(trackingID);
    	return false;
    }
}
