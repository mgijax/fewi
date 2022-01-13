package org.jax.mgi.fewi.view;

import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.jax.mgi.fewi.finder.GxdBatchFinder;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.entities.SolrAssayResult;
import org.jax.mgi.fewi.util.FormatHelper;
import org.jax.mgi.fewi.util.NotesTagConverter;

public class TextGxdResultsSummary extends AbstractTextView
{
	// returns an empty string if 's' is null, or 's' otherwise
	private String nullProtected(String s) {
		if (s == null) return "";
		return s;
	}

	@Override
	protected void buildTextDocument(Map<String, Object> model, BufferedWriter writer,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		String filename = "MGIgeneExpressionQuery_"+getCurrentDate();
		response.setHeader("Content-Disposition","attachment; filename=\""+filename+".txt\"");
		System.out.println(response.getCharacterEncoding());

		GxdBatchFinder finder = (GxdBatchFinder) model.get("resultFinder");

		// set the batchSize
		int batchSize = 50000;
		finder.batchSize = batchSize;

		// write the headers
		String[] headerTitles = {"MGI Gene ID",
				"Gene Symbol",
				"Gene Name",
				"MGI Assay ID",
				"Assay Type",
				"Age",
				"Theiler Stage",
				"Structure",
				"Cell Type",
				"Detected",
				"TPM Level (RNA-Seq)",
				"Biological Replicates (RNA-Seq)",
				"Images",
				"Mutant Allele(s)",
				"Strain",
				"Sex",
				"Notes (RNA-Seq)",
				"TPM (avg_quantile normalization)",
				"MGI Reference ID",
				"PubMed ID",
				"Citation"};
		for(int i=0;i<headerTitles.length;i++)
		{
			writer.write(headerTitles[i]+"\t");
		}
		writer.write("\r\n");

		// need this for processing genotypes
		NotesTagConverter ntc = new NotesTagConverter();
		while(finder.hasNextResults())
		{
			SearchResults<SolrAssayResult> results = finder.getNextResults();
			for (SolrAssayResult r : results.getResultObjects())
			{
				// for now we will steal logic from the summary row class
				writer.write(r.getMarkerMgiid()+"\t");
				writer.write(r.getMarkerSymbol() + "\t");
				writer.write(r.getMarkerName()+"\t");
				writer.write(r.getAssayMgiid()+"\t");
				writer.write(r.getAssayType() + "\t");
				writer.write(r.getAge() + "\t");
				writer.write(r.getTheilerStage() + "\t");
				writer.write(r.getPrintname() + "\t");
				
				String cellType = "";
				if (r.getCellType() != null) {
					cellType = r.getCellType();
				}
				writer.write(cellType + "\t");
				writer.write(format(r.getDetectionLevel()) + "\t");

				writer.write(nullProtected(r.getTpmLevel()) + "\t");
				writer.write(nullProtected(r.getBiologicalReplicates()) + "\t");

				// generate the figure text
				String figureText = "";
				List<String> formattedFigures = new ArrayList<String>(0);
				if(r.getFiguresPlain() != null)
				{
					for(String figure : r.getFiguresPlain())
					{
						if(!figure.equals(""))
						{
							formattedFigures.add(figure);
						}
					}
				}
				if(formattedFigures.size()>0)
				{
					figureText = StringUtils.join(formattedFigures,", ");
				}
				writer.write(figureText+"\t");
				//genotype
				String genotypeText = "";
				if(r.getGenotype() !=null && !r.getGenotype().equals(""))
				{
					genotypeText = FormatHelper.newline2Comma(ntc.convertNotes(r.getGenotype(), '|',true,true));
				}
				writer.write(genotypeText+"\t");

				writer.write(nullProtected(r.getStrain()) + "\t");
				writer.write(nullProtected(r.getSex()) + "\t");
				writer.write(nullProtected(r.getNotes()) + "\t");
				writer.write(nullProtected(r.getAvgQnTpmLevel()) + "\t");

				writer.write(r.getJNum() + "\t");
				writer.write(format(r.getPubmedId()) + "\t"); //pub med id
				writer.write(r.getShortCitation());

				writer.write("\r\n");
			}
		}
	}
	private String format(String str)
	{
		if(str == null) return "";
		return str;
	}

}
