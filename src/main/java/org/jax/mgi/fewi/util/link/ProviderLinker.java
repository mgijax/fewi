package org.jax.mgi.fewi.util.link;

import org.jax.mgi.fe.datamodel.Sequence;
import org.jax.mgi.fewi.util.DBConstants;
import org.jax.mgi.shr.jsonmodel.SimpleSequence;

/**
* provides static methods to generate links to external providers
*/
public class ProviderLinker
{
    /*--------------------*/
    /* instance variables */
    /*--------------------*/

	private static String genBankUrl = "https://www.ncbi.nlm.nih.gov/entrez/viewer.fcgi?db=nuccore&id=";
	private static String enaUrl = "http://www.ebi.ac.uk/ena/data/view/";
	private static String ddbjUrl = "http://getentry.ddbj.nig.ac.jp/cgi-bin/get_entry.pl?";
	private static String dfciUrl = "http://compbio.dfci.harvard.edu/tgi/cgi-bin/tgi/tc_report.pl?species=mouse&tc=";
	private static String ensemblGmUrl = "http://www.ensembl.org/Mus_musculus/geneview?gene=";
	private static String ensemblProtUrl = "http://www.ensembl.org/Mus_musculus/Transcript/ProteinSummary?db=core;p=";
	private static String ensemblTranUrl = "http://www.ensembl.org/Mus_musculus/Transcript/Summary?db=core;t=";
	private static String uniProtUrl = "http://www.uniprot.org/entry/";
	private static String ebiSwissProtUrl = "http://www.ebi.ac.uk/s4/summary/molecular?term=";
	private static String ebiTrEMBLUrl = "https://www.ebi.ac.uk/ebisearch/search.ebi?db=allebi&query=";
	private static String ncbiGmUrl = "https://www.ncbi.nlm.nih.gov/entrez/query.fcgi?db=gene&cmd=Retrieve&dopt=Graphics&list_uids=";
	private static String niaUrl = "http://lgsun.grc.nia.nih.gov/geneindex/mm9/bin/giU.cgi?genename=";
	private static String refSeqUrl = "https://www.ncbi.nlm.nih.gov/entrez/viewer.cgi?val=";
	private static String dotsUrl = "http://genomics.betacell.org/gbco/showSummary.do?questionFullName=TranscriptQuestions.TranscriptFromDtIds&myProp%28dtIdP%29=";
	private static String mgpSeqUrl = "http://useast.ensembl.org/Mus_musculus_<strain>/Gene/Summary?db=core;g=<id>";
	private static String ensemblRegUrl = "http://useast.ensembl.org/Mus_musculus/Regulation/Summary?rf=";
	private static String vistaUrl = "https://enhancer.lbl.gov/vista/element?vistaId=mm";

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
			links.append("<a href='" + enaUrl + genbankID + "'>ENA</a> | ");
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
        else if (seqProvider.equals(DBConstants.PROVIDER_SWISSPROT)) {

			links.append("<a href='" + uniProtUrl + seqID + "'>UniProt</a> | ");
			links.append("<a href='" + ebiSwissProtUrl + seqID + "'>EBI</a>");
		}
        else if (seqProvider.equals(DBConstants.PROVIDER_TREMBL)) {

			links.append("<a href='" + uniProtUrl + seqID + "'>UniProt</a> | ");
			links.append("<a href='" + ebiTrEMBLUrl + seqID + "'>EBI</a>");
		}
        else if (seqProvider.equals(DBConstants.PROVIDER_NCBI)) {

			links.append("<a href='" + ncbiGmUrl + seqID + "'>NCBI Gene Model</a>");
		}
        else if (seqProvider.equals(DBConstants.PROVIDER_REFSEQ)) {

			links.append("<a href='" + refSeqUrl + seqID + "'>RefSeq</a>");
		}
        else if (seqProvider.equals(DBConstants.PROVIDER_MGP)) {
			
			// need to pull the strain out of the ID, then insert it into the URL, and insert the ID into the URL
			String[] pieces = seqID.split("_");
			if (pieces.length == 3) {
				String myUrl = mgpSeqUrl.replace("<strain>", tweakStrainValue(pieces[1])).replaceAll("<id>", seqID);
				links.append("<a href='" + myUrl + "'>Ensembl</a>");
			} else {
				// bad ID format, just show the provider
				links.append("Mouse Genomes Project");
			}

		}
        else if (seqProvider.equals(DBConstants.PROVIDER_MGI_SGM)) {
			
			// no extra link for MGI C57BL/6J Strain Gene Model; just use the standard sequence detail link
		}
        else if (seqProvider.equals(DBConstants.PROVIDER_ENSEMBLREG)) {
			links.append("<a href='" + ensemblRegUrl + seqID + "'>Ensembl</a>");
		}

        else if (seqProvider.equals(DBConstants.PROVIDER_VISTA)) {
                        String numericPart = seqID.replaceAll("^[^0-9]*","");
			links.append("<a href='" + vistaUrl + numericPart + "'>VISTA</a>");
		}

        return links.toString();
    }

    /* The strain we extract from the MGP ID is not always what we want to plug into the middle of mgpSeqUrl;
     * sometimes we need to tweak it a bit.
     */
    public static String tweakStrainValue(String strain) {
    	if (strain.equals("129S1SvImJ")) {			// 129S1/SvImJ
    		return "129S1_SvImJ";
    	} else if (strain.endsWith("EiJ")) {		// CAROLI/EiJ, CAST/EiJ, SPRET/EiJ, WSB/EiJ    
    		return strain.replace("EiJ", "_EiJ");
    	} else if (strain.equals("PWKPhJ")) {		// PWK/PhJ    
    		return "PWK_PhJ";
    	} else if (strain.equals("NODShiLtJ")) {	// NOD/ShiLtJ 
    		return "NOD_ShiLtJ";
    	} else if (strain.equals("NZOHlLtJ")) {		// NZO/HlLtJ  
    		return "NZO_HlLtJ";
    	} else if (strain.equals("C57BL6NJ")) {		// C57BL/6NJ  
    		return "C57BL_6NJ";
    	} else if (strain.equals("FVBNJ")) {		// FVB/NJ     
    		return "FVB_NJ";
    	} else if (strain.equals("DBA2J")) {		// DBA/2J     
    		return "DBA_2J";
    	} else if (strain.equals("C3HHeJ")) {		// C3H/HeJ
    		return "C3H_HeJ";    
    	} else if (strain.equals("BALBcJ")) {		// BALB/cJ    
    		return "BALB_cJ";    
    	} else if (strain.endsWith("J")) {			// A/J, AKR/J, CBA/J, LP/J       
    		return strain.replace("J", "_J");
    	}
    	return strain;
    }
} // end of class ProviderLinker

