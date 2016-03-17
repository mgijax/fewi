package org.jax.mgi.fewi.searchUtil;


public class PrettyFilterPrinter extends PrinterUtil implements VisitorInterface {

	public void Visit(Filter filter) {
		
		if(filter.isNegate()) printni("(NOT ");
		
		if(filter.getNestedFilters().size() > 0) {
			
			if(filter.getNestedFilters().size() > 1) printni("(");
			int c = 0;
			for(Filter f: filter.getNestedFilters()) {
				f.Accept(this);
				c++;
				if(c < filter.getNestedFilters().size()) {
					printni(" " + filter.getFilterJoinClause().getName() + " ");
				}
			}
			if(filter.getNestedFilters().size() > 1) printni(")");
		} else {

			if(filter.isQuoted()) {
				printni("\"" + filter.getValue() + "\"");
			} else {
				if( filter.getValue() != null &&
					(filter.getValue().equals("AND") ||
					filter.getValue().equals("(AND") ||
					filter.getValue().equals("AND)") ||
					filter.getValue().equals("OR") ||
					filter.getValue().equals("(OR") ||
					filter.getValue().equals("OR)") ||
					filter.getValue().equals("(NOT") ||
					filter.getValue().equals("NOT)") ||
					filter.getValue().equals("NOT"))
				) {
					printni(filter.getValue().toLowerCase());
				} else {
					printni(filter.getValue());
				}
			}
		}
		
		if(filter.isNegate()) printni(")");
		
	}
}
