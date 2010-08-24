package org.jax.mgi.fewi.test;

import java.util.ArrayList;

import org.jax.mgi.fewi.queryMapper.SolrPropertyMapper;
import org.jax.mgi.fewi.searchUtil.Filter;

public class GenericTesting {

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub

        ArrayList <String> foo = new ArrayList <String> ();
        foo.add("foo");
        foo.add("foo2");
        foo.add("foo3");
        
        Filter testFilter = new Filter("formProp", "two");
        
        SolrPropertyMapper sqm = new SolrPropertyMapper(foo, "AND");
        System.out.println(sqm.getClause(testFilter.getProperty(), testFilter.getValue()));
        
    }

}
