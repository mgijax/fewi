package org.jax.mgi.fewi.searchUtil;


public class PrettyFilterPrinter extends PrinterUtil implements VisitorInterface {


	public void Visit(Filter filter) {
		
		if(filter.getNestedFilters().size() > 0) {
			if(filter.getFilterJoinClause() == Filter.JoinClause.FC_AND) printni("(");
			int c = 0;
			for(Filter f: filter.getNestedFilters()) {
				if(filter.getFilterJoinClause() == Filter.JoinClause.FC_AND) {
					printni("("); f.Accept(this); printni(")");
				} else {
					f.Accept(this);
				}
				c++;
				if(c < filter.getNestedFilters().size()) {
					printni(" " + filter.getFilterJoinClause().getName() + " ");
				}
			}
			if(filter.getFilterJoinClause() == Filter.JoinClause.FC_AND) printni(")");
		} else {
			if(filter.doNegation()) {
				printni("NOT ");
			}
			if(filter.getValue() != null && filter.getValue().split(" ").length > 1) {
				printni("\"" + filter.getValue() + "\"");
			} else {
				printni(filter.getValue());
			}
		}
	}

}
