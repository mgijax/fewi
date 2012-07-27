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

public class AssertNoResultsContainCommand extends AbstractCommand {

    private Announcer<AssertEqualsListener> listeners = Announcer.to(AssertEqualsListener.class);
    private final Comparator<Object> comparator;

    public AssertNoResultsContainCommand() {
        this(new BrowserStyleWhitespaceComparator());
    }
    
    public AssertNoResultsContainCommand(Comparator<Object> comparator) {
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
        
        // check equality of each result
        boolean fail = false;
        for(Object result : results)
        {
        	if(comparator.compare(result, expected) == 0)
        	{
        		// if any one result matches, then we fail
        		fail = true;
        	}
        }
        if(fail)
        {
        	String failString = "found "+expected+" in result set";
        	resultRecorder.record(Result.FAILURE);
            announceFailure(element, expected, failString);
        }
        else
        {
        	// even if zero results were returned, we still register success.
	        resultRecorder.record(Result.SUCCESS);
	    	announceSuccess(element);
        }
    }
    
    private void announceSuccess(Element element) {
        listeners.announce().successReported(new AssertSuccessEvent(element));
    }

    private void announceFailure(Element element, String expected, Object actual) {
        listeners.announce().failureReported(new AssertFailureEvent(element, expected, actual));
    }
}
