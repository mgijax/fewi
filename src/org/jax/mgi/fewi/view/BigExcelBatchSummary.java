package org.jax.mgi.fewi.view;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mgi.frontend.datamodel.Annotation;
import mgi.frontend.datamodel.BatchMarkerAllele;
import mgi.frontend.datamodel.BatchMarkerId;
import mgi.frontend.datamodel.Marker;
import mgi.frontend.datamodel.MarkerID;
import mgi.frontend.datamodel.MarkerLocation;
import mgi.frontend.datamodel.MarkerTissueCount;

import org.apache.poi.ss.formula.FormulaParseException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.jax.mgi.fewi.forms.BatchQueryForm;
import org.jax.mgi.fewi.util.DBConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BigExcelBatchSummary extends AbstractBigExcelView {
	
	// logger for the class
	private final Logger logger = LoggerFactory.getLogger(BigExcelBatchSummary.class);
	
	@SuppressWarnings("unchecked")
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
			checkProgress(rownum);
			List<List<List<String>>> associations = new ArrayList<List<List<String>>>();
			List<MarkerID> ids=null;
			
			row = sheet.createRow(rownum++);
			col = 0;
			row.createCell(col++).setCellValue(id.getTerm());
			row.createCell(col++).setCellValue(id.getTermType());
			
			Marker m = id.getMarker();			
			
			if (m != null){				
				ids = m.getIds();
				row.createCell(col++).setCellValue(m.getPrimaryID());

				if(queryForm.getNomenclature()){
					row.createCell(col++).setCellValue(m.getSymbol());
					row.createCell(col++).setCellValue(m.getName());
					row.createCell(col++).setCellValue(m.getMarkerSubtype());
				}
				if(queryForm.getLocation()){
					MarkerLocation loc = m.getPreferredCoordinates();
					if (loc != null) {
						row.createCell(col++).setCellValue(loc.getChromosome());
						row.createCell(col++).setCellValue(loc.getStrand());
						row.createCell(col++).setCellValue(loc.getStartCoordinate());
						row.createCell(col++).setCellValue(loc.getEndCoordinate());
						evictCollection(session,m.getLocations());
					} else {
						row.createCell(col++).setCellValue("UN");
						col += 3;
					}
				}
				
				// build associations matrix
				if(queryForm.getEnsembl()){
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
		    		evictCollection(session,m.getAnnotations());
				} else if(queryForm.getMp()){
					List<List<String>> wrapper = new ArrayList<List<String>>();
					List<String> mpIds;
	    			for (Annotation mpAnnot : m.getMPAnnotations()) {
	    				mpIds = new ArrayList<String>();
	    				mpIds.add(mpAnnot.getTermID());
	    				mpIds.add(mpAnnot.getTerm());
						wrapper.add(mpIds);
					}		    		
		    		associations.add(wrapper);
		    		evictCollection(session,m.getAnnotations());
				} else if(queryForm.getOmim()){
					List<List<String>> wrapper = new ArrayList<List<String>>();
					List<String> mpIds;
	    			for (Annotation omimAnnot : m.getOMIMAnnotations()) {
	    				mpIds = new ArrayList<String>();
	    				mpIds.add(omimAnnot.getTermID());
	    				mpIds.add(omimAnnot.getTerm());
	    				wrapper.add(mpIds);
					}		
		    		associations.add(wrapper);
    				evictCollection(session,m.getAnnotations());
				} else if(queryForm.getAllele()){
					List<List<String>> wrapper = new ArrayList<List<String>>();
					List<String> alleles;
	    			for (BatchMarkerAllele bma : m.getBatchMarkerAlleles()) {
	    				alleles = new ArrayList<String>();
	    				alleles.add(bma.getAlleleID());
	    				alleles.add(bma.getAlleleSymbol());
	    				wrapper.add(alleles);
	    				session.evict(bma);
					}
		    		associations.add(wrapper);
				} else if(queryForm.getExp()){
					List<List<String>> wrapper = new ArrayList<List<String>>();
					List<String> expression;
	    			for (MarkerTissueCount tissue : m.getMarkerTissueCounts()) {
	    				expression = new ArrayList<String>();
	    				expression.add(tissue.getStructure());
	    				expression.add(String.valueOf(tissue.getAllResultCount()));
	    				expression.add(String.valueOf(tissue.getDetectedResultCount()));
	    				expression.add(String.valueOf(tissue.getNotDetectedResultCount()));
	    				wrapper.add(expression);
	    				session.evict(tissue);
					}
		    		associations.add(wrapper);
				} else if(queryForm.getRefsnp()){
					List<List<String>> wrapper = new ArrayList<List<String>>();
					List<String> refSnpIds;
        			for (String snp : m.getBatchMarkerSnps()) {
        				refSnpIds = new ArrayList<String>();
        				refSnpIds.add(snp);
        				wrapper.add(refSnpIds);
    				}
		    		associations.add(wrapper);
				} else if(queryForm.getRefseq()){
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
				
				for (List<List<String>> assoc: associations) {
					if (assoc.size() == 0) {
						List<String> empty = new ArrayList<String>();
						empty.add("");
						assoc.add(empty);
					}
				}
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
				if(ids!=null)
				{
					evictCollection(session,ids);
				}
			} else {
				row.createCell(col++).setCellValue("No associated gene");
			}
			if(m!=null) {
				session.evict(m);
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
		if(queryForm.getVega()){
			row.createCell(i++).setCellValue("VEGA ID");
		}
		
		if(queryForm.getGo()){
			row.createCell(i++).setCellValue("GO ID");
			row.createCell(i++).setCellValue("Term");
			row.createCell(i++).setCellValue("Code");
		} else if(queryForm.getMp()){
			row.createCell(i++).setCellValue("MP ID");
			row.createCell(i++).setCellValue("Term");
		} else if(queryForm.getOmim()){
			row.createCell(i++).setCellValue("OMIM ID");
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