package org.jax.mgi.fewi.summary;

import java.util.*;

import mgi.frontend.datamodel.Sequence;
import mgi.frontend.datamodel.SequenceSource;
import mgi.frontend.datamodel.Marker;

import javax.persistence.Column;

import org.jax.mgi.fewi.util.FormatHelper;
import org.jax.mgi.fewi.util.IDGenerator;

import org.jax.mgi.fewi.config.ContextLoader;

/**
 * wrapper around a sequence;  represents on row in summary
 */
public class SeqSummaryRow {

	//-------------------
	// instance variables
	//-------------------

	// encapsulated row object
	private Sequence seq;

	// config values
	String fewiUrl = ContextLoader.getConfigBean().getProperty("FEWI_URL");


	//-------------
	// constructors
	//-------------

	// hide the default constructor - we NEED a sequence
    private SeqSummaryRow () {}

    public SeqSummaryRow (Sequence seq) {
    	this.seq = seq;
    	return;
    }

    //------------------------
    // public instance methods
    //------------------------


    public String getSeqInfo() {

        StringBuffer seqInfo = new StringBuffer();
        seqInfo.append(this.seq.getPrimaryID());
        seqInfo.append("&nbsp;<br/>");
        seqInfo.append("<a href='" + fewiUrl + "sequence/"
          + seq.getPrimaryID() + "'> MGI Sequence Detail </a>");

    	return seqInfo.toString();
    }

    public String getSeqType() {
    	return this.seq.getSequenceType();
    }

    public String getLength() {
    	return this.seq.getLength().toString();
    }

    public String getStrainSpecies() {

		List<SequenceSource> sourceList = seq.getSources();

		SequenceSource ss = seq.getSources().get(0);
		if (ss != null) {
			return ss.getStrain();
		}
    	return "";
    }

    public String getDescription() {
    	return this.seq.getDescription();
    }

    public String getCloneCollection() {
    	return "";
    }

    public String getMarkerSymbol() {

		Set<Marker> markerSet = seq.getMarkers();
        Iterator<Marker> markerIter = markerSet.iterator();
		Marker marker = markerIter.next();

		if (marker != null) {
          return "<a href='" + fewiUrl + "marker/"
            + marker.getSymbol() + "'>" + marker.getSymbol() + "</a>";
		}
    	return "";
    }

}
