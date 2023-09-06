package org.jax.mgi.fewi.util.file;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;


/*
 * Utility class for processing Files as user input
 * 
 * @author kstone
 */
public class FileProcessor 
{
	 private static Logger logger = LoggerFactory.getLogger(FileProcessor.class);
	 
	 
	 private static char VCF_COMMENT_CHAR = '#';
	 private static char VCF_COL_DELIM = '\t';
	 private static int VCF_COORDINATE_COL = 1;
	 private static int VCF_ID_COL = 2;
	 private static int VCF_FILTER_COL = 6;
	 private static int VCF_ROW_LIMIT = 200000;
	 
	 private static int SINGLE_COL_ROW_LIMIT = 1000000;
	 
	 /*
	  * Reads throught a VCF file to find all the coordinates.
	  * 	Appends them all to the result string as comma separated list.
	  */
	 public static VcfProcessorOutput processVCFCoordinates(MultipartFile vcf) throws IOException
	 {
		 return processVCFCoordinates(vcf,true,true);
	 }
	 public static VcfProcessorOutput processVCFCoordinates(MultipartFile vcf,boolean kickIds,boolean kickBadFilters) throws IOException
	 {
		 InputStream inputStream = vcf.getInputStream();
		 BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
		 
		 VcfProcessorOutput vpo = new VcfProcessorOutput();
		 
		 StringBuilder sb = new StringBuilder("");
		 String line;
		 int count=0;
		 while ((line = bufferedReader.readLine()) != null)
		 {
			 if(count++ > VCF_ROW_LIMIT) break;
			 //logger.debug("line="+line);
			 // ignore comment lines
			 vpo.addProcessedRow();
			 if(line.length()<1) { vpo.kickRowWithNoData(); continue; }
			 if(line.charAt(0) == VCF_COMMENT_CHAR) { vpo.kickRowWithNoData(); continue; }

			 int coordColStringIndex = getNthIndexOfCharacter(line,VCF_COL_DELIM,VCF_COORDINATE_COL);
			 if(coordColStringIndex<2){ vpo.kickRowWithNoData(); continue; }// this must be atleast 2 to have any values
			 
			 // check if ID column exists
			 int idColStringStart = getNthIndexOfCharacter(line,VCF_COL_DELIM,VCF_ID_COL-1);
			 if(idColStringStart>0 && kickIds)
			 {
				 int idColStringStop = line.indexOf(VCF_COL_DELIM,idColStringStart+1);
				 
				 if(idColStringStop>idColStringStart)
				 {
					 String id = line.substring(idColStringStart+1,idColStringStop);
					 //logger.debug("id="+id);
					// skip this row for having an id (we want to remove known variants)
					 if(!"".equals(id) && !".".equals(id)) { vpo.kickRowWithId(); continue; } 
				 }
			 }
			 
			 // check the filter column if it exists
			 int filterColStart = getNthIndexOfCharacter(line,VCF_COL_DELIM,VCF_FILTER_COL-1);
			 if(filterColStart>0 && kickBadFilters)
			 {
				 int filterColStop = line.indexOf(VCF_COL_DELIM,filterColStart+1);
				 
				 if(filterColStop>filterColStart)
				 {
					 String filter = line.substring(filterColStart+1,filterColStop);
					 //logger.debug("filter="+filter);
					 // skip this row for not having a non-passing filter
					 if(!"".equals(filter) && !".".equals(filter) && !"pass".equalsIgnoreCase(filter)) { vpo.kickRowWithNotPass(); continue; }
				 }
			 }
			 
			 // look at the first two columns for the chromosome and coordinate
			 String chromCoordSubStr = line.substring(0,coordColStringIndex);
			 int chromColStringIndex =  chromCoordSubStr.indexOf(VCF_COL_DELIM);
			 String chromosome = chromCoordSubStr.substring(0,chromColStringIndex);
			 String coordinate = chromCoordSubStr.substring(chromColStringIndex+1);
			 //logger.debug("processed line "+count+" chromosome="+chromosome+",coordinate="+coordinate);
			 
			 // we don't want to include any data with single/double quotes. It messes up Solr
			 if(hasQuotes(chromosome) || hasQuotes(coordinate)) { vpo.kickRowWithNoData(); continue; }
			 
			 if(!"".equals(chromosome)) sb.append(chromosome).append(":");
			 sb.append(coordinate).append(",");
			 vpo.addRowWithCoordinate();
		 }
		 bufferedReader.close();
		 inputStream.close();

		 logger.debug("vpo = "+vpo);
		 vpo.setCoordinates(sb.toString());
		 return vpo;
	 }
	 

	 /*
	  * Reads throught a single column file to find all the rows.
	  * 	Appends them all to the result string as comma separated list.
	  * 	Assumes: each row in the file is a single column
	  */
	 public static FileProcessorOutput processSingleCol(MultipartFile file) throws IOException
	 {
		 InputStream inputStream = file.getInputStream();
		 BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
		 
		 FileProcessorOutput po = new FileProcessorOutput();
		 
		 StringBuilder sb = new StringBuilder("");
		 String line;
		 int count=0;
		 while ((line = bufferedReader.readLine()) != null)
		 {
			 if(count++ > SINGLE_COL_ROW_LIMIT) break;
			 //logger.debug("line="+line);
			 // ignore comment lines
			 po.addProcessedRow();
			 line = line.trim();
			 if(line.length()<1) { po.kickRowWithNoData(); continue; }
			
			 sb.append(line).append(",");
			 po.addValidRow();
		 }
		 bufferedReader.close();
		 inputStream.close();

		 logger.debug("po = "+po);
		 po.setValueString(sb.toString());
		 return po;
	}
		
	 /*
	  * utility function to get nth occurance of a character in a sourceString 
	  */
	 public static int getNthIndexOfCharacter(String sourceString,char character, int n)
	 {
		 int pos = sourceString.indexOf(character, 0);
		 while (n-- > 0 && pos != -1)
			 pos = sourceString.indexOf(character, pos+1);
		 return pos;
	 }
	 
	 public static String printChars(String s)
	 {
		 char[] chars = s.toCharArray();
		 StringBuilder charVals = new StringBuilder();;
		 for(int i=0; i<chars.length; i++)
		 {
			int cInt = chars[i];
			charVals.append(cInt).append(",");
		 }
		 return charVals.toString();
	 }
	 
	 public static boolean hasQuotes(String str)  
	 {  
		 return str.indexOf('\'')>=0 || str.indexOf('\"')>=0;
	 }
}
