package org.jax.mgi.fewi.hunter;

import java.util.ArrayList;
import java.util.List;

import org.jax.mgi.fe.datamodel.Marker;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;


//----------------------------------------------------------------------------
//                   WARNING - WARNING - WARNING

// This is a faux-hunter and should only be used to create other faux-hunters.

// This class should NOT be used to template actual fewi hunters

//                   WARNING - WARNING - WARNING
//----------------------------------------------------------------------------


@Repository
public class FooKeyHunter {

    // logger for the class
    private Logger logger = LoggerFactory.getLogger(FooKeyHunter.class);

    public void hunt(SearchParams searchParams, SearchResults<Marker> searchResults) {

        logger.debug("-> Faking hunter responsibilities");

        List<String> keyList = new ArrayList<String>();
        keyList.add("33429"); // key of gene 'bcan'
        searchResults.setResultKeys(keyList);
        searchResults.setTotalCount(1);
    }
}