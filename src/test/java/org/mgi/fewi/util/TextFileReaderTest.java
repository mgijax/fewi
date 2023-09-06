package org.mgi.fewi.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.jax.mgi.fewi.util.file.TextFileReader;
import org.junit.Assert;
import org.junit.Test;


public class TextFileReaderTest 
{
	/*
	 * test readFile() method
	 */
	@Test
	public void testReadFileEmpty() throws IOException
	{
		String contents = TextFileReader.readFile(mockFile("test",""));
		Assert.assertEquals("",contents);
	}
	@Test
	public void testReadFileSingleRow() throws IOException
	{
		String contents = TextFileReader.readFile(mockFile("test","First row"));
		Assert.assertEquals("First row",contents);
	}	
	@Test
	public void testReadFileBlankRow() throws IOException
	{
		String contents = TextFileReader.readFile(mockFile("test","    "));
		Assert.assertEquals("    ",contents);
	}	
	@Test
	public void testReadFileManyRows() throws IOException
	{
		String contents = TextFileReader.readFile(mockFile("test",
				Arrays.asList("Row1",
						"Row2",
						"Really long row 3")
				));
		Assert.assertEquals("Row1\nRow2\nReally long row 3",contents);
	}

	/*
	 * private helper functions
	 */
	private File mockFile(String name,List<String> rows) throws IOException
	{
		return mockFile(name,StringUtils.join(rows,"\n"));
	}
	private File mockFile(String name,String content) throws IOException
	{
		File f = File.createTempFile("name",".tmp");
		FileWriter fw = new FileWriter(f.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(content);
		bw.close();
		return f;
	}
}
