package org.jax.mgi.fewi.view;

import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.jax.mgi.fe.datamodel.Allele;
import org.jax.mgi.fe.datamodel.AlleleSynonym;
import org.jax.mgi.fe.datamodel.phenotype.AlleleSummaryDisease;
import org.jax.mgi.fe.datamodel.phenotype.AlleleSummarySystem;

public class TextAlleleSummary extends AbstractTextView
{

    //-------------------
    // instance variables
    //-------------------
    private static String NORMAL_PHENOTYPE="normal phenotype";
    private static String PHENOTYPE_NOT_ANALYZED="phenotype not analyzed";
    //private static String CELL_LINE="Cell Line";
    //private static String CHIMERIC="Chimeric";
    private static String NOT_APPLICABLE="Not Applicable";

    //-----------------------------------
    // method to generate text document
    //-----------------------------------
	@Override
	protected void buildTextDocument(Map<String, Object> model, BufferedWriter writer,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		String filename = "MGIalleleQuery_"+getCurrentDate();
		response.setHeader("Content-Disposition","attachment; filename=\""+filename+".txt\"");
		System.out.println(response.getCharacterEncoding());

        @SuppressWarnings("unchecked")
		List<Allele> alleles = (List<Allele>) model.get("alleles");

		// write the headers
		String[] headerTitles = {
			"MGI Allele ID",
			"Allele Symbol",
			"Allele Name",
			"Chromosome",
			"Synonyms",
			"Allele Type",
			"Allele Attributes",
			"Transmission",
			"Abnormal Phenotypes Reported in these Systems",
			"Human Disease Models"
		};
		for(int i=0;i<headerTitles.length;i++)
		{
			writer.write(headerTitles[i]+"\t");
		}
		writer.write("\r\n");

		// row generation
		for (Allele allele : alleles)
		{
            // prep synonyms
            List<String> synonyms = new ArrayList<String>();
            for(AlleleSynonym synonym : allele.getSynonyms())
            {
                synonyms.add(synonym.getSynonym());
            }

            // prep systems
            List<String> systems = new ArrayList<String>();
            for(AlleleSummarySystem system : allele.getSummarySystems())
            {
              if(!NORMAL_PHENOTYPE.equals(system.getSystem())
				  && !PHENOTYPE_NOT_ANALYZED.equals(system.getSystem()))
              {
                systems.add(system.getSystem());
              }
            }

            // prep diseases
            List<String> diseases = new ArrayList<String>();
            for(AlleleSummaryDisease disease : allele.getSummaryDiseases())
            {
              diseases.add(disease.getDisease() + " " + disease.getDoID());
            }

            // prep allele attribute
            String subtype = allele.getAlleleSubType();
            if (subtype == null) {subtype = " ";}

            // prep transmission
            String transmissionType = allele.getTransmissionType();
            if (transmissionType != null && NOT_APPLICABLE.equals(transmissionType))
            {
				transmissionType = " ";
			}

            // write out this row to file writer
			writer.write(allele.getPrimaryID() + "\t");
			writer.write(allele.getSymbol() + "\t");
			writer.write(allele.getName() + "\t");
			writer.write(allele.getChromosome() + "\t");
			writer.write(StringUtils.join(synonyms," | ") + "\t");
			writer.write(allele.getAlleleType() + "\t");
			writer.write(subtype + "\t");
			writer.write(transmissionType + "\t");
			writer.write(StringUtils.join(systems," | ") + "\t");
			writer.write(StringUtils.join(diseases," | ") + "\t");
			writer.write("\r\n");
		}
	}
}
