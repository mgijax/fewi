package org.jax.mgi.fewi.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.jax.mgi.fe.datamodel.Sequence;
import org.jax.mgi.fewi.config.ContextLoader;

/* Is: a sequence that is being prepared for forwarding to NCBI's BLAST query
 * 	form
 * Has: a seqfetch-style specification for the sequence, a Sequence object,
 * 	the text of the sequence from seqfetch (if needed), an amount of
 * 	flanking sequence requested, etc.
 * Does: retrieves the text of the sequence from seqfetch (if needed), ...
 */
public class BlastableSequence {

    //--------------------------//
    //--- instance variables ---//
    //--------------------------//

    private String seqID;	// the sequence ID
    private String params;	// seqfetch-style parameters from URL
    private Sequence sequence;	// the Sequence object from the db
    private String provider;	// the provider of the sequence
    private String flankAmount;	// amount of flanking sequence requested
    private String fasta;	// FASTA sequence from seqfetch
    private String snpFlank;	// flanking sequence submitted for a SNP
    private String snpID;	// ID of the SNP for the snpFlank

    // list of error messages accumulated in this object
    private List<String> errors = new ArrayList<String>();

    //--------------------//
    //--- constructors ---//
    //--------------------//

    // use the default constructor
    public BlastableSequence() {}

    //-----------------------//
    //--- private methods ---//
    //-----------------------//

    private void logError(String s) {
	this.errors.add(s);
    }

    //------------------------//
    //--- instance methods ---//
    //------------------------//

    // set the sequence ID
    public void setSequenceID(String seqID) {
	this.seqID = seqID;
    }

    // set the seqfetch-style parameter string
    public void setParameters(String params) {
	if (params != null) {
	this.params = params;
	} else {
	    this.logError("Parameter string is null for " + this.seqID);
	}
    }

    // set the Sequence object from the database
    public void setSequence(Sequence seq) {
	if (seq != null) {
	    this.sequence = seq;
	    this.provider = seq.getProvider();
	} else {
	    this.logError(this.seqID + " did not uniquely identify a sequence");
	}
    }

    // set the amount of flanking sequence requested (in kb)
    public void setFlankAmount(String flank) {
	// just ignore null and zero amounts
	if ((flank == null) || ("0".equals(flank)) || "".equals(flank)) {
	    return;
	}

	this.flankAmount = flank;

	// if the flank String does not contain an integer (or if it is a 
	// negative number), log the error
	try {
	    int i = Integer.parseInt(flank);
	    if (i < 0) {
		this.logError("Flank amount (" + flank
		    + ") may not be negative for " + this.seqID);
	    }
	} catch (NumberFormatException e) {
	    this.logError("Flank amount (" + flank
		+ ") must be an integer");
	}
    }

    // set the SNP ID to go with the SNP flanking sequence
    public void setSnpID (String snpID) {
	this.snpID = snpID;
    }

    // set the flanking sequence for a SNP
    public void setSnpFlank(String snpFlank) {
	this.snpFlank = snpFlank;
    }

    // gets the Sequence object
    public Sequence getSequence() {
	return this.sequence;
    }

    // gets the sequence provider
    public String getProvider() {
	return this.provider;
    }

    // gets the sequence ID
    public String getSeqID() {
	return this.seqID;
    }

    // gets the seqfetch-style parameter string
    public String getParameters() {
	return this.params;
    }

    // gets the flank amount requested
    public String getFlankAmount() {
	return this.flankAmount;
    }

    // returns true if errors have been caught, false if not
    public boolean hasErrors() {
	if (this.errors.size() > 0) {
	    return true;
	}
	return false;
    }

    // gets the list of error message strings; will be an empty list if no
    // errors have been found
    public List<String> getErrors() {
	return this.errors;
    }

    // get the organism of the sequence
    public String getOrganism() {
	if (this.sequence != null) {
	    return this.sequence.getOrganism();
	}
	return null;
    }

    // get the type of the sequence
    public String getType() {
	if (this.sequence != null) {
	    return this.sequence.getSequenceType();
	}
	return null;
    }

    // returns the SNP ID, if one was set
    public String getSnpID() {
	return this.snpID;
    }

    // returns the SNP flanking sequence, if one was set
    public String getSnpFlank() {
	return this.snpFlank;
    }

    // can we submit this sequence to NCBI BLAST by ID?  true if yes, false if
    // not
    public boolean canSubmitByID() {
	// We can only submit by ID for three sequence providers:
	// 	GenBank, SWISS-PROT, and RefSeq

	if (this.provider == null) {
	    return false;
	} else if (this.provider.startsWith("GenBank")) {
	    return true;
	} else if ("SWISS-PROT".equals(this.provider)) {
	    return true;
	} else if ("RefSeq".equals(this.provider)) {
	    return true;
	}
	return false;
    }

    // get a brief description of this sequence (for display)
    public String getDescription() {
	if (this.seqID != null) {
	    if (this.getProvider() != null) {
		return this.seqID + ", " + this.getProvider();
	    } else {
		return this.seqID;
	    }
	} else if (this.snpID != null) {
	    return this.snpID + ", flanking sequence";
	} else {
	    return "Unidentified sequence";
	}
    }

    // return the FASTA format for this sequence
    public String getFasta() {
	// if we've already cached it, return it without further ado

	if (this.fasta != null) {
	    return this.fasta;
	} else if (this.snpFlank != null) {
	    if (this.snpID != null) {
		return ">" + this.snpID + "_flanking_sequence\n"
		    + this.snpFlank;
	    } else {
	        return ">SNP_flanking_sequence\n" + this.snpFlank;
	    }
	}

	// collects the response from seqfetch
	StringBuffer response = new StringBuffer();

	// the URL to get the sequence from seqfetch
	String sfUrl = ContextLoader.getConfigBean().getProperty("SEQFETCH_URL")
	    + "seq1=" + this.params;

	if (this.flankAmount != null) {
	    sfUrl = sfUrl + "&flank1=" + this.flankAmount;
	}

	try {
	    URL url = new URL(sfUrl);
	    URLConnection urlConnection = url.openConnection();
	    HttpURLConnection connection = null;

	    if (urlConnection instanceof HttpURLConnection) {
		connection = (HttpURLConnection) urlConnection;
		BufferedReader in = new BufferedReader(new InputStreamReader (
		    connection.getInputStream()));
		String current;
		current = in.readLine();

		while (current != null) {
		    if (response.length() > 0) {
			response.append("\n");
		    }
		    response.append(current);
		    current = in.readLine(); 
		}
		in.close();
		connection.disconnect();
	    } else {
		this.logError("Could not retrieve FASTA for " + this.seqID
		    + " (URL is not HTTP)");
	    }

	} catch (MalformedURLException mue) {
	    this.logError("Could not retrieve FASTA for " + this.seqID
		+ " (errant URL)");

	} catch (IOException ioe) {
	    this.logError("Could not retrieve FASTA for " + this.seqID
		+ " (I/O error)");
	}

	// if we didn't get the FASTA for the sequence, just return null
	if (response.length() == 0) {
	    return null;
	}

	this.fasta = response.toString();

	// if we got an error from seqfetch, then log it and skip the FASTA
	if (this.fasta.indexOf(" error ") >= 0) {
	    this.fasta = null;
	    this.logError("Could not retrieve FASTA for " + this.seqID
		+ " (EMBOSS error)");
	}

	// replace the header line (the first line) with one that only contains
	// the seq ID, if we have a space in that first line
	
	if ((this.fasta != null) && (this.seqID != null)) {
	    int lineEnd = this.fasta.indexOf("\n");
	    int firstSpace = this.fasta.indexOf(" ");

	    if ((firstSpace >= 0) && (firstSpace < lineEnd)) {
		this.fasta = ">" + this.seqID + this.fasta.substring(lineEnd);
	    }
	}
	return this.fasta;
    }
}
