package org.jax.mgi.fewi.summary;

import java.util.*;

import mgi.frontend.datamodel.Sequence;
import mgi.frontend.datamodel.SequenceSource;
import mgi.frontend.datamodel.Marker;

import org.jax.mgi.fewi.util.FormatHelper;
import org.jax.mgi.fewi.util.IDGenerator;
import org.jax.mgi.fewi.util.ProviderLinker;
import org.jax.mgi.fewi.config.ContextLoader;

import javax.persistence.Column;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * wrapper around a sequence;  represents on row in summary
 */
public class SeqSummaryRow {

	//-------------------
	// instance variables
	//-------------------

    private Logger logger = LoggerFactory.getLogger(SeqSummaryRow.class);

	// encapsulated row object
	private Sequence seq;

	// config values
    String fewiUrl = ContextLoader.getConfigBean().getProperty("FEWI_URL");
    String mblastUrl = ContextLoader.getConfigBean().getProperty("MOUSEBLAST_URL") + "seqSelect.cgi?seqs=";
    String seqFetchUrl = ContextLoader.getConfigBean().getProperty("SEQFETCH_URL") + "seqs=";


	//-------------
	// constructors
	//-------------

	// hide the default constructor - we NEED a sequence
    private SeqSummaryRow () {}

    public SeqSummaryRow (Sequence seq) {
    	this.seq = seq;
    	logger.debug("SeqSummaryRow wrapping sequence w/ key - "
    	  + seq.getSequenceKey());
    	return;
    }


    //------------------------
    // public instance methods
    //------------------------

    public String getSeqForward() {

        StringBuffer seqForward = new StringBuffer();
        seqForward.append("<a href='" + seqFetchUrl + FormatHelper.getSeqForwardValue(seq) + "'>FASTA</a>");
        seqForward.append("<br/>");
        seqForward.append("<a href='" + mblastUrl + FormatHelper.getSeqForwardValue(seq) + "'>MouseBLAST</a>");

    	return seqForward.toString();
    }


    public String getSeqInfo() {

        StringBuffer seqInfo = new StringBuffer();
        seqInfo.append(this.seq.getPrimaryID());
        seqInfo.append("<br/>&nbsp;&nbsp;");
        seqInfo.append(ProviderLinker.getSeqProviderLinks(this.seq));
        seqInfo.append("<br/>&nbsp;&nbsp;");
        seqInfo.append("<a href='" + fewiUrl + "sequence/"
          + seq.getPrimaryID() + "'>MGI Sequence Detail </a>");

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
