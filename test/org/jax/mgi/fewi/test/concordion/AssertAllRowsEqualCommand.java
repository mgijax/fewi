package org.jax.mgi.fewi.test.concordion;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.concordion.api.AbstractCommand;
import org.concordion.api.CommandCall;
import org.concordion.api.Element;
import org.concordion.api.Evaluator;
import org.concordion.api.Result;
import org.concordion.api.ResultRecorder;
import org.concordion.api.listener.AssertEqualsListener;
import org.concordion.api.listener.AssertFailureEvent;
import org.concordion.api.listener.AssertSuccessEvent;
import org.concordion.internal.BrowserStyleWhitespaceComparator;
import org.concordion.internal.util.Announcer;
import org.concordion.internal.util.Check;

public class AssertAllRowsEqualCommand extends AbstractCommand {

    private Announcer<AssertEqualsListener> listeners = Announcer.to(AssertEqualsListener.class);
    private final Comparator<Object> comparator;

    public AssertAllRowsEqualCommand() {
        this(new BrowserStyleWhitespaceComparator());
    }
    
    public AssertAllRowsEqualCommand(Comparator<Object> comparator) {
        this.comparator = comparator;
    }
    
    public void addAssertEqualsListener(AssertEqualsListener listener) {
        listeners.addListener(listener);
    }

    public void removeAssertEqualsListener(AssertEqualsListener listener) {
        listeners.removeListener(listener);
    }
    
    @Override
    public void verify(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder) {
        Check.isFalse(commandCall.hasChildCommands(), "Nesting commands inside an 'assertEquals' is not supported");
        
        Element element = commandCall.getElement();
        
        Object actuals = evaluator.evaluate(commandCall.getExpression());
        Check.isTrue(actuals instanceof Iterable, actuals.getClass().getCanonicalName() + " is not Iterable");
        Iterable<Object> results = (Iterable<Object>) actuals;
        
        String expected = element.getText();
        List<Object> failures = new ArrayList<Object>();
        
        // check equality of each result
        int resultSize = 0;
        for(Object result : results)
        {
        	resultSize ++;
        	if(comparator.compare(result, expected) != 0)
        	{
        		// if any one result doesn't match, then we fail
        		failures.add(result);
        	}
        }
        if(failures.size()==0 && resultSize > 0) 
        {
        	// only succeed if there were no failed matches
        	resultRecorder.record(Result.SUCCESS);
        	announceSuccess(element);
        }
        else
        {
        	//announce failures
        	String failString;
        	if(resultSize > 0)
        	{
        		// distinct the list of failures
        		Set<Object> failureSet = new HashSet<Object>(failures);
	        	failString = failures.size()+" of "+resultSize+" results failed: ["+StringUtils.join(failureSet.toArray(),", ")+"]";
        	}
        	else
        	{
        		failString = "0 results returned";
        	}
        	resultRecorder.record(Result.FAILURE);
            announceFailure(element, expected, failString);
        }
    }
    
    private void announceSuccess(Element element) {
        listeners.announce().successReported(new AssertSuccessEvent(element));
    }

    private void announceFailure(Element element, String expected, Object actual) {
        listeners.announce().failureReported(new AssertFailureEvent(element, expected, actual));
    }
}
