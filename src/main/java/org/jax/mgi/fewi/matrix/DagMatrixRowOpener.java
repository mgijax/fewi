package org.jax.mgi.fewi.matrix;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jax.mgi.fe.datamodel.VocabTerm;
import org.jax.mgi.fewi.finder.VocabularyFinder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/*
 * sets the open flag for matrix rows
 * 
 */
@Repository
public class DagMatrixRowOpener {
	
	@Autowired
	private VocabularyFinder vocabFinder;
	
	/*
	 * Takes originalRows and for each path in pathsToOpen
	 * 	(represented as ID1|ID2,... pipe separated list)
	 * 	if there is a row in originalRows with that path, attempts to open it
	 * 		and add any child terms as new rows
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void openRows(List<? extends AbstractDagMatrixRow> originalRows, List<String> pathsToOpen)
	{
		if (empty(pathsToOpen) || empty(originalRows)) return; 
		
		// save copy to be modified
		pathsToOpen = new ArrayList<String>(pathsToOpen);
		
		// make a lookup of path to row
		Map<String,AbstractDagMatrixRow> pathToRowMap = new HashMap<String,AbstractDagMatrixRow>();
		populatePathMap(originalRows,"",pathToRowMap);
		
		// add the shortened paths to the list
		pathsToOpen.addAll(getShortenedPaths(pathsToOpen));
		
		// order paths by length first 
		java.util.Collections.sort(pathsToOpen, new StringLengthComparator());
		
		// now expand rows for each path to open, if possible
		for(String path : pathsToOpen)
		{
			if (pathToRowMap.containsKey(path))
			{
				AbstractDagMatrixRow row = pathToRowMap.get(path);
				expandRow(row);
				 
				// update path map
				populatePathMap(row.getChildren(),path,pathToRowMap);
			}
		}
	}
	
	/*
	 * Add versions of the paths with the parents off.
	 * 	Ex: Path = "ID1|ID2|ID3", would yield ["ID2|ID3","ID3"].
	 * 	This allows for cases where the parents in the specified path don't exist in the original rows.
	 */
	private List<String> getShortenedPaths(List<String> pathsToOpen)
	{
		List<String> shortPaths = new ArrayList<String>();
		for (String path : pathsToOpen)
		{
			int delimIndex = path.indexOf('|');
			while (delimIndex > 0)
			{
				path = path.substring(delimIndex+1);
				shortPaths.add(path);
				delimIndex = path.indexOf('|');
			}
		}
		return shortPaths;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void populatePathMap(List<? extends AbstractDagMatrixRow> originalRows,String currentPath,Map<String,AbstractDagMatrixRow> map)
	{
		for(AbstractDagMatrixRow parent : originalRows)
		{
			String pathString = currentPath;
			if (!"".equals(pathString)) pathString += "|";
			pathString += parent.getRid();
			map.put(pathString, parent);
			if (notEmpty(parent.getChildren()))
			{
				populatePathMap(parent.getChildren(),pathString,map);
			}
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void expandRow(AbstractDagMatrixRow row)
	{
		if (empty(row.getChildren()))
		{
			// attempt to open the row
			List<VocabTerm> terms = vocabFinder.getTermByID(row.getRid());
			if (notEmpty(terms))
			{
				// expand row
				VocabTerm term = terms.get(0);
				for(VocabTerm child : term.getChildren())
				{
					row.addChild(row.makeMatrixRow(child));
				}
				if (notEmpty(row.getChildren()))
				{
					row.setOc(OpenCloseState.OPEN);
				}
			}
		}
		else
		{
			row.setOc(OpenCloseState.OPEN);
		}
	}
	
	
	private boolean notEmpty(List<?> list)
	{
		return list != null && list.size() > 0;
	}
	private boolean empty(List<?> list)
	{
		return !notEmpty(list);
	}
	/*
	 * exposed for testing purposes
	 */
	public void setVocabFinder(VocabularyFinder vocabFinder)
	{
		this.vocabFinder = vocabFinder;
	}
	
	
	
	private class StringLengthComparator implements java.util.Comparator<String> {
	    public int compare(String s1, String s2) {
	        return s1.length() - s2.length();
	    }
	}
}
