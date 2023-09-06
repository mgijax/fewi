package org.jax.mgi.fewi.view;

import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jax.mgi.fe.datamodel.Disease;
import org.jax.mgi.fe.datamodel.DiseaseGroupRow;
import org.jax.mgi.fe.datamodel.DiseaseRow;
import org.jax.mgi.fe.datamodel.DiseaseRowToMarker;

public class TextDiseaseGeneTab extends AbstractTextView {

	
	@Override
	protected void buildTextDocument(
			Map<String, Object> model, 
			BufferedWriter writer,
			HttpServletRequest request, 
			HttpServletResponse response)
			throws Exception 
	{
		
		response.setHeader("Content-Disposition","attachment; filename=\"geneTabReport.txt\"");
		//System.out.println(response.getCharacterEncoding());
		
		// pull in the disease for this report
		@SuppressWarnings("unchecked")
		Disease disease = (Disease) model.get("disease");
		
		// group identifiers to display where/how this model was found
		String mouseAndHuman = "MouseAndHuman";
		String mouseOnly     = "MouseOnly";
		String humanOnly     = "HumanOnly";

		// order to display
		List<String> groupIdentifiers = new ArrayList<String>();
		groupIdentifiers.add(mouseAndHuman);
		groupIdentifiers.add(mouseOnly);
		groupIdentifiers.add(humanOnly);
		
		// map group identifiers to list of disease models for this disease
		Map<String, List<DiseaseGroupRow>> diseaseGroupRows = new HashMap<String, List<DiseaseGroupRow>>();
		if (disease.getMouseHumanGroup() != null) {
			diseaseGroupRows.put(mouseAndHuman, disease.getMouseHumanGroup().getDiseaseGroupRows());
		}
		if (disease.getMouseOnlyGroup() != null) {
			diseaseGroupRows.put(mouseOnly,     disease.getMouseOnlyGroup().getDiseaseGroupRows());
		}
		if (disease.getHumanOnlyGroup() != null) {
			diseaseGroupRows.put(humanOnly,     disease.getHumanOnlyGroup().getDiseaseGroupRows());
		}

		// write the headers out to the file
		writer.write("GeneCategory\tDisease Term\tMouse Homologs\tHuman Homologs\tTransgenes And Other Features\tMouse Models\tHomology Source\r\n");		


		// loop though ordered list of groups (non-transgenes -- they have a different display)
		for (String groupIdentifier : groupIdentifiers) {
			
			List<DiseaseGroupRow> dgRows = diseaseGroupRows.get(groupIdentifier);

			// if we have any, loop through these models and output to file writer
			if (dgRows != null) {
			for (DiseaseGroupRow dgRow : dgRows) {
			
				DiseaseRow dRow = dgRow.getDiseaseRow();

				// type
				writer.write(groupIdentifier + "\t");

				// annotated disease
				writer.write(dgRow.getAnnotatedDisease() + "\t");

				// mouse markers
				StringBuffer  mouseMarkerBuffer = new StringBuffer(" ");
				for (DiseaseRowToMarker marker : dRow.getMouseMarkers()) {
					mouseMarkerBuffer.append(marker.getSymbol());
					if (marker.getIsCausative() == 1){
						mouseMarkerBuffer.append("*");					
					}
					mouseMarkerBuffer.append(" ");
				}
				writer.write(mouseMarkerBuffer.toString() + "\t");

				// human markers
				StringBuffer  humanMarkerBuffer = new StringBuffer(" ");
				for (DiseaseRowToMarker marker : dRow.getHumanMarkers()) {
					humanMarkerBuffer.append(marker.getSymbol());
					if (marker.getIsCausative() == 1){
						humanMarkerBuffer.append("*");					
					}
					humanMarkerBuffer.append(" ");
				}
				writer.write(humanMarkerBuffer.toString() + "\t");

				// trangenes column empty
				writer.write(" \t");
						
				// model count
				writer.write(dRow.getMouseModels().size() + "\t");

				// homology cluster source
				String source = "";
				if (dRow.getHomologyCluster() != null){
				  source = dRow.getHomologyCluster().getSecondarySource();
				}
				writer.write(source + "\t");

				// close this row
				writer.write("\r\n");
			}
			}
		}

		// transgene rows fill different columns
		if (disease.getOtherGroup() != null) {
		for (DiseaseGroupRow dgRow : disease.getOtherGroup().getDiseaseGroupRows()) {
			
			DiseaseRow dRow = dgRow.getDiseaseRow();

			// type
			writer.write("TransgenesAndOtherMutations" + "\t");

			// annotated disease
			writer.write(dgRow.getAnnotatedDisease() + "\t");

			// mouse markers are empty
			writer.write(" \t");

			// human markers are empty
			writer.write(" \t");

			// trangenes column
			StringBuffer  mouseMarkerBuffer = new StringBuffer(" ");
			for (DiseaseRowToMarker marker : dRow.getMouseMarkers()) {
				mouseMarkerBuffer.append(marker.getSymbol());
				mouseMarkerBuffer.append(" ");
			}
			writer.write(mouseMarkerBuffer.toString() + "\t");
					
			// model count
			writer.write(dRow.getMouseModels().size() + "\t");

			// homology cluster source is empty for this group
			writer.write(" \t");

			// close this row
			writer.write("\r\n");
		}
		}

	
	}
}