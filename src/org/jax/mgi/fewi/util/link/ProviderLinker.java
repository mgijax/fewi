package org.jax.mgi.fewi.util.link;

import org.jax.mgi.fewi.util.DBConstants;
import org.jax.mgi.shr.jsonmodel.SimpleSequence;

import mgi.frontend.datamodel.Sequence;
import mgi.frontend.datamodel.SequenceID;

/**
* provides static methods to generate links to external providers
*/
public class ProviderLinker
{
    /*--------------------*/
    /* instance variables */
    /*--------------------*/

	// logger for the class

	private static String genBankUrl = "https://www.ncbi.nlm.nih.gov/entrez/viewer.fcgi?db=nuccore&id=";
	private static String emblUrl = "http://www.ebi.ac.uk/ena/data/view/";
	private static String ddbjUrl = "http://getentry.ddbj.nig.ac.jp/cgi-bin/get_entry.pl?";
	private static String dfciUrl = "http://compbio.dfci.harvard.edu/tgi/cgi-bin/tgi/tc_report.pl?species=mouse&tc=";
	private static String ensemblGmUrl = "http://www.ensembl.org/Mus_musculus/geneview?gene=";
	private static String ensemblProtUrl = "http://www.ensembl.org/Mus_musculus/Transcript/ProteinSummary?db=core;p=";
	private static String ensemblTranUrl = "http://www.ensembl.org/Mus_musculus/Transcript/Summary?db=core;t=";
	private static String uniProtUrl = "http://www.uniprot.org/entry/";
	private static String ebiUrl = "http://www.ebi.ac.uk/s4/summary/molecular?term=";
	private static String ncbiGmUrl = "https://www.ncbi.nlm.nih.gov/entrez/query.fcgi?db=gene&cmd=Retrieve&dopt=Graphics&list_uids=";
	private static String niaUrl = "http://lgsun.grc.nia.nih.gov/geneindex/mm9/bin/giU.cgi?genename=";
	private static String refSeqUrl = "https://www.ncbi.nlm.nih.gov/entrez/viewer.cgi?val=";
	private static String dotsUrl = "http://genomics.betacell.org/gbco/showSummary.do?questionFullName=TranscriptQuestions.TranscriptFromDtIds&myProp%28dtIdP%29=";
	private static String mgpSeqUrl = "http://useast.ensembl.org/Mus_musculus_A_J/Gene/Summary?db=core;g=";

    /*-------------------------*/
    /* public instance methods */
    /*-------------------------*/

    /**
     *  Generates the provider links for a given sequence
     */
    public static String getSeqProviderLinks (Sequence sequence) {
    	if (sequence.getPreferredGenBankID() != null) {
    		return getSeqProviderLinks(sequence.getProvider(), sequence.getPrimaryID(),
    				sequence.getPreferredGenBankID().getAccID());
    	}
    	return getSeqProviderLinks(sequence.getProvider(), sequence.getPrimaryID(), null);
    }
    
    /**
     *  Generates the provider links for a given sequence
     */
    public static String getSeqProviderLinks (SimpleSequence sequence) {
    	return getSeqProviderLinks(sequence.getProvider(), sequence.getPrimaryID(),
    		sequence.getPreferredGenbankID());
    }
    
    public static String getSeqProviderLinks (String seqProvider, String seqID, String genbankID) {

        StringBuffer links = new StringBuffer();

        // all genbank
        if (seqProvider.startsWith(DBConstants.PROVIDER_SEQUENCEDB)) {
        	if (genbankID == null) {
        		genbankID = seqID;
        	}

			links.append("<a href='" + genBankUrl + genbankID + "'>GenBank</a> | ");
			links.append("<a href='" + emblUrl + genbankID + "'>EMBL</a> | ");
			links.append("<a href='" + ddbjUrl + genbankID + "'>DDBJ</a>");
		}
        else if (seqProvider.equals(DBConstants.PROVIDER_ENSEMBL)) {

			links.append("<a href='" + ensemblGmUrl + seqID + "'>Ensembl Gene Model</a>");
		}
        else if (seqProvider.equals(DBConstants.PROVIDER_ENSEMBLPROTEIN)) {

			links.append("<a href='" + ensemblProtUrl + seqID + "'>Ensembl</a>");
		}
        else if (seqProvider.equals(DBConstants.PROVIDER_ENSEMBLTRANSCRIPT)) {

			links.append("<a href='" + ensemblTranUrl + seqID + "'>Ensembl</a>");
		}
        else if (seqProvider.equals(DBConstants.PROVIDER_TREMBL) ||
                 seqProvider.equals(DBConstants.PROVIDER_SWISSPROT)) {

			links.append("<a href='" + uniProtUrl + seqID + "'>UniProt</a> | ");
			links.append("<a href='" + ebiUrl + seqID + "'>EBI</a>");
		}
        else if (seqProvider.equals(DBConstants.PROVIDER_NCBI)) {

			links.append("<a href='" + ncbiGmUrl + seqID + "'>NCBI Gene Model</a>");
		}
        else if (seqProvider.equals(DBConstants.PROVIDER_REFSEQ)) {

			links.append("<a href='" + refSeqUrl + seqID + "'>RefSeq</a>");
		} else if (seqProvider.equals(DBConstants.PROVIDER_MGP)) {

			links.append("<a href='" + mgpSeqUrl + seqID + "'>Mouse Genomes Project</a>");
		} else if (seqProvider.equals(DBConstants.PROVIDER_MGI_SGM)) {
			
			// no extra link for MGI C57BL/6J Strain Gene Model; just use the standard sequence detail link
		}

        return links.toString();
    }


} // end of class ProviderLinker

