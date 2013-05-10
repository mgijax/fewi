package org.jax.mgi.fewi.summary;

//public class GxdMarkerSummary {
//
//}

import java.util.*;

import mgi.frontend.datamodel.Genotype;
import mgi.frontend.datamodel.GxdAssayResult;
import mgi.frontend.datamodel.Marker;
import mgi.frontend.datamodel.MarkerLocation;
import mgi.frontend.datamodel.ImagePane;

import org.apache.commons.lang.StringUtils;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.entities.SolrAssayResult;
import org.jax.mgi.fewi.util.DBConstants;
import org.jax.mgi.fewi.util.FormatHelper;
import org.jax.mgi.fewi.util.NotesTagConverter;
import org.jax.mgi.fewi.config.ContextLoader;
import org.jax.mgi.fewi.finder.MarkerFinder;

import javax.persistence.Column;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * wrapper around a marker; represents on row in summary
 */
public class GxdAssayResultSummaryRow {
	// -------------------
		// instance variables
		// -------------------

		private Logger logger = LoggerFactory.getLogger(GxdAssayResultSummaryRow.class);

		// encapsulated row object
		private SolrAssayResult result;

		// config values
		String fewiUrl = ContextLoader.getConfigBean().getProperty("FEWI_URL");
		String pywiUrl = ContextLoader.getConfigBean().getProperty("WI_URL");
		 
		private String score;
		
		//helper objects
		NotesTagConverter ntc;

		// -------------
		// constructors
		// -------------
		public GxdAssayResultSummaryRow(SolrAssayResult result) {
			this.result = result;
			
			setNTC();
			
			return;
		}
		
		private void setNTC()
		{
			try {
			      ntc = new NotesTagConverter();
			    }catch (Exception e) {}
		}

		// -------------------------------------------------------------------
		// public instance methods; JSON serializer will call all public methods
		// -------------------------------------------------------------------
		public void setScore(String score) {
			this.score = score;
		}

		public String getScore() {
			return score;
		}

		public String getGene() {
			
//			return "<a href='" + fewiUrl + "marker/" + mrk.getPrimaryID() + "'>"
//					+ mrk.getSymbol() + "</a>";
			
			return result.getMarkerSymbol();
		}
		
		public String getAssayID()
		{
			return "<a href='"+fewiUrl+"assay/"+result.getAssayMgiid()+"'>data</a><span> ("+result.getAssayMgiid()+")</span>";
		}
		
		public String getAssayType() {
			return result.getAssayType();
		}
		
		public String getAnatomicalSystem()
		{
			return result.getAnatomicalSystem();
		}

		
		public String getAge()
		{
			return result.getAge();
		}
		
		public String getStructure()
		{
			return "TS"+result.getTheilerStage()+": "+result.getPrintname();
		}

		public String getDetectionLevel()
		{
			return result.getDetectionLevel();
		}
		
		public String getFigures()
		{
			List<String> formattedFigures = new ArrayList<String>(0);
			if(result.getFigures() != null)
			{
				for(String figure : result.getFigures())
				{
					if(!figure.equals(""))
					{
						formattedFigures.add(figure.replace("###FEWIURL###", fewiUrl));
					}
				}
			}
			if(formattedFigures.size()>0)
			{
				return StringUtils.join(formattedFigures,", ");
			}
			return "";
		}
		
		public String getGenotype()
		{
			if(result.getGenotype() !=null && !result.getGenotype().equals(""))
			{
				if(ntc==null) setNTC(); //fail safe
				return FormatHelper.newline2HTMLBR(ntc.convertNotes(result.getGenotype(), '|',true));
			}
			return "";
		}

		public String getReference()
		{
			return "<a href='"+fewiUrl+"reference/"+result.getJNum()+"'>"+result.getJNum()+"</a> "+result.getShortCitation();

		}

}
