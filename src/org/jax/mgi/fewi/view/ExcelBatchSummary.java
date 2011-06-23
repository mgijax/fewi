package org.jax.mgi.fewi.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mgi.frontend.datamodel.Annotation;
import mgi.frontend.datamodel.BatchMarkerAllele;
import mgi.frontend.datamodel.BatchMarkerSnp;
import mgi.frontend.datamodel.BatchMarkerId;
import mgi.frontend.datamodel.Marker;
import mgi.frontend.datamodel.MarkerID;
import mgi.frontend.datamodel.MarkerLocation;
import mgi.frontend.datamodel.MarkerTissueCount;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.jax.mgi.fewi.forms.BatchQueryForm;
import org.jax.mgi.fewi.util.DBConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.view.document.AbstractExcelView;

public class ExcelBatchSummary extends AbstractExcelView {
	
	// logger for the class
	private Logger logger = LoggerFactory.getLogger(ExcelReferenceSummary.class);

	@Override
	protected void buildExcelDocument(Map<String, Object> model, HSSFWorkbook workbook, 
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		logger.debug("buildExcelDocument");
		BatchQueryForm queryForm = (BatchQueryForm)model.get("queryForm");
		List<BatchMarkerId> results = (List<BatchMarkerId>) model.get("results");

		HSSFSheet sheet = workbook.createSheet();		
		genHeader(sheet.createRow(0), queryForm);
		
		HSSFRow row, addlRow;
		int rownum = 1;
		int col;
		
		Marker m;
		List<MarkerID> ids;
		List<List<List<String>>> associations = new ArrayList<List<List<String>>>();

		for (BatchMarkerId id : results) {
			associations = new ArrayList<List<List<String>>>();

			
			row = sheet.createRow(rownum++);
			col = 0;
			logger.debug("input: " + id.getTerm());
			row.createCell(col++).setCellValue(id.getTerm());
			logger.debug("type: " + id.getTermType());
			row.createCell(col++).setCellValue(id.getTermType());
			
			m = id.getMarker();			
			
			if (m != null){
				
				ids = m.getIds();
				
				row.createCell(col++).setCellValue(m.getPrimaryID());

				if(queryForm.getNomenclature()){
					logger.debug("nomen");
					row.createCell(col++).setCellValue(m.getSymbol());
					row.createCell(col++).setCellValue(m.getName());
					row.createCell(col++).setCellValue(m.getMarkerSubtype());
				}
				if(queryForm.getLocation()){
					logger.debug("loc");
					MarkerLocation loc = m.getPreferredCoordinates();
					if (loc != null) {
						row.createCell(col++).setCellValue(loc.getChromosome());
						row.createCell(col++).setCellValue(loc.getStrand());
						row.createCell(col++).setCellValue(loc.getStartCoordinate());
						row.createCell(col++).setCellValue(loc.getEndCoordinate());
					} else {
						row.createCell(col++).setCellValue("UN");
						col += 3;
					}
				}
				
				// build associations matrix
				if(queryForm.getEnsembl()){
					logger.debug("ensembl");
					List<List<String>> wrapper = new ArrayList<List<String>>();
					List<String> eIds;
	        		if(ids != null){
	        			for (MarkerID mId : ids) {
	    					if (mId.getLogicalDB().equals(DBConstants.PROVIDER_ENSEMBL)){
	    						eIds = new ArrayList<String>();
	    						eIds.add(mId.getAccID());
	    						wrapper.add(eIds);
	    					}						
	    				}
	        		}
	        		associations.add(wrapper);
				}
				if(queryForm.getEntrez()){
					logger.debug("entrez");
					List<List<String>> wrapper = new ArrayList<List<String>>();
					List<String> eIds;
		    		if(ids != null){
		    			for (MarkerID mId : ids) {
							if (mId.getLogicalDB().equals(DBConstants.PROVIDER_ENTREZGENE)){
								eIds = new ArrayList<String>();
								eIds.add(mId.getAccID());
								wrapper.add(eIds);
							}						
						}
		    		}
		    		associations.add(wrapper);
				}
				if(queryForm.getVega()){
					logger.debug("vega");
					List<List<String>> wrapper = new ArrayList<List<String>>();
					List<String> vIds;
		    		if(ids != null){
		    			for (MarkerID mId : ids) {
							if (mId.getLogicalDB().equals(DBConstants.PROVIDER_VEGA)){
								vIds = new ArrayList<String>();
								vIds.add(mId.getAccID());
								wrapper.add(vIds);
							}						
						}
		    		}
		    		associations.add(wrapper);
				}
				
				if(queryForm.getGo()){
					logger.debug("go");
					List<List<String>> wrapper = new ArrayList<List<String>>();
					List<String> goIds;
	    			for (Annotation goAnnot : m.getGoAnnotations()) {
	    				goIds = new ArrayList<String>();
	    				goIds.add(goAnnot.getTermID());
	    				goIds.add(goAnnot.getTerm());
	    				goIds.add(goAnnot.getEvidenceCode());
	    				wrapper.add(goIds);
					}		    		
		    		associations.add(wrapper);
				} else if(queryForm.getMp()){
					logger.debug("mp");
					List<List<String>> wrapper = new ArrayList<List<String>>();
					List<String> mpIds;
	    			for (Annotation mpAnnot : m.getMPAnnotations()) {
	    				mpIds = new ArrayList<String>();
	    				mpIds.add(mpAnnot.getTermID());
	    				mpIds.add(mpAnnot.getTerm());
						wrapper.add(mpIds);
					}		    		
		    		associations.add(wrapper);
				} else if(queryForm.getOmim()){
					logger.debug("omim");
					List<List<String>> wrapper = new ArrayList<List<String>>();
					List<String> mpIds;
	    			for (Annotation omimAnnot : m.getOMIMAnnotations()) {
	    				mpIds = new ArrayList<String>();
	    				mpIds.add(omimAnnot.getTermID());
	    				mpIds.add(omimAnnot.getTerm());
	    				wrapper.add(mpIds);
					}		
		    		associations.add(wrapper);
				} else if(queryForm.getAllele()){
					logger.debug("allele");
					List<List<String>> wrapper = new ArrayList<List<String>>();
					List<String> alleles;
	    			for (BatchMarkerAllele bma : m.getBatchMarkerAlleles()) {
	    				alleles = new ArrayList<String>();
	    				alleles.add(bma.getAlleleID());
	    				alleles.add(bma.getAlleleSymbol());
	    				wrapper.add(alleles);
					}
		    		associations.add(wrapper);
				} else if(queryForm.getExp()){
					logger.debug("exp");
					List<List<String>> wrapper = new ArrayList<List<String>>();
					List<String> expression;
	    			for (MarkerTissueCount tissue : m.getMarkerTissueCounts()) {
	    				expression = new ArrayList<String>();
	    				expression.add(tissue.getStructure());
	    				expression.add(String.valueOf(tissue.getAllResultCount()));
	    				expression.add(String.valueOf(tissue.getDetectedResultCount()));
	    				expression.add(String.valueOf(tissue.getNotDetectedResultCount()));
	    				wrapper.add(expression);
					}
		    		associations.add(wrapper);
				} else if(queryForm.getRefsnp()){
					logger.debug("refsnp");
					List<List<String>> wrapper = new ArrayList<List<String>>();
					List<String> refSnpIds;
        			for (BatchMarkerSnp snp : m.getBatchMarkerSnps()) {
        				refSnpIds = new ArrayList<String>();
        				refSnpIds.add(snp.getSnpID());
        				wrapper.add(refSnpIds);
    				}
		    		associations.add(wrapper);
				} else if(queryForm.getRefseq()){
					logger.debug("refseq");
					List<List<String>> wrapper = new ArrayList<List<String>>();
					List<String> refSeqIds;
	        		if(ids != null){
	        			for (MarkerID mId : ids) {
	    					if (mId.getLogicalDB().equals(DBConstants.PROVIDER_REFSEQ) ||
	    							mId.getLogicalDB().equals("Sequence DB")){
	    						refSeqIds = new ArrayList<String>();
	    						refSeqIds.add(mId.getAccID());
	    						wrapper.add(refSeqIds);
	    					}						
	    				}
	        		}
		    		associations.add(wrapper);
				} else if(queryForm.getUniprot()){
					logger.debug("uniprot");
					List<List<String>> wrapper = new ArrayList<List<String>>();
					List<String> uniProtIds;
	        		if(ids != null){
	        			for (MarkerID mId : ids) {
	    					if (mId.getLogicalDB().equals(DBConstants.PROVIDER_SWISSPROT) ||
	    							mId.getLogicalDB().equals(DBConstants.PROVIDER_TREMBL)){
	    						uniProtIds = new ArrayList<String>();	    						
	    						uniProtIds.add(mId.getAccID());
	    						wrapper.add(uniProtIds);
	    					}						
	    				}
	        		}	    		
		    		associations.add(wrapper);
				}
				
				logger.debug("assoc");
				for (List<List<String>> assoc: associations) {
					if (assoc.size() == 0) {
						List<String> empty = new ArrayList<String>();
						empty.add("");
						assoc.add(empty);
					}
				}
				logger.debug("combine");
				List<List<String>> combineResults = new ArrayList<List<String>>();
				if (associations.size() > 0) {
					List<String> l;
					for (List<String> list: associations.get(0)) {
						l = new ArrayList<String>();
						for (String s: list) {
							l.add(s);
						}
						combineResults.add(l);
					}				
					if (associations.size() > 1) {				
						for (int i = 1; i < associations.size(); i++) {
							combineResults = combineSets(combineResults, associations.get(i));
						}				
					}
				}
				logger.debug("combineResults: " + combineResults.size());
				
				int curRow = rownum - 1;
				if (combineResults.size() > 0){
					boolean newRow = false;
					int storeCol = col;
					
					for (List<String> ls: combineResults) {
						logger.debug("combineCols: " + ls.size());
						if (newRow) {
							addlRow = sheet.createRow(rownum++);
							for (int i = 0; i < col; i++) {
								logger.debug("copy cell: " + i);
								if (row.getCell(i).getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
									addlRow.createCell(i).setCellValue(row.getCell(i).getNumericCellValue());
								} else if (row.getCell(i).getCellType() == HSSFCell.CELL_TYPE_STRING){
									addlRow.createCell(i).setCellValue(row.getCell(i).getStringCellValue());
								}
								
								
							}
							row = addlRow;
						}
						for (String s: ls) {
							row.createCell(col++).setCellValue(s);
						}
						
						newRow = true;
						col = storeCol;
					} 
					workbook.setRepeatingRowsAndColumns(0,0,0,curRow,curRow + combineResults.size());
				}
			}
		}
		for (int i = 0; i < 20; i++) {
			sheet.autoSizeColumn(i);
			
		}
	}
	
	private  HSSFRow genHeader(HSSFRow headerRow, BatchQueryForm queryForm){
		int i = 0;
		headerRow.createCell(i++).setCellValue("Input");
		headerRow.createCell(i++).setCellValue("Input Type");
		headerRow.createCell(i++).setCellValue("MGI Gene/Marker ID");
		 
		if(queryForm.getNomenclature()){
			headerRow.createCell(i++).setCellValue("Symbol");
			headerRow.createCell(i++).setCellValue("Name");
			headerRow.createCell(i++).setCellValue("Feature Type");
		}
		if(queryForm.getLocation()){
			headerRow.createCell(i++).setCellValue("Chr");
			headerRow.createCell(i++).setCellValue("Strand");
			headerRow.createCell(i++).setCellValue("Start");
			headerRow.createCell(i++).setCellValue("End");
		}
		if(queryForm.getEnsembl()){
			headerRow.createCell(i++).setCellValue("Ensembl IDs");
		}
		if(queryForm.getEntrez()){
			headerRow.createCell(i++).setCellValue("Entrez Gene IDs");
		}
		if(queryForm.getVega()){
			headerRow.createCell(i++).setCellValue("Vega IDs");
		}
		
		if(queryForm.getGo()){
			headerRow.createCell(i++).setCellValue("GO ID");
			headerRow.createCell(i++).setCellValue("Term");
			headerRow.createCell(i++).setCellValue("Code");
		} else if(queryForm.getMp()){
			headerRow.createCell(i++).setCellValue("MP ID");
			headerRow.createCell(i++).setCellValue("Term");
		} else if(queryForm.getOmim()){
			headerRow.createCell(i++).setCellValue("OMIM ID");
			headerRow.createCell(i++).setCellValue("Term");
		} else if(queryForm.getAllele()){
			headerRow.createCell(i++).setCellValue("Allele ID");
			headerRow.createCell(i++).setCellValue("Symbol");
		} else if(queryForm.getExp()){
			headerRow.createCell(i++).setCellValue("Anatomical Structure");
			headerRow.createCell(i++).setCellValue("Assay Results");
			headerRow.createCell(i++).setCellValue("Detected");
			headerRow.createCell(i++).setCellValue("Not Detected");
		} else if(queryForm.getRefsnp()){
			headerRow.createCell(i++).setCellValue("RefSNP IDs");
		} else if(queryForm.getRefseq()){
			headerRow.createCell(i++).setCellValue("GenBank/RefSeq IDs");
		} else if(queryForm.getUniprot()){
			headerRow.createCell(i++).setCellValue("Uniprot IDs");
		}
		return headerRow;
	}
	
	private List<List<String>> combineSets (List<List<String>> results, List<List<String>> add){
		List<List<String>> newResults = new ArrayList<List<String>>();
		List<String> newList;
		for (List<String> a: results ){
			for (List<String> list: add) {
				newList = new ArrayList<String>();
				newList.addAll(a);
				logger.debug("pre: " + newList.size());
				for (String s: list) {
					logger.debug(s);
					newList.add(s);
				}
				logger.debug("post: " + newList.size());
				newResults.add(newList);
			}
		}
		return newResults;
	}
}