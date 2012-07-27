package org.jax.mgi.fewi.test.mock;

import java.util.ArrayList;
import java.util.List;

import mgi.frontend.datamodel.GxdAssayResult;
import mgi.frontend.datamodel.ImagePane;
import mgi.frontend.datamodel.Marker;

import org.apache.commons.lang.StringUtils;
import org.jax.mgi.fewi.config.ContextLoader;
import org.jax.mgi.fewi.util.FormatHelper;
import org.jax.mgi.fewi.util.NotesTagConverter;

/**
 * Represents a JSON summary object that was returned from a controller's response. 
 * 
 * @author kstone
 *
 */
public class MockJSONGXDAssayResult 
{
	private String score;
	private String gene;
	private String assayID;
	private String assayType;
	private String anatomicalSystem;
	private String age;
	private String detectionLevel;
	private String figures;
	private String genotype;
	private String structure;
	private String reference;
	
	
	// -------------------------------------------------------------------
	// public instance methods; JSON serializer will call all public methods
	// -------------------------------------------------------------------
	public void setScore(String score) {
		this.score = score;
	}

	public String getScore() {
		return this.score;
	}

	public String getGene() {

		return this.gene;
	}
	
	public String getAssayID()
	{
		return this.assayID;
	}
	
	public String getAssayType() {
		return this.assayType;
	}
	
	public String getAnatomicalSystem()
	{
		return this.anatomicalSystem;
	}

	
	public String getAge()
	{
		return this.age;
	}
	
	public String getStructure()
	{
		return this.structure;
	}

	public String getDetectionLevel()
	{
		return this.detectionLevel;
	}
	
	public String getFigures()
	{
		return this.figures;
	}
	
	public String getGenotype()
	{
		return this.genotype;
	}

	public String getReference()
	{
		return this.reference;
	}
	
	public void setGene(String gene) {
		this.gene = gene;
	}

	public void setAssayID(String assayID) {
		this.assayID = assayID;
	}

	public void setAssayType(String assayType) {
		this.assayType = assayType;
	}

	public void setAnatomicalSystem(String anatomicalSystem) {
		this.anatomicalSystem = anatomicalSystem;
	}

	public void setAge(String age) {
		this.age = age;
	}

	public void setDetectionLevel(String detectionLevel) {
		this.detectionLevel = detectionLevel;
	}

	public void setFigures(String figures) {
		this.figures = figures;
	}

	public void setGenotype(String genotype) {
		this.genotype = genotype;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}
	
	public void setStructure(String structure) {
		this.structure = structure;
	}


}
