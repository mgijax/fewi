package org.jax.mgi.fewi.test.data;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.spreadsheet.CustomElementCollection;
import com.google.gdata.data.spreadsheet.ListEntry;
import com.google.gdata.data.spreadsheet.ListFeed;
import com.google.gdata.data.spreadsheet.WorksheetFeed;
import com.google.gdata.data.spreadsheet.WorksheetEntry;
import com.google.gdata.util.ServiceException;

/**
 * This class pull dynamic test data from our Google spreadsheet
 * It stores the Id/Name and return data (i.e. key:value) from each row.
 * 
 * @author kstone
 *
 */
public class DynamicTestData 
{
	
	public static Map<String,String> testDataMap = new HashMap<String,String>();
	static
	{
		// Initialise the test data map by reading the Google service.
		System.out.println("Initialising the dynamic test data by reading from Google service.");
		String user = "gxdscrum.mgi@gmail.com";
		String pass = "GXDD0CSmgi";
		
		SpreadsheetService service = new SpreadsheetService("huh?");
		try
		{
			service.setUserCredentials(user, pass);
		}
		catch (Exception e)
		{
			System.out.println("Error authenticating Google service. Check credentials.");
			e.printStackTrace();
		}
		service.setProtocolVersion(SpreadsheetService.Versions.V3);
		
		String ss_key = "0ArepxibBtJW2dEl0eEg1bElOc1Mxc1FnNGtmSmlUSlE";
		
		String urlString = "https://spreadsheets.google.com/feeds/worksheets/"+ss_key+"/public/values";
		
		WorksheetFeed feed = new WorksheetFeed();
		try {
			URL url = new URL(urlString);
			feed = service.getFeed(url, WorksheetFeed.class);
		} catch (IOException e) {
			System.out.println("Bad url '"+urlString+"' for fetching google service in DynamicTestData class");
			e.printStackTrace();
		} catch (ServiceException e) {
			System.out.println("Error fetching google service in DynamicTestData class");
			e.printStackTrace();
		}
		
		for (WorksheetEntry we : feed.getEntries())
		{
			System.out.println("Reading list feed URL: "+we.getListFeedUrl().toString());
			// get the individual list feeds for each work sheet
			ListFeed listFeed = new ListFeed();
			try {
				URL url = we.getListFeedUrl();
				listFeed = service.getFeed(url, ListFeed.class);
			} catch (IOException e) {
				System.out.println("Bad url '"+we.getListFeedUrl().toString()+"' for fetching google service in DynamicTestData class");
				e.printStackTrace();
			} catch (ServiceException e) {
				System.out.println("Error fetching google service in DynamicTestData class");
				e.printStackTrace();
			}
			
			for (ListEntry le : listFeed.getEntries())
			{
				CustomElementCollection elements = le.getCustomElements();
				String id = elements.getValue("id");
				String data = elements.getValue("returndata");
				if( id != null && data != null)
				{
					System.out.println(id+" has value "+data);

					testDataMap.put(id, data);
				}
			}
		}
		System.out.println("Completed initialising the dynamic test data.");

	}
	
	public static String get(String id)
	{
		if(!testDataMap.containsKey(id))
		{
			System.out.println("Cannot find test value for id '"+id+"'. Id either does not exist or has an error.");
			return "";
		}
		String returnValue = testDataMap.get(id);
		if (returnValue == null || returnValue.trim().equals(""))
		{
			System.out.println("Test data for id '"+id+"' is empty. Query did not return a value.");
			return "";
		}
		
		return returnValue;
	}
	
}
