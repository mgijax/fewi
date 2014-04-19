package org.jax.mgi.fewi.sortMapper;

import java.util.ArrayList;
import java.util.List;

public class SolrSortMapper {
    
    List <String> sortList = new ArrayList<String>();
    
    public SolrSortMapper(List<String> sortList) {
        this.sortList = sortList;
    }
    
    public SolrSortMapper(String sort) {
        this.sortList.add(sort);
    }

    public List<String> getSortList() {
        return sortList;
    }

    public void setSortList(List<String> sortList) {
        this.sortList = sortList;
    }

}
