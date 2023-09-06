package org.jax.mgi.fewi.view;

import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jax.mgi.fe.datamodel.Disease;
import org.jax.mgi.fe.datamodel.DiseaseModel;
import org.jax.mgi.fe.datamodel.Reference;
import org.jax.mgi.fewi.util.DiseaseModelFilter;
import org.jax.mgi.fewi.util.NotesTagConverter;

public class TextDiseaseModelTab extends AbstractTextView {

	private final NotesTagConverter ntc = new NotesTagConverter();

	
	@Override
	protected void buildTextDocument(
			Map<String, Object> model, 
			BufferedWriter writer,
			HttpServletRequest request, 
			HttpServletResponse response)
			throws Exception {
		
		DiseaseModelFilter dmFilter = new DiseaseModelFilter();

		response.setHeader("Content-Disposition","attachment; filename=\"modelTabReport.txt\"");
		//System.out.println(response.getCharacterEncoding());
		
		// pull in the disease for this report
		@SuppressWarnings("unchecked")
		Disease disease = (Disease) model.get("disease");
		
		// group identifiers to display where/how this model was found
		String mouseAndHuman = "MouseAndHuman";
		String mouseOnly     = "MouseOnly";
		String humanOnly     = "HumanOnly";
		String transgenes    = "TransgenesAndOtherMutations";
		String complexModels = "AdditionalComplexModels";
		String notModels     = "NotModels";

		// order to display
		List<String> groupIdentifiers = new ArrayList<String>();
		groupIdentifiers.add(mouseAndHuman);
		groupIdentifiers.add(mouseOnly);
		groupIdentifiers.add(humanOnly);
		groupIdentifiers.add(transgenes);
		groupIdentifiers.add(complexModels);
		groupIdentifiers.add(notModels);
		
		// map group identifiers to list of disease models for this disease
		Map<String, List<DiseaseModel>> groupDiseaseModels = new HashMap<String, List<DiseaseModel>>();
		groupDiseaseModels.put(mouseAndHuman, dmFilter.filter(disease.getMouseAndHumanModels()));
		groupDiseaseModels.put(mouseOnly, dmFilter.filter(disease.getMouseOnlyModels()));
		groupDiseaseModels.put(humanOnly, dmFilter.filter(disease.getHumanOnlyModels()));
		groupDiseaseModels.put(transgenes, dmFilter.filter(disease.getOtherModels()));
		groupDiseaseModels.put(complexModels, dmFilter.filter(disease.getAdditionalModels()));
		groupDiseaseModels.put(notModels, dmFilter.filter(disease.getNotModels()));

		// write the headers out to the file
		writer.write("ModelCategory\tDisease Term\tAllele Composition\tGenetic Background\tReference\r\n");		
		
		// loop though ordered list of groups
		for (String groupIdentifier : groupIdentifiers) {
			
			List<DiseaseModel> diseaseModels = groupDiseaseModels.get(groupIdentifier);

			// if we have any, loop through these models and output to file writer
			if (diseaseModels != null) {
			for (DiseaseModel diseaseModel : diseaseModels) {
			
				// type
				writer.write(groupIdentifier + "\t");
			
				//disease
				writer.write(diseaseModel.getDisease() + "\t");
			
				// allelic composition - massage for .txt output
				String allComp = ntc.convertNotes(diseaseModel.getAllelePairs(),'|',true);
				allComp = allComp.replace("\n", " ").replace("<sup>","<").replace("</sup>",">");
				writer.write(allComp + "\t");
			
				// background strain
				writer.write(diseaseModel.getBackgroundStrain() + "\t");

				// references
				for (Reference ref : diseaseModel.getReferences()) {
					writer.write(ref.getJnumID() + " ");
				}

				// close this row
				writer.write("\r\n");
			}
			}
		}

	}

}
