package org.jax.mgi.fewi.test.mock;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
 * Some handy methods for parsing test input to build the mock queries
 */
public class MockGxdQueryParser {

	public static List<String> parseAgeString(String ageStr)
	{
		List<String> ageTokens = new ArrayList<String>();
		if(ageStr.contains(","))
		{
			// split by comma
			List<String> byComma = Arrays.asList(ageStr.split(","));
			return convertAge(byComma);
		}
		else if(ageStr.contains("-"))
		{
			// more complicated?
//			Float min;
//			Float max;
//			String[] byDash = ageStr.split("-");
		}
		ageTokens.add(ageStr);
		return convertAge(ageTokens);
	}
	
	public static String convertAge(String ageStr)
	{
		if(ageStr.equalsIgnoreCase("any")) return "ANY";
		if(!ageStr.equalsIgnoreCase("embryonic") && !ageStr.equalsIgnoreCase("postnatal") 
				&& ageStr.contains("E"))
		{
			ageStr = ageStr.substring(1);
		}
		return ageStr;
	}
	public static  List<String> convertAge(List<String> ages)
	{
		List<String> ageList = new ArrayList<String>();
		for(String ageToken : ages)
		{
			ageList.add(convertAge(ageToken));
		}
		return ageList;
	}
	
	public static List<Integer> parseTS(String tsStr)
	{
		List<Integer> tsTokens = new ArrayList<Integer>();
		if(tsStr.contains(","))
		{
			// split by comma
			List<String> byComma = Arrays.asList(tsStr.split(","));
			return convertTS(byComma);
		}
		else if(tsStr.contains("-"))
		{
			// more complicated?
			String[] byDash = tsStr.split("-");
			Integer min = convertTS(byDash[0]);
			Integer max = convertTS(byDash[1]);
			Integer value = min;
			while (value <= max )
			{
				tsTokens.add(value);
				value += 1;
			}
			return tsTokens;
			
		}
		tsTokens.add(convertTS(tsStr));
		return tsTokens;
	}
	
	public static Integer convertTS(String ts)
	{
		if(ts.contains("TS"))
		{
			ts = ts.substring(2);
		}
		return Integer.parseInt(ts);
	}
	public static List<Integer> convertTS(List<String> ts)
	{
		List<Integer> tsList = new ArrayList<Integer>();
		for(String tsToken : ts)
		{
			tsList.add(convertTS(tsToken));
		}
		return tsList;
	}
	 public static List<String> convertStringToAssayTypes(String assayTypeStr)
	    {
			List<String> assayTypes = new ArrayList<String>();
			try {
		    	if (assayTypeStr.equals("All")) {
		    		assayTypes.add("Immunohistochemistry");
		    		assayTypes.add("In situ reporter (knock in)");
		    		assayTypes.add("Northern blot");
		    		assayTypes.add("Nuclease S1");
		    		assayTypes.add("RNA in situ");
		    		assayTypes.add("RNase protection");
		    		assayTypes.add("RT-PCR");
		    		assayTypes.add("Western blot");
		    	} else {
		    		for (String s : assayTypeStr.split(",")) {
		    			assayTypes.add(s);    			
		    		}
		    	}
			} catch (Exception e) {
				// Can't convert the passed in string to a list of 
				// assayTypes
	    		fail("Call to convertStringToAssayTypes with an invalid list of assayTypes: ("+assayTypeStr+")");
	    	}
			return assayTypes;
	    }
}
