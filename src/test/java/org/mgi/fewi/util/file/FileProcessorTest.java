package org.mgi.fewi.util.file;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.jax.mgi.fewi.util.file.FileProcessor;
import org.jax.mgi.fewi.util.file.FileProcessorOutput;
import org.jax.mgi.fewi.util.file.VcfProcessorOutput;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

/**
 * Test the FileProcessor utility class
 * 	which facilitates in parsing VCF and standard files for query input
 */
public class FileProcessorTest 
{
	/*
	 * Tests for processVCFCoordinates()
	 */
	@Test
	public void testVCFEmptyFile() throws IOException
	{
		VcfProcessorOutput po = FileProcessor.processVCFCoordinates(mockFile("test",""));

		Assert.assertEquals(0,po.getRowsProcessed());
		Assert.assertEquals(0,po.getRowsWithCoordinates());
		Assert.assertEquals(0,po.getRowsKicked());
		Assert.assertEquals(0,po.getRowsKickedWithNoData());
		Assert.assertEquals(0,po.getRowsKickedWithNotPass());
		Assert.assertEquals(0,po.getRowsKickedWithId());
	}
	
	@Test
	public void testVCFBlankRow() throws IOException
	{
		VcfProcessorOutput po = FileProcessor.processVCFCoordinates(mockFile("test","  "));

		Assert.assertEquals(1,po.getRowsProcessed());
		Assert.assertEquals(0,po.getRowsWithCoordinates());
		Assert.assertEquals(1,po.getRowsKicked());
		Assert.assertEquals(1,po.getRowsKickedWithNoData());
		Assert.assertEquals(0,po.getRowsKickedWithNotPass());
		Assert.assertEquals(0,po.getRowsKickedWithId());
	}
	
	@Test
	public void testVCFManyRows() throws IOException
	{
		VcfProcessorOutput po = FileProcessor.processVCFCoordinates(mockFile("test",
				Arrays.asList("chr2\t3456\t.\tC\tT\t28601.2\tPASS",
						"chr1\t2345\t.\tC\tT\t28601.2\tPASS",
						"chr3\t4567\t.\tC\tT\t28601.2\tPASS",
						"chr3\t4567",
						"\t",
						"chr1\t2345\t.\tT\t232323")
						));

		Assert.assertEquals(6,po.getRowsProcessed());
		Assert.assertEquals(4,po.getRowsWithCoordinates());
		Assert.assertEquals(2,po.getRowsKicked());
		Assert.assertEquals(2,po.getRowsKickedWithNoData());
		Assert.assertEquals(0,po.getRowsKickedWithNotPass());
		Assert.assertEquals(0,po.getRowsKickedWithId());
	}
	
	@Test
	public void testVCFCommentRow() throws IOException
	{
		VcfProcessorOutput po = FileProcessor.processVCFCoordinates(mockFile("test",
				Arrays.asList("chr2\t3456\t.\tC\tT\t28601.2\tPASS",
						"#chr2\t3456\t.\tC\tT\t28601.2\tPASS")
						));

		Assert.assertEquals(2,po.getRowsProcessed());
		Assert.assertEquals(1,po.getRowsWithCoordinates());
		Assert.assertEquals(1,po.getRowsKicked());
		Assert.assertEquals(1,po.getRowsKickedWithNoData());
		Assert.assertEquals(0,po.getRowsKickedWithNotPass());
		Assert.assertEquals(0,po.getRowsKickedWithId());
	}
	
	@Test
	public void testVCFNoPassRow() throws IOException
	{
		VcfProcessorOutput po = FileProcessor.processVCFCoordinates(mockFile("test",
				Arrays.asList("chr2\t3456\t.\tC\tT\t28601.2\tPASS\t",
						"chr2\t3456\t.\tC\tT\t28601.2\tfalse\t",
						"chr2\t3456\t.\tC\tT\t28601.2\tfalse\t",
						"chr2\t3456\t.\tC\tT\t28601.2\t\t",
						"chr2\t3456\t.\tC\tT\t28601.2\t.\t",
						"chr2\t3456\t.\tC\tT\t28601.2\tpass\t")
						));

		Assert.assertEquals(6,po.getRowsProcessed());
		Assert.assertEquals(4,po.getRowsWithCoordinates());
		Assert.assertEquals(2,po.getRowsKicked());
		Assert.assertEquals(0,po.getRowsKickedWithNoData());
		Assert.assertEquals(2,po.getRowsKickedWithNotPass());
		Assert.assertEquals(0,po.getRowsKickedWithId());
	}
	
	@Test
	public void testVCFRowWithId() throws IOException
	{
		VcfProcessorOutput po = FileProcessor.processVCFCoordinates(mockFile("test",
				Arrays.asList("chr2\t3456\t.\tC\tT\t28601.2\tPASS\t",
						"chr2\t3456\t\tC\tT\t28601.2\tPASS\t",
						"chr2\t3456\tID:12345\tC\tT\t28601.2\tPASS\t",
						"chr2\t3456\t1345\tC\tT\t28601.2\tPASS\t")
						));

		Assert.assertEquals(4,po.getRowsProcessed());
		Assert.assertEquals(2,po.getRowsWithCoordinates());
		Assert.assertEquals(2,po.getRowsKicked());
		Assert.assertEquals(0,po.getRowsKickedWithNoData());
		Assert.assertEquals(0,po.getRowsKickedWithNotPass());
		Assert.assertEquals(2,po.getRowsKickedWithId());
	}
	
	@Test
	public void testVCFDisableIDFilter() throws IOException
	{
		VcfProcessorOutput po = FileProcessor.processVCFCoordinates(mockFile("test",
				Arrays.asList(
						"chr2\t3456\tID:12345\tC\tT\t28601.2\tPASS\t",
						"chr2\t3456\t1345\tC\tT\t28601.2\tPASS\t")
						),false,true);

		Assert.assertEquals(2,po.getRowsProcessed());
		Assert.assertEquals(2,po.getRowsWithCoordinates());
		Assert.assertEquals(0,po.getRowsKicked());
		Assert.assertEquals(0,po.getRowsKickedWithNoData());
		Assert.assertEquals(0,po.getRowsKickedWithNotPass());
		Assert.assertEquals(0,po.getRowsKickedWithId());
	}
	
	@Test
	public void testVCFDisableBadFilters() throws IOException
	{
		VcfProcessorOutput po = FileProcessor.processVCFCoordinates(mockFile("test",
				Arrays.asList("chr2\t3456\t.\tC\tT\t28601.2\tPASS\t",
						"chr2\t3456\t\tC\tT\t28601.2\tfail\t")
						),true,false);

		Assert.assertEquals(2,po.getRowsProcessed());
		Assert.assertEquals(2,po.getRowsWithCoordinates());
		Assert.assertEquals(0,po.getRowsKicked());
		Assert.assertEquals(0,po.getRowsKickedWithNoData());
		Assert.assertEquals(0,po.getRowsKickedWithNotPass());
		Assert.assertEquals(0,po.getRowsKickedWithId());
	}
	@Test
	public void testVCFDisableAllFilters() throws IOException
	{
		VcfProcessorOutput po = FileProcessor.processVCFCoordinates(mockFile("test",
				Arrays.asList("chr2\t3456\tID:12340\tC\tT\t28601.2\tPASS\t",
						"chr2\t3456\t\tC\tT\t28601.2\tfail\t")
						),false,false);

		Assert.assertEquals(2,po.getRowsProcessed());
		Assert.assertEquals(2,po.getRowsWithCoordinates());
		Assert.assertEquals(0,po.getRowsKicked());
		Assert.assertEquals(0,po.getRowsKickedWithNoData());
		Assert.assertEquals(0,po.getRowsKickedWithNotPass());
		Assert.assertEquals(0,po.getRowsKickedWithId());
	}
	
	/*
	 * Tests for processSingleCol()
	 */
	@Test
	public void testSingleColEmptyFile() throws IOException {
		FileProcessorOutput po = FileProcessor.processSingleCol(mockFile("test",""));
		Assert.assertEquals(0,po.getRowsProcessed());
		Assert.assertEquals(0,po.getValidRows());
		Assert.assertEquals(0,po.getRowsKicked());
		Assert.assertEquals(0,po.getRowsKickedWithNoData());
	}
	@Test
	public void testSingleColFileBlankRow() throws IOException {
		FileProcessorOutput po = FileProcessor.processSingleCol(mockFile("test","  "));
		Assert.assertEquals(1,po.getRowsProcessed());
		Assert.assertEquals(0,po.getValidRows());
		Assert.assertEquals(1,po.getRowsKicked());
		Assert.assertEquals(1,po.getRowsKickedWithNoData());
	}
	
	@Test
	public void testSingleColFileOneRow() throws IOException {
		FileProcessorOutput po = FileProcessor.processSingleCol(mockFile("test","Pax6"));
		Assert.assertEquals(1,po.getRowsProcessed());
		Assert.assertEquals(1,po.getValidRows());
		Assert.assertEquals(0,po.getRowsKicked());
		Assert.assertEquals(0,po.getRowsKickedWithNoData());
		Assert.assertEquals("Pax6,",po.getValueString());
	}

	@Test
	public void testSingleColFileManyRows() throws IOException {
		FileProcessorOutput po = FileProcessor.processSingleCol(mockFile("test", Arrays.asList("Pax6","","Pax5"," Kit ")));

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
	private MultipartFile mockFile(String name, String content)
	{
		return new MockMultipartFile(name, content.getBytes(Charset.forName("UTF-8")));
	}
}
