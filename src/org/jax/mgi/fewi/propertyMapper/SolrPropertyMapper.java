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

public class SolrPropertyMapper implements PropertyMapper {

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
    
    public SolrPropertyMapper(ArrayList<String> fieldList, String joinClause) {
        this.fieldList = fieldList;
        this.joinClause = joinClause;
    }
    
    /**
     * A single property.
     * @param field
     */
    
    public SolrPropertyMapper(String field) {
        this.singleField = field;
    }
    
    
    /**
     * This is the standard api, which returns a string that will be passed 
     * to the underlying technology.
     */
    
    @Override
    public String getClause(String value, int operand) {

        String outClause = "";
        
        if (!singleField.equals("")) {
            outClause = handleOperand(operand, value, singleField);
        }
        else {
            for (String field: fieldList) {
                if (outClause.equals("")) {
                   outClause = handleOperand(operand, value, field); 
                }
                else {
                   outClause += " " + joinClause + " " + handleOperand(operand, value, field);
                }
            }
        }
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
            return field + ":" + "*" + value + "*";
        }
        return "";
    }

}
