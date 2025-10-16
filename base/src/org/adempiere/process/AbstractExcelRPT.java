package org.adempiere.process;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;

import jxl.format.CellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import org.adempiere.utils.ExcelUtils;
import org.openXpertya.process.SvrProcess;

/**
 * Clase abstracta para la generacion de Reportes.
 * Contiene metodos utilitarios.
 *
 */
public abstract class AbstractExcelRPT 
	extends SvrProcess 
{
	protected ExcelUtils excelUtils = ExcelUtils.getInstance();
	
	protected WritableWorkbook crearLibro(String rutaArchivo)
		throws IOException
	{
		return excelUtils.crearLibro(rutaArchivo);
	}
	
	protected WritableSheet crearHoja(WritableWorkbook workbook, String tituloPlanilla)
	{
		return excelUtils.crearHoja(workbook, tituloPlanilla);
	}
	
	protected void addLabel(WritableSheet sheet, int column, int row, String labelText, int columnWidth, CellFormat format) 
		throws RowsExceededException, 
			   WriteException
	{
		excelUtils.addLabel(sheet, column, row, labelText, columnWidth, format);
	}
	
	protected void addLabel(WritableSheet sheet, int column, int row, String labelText, CellFormat format) 
		throws RowsExceededException, 
			   WriteException
	{
		excelUtils.addLabel(sheet, column, row, labelText, format);
	}
	
	protected void addFormula(WritableSheet sheet, int column, int row, String formulaText, CellFormat format) 
		throws RowsExceededException, 
	           WriteException
	{
		excelUtils.addFormula(sheet, column, row, formulaText, format);
	}
	
	protected void addNumber(WritableSheet sheet, int column, int row, BigDecimal numberValue, CellFormat format) 
		throws RowsExceededException, 
			   WriteException
	{
		excelUtils.addNumber(sheet, column, row, numberValue, format);
	}
	
	protected void addNumber(WritableSheet sheet, int column, int row, Integer numberValue, CellFormat format) 
		throws RowsExceededException, 
	           WriteException
	{
		excelUtils.addNumber(sheet, column, row, numberValue, format);
	}
	
	protected void addDateTime(WritableSheet sheet, int column, int row, Timestamp dateTimeValue, CellFormat format) 
		throws RowsExceededException, 
	           WriteException
	{
		excelUtils.addDateTime(sheet, column, row, dateTimeValue, format);
	}
	
	protected void addBlankCell(WritableSheet sheet, int column, int row, CellFormat format) 
		throws RowsExceededException, 
			   WriteException
	{
		excelUtils.addBlankCell(sheet, column, row, format);
	}
}
