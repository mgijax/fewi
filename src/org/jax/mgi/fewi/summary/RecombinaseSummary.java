package org.jax.mgi.fewi.summary;

import mgi.frontend.datamodel.Allele;
import mgi.frontend.datamodel.AlleleSystem;
import mgi.frontend.datamodel.RecombinaseInfo;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

import javax.persistence.Column;
import mgi.frontend.datamodel.AlleleSynonym;

/** wrapper around an allele, to expose only certain data for a recombinase
 * summary page.  This will aid in efficient conversion to JSON notation and
 * transportation of data across the wire.  (We won't serialize more data
 * than needed.
 * @author jsb
 */
public class RecombinaseSummary {
	//-------------------
	// instance variables
	//-------------------

	private Allele allele;
	private RecombinaseInfo recombinaseInfo;
	
	// hide the default constructor
    private RecombinaseSummary () {}

    public RecombinaseSummary (Allele allele) {
    	this.allele = allele;
    	this.recombinaseInfo = allele.getRecombinaseInfo();
    	return;
    }
    
    public List<AlleleSystemSummary> getAffectedSystems() {
    	return this.wrapSystems(this.recombinaseInfo.getAffectedSystems());
    }
    
    public List<AlleleSystemSummary> getAlleleSystems() {
    	return this.wrapSystems(this.recombinaseInfo.getAlleleSystems());
    }
    
    public String getAlleleType() {
    	return this.allele.getAlleleType();
    }
    
    public Integer getCountOfReferences() {
    	return this.allele.getCountOfReferences();
    }
    
    public Integer getDetectedCount() {
		return this.recombinaseInfo.getDetectedCount();
	}
    
    public String getDriver() {
    	return this.allele.getDriver();
    }
    
    public String getGeneName() {
    	return this.allele.getGeneName();
    }
    
    public Integer getImsrCount() {
    	return this.allele.getImsrStrainCount();
    }
    
    public String getInAdiposeTissue() {
		return this.flagToString(this.recombinaseInfo.getInAdiposeTissue());
	}
    
    public String getInAlimentarySystem() {
		return this.flagToString(this.recombinaseInfo.getInAlimentarySystem());
	}

	public String getInBranchialArches() {
		return this.flagToString(this.recombinaseInfo.getInBranchialArches());
	}

    public String getInCardiovascularSystem() {
		return this.flagToString(this.recombinaseInfo.getInCardiovascularSystem());
	}

	public String getInCavitiesAndLinings() {
		return this.flagToString(this.recombinaseInfo.getInCavitiesAndLinings());
	}

    public String getInducibleNote() {
    	return this.allele.getInducibleNote();
    }

	public String getInEarlyEmbryo() {
		return this.flagToString(this.recombinaseInfo.getInEarlyEmbryo());
	}

	public String getInEmbryoOther() {
		return this.flagToString(this.recombinaseInfo.getInEmbryoOther());
	}

	public String getInEndocrineSystem() {
		return this.flagToString(this.recombinaseInfo.getInEndocrineSystem());
	}

	public String getInExtraembryonicComponent() {
		return this.flagToString(this.recombinaseInfo.getInExtraembryonicComponent());
	}

	public String getInHead() {
		return this.flagToString(this.recombinaseInfo.getInHead());
	}

	public String getInHemolymphoidSystem() {
		return this.flagToString(this.recombinaseInfo.getInHemolymphoidSystem());
	}

	public String getInIntegumentalSystem() {
		return this.flagToString(this.recombinaseInfo.getInIntegumentalSystem());
	}

	public String getInLimbs() {
		return this.flagToString(this.recombinaseInfo.getInLimbs());
	}

	public String getInLiverAndBiliarySystem() {
		return this.flagToString(this.recombinaseInfo.getInLiverAndBiliarySystem());
	}

	public String getInMesenchyme() {
		return this.flagToString(this.recombinaseInfo.getInMesenchyme());
	}

	public String getInMuscle() {
		return this.flagToString(this.recombinaseInfo.getInMuscle());
	}

	public String getInNervousSystem() {
		return this.flagToString(this.recombinaseInfo.getInNervousSystem());
	}

	public String getInPostnatalOther() {
		return this.flagToString(this.recombinaseInfo.getInPostnatalOther());
	}

	public String getInRenalAndUrinarySystem() {
		return this.flagToString(this.recombinaseInfo.getInRenalAndUrinarySystem());
	}

	public String getInReproductiveSystem() {
		return this.flagToString(this.recombinaseInfo.getInReproductiveSystem());
	}

	public String getInRespiratorySystem() {
		return this.flagToString(this.recombinaseInfo.getInRespiratorySystem());
	}

	public String getInSensoryOrgans() {
		return this.flagToString(this.recombinaseInfo.getInSensoryOrgans());
	}

	public String getInSkeletalSystem() {
		return this.flagToString(this.recombinaseInfo.getInSkeletalSystem());
	}

	public String getInTail() {
		return this.flagToString(this.recombinaseInfo.getInTail());
	}

	public String getName() {
    	return this.allele.getName();
    }

	public Integer getNotDetectedCount() {
		return this.recombinaseInfo.getNotDetectedCount();
	}

	public String getSymbol() {
    	return this.allele.getSymbol();
    }

	public String getSynonyms() {
		Set<AlleleSynonym> alleleSynonyms = this.allele.getSynonyms();
		ArrayList<String> synonyms = new ArrayList<String>();
		Iterator<AlleleSynonym> it = alleleSynonyms.iterator();
		AlleleSynonym alleleSynonym = null;
		
		while (it.hasNext()) {
			alleleSynonym = it.next();
			synonyms.add(alleleSynonym.getSynonym());
		}
		Collections.sort(synonyms);
		
		Iterator<String> si = synonyms.iterator();
		StringBuffer sb = new StringBuffer();
		while (si.hasNext()) {
			sb.append (si.next());
			if (si.hasNext()) {
				sb.append (", ");
			}
		}
		return sb.toString();
	}
	
    public List<AlleleSystemSummary> getUnaffectedSystems() {
    	return this.wrapSystems(this.recombinaseInfo.getUnaffectedSystems());
    }

    private String flagToString (Integer isDetected) {
    	if (isDetected == null) {
    		return "";
    	} else if (isDetected.intValue() == 1) {
    		return "Detected";
    	}
    	return "Not Detected";
    }
    
    private List<AlleleSystemSummary> wrapSystems (List<AlleleSystem> systems) {
    	ArrayList<AlleleSystemSummary> summaries = new ArrayList<AlleleSystemSummary> ();
    	Iterator<AlleleSystem> it = systems.iterator();
    	while (it.hasNext()) {
    		summaries.add(new AlleleSystemSummary(it.next()));
    	}
    	return summaries;
    }
}
