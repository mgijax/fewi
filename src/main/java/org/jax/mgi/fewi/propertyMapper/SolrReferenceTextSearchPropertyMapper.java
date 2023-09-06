package org.jax.mgi.fewi.propertyMapper;

import java.util.ArrayList;

import org.jax.mgi.fewi.searchUtil.Filter.Operator;

/**
 * The SolrPropertyMapper class handles the mapping of operators being passed in from the 
 * hunter, and mapping them specifically to Solr. 
 * 
 * It also handles the cases where we map 1 -> N column relationships between the hunters and
 * the underlying technology.
 * 
 * In theory this class should handle all possible cases for a normal filter, the only exception 
 * is where we might have to do some sort of special text manipulation.  For that we will need to 
 * have extend this class and override the appropriate function.
 * 
 * @author mhall
 *
 * - refactored by kstone on 2013-05-02 to actually use objects properly. What a complete WTF this code was.
 */

public class SolrReferenceTextSearchPropertyMapper extends SolrPropertyMapper 
{
    public SolrReferenceTextSearchPropertyMapper(String field) {
		super(field);
	}

	public SolrReferenceTextSearchPropertyMapper(ArrayList<String> fieldList, String joinClause) {
		super(fieldList, joinClause);
	}

	@Override
    public String getClause(String value, Operator operand) {

        Boolean flag = Boolean.FALSE;
        Boolean tempFlag = Boolean.FALSE;
        
        String queryString = "";
        
        String [] qouteList = value.toLowerCase().split("\"");
        
        Boolean inQoute = Boolean.FALSE;
        
        for (String qouteString: qouteList) {
        
            if (! inQoute) {
                String [] valueList = qouteString.split("\\s");
                String tempQueryString = "";
                for (String tempValue: valueList) {
                    if (tempValue.endsWith("*")) {
                        flag = Boolean.TRUE;
                        tempFlag = Boolean.TRUE;
                    }
                    tempValue = tempValue.replaceAll("\\p{Punct}", " ");
                    tempValue = tempValue.trim();
                    if (tempFlag) {
                        tempValue = tempValue + "*";
                        tempFlag = Boolean.FALSE;
                    }
        
                    tempQueryString = tempQueryString + tempValue + " ";
                }
                
                queryString = queryString + " " + tempQueryString;
                inQoute = Boolean.TRUE;
            }
            else {
                String tempQueryString = "";
                String [] valueList = qouteString.split("\\s");                
                for (String tempValue: valueList) {
                    tempValue = tempValue.replaceAll("\\p{Punct}", " ");
                    tempValue = tempValue.trim();        
                    tempQueryString = tempQueryString + tempValue + " ";
                }                
                
                queryString = queryString + " " + "\"" + tempQueryString + "\"" + " ";                
                inQoute = Boolean.FALSE;
            }
        
        }
        
        System.out.println("This is the revised query string: '" + queryString + "'");
        
        value = queryString;
        
        // Do we have a case where the flag is true? if so we want to only send this query to the second field.
        // Otherwise we send it to both.
        
        String outClause = "";
        
        if (flag) {
            System.out.println("In the found a wildcard section.");
            outClause = handleOperand(operand, value, fieldList.get(1));
        }
        else {
        
            System.out.println("Did not find a wildcard.");
            
            for (String field: fieldList) {
                System.out.println("Field: " + field + " operand:" + operand + " value:" + value);
                if (outClause.equals("")) {
                   outClause = handleOperand(operand, value, field); 
                }
                else {
                   outClause += " " + joinClause + " " + handleOperand(operand, value, field);
                }
            }
        }
        
        System.out.println("outClause: " + outClause);
        
        return "(" + outClause + ")";
    }
}
