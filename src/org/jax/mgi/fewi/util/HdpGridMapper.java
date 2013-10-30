package org.jax.mgi.fewi.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mgi.frontend.datamodel.HdpGenoClusterAnnotation;
import mgi.frontend.datamodel.HdpGridAnnotation;

/*
 *  A class for mapping columns to data for making rows in the Grid
 *  	Applicable to all the disease portal grids (summary, phenotype popup, disease popup)
 *  
 *  	Uses an interface HdpGridAnnotation to utilise different types of either Hibernate objects or Solr populated objects
 *   		for flexibility in gathering data.
 *     
 *  @author kstone
 */
public class HdpGridMapper {
	private List<String> colIdList;
	private List<HdpGridAnnotation> gridAnnotations;
	private List<GridCell> gridCells = new ArrayList<GridCell>();

	/*
	 * colIdList is the ordered list of columns by their identifier (could be ID or term name)
	 * gridAnnotations is the data that we have (to be matched by the above identifier). 
	 * 	Not all colIds will have a matching gridAnnotation.
	 */
	public HdpGridMapper(List<String> colIdList,List<? extends HdpGridAnnotation> gridAnnotations)
	{
		this.colIdList = colIdList;
		this.gridAnnotations = new ArrayList<HdpGridAnnotation>(gridAnnotations);
		init();
	}


	private void init()
	{
		// map annotations by term ID
		// keep two separate maps, one for abnormal annotations, one for normal
		Map<String,HdpGridAnnotation> annotationMap = new HashMap<String,HdpGridAnnotation>();
		Map<String,HdpGridAnnotation> normalMap = new HashMap<String,HdpGridAnnotation>();

		for(HdpGridAnnotation annot : gridAnnotations)
		{
			//logger.info("found annot for "+annot.getTerm()+" "+annot.getTermIdentifier());
			String annotId = annot.getTermIdentifier();
			if(annotationMap.containsKey(annotId)) continue;
			if(notEmpty(annot.getQualifier()))
			{
				normalMap.put(annotId,annot);
			}
			else
			{
				annotationMap.put(annotId,annot);
			}
		}

		// create cells by looking up if termId has an annotation in
		// the map; create blank cells for no-matches
		for(String colId : colIdList)
		{
			//logger.debug("found column for "+colId);
			GridCell gc = new GridCell();

			HdpGridAnnotation annot=null;
			// get annotation summary if it exists for this cell
			if(annotationMap.containsKey(colId)) annot = annotationMap.get(colId);
			else if(normalMap.containsKey(colId))
			{
				// set normal if it is normal
				annot = normalMap.get(colId);
				gc.setIsNormal();
			}
			
			if(annot!=null)
			{
				//logger.debug("mapped cell for "+)
				//logger.info("mapped cell for colID="+colId+",term="+annot.getTerm()+",qual="+annot.getQualifier()+".");
				gc.setTerm(annot.getTerm());
				gc.setTermId(annot.getTermId());
				gc.setHasPopup();
				
				// set background note if it is a HdpGenoClusterAnnotation (applies to phenotype popup only)
				if(annot instanceof HdpGenoClusterAnnotation
						&& ((HdpGenoClusterAnnotation)annot).getHasBackgroundNote())
				{
					gc.setHasBackgroundNote();
				}
			}
			gridCells.add(gc);
		}
	}

	public List<GridCell> getGridCells()
	{
		return gridCells;
	}

	
	/*
	 * Bean that represents a grid cell (correlates to each <td> in the JSP)
	 * 	Has some attributes to determine which symbols or links to display
	 */
	public class GridCell
	{
		private String term="";
		private String termId="";
		private Boolean isNormal = false;
		private Boolean hasPopup = false;
		private Boolean hasBackgroundNote = false;

		public String getTerm()
		{
			return term;
		}
		public void setTerm(String term)
		{
			this.term=term;
		}
		public String getTermId()
		{
			return termId;
		}
		public void setTermId(String termId)
		{
			this.termId=termId;
		}
		public void setIsNormal()
		{
			this.isNormal = true;
		}
		public Boolean getIsNormal()
		{
			return isNormal;
		}
		public void setHasPopup()
		{
			this.hasPopup = true;
		}
		public Boolean getHasPopup()
		{
			return hasPopup;
		}
		public void setHasBackgroundNote()
		{
			this.hasBackgroundNote = true;
		}
		public Boolean getHasBackgroundNote()
		{
			return hasBackgroundNote;
		}

		// encapsulate how we generate a display mark
		public String getDisplayMark()
		{
			if(getHasPopup())
			{
				if(getIsNormal()) return "N";
				return "&#8730;"; // a check mark
			}

			return "";
		}
		
	}
	
	/* convenience method */
	private boolean notEmpty(String str)
	{ return str!=null && !str.equals(""); }
}
