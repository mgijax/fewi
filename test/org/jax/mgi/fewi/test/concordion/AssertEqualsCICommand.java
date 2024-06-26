package org.jax.mgi.fewi.test.concordion;

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

/*
 * Case insenstive version of the assertEquals command
 * Only valid for string comparisons
 */
public class AssertEqualsCICommand extends AbstractCommand {

    private Announcer<AssertEqualsListener> listeners = Announcer.to(AssertEqualsListener.class);
    public AssertEqualsCICommand() {
        this(new BrowserStyleWhitespaceComparator());
    }
    
    public AssertEqualsCICommand(Comparator<Object> comparator) {
    }
    
    public void addAssertEqualsListener(AssertEqualsListener listener) {
        listeners.addListener(listener);
    }

    public void removeAssertEqualsListener(AssertEqualsListener listener) {
        listeners.removeListener(listener);
    }
    
    @Override
    public void verify(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder) {
        //Check.isFalse(commandCall.hasChildCommands(), "Nesting commands inside an 'assertEquals' is not supported");
        
        Element element = commandCall.getElement();
        
        Object actual = evaluator.evaluate(commandCall.getExpression());
        Check.isTrue(actual instanceof String, actual.getClass().getCanonicalName() + " is not a String");
        String actualString = (String) actual;
        if(commandCall.hasChildCommands())
        {
        	// attempt to process any child commands (like echo)
            commandCall.getChildren().verify(evaluator, resultRecorder);
        }
        String expected = element.getText();
        
        if (actualString.equalsIgnoreCase(expected)) {
            resultRecorder.record(Result.SUCCESS);
            announceSuccess(element);
        } else {
            resultRecorder.record(Result.FAILURE);
            announceFailure(element, expected, actual);
        }
    }
    
    private void announceSuccess(Element element) {
        listeners.announce().successReported(new AssertSuccessEvent(element));
    }

    private void announceFailure(Element element, String expected, Object actual) {
        listeners.announce().failureReported(new AssertFailureEvent(element, expected, actual));
    }
}
