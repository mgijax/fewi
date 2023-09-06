package org.jax.mgi.fewi.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.jax.mgi.fe.datamodel.Disease;
import org.jax.mgi.fe.datamodel.DiseaseModel;
import org.jax.mgi.fe.datamodel.Reference;
import org.jax.mgi.fewi.util.DiseaseModelFilter;
import org.jax.mgi.fewi.util.NotesTagConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExcelDiseaseModelTab extends AbstractBigExcelView {

	private final NotesTagConverter ntc = new NotesTagConverter();

	// logger for the class
	private Logger logger = LoggerFactory.getLogger(ExcelDiseaseModelTab.class);
	
	@Override
	public void buildExcelDocument(
			Map<String,Object> model, 
			SXSSFWorkbook workbook, 
			HttpServletRequest request, 
			HttpServletResponse response)
	{
		logger.debug("buildExcelDocument");
		
		DiseaseModelFilter dmFilter = new DiseaseModelFilter();

		response.setHeader("Content-Disposition","attachment; filename=\"modelTabReport.xlsx\"");
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

		
		Sheet sheet = workbook.createSheet();
		String[] headerTitles = {"ModelCategory",
				"Disease Term",
				"Allele Composition",
				"Genetic Background",
				"Reference"};
		Row header = sheet.createRow(0);
		// add the header row
		for(int i=0;i<headerTitles.length;i++)
		{
			header.createCell(i).setCellValue(headerTitles[i]);
		}

		Row row;
		int rownum = 1;
		
		// loop though ordered list of groups
		for (String groupIdentifier : groupIdentifiers) {
			
			List<DiseaseModel> diseaseModels = groupDiseaseModels.get(groupIdentifier);

			// if we have any, loop through these models and output to file writer
			if (diseaseModels != null) {
			for (DiseaseModel diseaseModel : diseaseModels) {
			
				row = sheet.createRow(rownum++);

				// add group identifier and disease term
				row.createCell(0).setCellValue(groupIdentifier);
				row.createCell(1).setCellValue(diseaseModel.getDisease());

				// alleleic composition
				String allComp = ntc.convertNotes(diseaseModel.getAllelePairs(),'|',true);
				allComp = allComp.replace("\n", " ").replace("<sup>","<").replace("</sup>",">");
				row.createCell(2).setCellValue(allComp);

				// background 
				row.createCell(3).setCellValue(diseaseModel.getBackgroundStrain());
				
				// references
				String refs = "";
				for (Reference ref : diseaseModel.getReferences()) {
					refs = refs + ref.getJnumID() + " ";
				}
				row.createCell(4).setCellValue(refs);
				
/*				
				// references
				for (Reference ref : diseaseModel.getReferences()) {
					writer.write(ref.getJnumID() + " ");
				}
*/				
			}
			}
		}

	}

}
