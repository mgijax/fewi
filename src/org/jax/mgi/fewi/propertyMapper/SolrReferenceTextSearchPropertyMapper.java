package org.jax.mgi.fewi.propertyMapper;

import java.util.ArrayList;
import java.util.List;

import org.jax.mgi.fewi.searchUtil.Filter;

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
 */

public class SolrReferenceTextSearchPropertyMapper implements PropertyMapper {

    ArrayList <String> fieldList = new ArrayList<String>();
    // The default operand is equals.
    int operand = 0;
    String singleField = "";
    String joinClause = "";
    
    /**
     * The constructor that allows us to have an entire fieldlist.
     * @param fieldList
     * @param joinClause
     */
    
    public SolrReferenceTextSearchPropertyMapper(ArrayList<String> fieldList, String joinClause) {
        this.fieldList = fieldList;
        this.joinClause = joinClause;
    }
    
    /**
     * A single property.
     * @param field
     * For this object you can ONLY accept a list of fields, due to special requirements below.
     */
    
/*    public SolrReferenceTextSearchPropertyMapper(String field) {
        this.singleField = field;
    }*/
    
    
    /**
     * This is the standard api, which returns a string that will be passed 
     * to the underlying technology.
     */
    
    @Override
    public String getClause(String value, int operand) {

        Boolean flag = Boolean.FALSE;
        Boolean tempFlag = Boolean.FALSE;
        
        String queryString = "";
        
        String [] qouteList = value.split("\"");
        
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
        
        return outClause;
    }
    
    /***
     * This function handles the mappings from the hunters to the respective solr operands.
     * It basically functions as a mapping from the front end to the back end.
     * @param operand
     * @param value
     * @param field
     * @return
     */
    private String handleOperand(int operand, String value, String field) {

        if (operand == Filter.OP_EQUAL) {
            return field + ":\"" + value + "\"";
        }
        else if (operand == Filter.OP_GREATER_THAN) {
            Integer newValue = new Integer(value);
            newValue++;
            return field + ":[" + newValue + " TO *]";
        }
        else if (operand == Filter.OP_LESS_THAN) {
            Integer newValue = new Integer(value);
            newValue--;
            return field + ":[* TO "+newValue+"]";
        }
        else if (operand == Filter.OP_WORD_BEGINS || operand == Filter.OP_HAS_WORD) {
            return field + ":" + value;
        }
        else if (operand == Filter.OP_GREATER_OR_EQUAL) {
            return field + ":[" + value + " TO *]";
        }
        else if (operand == Filter.OP_LESS_OR_EQUAL) {
            return field + ":[* TO "+value+"]";
        }
        else if (operand == Filter.OP_NOT_EQUAL) {
            return "-" + field + ":" + value;
        }
        else if (operand == Filter.OP_BEGINS) {
            return field + ":" + value + "*";
        }
        else if (operand == Filter.OP_ENDS) {
            return field + ":" + "*" + value;
        }
        else if (operand == Filter.OP_CONTAINS) {
            return field + ":" + "(" + value + ")";
        }
        return "";
    }

}
