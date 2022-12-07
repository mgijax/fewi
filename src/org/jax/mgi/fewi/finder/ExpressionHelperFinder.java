package org.jax.mgi.fewi.finder;

import java.lang.String;
import java.lang.StringBuffer;
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

        String allCre = 
          "allExpression AS ("
          + "SELECT distinct  "
          + "  ras.allele_key as subject_key,  "
          + "  rar.structure_key "
          + "FROM recombinase_assay_result rar, recombinase_allele_system ras "
          + "WHERE rar.allele_system_key = ras.allele_system_key "
          + "AND rar.level != 'Absent' "
          + "), ";

        String xallGxd = 
          "allExpression AS ("
          + "SELECT distinct "
          + "  sm.marker_key as subject_key, "
          + "  te.term_key as structure_key "
          + "FROM expression_ht_consolidated_sample_measurement sm, "
          + "  expression_ht_consolidated_sample s, "
          + "  term_emap te "
          + "WHERE sm.level != 'Below Cutoff' "
          + "AND sm.consolidated_sample_key = s.consolidated_sample_key "
          + "AND s.emapa_key = te.emapa_term_key "
          + "AND cast(s.theiler_stage as int) = te.stage "
          + "UNION "
          + "SELECT distinct  "
          + "marker_key as subject_key, "
          + "structure_key "
          + "FROM expression_result_summary "
          + "WHERE is_expressed = 'Yes' "
          + "), ";

        String allGxd = 
          "allExpression AS ("
          + "SELECT * "
          + "FROM uni_all_genes_tissues "
          + "), ";

        query.append("gene".equals(geneOrRecombinase) ? allGxd : allCre );
        
        /* EMAPS structure keys corresponding to one EMAPA key */
        String structureA = 
          "structure%s AS ( "
          + "SELECT tc.emaps_child_term_key as term_key "
          + "FROM term t, term_emaps_child tc "
          + "WHERE t.vocab_name = 'EMAPA' "
          + "AND t.term_key = tc.emapa_term_key  "
          + "AND t.term_key = %s "
          + "), " ;

        String roiA = 
          "roi%s AS ( "
          + "SELECT distinct td.descendent_term_key as term_key "
          + "FROM term_descendent td "
          + "WHERE td.term_key in (SELECT term_key from structure%s) "
          + "UNION "
          + "SELECT term_key from structure%s "
          + "), ";
        
        String expressedInRoiA = 
          "expressedInRoi%s AS ( "
          + "SELECT distinct subject_key "
          + "FROM allExpression "
          + "WHERE structure_key in (SELECT term_key FROM roi%s) "
          + "), ";

        int n = 0;
        for (String skey : structureKeys) {
            String A = "" + n++;
            query.append(String.format(structureA, A, skey));
            query.append(String.format(roiA, A, A, A));
            query.append(String.format(expressedInRoiA, A, A));
        }
 
        query.append("expressedInRoi as ( ");
        for(int i = 0; i < n; i++) {
            if (i > 0) query.append(allOrSome.equals("all") ? " INTERSECT " : " UNION ");
            query.append("SELECT subject_key FROM expressedInRoi" + i);
        }
        query.append("),");

        if (nowhereElse) {
            query.append("roi as ( ");
            for(int i = 0; i < n; i++) {
                if (i > 0) query.append(" UNION ");
                query.append("SELECT term_key from roi" + i); 
            }
            query.append("),");

            String nonRoi =
              "nonRoi as ("
              + "SELECT term_key "
              + "FROM term "
              + "WHERE vocab_name = 'EMAPS' "
              + "AND term_key not in (SELECT term_key FROM roi) "
              + "), ";
            query.append(nonRoi);

            String expressedInNonRoi = 
                  "expressedInNonRoi as ( "
                + "SELECT distinct subject_key "
                + "FROM allExpression "
                + "WHERE structure_key in (SELECT term_key FROM nonRoi) "
                + "), ";
            query.append(expressedInNonRoi);

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
            query.append("SELECT distinct subject_key, ' '  FROM expressedInRoi ");
        }
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
}
