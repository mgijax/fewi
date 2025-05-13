package org.jax.mgi.fewi.view;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.formula.FormulaParseException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.jax.mgi.fe.datamodel.Annotation;
import org.jax.mgi.fe.datamodel.BatchMarkerAllele;
import org.jax.mgi.fe.datamodel.BatchMarkerGoAnnotation;
import org.jax.mgi.fe.datamodel.BatchMarkerId;
import org.jax.mgi.fe.datamodel.BatchMarkerMpAnnotation;
import org.jax.mgi.fe.datamodel.MarkerTissueCount;
import org.jax.mgi.fewi.forms.BatchQueryForm;
import org.jax.mgi.fewi.util.BatchMarkerIdWrapper;
import org.jax.mgi.fewi.util.DBConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BigExcelBatchSummary extends AbstractBigExcelView {
	
	// logger for the class
	private final Logger logger = LoggerFactory.getLogger(BigExcelBatchSummary.class);
	
	private String notNull(String o) {
		if (o == null) { return ""; }
		return o;
	}
	
	private String notNull(Double o) {
		if (o == null) { return ""; }
		return String.format("%.0f", o);
	}
	

	@SuppressWarnings({ "unchecked", "deprecation" })
	@Override
	protected void buildExcelDocument(Map<String,Object> model, SXSSFWorkbook workbook,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		logger.debug("buildExcelDocument");
		BatchQueryForm queryForm = (BatchQueryForm)model.get("queryForm");
		startTimeoutPeriod();
		
		// setup
		String filename = "MGIBatchReport_"+getCurrentDate();
		response.setHeader("Content-Disposition","attachment; filename=\""+filename+".xlsx\"");
				
		// grab the session so we can control the hibernate cache by evicting objects that we are done using.
		SessionFactory sessionFactory = (SessionFactory)model.get("sessionFactory");
		Session session = sessionFactory.getCurrentSession();
			
		Sheet sheet = workbook.createSheet();
		genHeader(sheet.createRow(0), queryForm);
		
		Row row, addlRow;
		int rownum = 1;
		int col;
		

		for (BatchMarkerId id : (List<BatchMarkerId>) model.get("results")) 
		{
			BatchMarkerIdWrapper bmi = new BatchMarkerIdWrapper(id);
			checkProgress(rownum);
			List<List<List<String>>> associations = new ArrayList<List<List<String>>>();

			// is this match to a marker?
			boolean hasMarker = (id.getMarker() != null);

			row = sheet.createRow(rownum++);
			col = 0;

			// first three fields are mandatory -- search term, type of term matched, ID of matching object
			row.createCell(col++).setCellValue(bmi.getTerm());
			row.createCell(col++).setCellValue(bmi.getTermType());
			if (bmi.getMarkerID().length() != 0) {
				row.createCell(col++).setCellValue(bmi.getMarkerID());
			} else {
				row.createCell(col++).setCellValue("No associated gene");
			}

			// user requested nomenclature fields
			if (queryForm.getNomenclature()) {
				row.createCell(col++).setCellValue(bmi.getSymbol());
				row.createCell(col++).setCellValue(notNull(bmi.getName()));
				row.createCell(col++).setCellValue(bmi.getFeatureType());
			}
			
			// user requested location fields
			if (queryForm.getLocation()) {
				if ((bmi.getChromosome() != null) && (bmi.getChromosome().length() > 0)) {
					row.createCell(col++).setCellValue(bmi.getChromosome());
				} else {
					row.createCell(col++).setCellValue("");
				}
				
				row.createCell(col++).setCellValue(notNull(bmi.getStrand()));
				row.createCell(col++).setCellValue(notNull(bmi.getStart()));
				row.createCell(col++).setCellValue(notNull(bmi.getEnd()));
				if (hasMarker && id.getMarker().getLocations() != null) {
					evictCollection(session, id.getMarker().getLocations());
				}
			}
			
			// Now we begin building the lists of associations (1-n relationships for the match).  These
			// will be combined into a cross-product later on.  After the first two, only one option can
			// be chosen from the other association types.
			
			// user requested Ensembl IDs
			if(queryForm.getEnsembl()){
				List<List<String>> wrapper = new ArrayList<List<String>>();
				List<String> eIds;
				for (String mId : bmi.getIDs(DBConstants.PROVIDER_ENSEMBL)) {
					eIds = new ArrayList<String>();
					eIds.add(mId);
					wrapper.add(eIds);
				}
        		associations.add(wrapper);
			}

			// user requested Entrez Gene IDs
			if(queryForm.getEntrez()){
				List<List<String>> wrapper = new ArrayList<List<String>>();
				List<String> eIds;
				for (String mId : bmi.getIDs(DBConstants.PROVIDER_ENTREZGENE)) {
					eIds = new ArrayList<String>();
					eIds.add(mId);
					wrapper.add(eIds);
	    		}
	    		associations.add(wrapper);
			}
			
			// user requested GO annotations
			if(queryForm.getGo()){
				List<List<String>> wrapper = new ArrayList<List<String>>();
				List<String> goIds;
    			for (BatchMarkerGoAnnotation goAnnot : bmi.getGOAnnotations()) {
    				goIds = new ArrayList<String>();
    				goIds.add(goAnnot.getGoId());
    				goIds.add(goAnnot.getGoTerm());
    				goIds.add(goAnnot.getEvidenceCode());
    				wrapper.add(goIds);
				}		    		
	    		associations.add(wrapper);
	    		if (hasMarker && (wrapper.size() > 0)) {
	    			evictCollection(session, id.getMarker().getBatchMarkerGoAnnotations());
	    		}
			}
			
			// user requested MP annotations
			else if(queryForm.getMp()){
				List<List<String>> wrapper = new ArrayList<List<String>>();
				List<String> mpIds;
	    		for (BatchMarkerMpAnnotation mpAnnot : bmi.getMPAnnotations()) {
	    			mpIds = new ArrayList<String>();
	    			mpIds.add(mpAnnot.getMpId());
	    			mpIds.add(mpAnnot.getMpTerm());
					wrapper.add(mpIds);
				}		    		
		    	associations.add(wrapper);
		    	if (hasMarker && (wrapper.size() > 0)) {
		    		evictCollection(session, id.getMarker().getBatchMarkerMpAnnotations());
		    	}
			}
			
			// user requested DO annotations
			else if(queryForm.getDo()){
				List<List<String>> wrapper = new ArrayList<List<String>>();
				List<String> doIds;
	    		for (Annotation doAnnot : bmi.getDOAnnotations()) {
	    			doIds = new ArrayList<String>();
	    			doIds.add(doAnnot.getTermID());
	    			doIds.add(doAnnot.getTerm());
	    			wrapper.add(doIds);
				}		
		    	associations.add(wrapper);
		    	if (hasMarker && (wrapper.size() > 0)) {
		    		evictCollection(session, id.getMarker().getAnnotations());
		    	}
			}
			
			// user requested alleles
			else if(queryForm.getAllele()){
				List<List<String>> wrapper = new ArrayList<List<String>>();
				List<String> alleles;
	    		for (BatchMarkerAllele bma : bmi.getAlleles()) {
	    			alleles = new ArrayList<String>();
	    			alleles.add(bma.getAlleleID());
	    			alleles.add(bma.getAlleleSymbol());
	    			wrapper.add(alleles);
	    			session.evict(bma);
				}
		    	associations.add(wrapper);
			}
			
			// user requested expression counts by tissue
			else if(queryForm.getExp()){
				List<List<String>> wrapper = new ArrayList<List<String>>();
				List<String> expression;
	    		for (MarkerTissueCount tissue : bmi.getExpressionCountsByTissue()) {
	    			expression = new ArrayList<String>();
	    			expression.add(tissue.getStructure());
	    			expression.add(String.valueOf(tissue.getAllResultCount()));
	    			expression.add(String.valueOf(tissue.getDetectedResultCount()));
	    			expression.add(String.valueOf(tissue.getNotDetectedResultCount()));
	    			wrapper.add(expression);
	    			session.evict(tissue);
				}
		    	associations.add(wrapper);
			}
			
			// user requested RefSNP IDs
			else if(queryForm.getRefsnp()){
				List<List<String>> wrapper = new ArrayList<List<String>>();
				List<String> refSnpIds;
        		for (String snp : bmi.getRefSNPs()) {
        			refSnpIds = new ArrayList<String>();
        			refSnpIds.add(snp);
        			wrapper.add(refSnpIds);
    			}
		    	associations.add(wrapper);
			}
			
			// user requested RefSeq IDs
			else if(queryForm.getRefseq()){
				List<List<String>> wrapper = new ArrayList<List<String>>();
				List<String> refSeqIds;
				for (String mId : bmi.getIDs(DBConstants.PROVIDER_REFSEQ, "Sequence DB")) {
					refSeqIds = new ArrayList<String>();
					refSeqIds.add(mId);
					wrapper.add(refSeqIds);
	        	}
		    	associations.add(wrapper);
			}

			// user requested UniProt IDs
			else if(queryForm.getUniprot()){
				List<List<String>> wrapper = new ArrayList<List<String>>();
				List<String> uniProtIds;
				for (String mId : bmi.getIDs(DBConstants.PROVIDER_SWISSPROT, DBConstants.PROVIDER_TREMBL)) {
					uniProtIds = new ArrayList<String>();
					uniProtIds.add(mId);
					wrapper.add(uniProtIds);
	        	}
		    	associations.add(wrapper);
			}
			
			// if we have any association sets that are empty lists, add an empty value
			for (List<List<String>> assoc: associations) {
				if (assoc.size() == 0) {
					List<String> empty = new ArrayList<String>();
					empty.add("");
					assoc.add(empty);
				}
			}
			
			// compute the cross-product of the various lists of associations, so lists:
			// 		[ A, B, C ] and [ D, E, F ]
			// will produce rows:
			//		[ A, D ], [ A, E ], [ A, F ], [ B, D ], [ B, E ], [ B, F ], [ C, D ], [ C, E ], [ C, F ]

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
				
			int curRow = rownum - 1;
			if (combineResults.size() > 0){
				boolean newRow = false;
				int storeCol = col;
				
				for (List<String> ls: combineResults) {
					if (newRow) {
						addlRow = sheet.createRow(rownum++);
						for (int i = 0; i < col; i++) {
							if (row.getCell(i) != null){
								if (row.getCell(i).getCellType() == Cell.CELL_TYPE_NUMERIC) {
									addlRow.createCell(i).setCellValue(row.getCell(i).getNumericCellValue());
								} else if (row.getCell(i).getCellType() == Cell.CELL_TYPE_STRING){
									addlRow.createCell(i).setCellValue(row.getCell(i).getStringCellValue());
								}
							} else {
								addlRow.createCell(i);
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
					
				try {
					workbook.setRepeatingRowsAndColumns(0,0,col,curRow,curRow + combineResults.size());
				} catch (FormulaParseException e) {
					//logger.debug( "rowspan: " + curRow + " " + (curRow + combineResults.size()));
					//logger.debug(e.getMessage());
				}
					
				combineResults = null;
			}

			if (hasMarker) {
				session.evict(id.getMarker());
				id.setMarker(null);
			}
			session.evict(id);
		}
		for (int i = 0; i < 20; i++) 
		{
			sheet.autoSizeColumn(i);	
		}
		
		logger.info("finished processing batch excel report");
	}
	
	private void evictCollection(Session session, Collection<?> collection)
	{
		for(Object o : collection)
		{
			session.evict(o);
		}
	}
	
	private  Row genHeader(Row row, BatchQueryForm queryForm){
		int i = 0;
		row.createCell(i++).setCellValue("Input");
		row.createCell(i++).setCellValue("Input Type");
		row.createCell(i++).setCellValue("MGI Gene/Marker ID");
		 
		if(queryForm.getNomenclature()){
			row.createCell(i++).setCellValue("Symbol");
			row.createCell(i++).setCellValue("Name");
			row.createCell(i++).setCellValue("Feature Type");
		}
		if(queryForm.getLocation()){
			row.createCell(i++).setCellValue("Chr");
			row.createCell(i++).setCellValue("Strand");
			row.createCell(i++).setCellValue("Start");
			row.createCell(i++).setCellValue("End");
		}
		if(queryForm.getEnsembl()){
			row.createCell(i++).setCellValue("Ensembl ID");
		}
		if(queryForm.getEntrez()){
			row.createCell(i++).setCellValue("Entrez Gene ID");
		}
		
		if(queryForm.getGo()){
			row.createCell(i++).setCellValue("GO ID");
			row.createCell(i++).setCellValue("Term");
			row.createCell(i++).setCellValue("Code");
		} else if(queryForm.getMp()){
			row.createCell(i++).setCellValue("MP ID");
			row.createCell(i++).setCellValue("Term");
		} else if(queryForm.getDo()){
			row.createCell(i++).setCellValue("DO ID");
			row.createCell(i++).setCellValue("Term");
		} else if(queryForm.getAllele()){
			row.createCell(i++).setCellValue("Allele ID");
			row.createCell(i++).setCellValue("Symbol");
		} else if(queryForm.getExp()){
			row.createCell(i++).setCellValue("Anatomical Structure");
			row.createCell(i++).setCellValue("Assay Results");
			row.createCell(i++).setCellValue("Detected");
			row.createCell(i++).setCellValue("Not Detected");
		} else if(queryForm.getRefsnp()){
			row.createCell(i++).setCellValue("RefSNP ID");
		} else if(queryForm.getRefseq()){
			row.createCell(i++).setCellValue("GenBank/RefSeq ID");
		} else if(queryForm.getUniprot()){
			row.createCell(i++).setCellValue("Uniprot ID");
		}
		return row;
	}
	
	private List<List<String>> combineSets (List<List<String>> results, List<List<String>> add){
		List<List<String>> newResults = new ArrayList<List<String>>();
		List<String> newList;
		for (List<String> a: results ){
			for (List<String> list: add) {
				newList = new ArrayList<String>();
				newList.addAll(a);
				for (String s: list) {
					//logger.debug(s);
					newList.add(s);
				}
				newResults.add(newList);
			}
		}
		return newResults;
	}
}
