package org.jax.mgi.fewi.util;

import org.jax.mgi.fewi.util.DBConstants;

import java.util.*;
import java.io.IOException;
import java.io.InputStream;

import mgi.frontend.datamodel.Sequence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.annotation.PostConstruct;

/**
* provides static methods to generate links to external providers
*/
public class ProviderLinker
{
    /*--------------------*/
    /* instance variables */
    /*--------------------*/

	// logger for the class
	private static Logger logger = LoggerFactory.getLogger(ProviderLinker.class);

	private static String genBankUrl = "http://www.ncbi.nlm.nih.gov/entrez/viewer.fcgi?db=nuccore&id=";
	private static String emblUrl = "http://www.ebi.ac.uk/htbin/emblfetch?";
	private static String ddbjUrl = "http://getentry.ddbj.nig.ac.jp/cgi-bin/get_entry.pl?";
	private static String dfciUrl = "http://compbio.dfci.harvard.edu/tgi/cgi-bin/tgi/tc_report.pl?species=mouse&tc=";
	private static String ensemblGmUrl = "http://www.ensembl.org/Mus_musculus/geneview?gene=";
	private static String ensemblProtUrl = "http://www.ensembl.org/Mus_musculus/Transcript/ProteinSummary?db=core;p=";
	private static String ensemblTranUrl = "http://www.ensembl.org/Mus_musculus/Transcript/Summary?db=core;t=";
	private static String uniProtUrl = "http://www.uniprot.org/entry/";
	private static String ebiUrl = "http://www.ebi.ac.uk/htbin/swissfetch?";
	private static String ncbiGmUrl = "http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?db=gene&cmd=Retrieve&dopt=Graphics&list_uids=";
	private static String niaUrl = "http://lgsun.grc.nia.nih.gov/geneindex/mm9/bin/giU.cgi?genename=";
	private static String refSeqUrl = "http://www.ncbi.nlm.nih.gov/entrez/viewer.cgi?val=";
	private static String vegaGmUrl = "http://vega.sanger.ac.uk/Mus_musculus/Gene/Summary?db=core;g=";
	private static String vegaProtUrl = "http://vega.sanger.ac.uk/Mus_musculus/Transcript/ProteinSummary?db=core;t=";
	private static String vegaTranUrl = "http://vega.sanger.ac.uk/Mus_musculus/Transcript/Summary?db=core;t=";
	private static String dotsUrl = "	http://genomics.betacell.org/gbco/showSummary.do?questionFullName=TranscriptQuestions.TranscriptFromDtIds&myProp%28dtIdP%29=";


//TODO - move provider string test to use constants

    /*-------------------------*/
    /* public instance methods */
    /*-------------------------*/

    /**
     *  Generates the provider links for a given sequence
     */
    public static String getSeqProviderLinks (Sequence sequence)
    {

        StringBuffer links = new StringBuffer();
        String seqProvider = sequence.getProvider();
        String seqID       = sequence.getPrimaryID();

        logger.debug("getSeqProviderLinks().sequence.seqProvider->"
          + seqProvider);


        // all genbank
        if (seqProvider.startsWith(DBConstants.PROVIDER_SEQUENCEDB)) {

			links.append("(<a href='" + genBankUrl + seqID + "'>GenBank</a> | ");
			links.append("<a href='" + emblUrl + seqID + "'>EMBL</a> | ");
			links.append("<a href='" + ddbjUrl + seqID + "'>DDBJ</a>)");
		}
        // DFCI
        else if (seqProvider.equals(DBConstants.PROVIDER_DFCI)) {

			links.append("(<a href='" + dfciUrl + seqID + "'>DFCI</a>)");
		}
        else if (seqProvider.equals(DBConstants.PROVIDER_ENSEMBL)) {

			links.append("(<a href='" + ensemblGmUrl + seqID + "'>Ensembl Gene Model</a>)");
		}
        else if (seqProvider.equals(DBConstants.PROVIDER_ENSEMBLPROTEIN)) {

			links.append("(<a href='" + ensemblProtUrl + seqID + "'>Ensembl</a>)");
		}
        else if (seqProvider.equals(DBConstants.PROVIDER_ENSEMBLTRANSCRIPT)) {

			links.append("(<a href='" + ensemblTranUrl + seqID + "'>Ensembl</a>)");
		}
        else if (seqProvider.equals(DBConstants.PROVIDER_TREMBL) ||
                 seqProvider.equals(DBConstants.PROVIDER_SWISSPROT)) {

			links.append("(<a href='" + uniProtUrl + seqID + "'>UniProt</a> | ");
			links.append("<a href='" + ebiUrl + seqID + "'>EBI</a>)");
		}
        else if (seqProvider.equals(DBConstants.PROVIDER_NCBI)) {

			links.append("(<a href='" + ncbiGmUrl + seqID + "'>NCBI Gene Model</a>)");
		}
        else if (seqProvider.equals(DBConstants.PROVIDER_NIA)) {

			links.append("(<a href='" + niaUrl + seqID + "'>NIA</a>)");
		}
        else if (seqProvider.equals(DBConstants.PROVIDER_REFSEQ)) {

			links.append("(<a href='" + refSeqUrl + seqID + "'>RefSeq</a>)");
		}
        else if (seqProvider.equals(DBConstants.PROVIDER_VEGATRANSCRIPT)) {

			links.append("(<a href='" + vegaTranUrl + seqID + "'>VEGA</a>)");
		}
        else if (seqProvider.equals(DBConstants.PROVIDER_VEGA)) {

			links.append("(<a href='" + vegaGmUrl + seqID + "'>VEGA</a>)");
		}
        else if (seqProvider.equals(DBConstants.PROVIDER_VEGAPROTEIN)) {

			links.append("(<a href='" + vegaProtUrl + seqID + "'>VEGA</a>)");
		}
        else if (seqProvider.equals(DBConstants.PROVIDER_DOTS)) {

			links.append("(<a href='" + dotsUrl + seqID + "'>DoTS</a>)");
		}


        return links.toString();
    }


} // end of class ProviderLinker

