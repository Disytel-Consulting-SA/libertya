package org.openXpertya.process;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.openXpertya.model.MProduct;
import org.openXpertya.model.M_Table;
import org.openXpertya.model.PO;
import org.openXpertya.util.HTMLMsg;
import org.openXpertya.util.HTMLMsg.HTMLList;

public abstract class AddLinesQuicker extends AbstractSvrProcess {
	
	/** ID del registro de la cabecera (Maestro) */
	private Integer headerRecordID;
	
	/** Nombre de la tabla de la cabecera (Maestro) */
	private String headerTableName;
	
	/** PO del header */
	private PO headerPO;
	
	public AddLinesQuicker() {

	}

	/**
	 * Inicializar los valores necesarios de la cabecera o maestro
	 */
	protected void initialize() {
		// Se cargan los valores de las variables a esta altura para evitar NPE
		setHeaderRecordID(getRecord_ID());
		setHeaderTableName(M_Table.getTableName(getCtx(), getTable_ID()));
		setHeaderPO(M_Table.get(getCtx(), getTable_ID()).getPO(getRecord_ID(), get_TrxName()));
	}
	
	@Override
	protected String doIt() throws Exception {
		// Tomar el parámetro e inicializar las líneas a cargar
		initialize();
		// Parser del HTML
		List<QuickDocumentLine> quickLines = loadLines();
		List<String> errors = new ArrayList<String>();
		// Iterar por las líneas de texto y crear las líneas para este documento
		for (QuickDocumentLine quickDocumentLine : quickLines) {
			MProduct[] ps = MProduct.get(getCtx(),
					"value = '" + quickDocumentLine.getProductValue() + "' and isactive = 'Y'", get_TrxName());
			// Si existe el artículo, se crea la línea, error caso contrario
			if(ps != null && ps.length > 0) {
				quickDocumentLine.setProductID(ps[0].getID());
				try{
					createLine(quickDocumentLine);
				} catch(Exception e) {
					errors.add(e.getMessage());
				}
			}
			else {
				errors.add("Articulo inexistente con clave: "+quickDocumentLine.getProductValue());
			}
		}
		// Si hubo errores, disparar error
	    if(errors.size() > 0) {
	    	throw new Exception(getErrorMsg(errors));
	    }
		return "Cantidad de lineas creadas: "+quickLines.size();
	}

	/**
	 * Obtiene las líneas a partir de lo ingresado en el parámetro
	 * 
	 * @return líneas a crear
	 * @throws Exception
	 */
	protected List<QuickDocumentLine> loadLines() throws Exception{
		// El parámetro es un html
		List<QuickDocumentLine> linesToSave = new ArrayList<QuickDocumentLine>();
		List<String> errors = new ArrayList<String>();
		String lines = (String)getParametersValues().get("LINES");
		String[] allLines = lines.trim().split("\n");
		String[] fields; 
		for (String line : allLines) {
			// Primero intentamos por TAB
			fields = line.trim().split("\t");
			// Luego por espacio
			if(fields != null && fields.length == 1) {
				fields = line.trim().split(" ");
			}
			if(fields == null || fields.length != 2) {
				// Linea mal formada, debe tener el siguiente formato:
				// Clave de Artículo<TAB>Cantidad
				errors.add("La línea "+line+" no posee el formato requerido <Clave de Artículo><TAB><Cantidad>");

			}
			try {
				linesToSave.add(new QuickDocumentLine(fields[0], new BigDecimal(fields[1].replace(",", "."))));
			} catch(Exception e) {
				errors.add("La línea "+line+" no posee el formato requerido <Clave de Artículo><TAB><Cantidad>");
			}
		}
	    // Si hubo errores, disparar error
	    if(errors.size() > 0) {
	    	throw new Exception(getErrorMsg(errors));
	    }
	    return linesToSave;
	}

	/**
	 * A partir de una lista de errores, devuelve el mensaje de error completo
	 * 
	 * @param errors lista de errores
	 * @return Mensaje HTML con la lista de errores parámetro
	 */
	protected String getErrorMsg(List<String> errors) {
		HTMLMsg msg = new HTMLMsg();
		HTMLList elist = msg.new HTMLList("errors", "ul");
		int i = 0;
		for (String e : errors) {
			msg.createAndAddListElement("clave_"+i, e, elist);
			i++;
		}
		msg.addList(elist);
		return msg.toString();
	}
	
	/**
	 * Crea y guarda la línea del documento
	 */
	protected abstract void createLine(QuickDocumentLine quickDocumentLine) throws Exception;
	
	protected Integer getHeaderRecordID() {
		return headerRecordID;
	}

	protected void setHeaderRecordID(Integer headerRecordID) {
		this.headerRecordID = headerRecordID;
	}

	protected String getHeaderTableName() {
		return headerTableName;
	}

	protected void setHeaderTableName(String headerTableName) {
		this.headerTableName = headerTableName;
	}
	
	protected PO getHeaderPO() {
		return headerPO;
	}

	protected void setHeaderPO(PO headerPO) {
		this.headerPO = headerPO;
	}

	protected class QuickDocumentLine {
		private String productValue;
		private BigDecimal qty;
		private Integer productID;
		
		public QuickDocumentLine(String productValue, BigDecimal qty) {
			this.setProductValue(productValue);
			this.setQty(qty);
		}

		public String getProductValue() {
			return productValue;
		}

		public void setProductValue(String productValue) {
			this.productValue = productValue;
		}

		public BigDecimal getQty() {
			return qty;
		}

		public void setQty(BigDecimal qty) {
			this.qty = qty;
		}

		public Integer getProductID() {
			return productID;
		}

		public void setProductID(Integer productID) {
			this.productID = productID;
		}
	}
}
