package unittest.fewi.util.file;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.apache.commons.lang.StringUtils;
import org.jax.mgi.fewi.util.file.FileProcessor;
import org.jax.mgi.fewi.util.file.FileProcessorOutput;
import org.junit.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

/**
 * Test the FileProcessor utility class
 * 	which facilitates in parsing VCF and standard files for query input
 */
public class FileProcessorTest 
{

	@Test
	public void testSingleColEmptyFile() throws IOException
	{
		FileProcessorOutput po = FileProcessor.processSingleCol(mockFile("test",""));

		Assert.assertEquals(0,po.getRowsProcessed());
		Assert.assertEquals(0,po.getValidRows());
		Assert.assertEquals(0,po.getRowsKicked());
		Assert.assertEquals(0,po.getRowsKickedWithNoData());
	}
	@Test
	public void testSingleColFileBlankRow() throws IOException
	{
		FileProcessorOutput po = FileProcessor.processSingleCol(mockFile("test","  "));

		Assert.assertEquals(1,po.getRowsProcessed());
		Assert.assertEquals(0,po.getValidRows());
		Assert.assertEquals(1,po.getRowsKicked());
		Assert.assertEquals(1,po.getRowsKickedWithNoData());
	}
	
	@Test
	public void testSingleColFileOneRow() throws IOException
	{
		FileProcessorOutput po = FileProcessor.processSingleCol(mockFile("test","Pax6"));

		Assert.assertEquals(1,po.getRowsProcessed());
		Assert.assertEquals(1,po.getValidRows());
		Assert.assertEquals(0,po.getRowsKicked());
		Assert.assertEquals(0,po.getRowsKickedWithNoData());
		Assert.assertEquals("Pax6,",po.getValueString());
	}

	@Test
	public void testSingleColFileManyRows() throws IOException
	{
		FileProcessorOutput po = FileProcessor.processSingleCol(mockFile("test",
				Arrays.asList("Pax6","","Pax5"," Kit ")
				));

		Assert.assertEquals(4,po.getRowsProcessed());
		Assert.assertEquals(3,po.getValidRows());
		Assert.assertEquals(1,po.getRowsKicked());
		Assert.assertEquals(1,po.getRowsKickedWithNoData());
		Assert.assertEquals("Pax6,Pax5,Kit,",po.getValueString());
	}
	
	/*
	 * private helper functions
	 */
	private MultipartFile mockFile(String name,List<String> rows)
	{
		return mockFile(name,StringUtils.join(rows,"\n"));
	}
	private MultipartFile mockFile(String name,String content)
	{
		return new MockMultipartFile(name,content.getBytes(Charset.forName("UTF-8")));
	}
}
