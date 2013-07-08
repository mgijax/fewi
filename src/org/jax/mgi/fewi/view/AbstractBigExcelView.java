package org.jax.mgi.fewi.view;

import java.io.IOException;
import java.util.Map;
import java.util.Locale;

import org.springframework.web.servlet.view.AbstractView;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import org.springframework.core.io.Resource;
import org.springframework.web.servlet.support.RequestContextUtils;

/**
 * We need to use a special POI implementation that can handle files with more than 65536 rows
 * 
 * @author kstone
 *
 */
public abstract class AbstractBigExcelView extends AbstractReportView
{
	private static final String EXTENSION = ".xls";

	private static final String SEPARATOR = "_";


	private String url;

	public AbstractBigExcelView() {
		setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
	}

	/**
	 * Sets the url of the Excel workbook source without localization part nor extension.
	 */
	public void setUrl(String url) {
		this.url = url;
	}
	
	/**
	 * Renders the view given the specified model.
	 */
	protected final void renderMergedOutputModel(
			Map model, HttpServletRequest request, HttpServletResponse response) throws Exception {

		SXSSFWorkbook workbook;
		if (this.url != null) {
			workbook = getTemplateSource(this.url, request);
		}
		else {
			workbook = new SXSSFWorkbook();
			logger.debug("Created Excel Workbook from scratch");
		}

		buildExcelDocument(model, workbook, request, response);

		// response.setContentLength(workbook.getBytes().length);
		response.setContentType(getContentType());
		ServletOutputStream out = response.getOutputStream();
		workbook.write(out);
		out.flush();
	}

	/**
	 * Creates the workbook from an existing XLS document.
	 * @param url URL of the Excel template without localization part nor extension
	 * @param request current HTTP request
	 * @return the HSSFWorkbook
	 */
	protected SXSSFWorkbook getTemplateSource(String url, HttpServletRequest request)
			throws ServletException, IOException {

		String source = null;
		Resource inputFile = null;

		Locale userLocale = RequestContextUtils.getLocale(request);
		String lang = userLocale.getLanguage();
		String country = userLocale.getCountry();

		// check for document with language and country localisation
		if (country.length() > 1) {
			source = url + SEPARATOR + lang + SEPARATOR + country + EXTENSION;
			inputFile = getApplicationContext().getResource(source);
		}

		// check for document with language localisation
		if ((inputFile == null || !inputFile.exists()) && lang.length() > 1) {
			source = url + SEPARATOR + lang + EXTENSION;
			inputFile = getApplicationContext().getResource(source);
		}

		// check for document without localisation
		if (inputFile == null || !inputFile.exists()) {
			source = url + EXTENSION;
			inputFile = getApplicationContext().getResource(source);
		}

		// create the Excel document from source
		POIFSFileSystem fs = new POIFSFileSystem(inputFile.getInputStream());
		SXSSFWorkbook workBook = new SXSSFWorkbook(100);
		if (logger.isDebugEnabled()) {
			logger.debug("Loaded Excel workbook '" + source + "'");
		}
		return workBook;
	}

	/**
	 * Subclasses must implement this method to create an Excel HSSFWorkbook document,
	 * given the model.
	 * @param model the model Map
	 * @param workbook the Excel workbook to complete
	 * @param request in case we need locale etc. Shouldn't look at attributes.
	 * @param response in case we need to set cookies. Shouldn't write to it.
	 */
	protected abstract void buildExcelDocument(
			Map model, SXSSFWorkbook workbook, HttpServletRequest request, HttpServletResponse response)
			throws Exception;

	/**
	 * Convenient method to obtain the cell in the given sheet, row and column.
	 * <p>Creates the row and the cell if they still doesn't already exist.
	 * Thus, the column can be passed as an int, the method making the needed downcasts.
	 * @param sheet a sheet object. The first sheet is usually obtained by workbook.getSheetAt(0)
	 * @param row thr row number
	 * @param col the column number
	 * @return the HSSFCell
	 */
	protected Cell getCell(Sheet sheet, int row, int col) {
		Row sheetRow = sheet.getRow(row);
		if (sheetRow == null) {
			sheetRow = sheet.createRow(row);
		}
		Cell cell = sheetRow.getCell((short) col);
		if (cell == null) {
			cell = sheetRow.createCell((short) col);
		}
		return cell;
	}

	/**
	 * Convenient method to set a String as text content in a cell.
	 * @param cell the cell in which the text must be put
	 * @param text the text to put in the cell
	 */
	protected void setText(Cell cell, String text) {
		cell.setCellType(Cell.CELL_TYPE_STRING);
		cell.setCellValue(text);
	}
}
