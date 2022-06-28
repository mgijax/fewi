package org.jax.mgi.fewi.detail;

import mgi.frontend.datamodel.Allele;

/*
 * Logic for renderering the allele detail page
 */
public class AlleleDetail {
	Allele allele;
	boolean hasAnatomyTerms = false;
	
	public AlleleDetail(Allele allele)
	{
		this.allele = allele;
	}
	
	public AlleleDetail(Allele allele, boolean hasAnatomyTerms)
	{
		this.allele = allele;
		this.hasAnatomyTerms = hasAnatomyTerms;
	}
	
	/**
	 * Ribbon appearance logic sections
	 */
	public boolean getHasNomenclature()
        {
            return true;
        }
	public boolean getHasMutationOrigin()
        {
            return getHasTransmission() || getHasStrain() 
    			|| getHasMutantCellLines() || getHasParentCellLine();
        }
	public boolean getHasMutationDescription()
	{
		return true;
	}
	public boolean getHasIMSR()
	{
		return !allele.getAlleleType().equals("QTL");
	}
	public boolean getHasExpression()
	{
		return (allele.getCountOfExpressionAssayResults() > 0) || this.hasAnatomyTerms;
	}
	public boolean getHasReferences()
	{
		return allele.getCountOfReferences() > 0;
	}
	public boolean getHasDiseaseModel()
	{
		return allele.getHasDiseaseModel();
	}
	public boolean getHasTumorData()
	{
		return allele.getIsWildType() == 0 && allele.getHasTumorData() == 1;
	}
	public boolean getHasPhenotypes()
	{
		return allele.getPhenoTableSystems() != null 
				&& allele.getPhenoTableSystems().size()>0;
	}
	public boolean getHasRecombinaseData()
	{
//		return allele.getDriverNote() != null &&
//				!allele.getDriverNote().equals("");
		return allele.getDriver() != null && !allele.getDriver().trim().equals("");
	}
	public boolean getHasNotes()
	{
		return getHasQtlNotes() || getHasKnockoutNote() ||
				getHasDerivationNote() || getHasGeneralNote();
	}
	
	/*
	 * helper boolean checks
	 */
	public boolean getHasTransmission()
	{
		return allele.getTransmissionType() != null;
	}
	public boolean getHasStrain()
	{
		return allele.getStrain() != null;
	}
	public boolean getHasMutantCellLines()
	{
		return allele.getMutantCellLines().size()>0;
	}
	public boolean getHasParentCellLine()
	{
		return allele.getParentCellLine() != null;
	}
	
	public boolean getHasQtlNotes()
	{
		return "QTL".equals(allele.getAlleleType()) && (allele.getMarker().getQtlMappingNotes().size()>0 ||
				allele.getMarker().getQtlCandidateGeneNotes().size()>0);
	}
	public boolean getHasKnockoutNote()
	{
		return (allele.getHolder() != null) && (allele.getCompanyID() != null);
	}
	public boolean getHasDerivationNote()
	{
		return allele.getDerivationNote() != null;
	}
	public boolean getHasGeneralNote()
	{
		return allele.getGeneralNote() != null;
	}
}
