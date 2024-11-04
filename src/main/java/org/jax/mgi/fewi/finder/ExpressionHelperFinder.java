package org.jax.mgi.fewi.finder;

import java.util.ArrayList;
import java.util.List;

import org.jax.mgi.fewi.hunter.HibernateSqlHunter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/*
 * Helper used by GXD and Recombinase controllers to build filter expressions for queries that specify "and nowhere else".
 */
@Repository
public class ExpressionHelperFinder {

    /*--------------------*/
    /* instance variables */
    /*--------------------*/

    private Logger logger = LoggerFactory.getLogger(ExpressionHelperFinder.class);

    @Autowired
    private HibernateSqlHunter sqlHunter;
    
    /*
     * Given a list of EMAPA structure keys, returns the keys of genes or recombinase alleles where expression is
     * detected in all these structures and nowhere else.
     */
    public List<String> expressedIn (
        List<String> structureKeys,
        String geneOrRecombinase // do you want expressed "gene"s or detected "recombinase"s
        ) {
        return expressedIn(structureKeys, geneOrRecombinase, "all", true);
    }


    public List<String> expressedIn (
        List<String> structureKeys,
        String geneOrRecombinase, // do you want expressed "gene"s or detected "recombinase"s
        String allOrSome, // do they have to be expressed in "all" or "some" of the structures?
        boolean nowhereElse // are they allowed to have expression elsewhere (False=no, True=yes)
        ) {

        StringBuffer query = new StringBuffer();

        query.append("WITH ");

        /*
         * Defines allExpression as distinct allele_key/structure_key combinations
         * from all positive recombinase results. allele_key is renamed as subject_key
         * so downstream parts can be "generic".
         */
        String allCre = 
          "allExpression AS ("
          + "SELECT distinct  "
          + "  ras.allele_key as subject_key,  "
          + "  rar.structure_key "
          + "FROM recombinase_assay_result rar, recombinase_allele_system ras "
          + "WHERE rar.allele_system_key = ras.allele_system_key "
          + "AND rar.level != 'Absent' "
          + "), ";

        /*
         * Defines allExpression as distinct marker_key/structure_key combinations
         * from all positive GXD results (not including RNAseq data). marker_key is
         * renamed as subject_key so downstream parts can be "generic".
         */
        String allGxd = 
          "allExpression AS ("
          + "SELECT distinct " 
          + "s.marker_key as subject_key, "
          + "s.structure_key "
          + "FROM expression_result_summary s, genotype g "
          + "WHERE s.is_expressed = 'Yes' "
          + "AND s.genotype_key = g.genotype_key "
          + "AND ( "
          + "    g.combination_1 is null "
          + "    OR "
          + "    (s.assay_type = 'In situ reporter (knock in)' AND g.genotype_type = 'ht') "
          + ")), ";

        /*
         * Append allExpression, depending on argument
         */
        query.append("gene".equals(geneOrRecombinase) ? allGxd : allCre );
        
        /* EMAPS structure keys corresponding to one EMAPA key. Instantiate 
         * for easch EMAPA key passed by caller. In the final query, these will be named
         * "structure0", "structure1", ...
         */
        String structureA = 
          "structure%s AS ( "
          + "SELECT tc.emaps_child_term_key as term_key "
          + "FROM term t, term_emaps_child tc "
          + "WHERE t.vocab_name = 'EMAPA' "
          + "AND t.term_key = tc.emapa_term_key  "
          + "AND t.term_key = %s "
          + "), " ;

        /* Region of interest (ROI) for one EMAPA key. Equals all the 
         * of the EMAPS structures for that EMAPA key and all their descendants. 
         * In the final query, these will be named "roi0", "roi1", ...
         */
        String roiA = 
          "roi%s AS ( "
          + "SELECT distinct td.descendent_term_key as term_key "
          + "FROM term_descendent td "
          + "WHERE td.term_key in (SELECT term_key from structure%s) "
          + "UNION "
          + "SELECT term_key from structure%s "
          + "), ";
        
        /* Keys of things (alleles or genes) expressed in the ROI for one EMAPA key.
         * In the final query, these will be named "expressedInRoi0", "expressedInRoi1", ...
         */
        String expressedInRoiA = 
          "expressedInRoi%s AS ( "
          + "SELECT distinct subject_key "
          + "FROM allExpression "
          + "WHERE structure_key in (SELECT term_key FROM roi%s) "
          + "), ";

        /* Instantiate each of the above three queries for each EMAPA key,
         * and append to the final query.
         */
        int n = 0;
        for (String skey : structureKeys) {
            String A = "" + n++;
            query.append(String.format(structureA, A, skey));
            query.append(String.format(roiA, A, A, A));
            query.append(String.format(expressedInRoiA, A, A));
        }
 
        /* Compute the things (allele or genes) expressed in any or all (depending on allOrSome argument)
         * regions of interest. For the current profile queries, we want "all", so we compute the intesection
         * of all the individual expressedInRoi sets.
         */
        query.append("expressedInRoi as ( ");
        for(int i = 0; i < n; i++) {
            if (i > 0) query.append(allOrSome.equals("all") ? " INTERSECT " : " UNION ");
            query.append("SELECT subject_key FROM expressedInRoi" + i);
        }
        query.append("),");

        /* if caller specifies "and nowhere else", the following will add code to
         * find things expressed "somewhere else" (i.e., not in the ROI), and subtract
         * them from the answer set.
         */
        if (nowhereElse) {
            /* Compute the complete ROI set as the union of EMAPS keys from
             * all the individual roi sets.
             */
            query.append("roi as ( ");
            for(int i = 0; i < n; i++) {
                if (i > 0) query.append(" UNION ");
                query.append("SELECT term_key from roi" + i); 
            }
            query.append("),");

            /* Compute non-ROI terms, i.e., all EMAPS terms not in the ROI.
             */
            String nonRoi =
              "nonRoi as ("
              + "SELECT term_key "
              + "FROM term "
              + "WHERE vocab_name = 'EMAPS' "
              + "AND term_key not in (SELECT term_key FROM roi) "
              + "), ";
            query.append(nonRoi);

            /* Compute the set of things expressed in a non-ROI structure.
             */
            String expressedInNonRoi = 
                  "expressedInNonRoi as ( "
                + "SELECT distinct subject_key "
                + "FROM allExpression "
                + "WHERE structure_key in (SELECT term_key FROM nonRoi) "
                + "), ";
            query.append(expressedInNonRoi);

            /* Final result is the set of things in the expressedInRoi set
             * and not in the expressedInNonRoi set.
             */
            String result = 
                "result as ("
              + "select distinct subject_key "
              + "from expressedInRoi "
              + "where subject_key not in (select subject_key from expressedInNonRoi) "
              + ") " 
              + "SELECT subject_key, ' ' FROM result"
              ;
            query.append(result);
        } else {
            /* User did not specify "and nowhere else", so the final result are the
             * things expressed in the ROI.
             */
            query.append("SELECT distinct subject_key, ' '  FROM expressedInRoi ");
        }
        /* Finally, run the query and return the list of allele or gene keys 
         */
        List<String> resultKeys = new ArrayList<String>();
        for(List<String> row : sqlHunter.sql(query.toString())){
            resultKeys.add(row.get(0));
        }
        return resultKeys;
    }

    public String termToKey (String term ) {
        String q = "SELECT term_key, term FROM term WHERE vocab_name = 'EMAPA' AND term ilike '" + term + "' ";
        List<List<String>> res = sqlHunter.sql(q);
        if (res.size() == 0) {
            q = "SELECT term_key, term FROM term WHERE vocab_name = 'EMAPA' AND term_key in ( SELECT term_key from term_synonym WHERE synonym ilike '" + term + "' AND synonym_type='exact') ";
            res = sqlHunter.sql(q);
        }
        if (res.size() == 0) {
            return null;
        } else {
            return res.get(0).get(0);
        }
    }

    public String emapa2key (String emapa_id) {
        String q = String.format("select t.term_key, ' ' from term t where t.vocab_name = 'EMAPA' and t.primary_id = '%s'", emapa_id);
	List<List<String>> res = sqlHunter.sql(q);
        if (res.size() == 0) {
            return null;
        } else {
            return res.get(0).get(0);
        }
    }

    // Translates an EMAPA id and (optionally) a list of stages into a list of EMAPS keys
    public List<String> emapa2emaps (String emapaId, String stages) {
        String q = "select t.primary_id, te.term_key, te.stage "
	    + "from term t, term_emap te "
	    + "where t.vocab_name = 'EMAPA' "
	    + "and t.primary_id = '%s' "
	    + "and t.term_key = te.emapa_term_key "
	    + " %s "
	    + "order by te.stage ";
	    ;
	String stgExpr = "";
	if (stages != null && !stages.equals("")) {
	    stgExpr = String.format("and te.stage in (%s) ", stages);
	}
	List<String> stageKeys = new ArrayList<String>();
	List<List<String>> res = sqlHunter.sql(String.format(q, emapaId, stgExpr));
	for (List<String> r : res) {
	    stageKeys.add(r.get(1));
	}
	return stageKeys;
    }

    public List<String> expressedOutsideOf (
        List<String> emapaIds,
        List<String> stages
        ) {

        /*
         * Defines allExpression as distinct marker_key/structure_key combinations
         * from all positive GXD results (not including RNAseq data). marker_key is
         * renamed as subject_key so downstream parts can be "generic".
         */
        String allGxd = 
          "allExpression AS ("
          + "SELECT distinct " 
          + "s.marker_key as subject_key, "
          + "s.structure_key "
          + "FROM expression_result_summary s, genotype g "
          + "WHERE s.is_expressed = 'Yes' "
          + "AND s.genotype_key = g.genotype_key "
          + "AND ( "
          + "    g.combination_1 is null "
          + "    OR "
          + "    (s.assay_type = 'In situ reporter (knock in)' AND g.genotype_type = 'ht') "
          + ")), ";

        
        /* EMAPS structure keys corresponding to one EMAPA key. Instantiate 
         * for each EMAPA key passed by caller. In the final query, these will be named
         * "structure0", "structure1", ...
         *
         * If stages are specified in the query, restrict the EMAPS structures in
         * this step accordingly.
         */
        String structureA = 
          "structure%s AS ( "
          + "SELECT te.term_key "
          + "FROM term t, term_emap te "
          + "WHERE t.vocab_name = 'EMAPA' "
          + "AND t.term_key = te.emapa_term_key  "
          + "AND t.primary_id = '%s' "
          + " %s " // stage spec goes here
          + "), " ;

        /* Region of interest (ROI) for one EMAPA key. Equals all the 
         * of the EMAPS structures for that EMAPA key and all their descendants. 
         * In the final query, these will be named "roi0", "roi1", ...
         */
        String roiA = 
          "roi%s AS ( "
          + "SELECT distinct td.descendent_term_key as term_key "
          + "FROM term_descendent td "
          + "WHERE td.term_key in (SELECT term_key from structure%s) "
          + "UNION "
          + "SELECT term_key from structure%s "
          + "), ";

        
        /* Instantiate each of the above three queries for each EMAPA key,
         * and append to the final query.
         */
        StringBuffer query = new StringBuffer();
        query.append("WITH ");
        query.append(allGxd);

        int n = 0;
        for (String eid : emapaIds) {
            String A = "" + n;
	    String stgs = stages.get(n);
	    if (!stgs.equals("")) {
	        stgs = "AND te.stage in (" + stgs + ")";
	    }
            query.append(String.format(structureA, A, eid, stgs));
            query.append(String.format(roiA, A, A, A));
	    n += 1;
        }
 
	/* Compute the complete ROI set as the union of EMAPS keys from
	 * all the individual roi sets.
	 */
	query.append("roi as ( ");
	for(int i = 0; i < n; i++) {
	    if (i > 0) query.append(" UNION ");
	    query.append("SELECT term_key from roi" + i); 
	}
	query.append("),");

	/* Compute non-ROI terms, i.e., all EMAPS terms not in the ROI.
	 */
	String nonRoi =
	  "nonRoi as ("
	  + "SELECT term_key "
	  + "FROM term "
	  + "WHERE vocab_name = 'EMAPS' "
	  + "AND term_key not in (SELECT term_key FROM roi) "
	  + "), ";
	query.append(nonRoi);

	/* Compute the set of things expressed in a non-ROI structure.
	 */
	String expressedInNonRoi = 
	      "expressedInNonRoi as ( "
	    + "SELECT distinct subject_key "
	    + "FROM allExpression "
	    + "WHERE structure_key in (SELECT term_key FROM nonRoi) "
	    + ") ";
	query.append(expressedInNonRoi);

	String result = "SELECT subject_key, ' ' FROM expressedInNonRoi" ;
	query.append(result);

        /* Run the query and return the list of gene keys 
         */
        List<String> resultKeys = new ArrayList<String>();
        for(List<String> row : sqlHunter.sql(query.toString())){
            resultKeys.add(row.get(0));
        }
        return resultKeys;
    }
}

