package org.jax.mgi.fewi.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jax.mgi.fewi.controller.DiseasePortalController;
import org.jax.mgi.fewi.searchUtil.entities.SolrDpGenoInResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	
	private Logger logger = LoggerFactory.getLogger(HdpGridMapper.class);
	
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
			if(annotationMap.containsKey(annotId))
			{
				if(annot.getAnnotCount()>0)
				{
					// aggregate all the annot counts that we come accross for this column in this row
					HdpGridAnnotation prevAnnot = annotationMap.get(annotId);
					Integer newAnnotCount = prevAnnot.getAnnotCount() + annot.getAnnotCount();
					prevAnnot.setAnnotCount(newAnnotCount);
				}
				// set the human annotation count separately
				if(annot.getHumanAnnotCount()>0)
				{
					HdpGridAnnotation prevAnnot = annotationMap.get(annotId);
					//logger.debug("adding prevHAC= "+prevAnnot.getHumanAnnotCount()+" to newHAC= "+annot.getHumanAnnotCount());
					Integer newHumanAnnotCount = prevAnnot.getHumanAnnotCount() + annot.getHumanAnnotCount();
					prevAnnot.setHumanAnnotCount(newHumanAnnotCount);
				}
				continue;
			}
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
				
				gc.setHumanAnnotCount(annot.getHumanAnnotCount());
				gc.setAnnotCount(annot.getAnnotCount());
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
		private int annotCount = 0;
		private int humanAnnotCount = 0;

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
		
		public void setAnnotCount(int annotCount)
		{
			this.annotCount = annotCount;
		}
		
		public int getAnnotCount()
		{
			return annotCount;
		}
		
		public void setHumanAnnotCount(int humanAnnotCount)
		{
			this.humanAnnotCount = humanAnnotCount;
		}
		
		public int getHumanAnnotCount()
		{
			return humanAnnotCount;
		}
		
		public int getMpBin()
		{
			return calculateMpBinSize(this.annotCount);
		}
		
		// get the bin size for disease / mouse
		public int getDiMouseBin()
		{
			return calculateDiseaseBinSize(this.annotCount);
		}
		// get the bin size for disease / human
		public int getDiHumanBin()
		{
			int humanBin = calculateDiseaseBinSize(this.humanAnnotCount);
			if(humanBin>0 && annotCount<1) humanBin += 4; // we shift the numbering up to get to the styles for full color squares
			return humanBin;
		}

		// encapsulate how we generate a display mark in the main grid
		public String getMpMark()
		{
			if(getHasPopup())
			{
				if(getIsNormal()) return "N";
				return ""; // don't display check mark here
			}

			return "";
		}
		// encapsulate how we generate a display mark in the main grid
		public String getDiseaseMark()
		{
			if(getHasPopup())
			{
				if(getIsNormal()) return "N";
				return ""; // don't display check mark here
			}

			return "";
		}
		
		
		// encapsulate how we generate a display mark
		// for the popup
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
	
	/*
	 * Method to calculate bin size for MP terms based on number of annotations
	 */
	public int calculateMpBinSize(int annotCount)
	{
		if(annotCount<2) return 1;
		if(annotCount<4) return 2;
		if(annotCount<100) return 3;
		return 4;
	}
	
	/*
	 * Method to calculate bin size for diseases based on number of annotations
	 */
	public int calculateDiseaseBinSize(int annotCount)
	{
		if(annotCount<1) return 0;
		if(annotCount<2) return 1;
		if(annotCount<4) return 2;
		if(annotCount<100) return 3;
		return 4;
	}
	
	/* convenience method */
	private boolean notEmpty(String str)
	{ return str!=null && !str.equals(""); }
}
