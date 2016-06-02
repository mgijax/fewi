package org.jax.mgi.fewi.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jax.mgi.shr.fe.sort.SmartAlphaComparator;

public class HmdcAnnotationGroup {
	
	//--------------------------//
	//--- instance variables ---//
	//--------------------------//
	
	// divider between human gene and disease in combination key
	private String separator = "::::";
	
	// true if this group has disease data, false if it has phenotype data
	private boolean hasDiseaseData = false;	
	
	// set of row IDs for mouse data (those not in this set are for human data)
	private Set<Integer> mouseRows = new HashSet<Integer>();
	
	// { row ID : text string for row header } and reversed
	private Map<Integer,String> rows = new HashMap<Integer,String>();
	private Map<String,Integer> rowsRev = new HashMap<String,Integer>();

	// { column ID : text string for column header } and reversed
	private Map<Integer,String> columns = new HashMap<Integer,String>();
	private Map<String,Integer> columnsRev = new HashMap<String,Integer>();

	// { row ID : { column ID : annotation count } }
	private Map<Integer,Map<Integer,Integer>> counts = new HashMap<Integer,Map<Integer,Integer>>();

	//--------------------//
	//--- constructors ---//
	//--------------------//
	
	/* hide the default constructor
	 */
	private HmdcAnnotationGroup() {}
	
	/* constructor must specify type of data contained in this annotation group (diseases = true, phenotypes = false)
	 */
	public HmdcAnnotationGroup (boolean hasDiseases) {
		this.hasDiseaseData = hasDiseases;
	}

	//-----------------------//
	//--- general methods ---//
	//-----------------------//
	
	/* returns true if the specified row has mouse data, false if it has human data
	 */
	public boolean isMouse(int rowID) {
		return mouseRows.contains(rowID);
	}
	
	/* returns true if this group has disease data, false if it has phenotype data
	 */
	public boolean hasDiseases() {
		return hasDiseaseData;
	}

	/* get the ID number corresponding to the given column text
	 */
	public int getColumnID (String columnText) {
		if (!columnsRev.containsKey(columnText)) {
			int columnID = columnsRev.size() + 1;
			columnsRev.put(columnText, columnID);
			columns.put(columnID, columnText);
		}
		return columnsRev.get(columnText);
	}
	
	public Map<String,Integer> getColumnIDMap() {
		return columnsRev;
	}
	
	/* increment the count of annotations for cell with the given row and column IDs
	 */
	public void addAnnotation (int rowID, int columnID) {
		if (!counts.containsKey(rowID)) {
			counts.put(rowID, new HashMap<Integer,Integer>());
		}
		if (!counts.get(rowID).containsKey(columnID)) {
			counts.get(rowID).put(columnID, 0);
		}
		counts.get(rowID).put(columnID, 1 + counts.get(rowID).get(columnID));
	}

	/* get a sorted list of the column headers
	 */
	public List<String> getColumns() {
		List<String> headers = new ArrayList<String>();
		for (String column : columnsRev.keySet()) {
			headers.add(column);
		}
		Collections.sort(headers, new SmartAlphaComparator());
		return headers;
	}
	
	/* get a list of column IDs, sorted by the header text itself
	 */
	public List<Integer> getColumnIDs() {
		List<Integer> headerIDs = new ArrayList<Integer>();
		for (String header : getColumns()) {
			headerIDs.add(getColumnID(header));
		}
		return headerIDs;
	}
	
	/* return the count of annotations for the cell identified by the given row and column IDs
	 */
	public int getCount (int rowID, int columnID) {
		if (counts.containsKey(rowID) && counts.get(rowID).containsKey(columnID)) {
			return counts.get(rowID).get(columnID);
		}
		return 0;
	}
	
	/* get a map of counts for all row, column combinations in this group
	 */
	public Map<Integer,Map<Integer,Integer>> getCountMap() {
		Map<Integer,Map<Integer,Integer>> full = new HashMap<Integer,Map<Integer,Integer>>();
		for (Integer rowID : this.rows.keySet()) {
			full.put(rowID, new HashMap<Integer,Integer>());
			for (Integer columnID : this.columns.keySet()) {
				full.get(rowID).put(columnID, getCount(rowID, columnID));
			}
		}
		return full;
	}
	
	// is this group empty?  (ie- no rows and/or no columns)
	public boolean isEmpty() {
		return (rows.size() == 0) || (columns.size() == 0);
	}
	
	private String join (String delimiter, Collection<String> items) {
		if (items == null) { return ""; }
		if (delimiter == null) { delimiter = ", "; }
		
		StringBuffer sb = new StringBuffer();
		Iterator<String> it = items.iterator();
		while (it.hasNext()) {
			sb.append(it.next());
			if (it.hasNext()) { 
				sb.append(delimiter);
			}
		}
		return sb.toString();
	}

	/* reasonable data to use for debugging output
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("[HmdcAnnotationGroup: ");
		if (this.isEmpty()) {
			sb.append("empty");
		} else {
			sb.append("columns (" + join(", ", getColumns()) + "), ");
			sb.append("rows (" + join(", ", rowsRev.keySet()) + "), ");
			sb.append(mouseRows.size());
			sb.append(" mouse rows, ");
			sb.append(rows.size() - mouseRows.size());
			sb.append(" human rows, ");
			sb.append(columns.size());
			if (hasDiseaseData) { sb.append(" diseases"); } else { sb.append(" phenotypes"); }
		}
		sb.append("]");
		return sb.toString();
	}

	//---------------------------------------//
	//--- mouse genotype-specific methods ---//
	//---------------------------------------//
	
	/* get the ID number corresponding to the given mouse genotype
	 */
	public int getMouseRowID (String genotypeText) {
		if (!rowsRev.containsKey(genotypeText)) {
			int rowID = rowsRev.size() + 1;
			rowsRev.put(genotypeText, rowID);
			rows.put(rowID, genotypeText);
			mouseRows.add(rowID);
		}
		return rowsRev.get(genotypeText);
	}

	/* increment the count of annotations for cell with the given row and column text values
	 */
	public void addMouseAnnotation (String genotypeText, String columnText) {
		addAnnotation(getMouseRowID(genotypeText), getColumnID(columnText));
	}

	/* get the header strings for the mouse rows
	 */
	public List<String> getMouseRowHeaders() {
		List<String> headers = new ArrayList<String>();
		for (Integer mouseID : mouseRows) {
			headers.add(rows.get(mouseID));
		}
		Collections.sort(headers, new SmartAlphaComparator());
		return headers;
	}
	
	/* get the row IDs for the mouse rows
	 */
	public List<Integer> getMouseRowIDs() {
		List<Integer> headerIDs = new ArrayList<Integer>();
		for (String header : getMouseRowHeaders()) {
			headerIDs.add(rowsRev.get(header));
		}
		return headerIDs;
	}

	/* returns true if there are any mouse rows, false if not
	 */
	public boolean hasMouseRows() {
		return !mouseRows.isEmpty();
	}

	//-------------------------------------------//
	//--- human gene/disease-specific methods ---//
	//-------------------------------------------//
	
	/* get the IDs for the rows containing human data, ordered first by human symbol and
	 * then by disease name
	 */
	public List<Integer> getHumanRowIDs() {
		// list of "symbol disease" strings to sort
		List<String> toSort = new ArrayList<String>();
		
		// maps from "symbol disease" string to row ID
		Map<String,Integer> idLookup = new HashMap<String,Integer>();
		
		for (Integer rowID : rows.keySet()) {
			if (!mouseRows.contains(rowID)) {
				String sortValue = rows.get(rowID).replace(separator, " ");
				toSort.add(sortValue);
				idLookup.put(sortValue, rowID);
			}
		}
		
		// smart-alpha sort by gene then by disease
		Collections.sort(toSort, new SmartAlphaComparator());

		// ordered list of row IDs to return
		List<Integer> toReturn = new ArrayList<Integer>(toSort.size());
		
		for (String sortValue : toSort) {
			toReturn.add(idLookup.get(sortValue));
		}
		return toReturn;
	}
	
	/* get the ID number corresponding to the given human marker/disease pair
	 */
	public int getHumanRowID (String humanMarkerSymbol, String columnText) {
		String comboKey = humanMarkerSymbol + separator + columnText;
		if (!rowsRev.containsKey(comboKey)) {
			int rowID = rowsRev.size() + 1;
			rowsRev.put(comboKey, rowID);
			rows.put(rowID,  comboKey);
		}
		return rowsRev.get(comboKey);
	}

	/* increment the count of annotations for cell with the given row and column text values.  If
	 * is an OMIM annotation, disease and annotatedTerm should match.  If is an HPO term, then the
	 * disease is the source of the HPO annotatedTerm.
	 */
	public void addHumanAnnotation (String humanMarkerSymbol, String disease, String annotatedTerm) {
		addAnnotation(getHumanRowID(humanMarkerSymbol, disease), getColumnID(annotatedTerm));
	}
	
	/* returns true if there are any human rows, false if not
	 */
	public boolean hasHumanRows() {
		return (rows.size() - mouseRows.size()) > 0;
	}
	
	/* get the combo key (generated by getHumanRowID) for the given row ID, or null if bad ID
	 */
	private String getHumanRowHeader (int rowID) {
		// if this is a mouse row or an unknown row, then bail out (should not happen)
		if (mouseRows.contains(rowID) || !rows.containsKey(rowID)) {
			return null;
		}
		return rows.get(rowID);
	}
	
	/* get the marker symbol for the given human row ID, or null if bad ID
	 */
	public String getHumanSymbol (int rowID) {
		String comboKey = getHumanRowHeader(rowID);
		if (comboKey != null) {
			String[] parts = comboKey.split(separator);
			if (parts.length >= 2) {
				return parts[0];
			}
		}
		return null;
	}
	
	public Map<Integer,String> getHumanSymbolMap() {
		Map<Integer,String> out = new HashMap<Integer,String>();
		for (Integer rowID : rows.keySet()) {
			out.put(rowID, getHumanSymbol(rowID));
		}
		return out;
	}
	
	/* get the disease name for the given human row ID, or null if bad ID
	 */
	public String getHumanDisease (int rowID) {
		String comboKey = getHumanRowHeader(rowID);
		if (comboKey != null) {
			String[] parts = comboKey.split(separator);
			if (parts.length >= 2) {
				return parts[1];
			}
		}
		return null;
	}

	public Map<Integer,String> getHumanDiseaseMap() {
		Map<Integer,String> out = new HashMap<Integer,String>();
		for (Integer rowID : rows.keySet()) {
			out.put(rowID, getHumanDisease(rowID));
		}
		return out;
	}
	
}