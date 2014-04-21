package org.jax.mgi.fewi.test.concordion;



import java.util.Collection;
import java.util.Comparator;

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

public class AssertEmptyCommand extends AbstractCommand {

    private final Announcer<AssertEqualsListener> listeners = Announcer.to(AssertEqualsListener.class);
    public AssertEmptyCommand() {
        this(new BrowserStyleWhitespaceComparator());
    }
    
    public AssertEmptyCommand(Comparator<Object> comparator) {
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
        @SuppressWarnings("unchecked")
		Collection<Object> results = (Collection<Object>) actuals;
        
        String expected = element.getText();
        
        // check if list is empty or not
        boolean fail = results != null && results.size() > 0;
        if(fail)
        {
        	resultRecorder.record(Result.FAILURE);
            announceFailure(element, expected, results.size()+" results found");
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
