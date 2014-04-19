package org.jax.mgi.fewi.summary;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import mgi.frontend.datamodel.Marker;
import mgi.frontend.datamodel.Probe;
import mgi.frontend.datamodel.ProbeCloneCollection;
import mgi.frontend.datamodel.Sequence;
import mgi.frontend.datamodel.SequenceSource;

import org.jax.mgi.fewi.config.ContextLoader;
import org.jax.mgi.fewi.util.DBConstants;
import org.jax.mgi.fewi.util.FormatHelper;
import org.jax.mgi.fewi.util.ProviderLinker;
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
    	if (this.seq.getLength() != null){
    		return this.seq.getLength().toString();
    	}
    	return "";
    }


    public String getStrainSpecies() {

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

        // return string
        StringBuffer probeSB = new StringBuffer();

        String seqProvider = seq.getProvider();

        // for these providers, clone collections are not applicable
        if (seqProvider.equals(DBConstants.PROVIDER_DFCI) ||
            seqProvider.equals(DBConstants.PROVIDER_REFSEQ) ||
            seqProvider.equals(DBConstants.PROVIDER_TREMBL) ||
            seqProvider.equals(DBConstants.PROVIDER_SWISSPROT) ||
            seqProvider.equals(DBConstants.PROVIDER_NIA) ||
            seqProvider.equals(DBConstants.PROVIDER_DOTS) )
        {
            probeSB.append("Not Applicable");
        }
        else // otherwise, derive the clone collections, if any
        {
			Set<Probe> probeSet = seq.getProbes();
			Set<String> cloneCollections = new HashSet<String>();

			// collect unique clone collection values
			if (probeSet.size() > 0 ) {

                // iterate over probes
                Probe probe;
                ProbeCloneCollection pcc;
                Iterator<Probe> probeIter = probeSet.iterator();
				while (probeIter.hasNext()) {
	                probe = probeIter.next();
	                Set<ProbeCloneCollection> pccSet = probe.getProbeCloneCollection();
                    Iterator<ProbeCloneCollection> pccIter = pccSet.iterator();
                    while (pccIter.hasNext()) {
						pcc = pccIter.next();
						cloneCollections.add(pcc.getCollection() + "<br/>");
					}
				}
                // generate actual string value
                Iterator<String> ccIter = cloneCollections.iterator();
				while (ccIter.hasNext()) {
					probeSB.append(ccIter.next());
				}
		    }
			cloneCollections = new HashSet<String>();
		}
        return probeSB.toString();
    }


    public String getMarkerSymbol() {

        StringBuffer markerLinks = new StringBuffer();
		Set<Marker> markerSet = seq.getMarkers();
        Iterator<Marker> markerIter = markerSet.iterator();

        // for each marker, make a marker link
        Marker marker;
        while (markerIter.hasNext()) {
			marker = markerIter.next();
            markerLinks.append("<div><a href='" + fewiUrl + "marker/"
              + marker.getPrimaryID() + "'>" + marker.getSymbol()
              + "</a></br></div>");
		}
    	return markerLinks.toString();
    }

}
