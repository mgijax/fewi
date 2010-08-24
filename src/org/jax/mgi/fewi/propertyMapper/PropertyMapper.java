package org.jax.mgi.fewi.propertyMapper;

import org.jax.mgi.fewi.searchUtil.Filter;

/***
 * The standard interface for a PropertyMapper, the only thing that will 
 * need to be implemented will be the getClause function.
 * 
 * @author mhall
 *
 */
public interface PropertyMapper {
 
    String getClause(String value, int operand);

}
