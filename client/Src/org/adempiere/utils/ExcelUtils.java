package org.adempiere.utils;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.logging.Level;
import java.util.logging.Logger;

import jxl.HeaderFooter;
import jxl.Workbook;
import jxl.format.CellFormat;
import jxl.format.PageOrientation;
import jxl.format.PaperSize;
import jxl.write.Formula;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.CellValue;
import jxl.write.biff.RowsExceededException;

import org.apache.commons.lang.StringUtils;
import org.openXpertya.apps.ADialog;
import org.openXpertya.util.CLogger;

public class ExcelUtils 
{
	private static ExcelUtils instance;
	/**	Logger							*/
	protected CLogger			log = CLogger.getCLogger (getClass());
	
	
	private ExcelUtils(){}
	
	public static ExcelUtils getInstance()
	{
		if (instance == null)
		{
			instance = new ExcelUtils();
		}
		
		return instance;
	}
	
	public WritableWorkbook crearLibro(String rutaArchivo)
		throws IOException
	{
		File archivoLibro = new File(rutaArchivo);
		String nombreArchivo = StringUtils.substringBefore(archivoLibro.getName(), ".");
		String extensionArchivo = StringUtils.substringAfter(archivoLibro.getName(), ".");
		File parentFile = archivoLibro.getParentFile();
		Boolean sobreescribir = Boolean.FALSE;
		if (archivoLibro.exists())
		{
			sobreescribir = ADialog.ask(-1, null, "Ya existe un archivo con ese nombre. Lo desea sobreescribir?");
			if (!sobreescribir)
			{
				int contadorArchivo = 0;
				String nuevoNombre;
				File nuevoArchivo;
				
				do
				{
					nuevoNombre = nombreArchivo + " (" + ++contadorArchivo + ")" + "." + extensionArchivo;
					nuevoArchivo = new File(parentFile, nuevoNombre);
				}
				while(nuevoArchivo.exists());
				
				archivoLibro = nuevoArchivo;
			}
		}
		else 
		{
			parentFile.mkdirs();			
		}
		
		log.log(Level.INFO, "Archivo creado=" + archivoLibro);
		
		return Workbook.createWorkbook(archivoLibro);
	}
	
	public WritableSheet crearHoja(WritableWorkbook workbook, String tituloPlanilla)
	{
		WritableSheet sheet = workbook.createSheet(tituloPlanilla, 0);
	
		// Set the orientation and the margins
		sheet.getSettings().setPaperSize(PaperSize.LEGAL);
		sheet.getSettings().setOrientation(PageOrientation.LANDSCAPE);
		sheet.getSettings().setHeaderMargin(0.5);
		sheet.getSettings().setFooterMargin(0.5);
	
		sheet.getSettings().setTopMargin(0.5);
		sheet.getSettings().setBottomMargin(0.5);
		
		// Add a header and footer
		HeaderFooter header = new HeaderFooter();
		header.getCentre().append("Adempiere 2019");
		sheet.getSettings().setHeader(header);
	
		HeaderFooter footer = new HeaderFooter();
		footer.getRight().append("Pag. ");
		footer.getRight().appendPageNumber();
		sheet.getSettings().setFooter(footer);
		
		return sheet;
	}
	
	public void addLabel(WritableSheet sheet, int column, int row, String labelText, int columnWidth, CellFormat format) 
		throws RowsExceededException, 
			   WriteException
	{
		Label label = new Label(column, row, labelText, format);
		sheet.addCell(label);
		sheet.setColumnView(column, columnWidth);
	}
	
	public void addLabel(WritableSheet sheet, int column, int row, String labelText, CellFormat format) 
		throws RowsExceededException, 
			   WriteException
	{
		Label label = new Label(column, row, (labelText == null ? "" : labelText), format);
		sheet.addCell(label);
	}
	
	public void addFormula(WritableSheet sheet, int column, int row, String formulaText, CellFormat format) 
		throws RowsExceededException, 
	           WriteException
	{
		if (formulaText != null)
		{
			Formula formula = new Formula(column, row, formulaText, format);
			sheet.addCell(formula);
		}
		else 
		{
			this.addBlankCell(sheet, column, row, format);
		}
	}
	
	public void addNumber(WritableSheet sheet, int column, int row, BigDecimal numberValue, CellFormat format) 
		throws RowsExceededException, 
			   WriteException
	{
		if (numberValue != null) 
		{
			CellValue number = new jxl.write.Number(column, row, numberValue.doubleValue(), format);
			sheet.addCell(number);
		}
		else 
		{
			this.addBlankCell(sheet, column, row, format);
		}
	}
	
	public void addNumber(WritableSheet sheet, int column, int row, Integer numberValue, CellFormat format) 
		throws RowsExceededException, 
	           WriteException
	{
		if (numberValue != null) 
		{
			CellValue number = new jxl.write.Number(column, row, numberValue.intValue(), format);
			sheet.addCell(number);
		}
		else 
		{
			this.addBlankCell(sheet, column, row, format);
		}
	}
	
	public void addDateTime(WritableSheet sheet, int column, int row, Timestamp dateTimeValue, CellFormat format) 
		throws RowsExceededException, 
	           WriteException
	{
		if (dateTimeValue != null) 
		{
			CellValue dateTime = new jxl.write.DateTime(column, row, dateTimeValue, format);
			sheet.addCell(dateTime);
		}
		else 
		{
			this.addBlankCell(sheet, column, row, format);
		}
	}
	
	public void addBlankCell(WritableSheet sheet, int column, int row, CellFormat format) 
		throws RowsExceededException, 
			   WriteException
	{
		sheet.addCell(new jxl.write.Blank(column, row, format));
	}
}
