package org.jax.mgi.fewi.summary;

import org.jax.mgi.fewi.config.ContextLoader;
import org.jax.mgi.fewi.util.DBConstants;
import org.jax.mgi.fewi.util.FormatHelper;
import org.jax.mgi.fewi.util.NotesTagConverter;
import org.jax.mgi.fewi.util.link.ProviderLinker;
import org.jax.mgi.shr.jsonmodel.SimpleMarker;
import org.jax.mgi.shr.jsonmodel.SimpleSequence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * wrapper around a sequence;  represents on row in summary
 */
public class SeqSummaryRow {

	//-------------------
	// instance variables
	//-------------------

    private final Logger logger = LoggerFactory.getLogger(SeqSummaryRow.class);
    
    private final NotesTagConverter ntc = new NotesTagConverter();

	// encapsulated row object
	private SimpleSequence seq;

	// config values
    String fewiUrl = ContextLoader.getConfigBean().getProperty("FEWI_URL");
    String mblastUrl = ContextLoader.getConfigBean().getProperty("FEWI_URL") + "sequence/blast?seq1=";
    String seqFetchUrl = ContextLoader.getConfigBean().getProperty("SEQFETCH_URL") + "seqs=";


	//-------------
	// constructors
	//-------------
    public SeqSummaryRow (SimpleSequence seq) {
    	this.seq = seq;
    	logger.debug("SeqSummaryRow wrapping sequence w/ key - " + seq.getSequenceKey());
    	return;
    }


    //------------------------
    // public instance methods
    //------------------------


    public String getSeqForward() {

        StringBuffer seqForward = new StringBuffer();
        seqForward.append("<a href='" + seqFetchUrl + FormatHelper.getSeqForwardValue(seq) + "'>FASTA</a>");
        seqForward.append("<br/>");
        seqForward.append("<a href='" + mblastUrl + FormatHelper.getSeqForwardValue(seq).replace("+", "%2B") + "' target='_blank'>BLAST at NCBI</a>");

    	return seqForward.toString();
    }


    public String getSeqInfo() {
    	String providerLinks = ProviderLinker.getSeqProviderLinks(this.seq);
        StringBuffer seqInfo = new StringBuffer();

        seqInfo.append(this.seq.getPrimaryID());
        seqInfo.append("<br/>&nbsp;&nbsp;");
        if ((providerLinks != null) && (providerLinks.trim().length() > 0)) {
        	seqInfo.append(ProviderLinker.getSeqProviderLinks(this.seq));
        	seqInfo.append("<br/>&nbsp;&nbsp;");
        }
        seqInfo.append("<a href='" + fewiUrl + "sequence/"
          + seq.getPrimaryID() + "'>MGI Sequence Detail </a>");

    	return seqInfo.toString();
    }


    public String getSeqType() {
    	if (this.seq.getSequenceType() == null) { return ""; }
    	return this.seq.getSequenceType();
    }


    public String getLength() {
    	if (this.seq.getLength() != null){
    		return this.seq.getLength();
    	}
    	return "";
    }


    public String getStrainSpecies() {
    	if (this.seq.getStrain() != null) {
    		return this.seq.getStrain();
    	}
    	if (this.seq.getSpecies() != null) {
    		return this.seq.getSpecies();
    	}
    	return "";
    }


    public String getDescription() {
    	if (this.seq.getDescription() == null) { return ""; }
    	return ntc.convertNotes(this.seq.getDescription(), '|');

    }

    public String getCloneCollection() {

        // return string
        StringBuffer probeSB = new StringBuffer();

        String seqProvider = seq.getProvider();

        // for these providers, clone collections are not applicable
        if ( (seqProvider != null) && (
        	seqProvider.equals(DBConstants.PROVIDER_REFSEQ) ||
            seqProvider.equals(DBConstants.PROVIDER_TREMBL) ||
            seqProvider.equals(DBConstants.PROVIDER_SWISSPROT) ) )
        {
            probeSB.append("Not Applicable");
        }
        else // otherwise, derive the clone collections, if any
        {
        	if (this.seq.getCloneCollections() != null) {
        		for (String collection : this.seq.getCloneCollections()) {
        			probeSB.append(collection);
        			probeSB.append("<br/>");
        		}
        	}
		}
        return probeSB.toString();
    }

    public String getMarkerSymbol() {
    	if (this.seq.getMarkers() == null) { return ""; }
    	
        StringBuffer markerLinks = new StringBuffer();

        // for each marker, make a marker link
        for (SimpleMarker marker : this.seq.getMarkers()) {
            markerLinks.append("<div><a href='" + fewiUrl + "marker/"
              + marker.getPrimaryID() + "'>" + marker.getSymbol()
              + "</a></br></div>");
		}
    	return markerLinks.toString();
    }
}
