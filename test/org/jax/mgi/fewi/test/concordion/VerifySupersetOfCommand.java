package org.jax.mgi.fewi.test.concordion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.concordion.api.AbstractCommand;
import org.concordion.api.CommandCall;
import org.concordion.api.Element;
import org.concordion.api.Evaluator;
import org.concordion.api.Result;
import org.concordion.api.ResultRecorder;
import org.concordion.api.listener.ExpressionEvaluatedEvent;
import org.concordion.api.listener.MissingRowEvent;
import org.concordion.api.listener.SurplusRowEvent;
import org.concordion.api.listener.VerifyRowsListener;
import org.concordion.internal.Row;
import org.concordion.internal.util.Announcer;
import org.concordion.internal.util.Check;

/**
 * Verify that the results returned is a superset of the list provided
 * It only fails if 0 results are returned or if a result is returned that does not exist in the expected result set.
 * 
 * @author kstone
 *
 */
public class VerifySupersetOfCommand extends AbstractCommand {

    private final Announcer<VerifyRowsListener> listeners = Announcer.to(VerifyRowsListener.class);

    public VerifySupersetOfCommand()
    {
    	super();
    }
    
    
    
    public void addVerifyRowsListener(VerifyRowsListener listener) {
        listeners.addListener(listener);
    }

    public void removeVerifyRowsListener(VerifyRowsListener listener) {
        listeners.removeListener(listener);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public void verify(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder) {
        Pattern pattern = Pattern.compile("(#.+?) *: *(.+)");
        Matcher matcher = pattern.matcher(commandCall.getExpression());
        if (!matcher.matches()) {
            throw new RuntimeException("The expression for a \"verifyRows\" should be of the form: #var : collectionExpr");
        }
        String loopVariableName = matcher.group(1);
        String iterableExpression = matcher.group(2);

        Object obj = evaluator.evaluate(iterableExpression);
        Check.notNull(obj, "Expression returned null (should be an Iterable).");
        Check.isTrue(obj instanceof Iterable, obj.getClass().getCanonicalName() + " is not Iterable");
        //Check.isTrue(!(obj instanceof HashSet) || (obj instanceof LinkedHashSet), obj.getClass().getCanonicalName() + " does not have a predictable iteration order");
        Check.isTrue(obj instanceof Collection,obj.getClass().getCanonicalName() + " is not a Collection");
        Collection<Object> results = (Collection<Object>) obj;
        
        TableSupport tableSupport = new TableSupport(commandCall,evaluator,resultRecorder);
        ArrayList<Row> detailRows = new ArrayList<Row>(Arrays.asList(tableSupport.getDetailRows()));

        announceExpressionEvaluated(commandCall.getElement());
        
    	if(results.size() <= 0)
    	{
    		//handle case where test returned too few results        	
    		resultRecorder.record(Result.FAILURE);
        	Row newRow = tableSupport.addDetailRow();
        	newRow.getElement().appendChild(new Element("b").appendText("0 results returned"));
        	announceSurplusRow(newRow.getElement());
        	return;
    	}
   
        for(Row detailRow : detailRows)
        {
        	boolean found = false;

        	List<Object> searchResults = new ArrayList<Object>(results);

    		String valueText = detailRow.getCells()[0].getText();
        	for(Object searchResult : searchResults)
        	{
        		if(searchResult.equals(valueText) && !found)
        		{
        			//handle a success
                    evaluator.setVariable(loopVariableName, searchResult);
        			tableSupport.copyCommandCallsTo(detailRow);
            		commandCall.getChildren().verify(evaluator, resultRecorder);
        			found = true;
        			//results.remove(searchResult);
        		}
        	}
        	if(!found)
        	{
        		resultRecorder.record(Result.FAILURE);
        		announceMissingRow(detailRow.getElement());
        	}
        	
        }
//        for(Object searchResult : results)
//        {
//        	//handle case where test returned too many results
//        	resultRecorder.record(Result.FAILURE);
//        	Row newRow = tableSupport.addDetailRow();
//        	newRow.getElement().appendText("unexpected value: "+searchResult.toString());
//        	announceSurplusRow(newRow.getElement());
//        }
    }
    
    private void announceExpressionEvaluated(Element element) {
        listeners.announce().expressionEvaluated(new ExpressionEvaluatedEvent(element));
    }

    private void announceMissingRow(Element element) {
        listeners.announce().missingRow(new MissingRowEvent(element));
    }

    private void announceSurplusRow(Element element) {
        listeners.announce().surplusRow(new SurplusRowEvent(element));
    }
}
