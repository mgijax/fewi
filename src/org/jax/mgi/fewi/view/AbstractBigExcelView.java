package org.jax.mgi.fewi.view;

import java.io.IOException;
import java.util.Locale;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.support.RequestContextUtils;

import org.jax.mgi.fewi.util.FormatHelper;

/**
 * We need to use a special POI implementation that can handle files with more than 65536 rows
 * 
 * @author kstone
 *
 */
public abstract class AbstractBigExcelView extends AbstractReportView
{
	//--- class variables ---//

	private static final String EXTENSION = ".xls";

	private static final String SEPARATOR = "_";

	private static final String HEADER_STYLE = "header";
	private static final String WRAPPED_STYLE = "wrapped";
	private static final String TOP_STYLE = "top";

	//--- instance variables ---//

	private String url;

	//--- methods ---//

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
			Map<String,Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {

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
		//POIFSFileSystem fs = new POIFSFileSystem(inputFile.getInputStream());
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
			Map<String,Object> model, SXSSFWorkbook workbook, HttpServletRequest request, HttpServletResponse response)
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
	
	/**
	 * Convenient method to get next row cell without knowing the index
	 */
	protected void addNextCell(Row row)
	{
		row.createCell(getNextCellIdx(row));
	}
	
	/**
	 * Convenient method to add a new cell with value without calling its index
	 * 	it merely increments from the previous index
	 */
	protected void addNextCellValue(Row row, String value)
	{
		if (value != null) {
			row.createCell(getNextCellIdx(row)).setCellValue(value);
		} else {
			row.createCell(getNextCellIdx(row)).setCellValue("");
		}
	}
	
	private int getNextCellIdx(Row row)
	{
		int idx = row.getLastCellNum();
		if(idx<0) return 0;
		return idx;
	}

	/* check that we have styles defined; if not, define them in the wkbk.
	 */
	protected void addStyles(Sheet sheet, Map<String, CellStyle> styles) {
		// header style - bold and italic with thin bottom border

		CellStyle hs = sheet.getWorkbook().createCellStyle();
		hs.setBorderBottom(CellStyle.BORDER_THIN);

		Font bold = sheet.getWorkbook().createFont();
		bold.setBoldweight(Font.BOLDWEIGHT_BOLD);
		bold.setItalic(true);
		bold.setBold(true);
		hs.setFont(bold);

		styles.put(HEADER_STYLE, hs);

		// set up a style for wrapped text (allowing multiple lines)
		
		CellStyle wrapped = sheet.getWorkbook().createCellStyle();
		wrapped.setWrapText(true);
		wrapped.setVerticalAlignment(CellStyle.VERTICAL_TOP);

		styles.put(WRAPPED_STYLE, wrapped);

		// set up a style for non-wrapped text

		CellStyle top = sheet.getWorkbook().createCellStyle();
		top.setVerticalAlignment(CellStyle.VERTICAL_TOP);

		styles.put(TOP_STYLE, top);
	}

	/* if we've not yet added any rows to the worksheet, add the specified
	 * column headers and format them.
	 */
	protected void addHeaderRow (Sheet sheet, int[] columnWidth,
		Map<String, CellStyle> styles, String[] titles) throws Exception {
		// get header style

		CellStyle hs = styles.get(HEADER_STYLE);

		// now add the titles

		Row header = sheet.createRow(0);
		for (int i=0; i < titles.length; i++) {
			Cell cell = header.createCell(i);
			cell.setCellValue(titles[i]);
			if (hs != null) {
				cell.setCellStyle(hs);
			}

			int desiredWidth = FormatHelper.maxWidth(titles[i]);
			if (desiredWidth > columnWidth[i]) {
				columnWidth[i] = desiredWidth;
			}
		}
	}

	/* add a new data row to the worksheet, formatted as needed (wrapping,
	 * column width monitoring)
	 */
	protected void addDataRow (Sheet sheet, int[] columnWidth, Map<String, CellStyle> styles, List<String> values) {
		Row row = sheet.createRow(sheet.getLastRowNum() + 1);
		int col = -1;

		CellStyle top = styles.get(TOP_STYLE);
		CellStyle wrapped = styles.get(WRAPPED_STYLE);

		for (String s : values) {
			col++;
			Cell cell = row.createCell(getNextCellIdx(row));
			if (s == null) {
				cell.setCellValue("");
			} else {
				cell.setCellValue(s);

				int desiredWidth = FormatHelper.maxWidth(s);
				if (desiredWidth > columnWidth[col]) {
					columnWidth[col] = desiredWidth;
				}

				if ((wrapped != null) && (top != null)) {
					if (s.indexOf("\n") >= 0) {
						cell.setCellStyle(wrapped);
					} else {
						cell.setCellStyle(top);
					}
				}
			}
		}
	}

	/* add any last minute style adjustments (like the freeze pane and
	 * column widths)
	 */
	protected void applyStandardFormat(Sheet sheet, int[] columnWidth,
		int[] maxColumnWidth) {

		// apply the column width for each column, with a cap applied
		// from maxColumnWidth).  

		for (int i = 0; i < columnWidth.length; i++) {
			int width = Math.min(columnWidth[i], maxColumnWidth[i]);
			if (width > 0) {
				sheet.setColumnWidth(i, (width + 1) * 255);
			}
		}

		// freeze the sheet so the headers remain while scrolling
		sheet.createFreezePane(0,1);

		logger.debug("normal exit from applyStandardFormat()");
		return;
	}
}
