package org.jax.mgi.fewi.finder;

import java.lang.String;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import org.jax.mgi.fewi.hunter.HibernateSqlHunter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/*
 * Helper used by GXD and Recombinase controllers to build filter expressions for queries that specify "and nowhere else".
 */
@Repository
public class EmapaHelperFinder {

    /*--------------------*/
    /* instance variables */
    /*--------------------*/

    private Logger logger = LoggerFactory.getLogger(EmapaHelperFinder.class);

    @Autowired
    private HibernateSqlHunter sqlHunter;
    
    /*
     * Given a list of EMAPA structure keys, returns a list containing their ancestors and fringe nodes.
     * Each list item is a Map containing keys "term", "term"key", and "type".
     * All attributes are Strings. The type attribute is either "A" or "F" indicating ancestor or fringe, respectively.
     *
     */
    public List<Map<String,String>> getAncestorsAndFringeNodes (List<String> structureKeys) {
        String strs = String.join(", ", structureKeys);
        logger.info("Getting ancestors and fringe nodes for " + strs);
        String query = 
              " WITH "
            // The subject nodes
            + "  subjectNodes as ( "
            + "    SELECT term_key, term, 'S' as nodeType "
            + "    FROM term  "
            + "    WHERE vocab_name = 'EMAPA' AND term_key in (" + strs + ") "
            + "  ), "
            // Union of subject nodes and all their descendants. This is the "region of interest" (ROI).
            + "  descendentNodes as ( "
            + "    SELECT distinct "
            + "      descendent_term_key as term_key, "
            + "      descendent_term as term, "
            + "      'D' as nodeType  "
            + "    FROM term_descendent "
            + "    WHERE term_key in (SELECT term_key FROM subjectNodes) "
            + "       UNION "
            + "    SELECT term_key, term, 'D' as nodeType "
            + "    FROM term "
            + "    WHERE term_key in (SELECT term_key FROM subjectNodes) "
            + "  ), "
            // Union of ancestors over all nodes in ROI, where ancestor is not itself in the ROI
            + "  ancestorNodes as ( "
            + "    SELECT distinct  "
            + "      ancestor_term_key as term_key,  "
            + "      ancestor_term as term,  "
            + "      'A' as nodeType "
            + "    FROM term_ancestor  "
            + "    WHERE term_key in (SELECT term_key FROM descendentNodes) "
            + "    AND ancestor_term_key not in (SELECT term_key FROM descendentNodes) "
            + "  ),  "
            // Fringe nodes = non-ancestor/non-ROI siblings of ancestor nodes
            + "  fringeNodes as ( "
            + "    SELECT distinct  "
            + "      sibling_term_key as term_key,  "
            + "      sibling_term as term,  "
            + "      'F' as nodeType "
            + "    FROM term_sibling "
            + "    WHERE term_key in ( "
            + "      SELECT term_key FROM ancestorNodes "
            + "      UNION "
            + "      SELECT term_key FROM subjectNodes) "
            + "    AND sibling_term_key not in (select term_key from ancestorNodes) "
            + "    AND sibling_term_key not in (select term_key from descendentNodes)     "
            + "  ) "
            // Return fringe nodes and ancestors.
            + "SELECT term_key, term, nodeType FROM fringeNodes "
            + "UNION "
            + "SELECT term_key, term, nodeType FROM ancestorNodes "
            + "ORDER BY nodeType,term "
            ;
        List<Map<String,String>> retVal = new ArrayList<Map<String,String>>();
        for(List<String> row : sqlHunter.sql(query)){
            Map<String,String> m = new HashMap<String,String>();
            m.put("term_key", row.get(0));
            m.put("term", row.get(1));
            m.put("type", row.get(2));
            retVal.add(m);
        }
        return retVal;
    }

    public String termToKey (String term ) {
        String q = "SELECT term_key, term FROM term WHERE vocab_name = 'EMAPA' AND term ilike '" + term + "' ";
        List<List<String>> res = sqlHunter.sql(q);
        if (res.size() == 0) {
            return null;
        } else {
            return res.get(0).get(0);
        }
    }
}
