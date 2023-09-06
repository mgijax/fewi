package org.jax.mgi.fewi.sortMapper;

import java.util.ArrayList;
import java.util.List;

public class ESSortMapper {
    
    List <String> sortList = new ArrayList<String>();
    
    public ESSortMapper(List<String> sortList) {
        this.sortList = sortList;
    }
    
    public ESSortMapper(String sort) {
        this.sortList.add(sort);
    }

    public List<String> getSortList() {
        return sortList;
    }

    public void setSortList(List<String> sortList) {
        this.sortList = sortList;
    }

}
