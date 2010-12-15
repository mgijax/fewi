package org.jax.mgi.fewi.hunter;

import java.util.*;

// mgi libs
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;

// external libs
import org.springframework.stereotype.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


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

    public void hunt(SearchParams searchParams, SearchResults searchResults) {

        logger.debug("-> Faking hunter responsibilities");

        List<String> keyList = new ArrayList<String>();
        keyList.add("33429"); // key of gene 'bcan'
        searchResults.setResultKeys(keyList);
        searchResults.setTotalCount(new Integer(1));
    }
}