package org.jax.mgi.fewi.searchUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for building ElasticSearch Query Language (ES|QL) queries.
 * <p>
 * This class provides helper methods to assemble an ES|QL query string,
 * including support for joins, extra statements (e.g., {@code KEEP},
 * {@code LIMIT}), and group-count operations.
 * </p>
 *
 * <h3>Example</h3>
 *
 * <pre>{@code
 * POST /_query
 * {
 *   "query": """
 *     FROM gxd_result
 *     | WHERE markerSymbol == "Wnt1"
 *     | KEEP markerSymbol, markerMgiid, assayType
 *     | LIMIT 10
 *   """
 * }
 * }</pre>
 *
 * <h3>Configuration</h3>
 * <ul>
 * <li><b>indexName</b>: the source index, e.g. {@code gxd_result}</li>
 * <li><b>extraStatements</b>: additional query operations, e.g.
 * {@code KEEP markerSymbol, markerMgiid, assayType | LIMIT 2}</li>
 * </ul>
 *
 * <h3>Sample Output</h3>
 * 
 * <pre>
 * {
 *   "took": 9,
 *   "is_partial": false,
 *   "documents_found": 10,
 *   "values_loaded": 30,
 *   "columns": [
 *     {"name": "markerSymbol", "type": "keyword"},
 *     {"name": "markerMgiid",  "type": "keyword"},
 *     {"name": "assayType",    "type": "keyword"}
 *   ],
 *   "values": [
 *     ["Wnt1", "MGI:98953", "RNA in situ"],
 *     ["Wnt1", "MGI:98954", "RNA in situ"]
 *   ]
 * }
 * </pre>
 */

public class ESQuery {

	protected String indexName;
	protected List<String> lookupJoinStatements = new ArrayList<>();
	protected List<String> extraStatements = new ArrayList<>();
	
	public ESQuery(String indexName) {
		this.indexName = indexName;
	}

	/**
	 * Builds the ES|QL query string.
	 *
	 * @param whereCause       a {@link String} condition, e.g.
	 *                         {@code markerSymbol == "Wnt1"}, typically generated
	 *                         by {@link SearchParams}
	 * @param size             the maximum number of results to return (LIMIT); if
	 *                         {@code <= 0}, no limit is applied
	 * @param isGroupCountOnly whether to append a
	 *                         {@code STATS total_count = COUNT(*)} clause
	 * @return a full ES|QL query string
	 */
	public String toQuery(List<String> whereCauses, int size, boolean isGetTotalCount) {
		StringBuffer query = new StringBuffer();
		query.append("FROM " + this.indexName);

		if (this.lookupJoinStatements != null) {
			for (String joinStatement : this.lookupJoinStatements) {
				query.append(" | " + joinStatement);
			}
		}
		if (whereCauses != null ) {
			for (String whereCause: whereCauses) {
				query.append(" | WHERE " + whereCause);
			}
		}
		if (this.extraStatements != null) {
			for (String extraStatement : this.extraStatements) {
				query.append(" | " + extraStatement);
			}
		}
		if (size > 0) {
			query.append(" | LIMIT " + size);
			
		//Hongping ES server will hang for lookup join without specifying size, need to investigate			
		} else if (this.lookupJoinStatements != null && this.lookupJoinStatements.size() > 0) {
			query.append(" | LIMIT 1000");
		}

		if (isGetTotalCount) {
			query.append(" | STATS total_count = COUNT(*)");
		}
		return query.toString();
	}

	public String toString() {
		return "ESQL: " + this.indexName;
	}

	public String getIndexName() {
		return indexName;
	}

	public void setIndexName(String indexName) {
		this.indexName = indexName;
	}

	public List<String> getExtraStatements() {
		return extraStatements;
	}

	public void setExtraStatements(List<String> extraStatements) {
		this.extraStatements = extraStatements;
	}

	public List<String> getLookupJoinStatements() {
		return lookupJoinStatements;
	}

	public void setLookupJoinStatements(List<String> lookupJoinStatements) {
		this.lookupJoinStatements = lookupJoinStatements;
	}

}
