/*
 * VConciliacionModel.java
 *
 * Created on 2 de agosto de 2007, 10:03
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.openXpertya.apps.form;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Level;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import org.openXpertya.apps.form.VModelHelper.ResultItem;
import org.openXpertya.apps.form.VModelHelper.ResultItemTableModel;
import org.openXpertya.model.MBankStatement;
import org.openXpertya.model.MBankStatementLine;
import org.openXpertya.model.MBoletaDeposito;
import org.openXpertya.model.MPayment;
import org.openXpertya.model.X_C_BankStatLine_Reconcil;
import org.openXpertya.model.X_I_BankStatement;
import org.openXpertya.process.ImportBankStatement;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;

/**
 *
 * @author usuario
 */
public class VConciliacionTableModel {
    
	public final static int MODOLINEAS_IMPORTADAS = 0;
	public final static int MODOLINEAS_EXISTENTES = 1;
	public final static int MODOPAGOS_PAGOS = 0;
	public final static int MODOPAGOS_BOLETAS = 1;
	public final static int MODOPAGOS_PAGOSUNIONBOLETAS = 2;
	
	public static boolean DiferenciaMenor(BigDecimal sum1, BigDecimal sum2, double limite) {
		BigDecimal max = sum1.abs().max(sum2.abs());
    	BigDecimal min = sum1.abs().min(sum2.abs());
    	
    	/*
    	 *   max ------ 100
    	 * (max-min) -- porcentaje
    	 */
    	
    	try {
    		BigDecimal porcentaje = max.subtract(min).multiply(new BigDecimal(100)).divide(max, BigDecimal.ROUND_UP);
    	
    		// Devuelve TRUE solo si la diferencia entre los dos numeros es menor del limite%
    	
    		return porcentaje.compareTo(new BigDecimal(limite)) <= 0;
    	} catch (Exception e) {
    		return false;
    	}	
	}
	
    interface ConciliacionColumnsData {
    	
    	// Para saber los indices de los ResultItems
    	
    	public int getRowIdxID();
    	public int getRowIdxDate();
    	public int getRowIdxAmt();
    	public int getRowIdxTipo();
    	
    	// Un semi-double-dispatching
    	
    	public boolean conciliarLinea(int i, boolean lineaValid, boolean pagoValid, ResultItem riLinea, ResultItem riPago, ConciliacionColumnsData pago) throws Exception;
    	public boolean conciliarPago(int i, boolean lineaValid, boolean pagoValid, MBankStatementLine line, ResultItem riPago) throws Exception;
    }
    
    private class PagosTableModel extends ResultItemTableModel implements ConciliacionColumnsData {
		public PagosTableModel() {
			VModelHelper.GetInstance().super();
			
    		columnNames = new Vector<String>();

            columnNames.add( "#$#" + Msg.getElement( Env.getCtx(),"C_Payment_ID" ));
            columnNames.add( Msg.getElement( Env.getCtx(),"DocumentNo" ));
            columnNames.add( Msg.translate( Env.getCtx(),"Date" ));
            columnNames.add( Msg.translate( Env.getCtx(),"ConvertedAmount" ));
            columnNames.add( Msg.translate( Env.getCtx(),"C_BPartner_ID" )); 
            columnNames.add( Msg.translate( Env.getCtx(),"CheckNo" ));
    	}

		public int getRowIdxID() {
			return 0;
		}

		public int getRowIdxDate() {
			return 2;
		}

		public int getRowIdxAmt() {
			return 3;
		}

		public boolean conciliarLinea(int i, boolean lineaValid, boolean pagoValid, ResultItem riLinea, ResultItem riPago, ConciliacionColumnsData pago) throws Exception {
			return false; // NO SOY UNA LINEA
		}

		public boolean conciliarPago(int i, boolean lineaValid, boolean pagoValid, MBankStatementLine line, ResultItem riPago) throws Exception {
			return conciliarPagoReal(this, i, lineaValid, pagoValid, line, riPago);
		}

		public int getRowIdxTipo() {
			return -1;
		}
    }
    
    public class LineasImportadasTableModel extends ResultItemTableModel implements ConciliacionColumnsData {
        public LineasImportadasTableModel() {
        	VModelHelper.GetInstance().super();
        	
        	columnNames = new Vector<String>();
        	
        	columnNames.add( "#$#" + Msg.getElement( Env.getCtx(),"I_BankStatement_ID" ));
        	columnNames.add( Msg.getElement( Env.getCtx(),"StatementDate" ));
        	columnNames.add( Msg.getElement( Env.getCtx(),"Description" ));
        	columnNames.add( Msg.getElement( Env.getCtx(),"StmtAmt" ));
        }

		public int getRowIdxID() {
			return 0;
		}

		public int getRowIdxDate() {
			return 1;
		}

		public int getRowIdxAmt() {
			return 3;
		}

		public int getRowIdxTipo() {
			return -1;
		}

		public boolean conciliarLinea(int i, boolean lineaValid, boolean pagoValid, ResultItem riLinea, ResultItem riPago, ConciliacionColumnsData pago) throws Exception {
			int linea_ID = lineaValid ? ((Integer)riLinea.getItem(getRowIdxID())) : -1;
			MBankStatementLine line = lineaValid ? crearLinea( linea_ID ) : null;
			return pago.conciliarPago(i, lineaValid, pagoValid, line, riPago);
		}

		public boolean conciliarPago(int i, boolean lineaValid, boolean pagoValid, MBankStatementLine line, ResultItem riPago) throws Exception {
			return false; // NO SOY UN PAGO
		}
    }
    
    public class LineasExistentesTableModel extends ResultItemTableModel implements ConciliacionColumnsData {
        public LineasExistentesTableModel() {
        	VModelHelper.GetInstance().super();
        	
        	columnNames = new Vector<String>();
        	
        	columnNames.add( "#$#" + Msg.getElement( Env.getCtx(),"C_BankStatementLine_ID" ));
        	columnNames.add( Msg.getElement( Env.getCtx(),"StatementDate" ));
        	columnNames.add( Msg.getElement( Env.getCtx(),"Description" ));
        	columnNames.add( Msg.getElement( Env.getCtx(),"StmtAmt" ));
        }

		public int getRowIdxID() {
			return 0;
		}

		public int getRowIdxDate() {
			return 1;
		}

		public int getRowIdxAmt() {
			return 3;
		}

		public int getRowIdxTipo() {
			return -1;
		}

		public boolean conciliarLinea(int i, boolean lineaValid, boolean pagoValid, ResultItem riLinea, ResultItem riPago, ConciliacionColumnsData pago) throws Exception {
			int linea_ID = lineaValid ? ((Integer)riLinea.getItem(getRowIdxID())) : -1;
			MBankStatementLine line = lineaValid ? new MBankStatementLine(m_ctx, linea_ID, m_trxName) : null;
			
			return pago.conciliarPago(i, lineaValid, pagoValid, line, riPago);
			
			// return crearConciliacion_LowLevel(i, lineaValid, pagoValid, line, null, null);
		}

		public boolean conciliarPago(int i, boolean lineaValid, boolean pagoValid, MBankStatementLine line, ResultItem riPago) throws Exception {
			return false; // NO SOY UN PAGO
		}
    }
    
    public class BoletasDepositoTableModel extends ResultItemTableModel implements ConciliacionColumnsData {
    	public BoletasDepositoTableModel() {
    		VModelHelper.GetInstance().super();
    		
    		columnNames = new Vector<String>();
    		
        	columnNames.add( "#$#" + Msg.getElement( Env.getCtx(),"C_BoletaDeposito_ID" ));
        	columnNames.add( Msg.getElement( Env.getCtx(),"FechaDeposito" ));
        	columnNames.add( Msg.getElement( Env.getCtx(),"FechaAcreditacion" ));
        	columnNames.add( Msg.getElement( Env.getCtx(),"DocumentNo" ));
        	columnNames.add( Msg.getElement( Env.getCtx(),"GrandTotal" ));
    	}

		public int getRowIdxID() {
			return 0;
		}

		public int getRowIdxDate() {
			return 1;
		}

		public int getRowIdxAmt() {
			return 4;
		}

		public int getRowIdxTipo() {
			return -1;
		}

		public boolean conciliarLinea(int i, boolean lineaValid, boolean pagoValid, ResultItem riLinea, ResultItem riPago, ConciliacionColumnsData pago) throws Exception {
			return false; // NO SOY UNA LINEA
		}

		public boolean conciliarPago(int i, boolean lineaValid, boolean pagoValid, MBankStatementLine line, ResultItem riPago) throws Exception {
			return conciliarBoletaReal(this, i, lineaValid, pagoValid, line, riPago);
		}
    }
    
    public class PagoUnionBoletaTableModel extends PagosTableModel implements ConciliacionColumnsData {
    	public PagoUnionBoletaTableModel() {
    		super();
    		
    		columnNames.add(0, "Tipo");
    		columnNames.set(1, "#$#" + "ID");
    	}
    	
    	public Object getValueAt(int row, int column) {
    		Object x = super.getValueAt(row, column);
    		
    		if (column == getRowIdxTipo())
    			x = ((String)x).equals("P") ? Msg.parseTranslation( Env.getCtx(),"@C_Payment_ID@" ) : Msg.parseTranslation( Env.getCtx(),"@M_BoletaDeposito_ID@" );
    		
    		return x;
    	}
    	
    	public int getRowIdxTipo() {
    		return 0;
    	}

		public int getRowIdxID() {
			return super.getRowIdxID() + 1;
		}

		public int getRowIdxDate() {
			return super.getRowIdxDate() + 1;
		}

		public int getRowIdxAmt() {
			return super.getRowIdxAmt() + 1;
		}
		
    	public boolean conciliarPago(int i, boolean lineaValid, boolean pagoValid, MBankStatementLine line, ResultItem riPago) throws Exception {
    		if (pagoValid) {
	    		int modoReal = getPagoRealMode((String)riPago.getItem(getRowIdxTipo()));
	    		
	    		if (modoReal == MODOPAGOS_PAGOS)
	    			return conciliarPagoReal(this, i, lineaValid, pagoValid, line, riPago);
	    		else if (modoReal == MODOPAGOS_BOLETAS)
	    			return conciliarBoletaReal(this, i, lineaValid, pagoValid, line, riPago);
    		} 
    		
    		if (lineaValid)
    			return crearConciliacion(i, line);
    		
    		return false;
    	}
    }
    
    public class ToConciliarTableModel extends AbstractTableModel {
        public ToConciliarTableModel() {
            
        }
        
        public void setColsModels(Vector<ResultItemTableModel> cm) {
        	colsModels = cm;
        	
        	for (int i = 0; i < cm.size(); i++) 
        		colsModels.get(i).setResultItem(toConciliarData.get(i));
        	
    		updateColCount();
        }
        
        public void updated() {
        	updateColCount();
        	
        	fireTableStructureChanged();
        }
        
        public int getRowCount() {
        	if (toConciliarData != null && toConciliarData.size() > 0)
        		return toConciliarData.get(0).size();
        	return 0;
        }
        
        public int getColumnCount() { 
            return colCount;
        }
        
        private void updateColCount() {
        	colCount = 0;
    		for (ResultItemTableModel x : colsModels)
    			colCount += x.getColumnCount();
        }
        
        private int [] getSampleColIdx(int column) {
        	int i = 0;
    		
    		while (i < colsModels.size() && column >= colsModels.get(i).getColumnCount()) {
    			column -= colsModels.get(i).getColumnCount();
    			i++;
    		}
        	
    		int[] a = {i, column};
    		
    		return a;
        }
        
        public Object getValueAt(int row, int column) {
        	try {
	        	if (colsModels != null) {
	        		int[] x = getSampleColIdx(column);
	        		
	        		if (x[0] < colsModels.size()) {
	    				Object obj = null;
    					obj = colsModels.get(x[0]).getValueAt(row, x[1]);
	    				return obj;
	        		}
	        	}
        	} catch (ArrayIndexOutOfBoundsException e) {
        		
        	}
            return null;
        }
        
        public Class<?> getColumnClass(int columnIndex) {
    		int[] x = getSampleColIdx(columnIndex);
    		
    		if (x[0] < colsModels.size())
    			return colsModels.get(x[0]).getColumnClass(x[1]);
    		
    		return Object.class;
        }
        
        public String getColumnName(int columnIndex) {
    		int[] x = getSampleColIdx(columnIndex);
    		
    		if (x[0] < colsModels.size())
    			return colsModels.get(x[0]).getColumnName(x[1]);
    		
    		return "" + columnIndex;
        }
        
        private int colCount = 0;
        private Vector<ResultItemTableModel> colsModels = null;
    }
    
    /** Creates a new instance of VConciliacionModel */
    public VConciliacionTableModel(int modoLineas, int modoPagos) {
    	
    	// 0 = lineas importadas
    	// 1 = lineas existentes
    	
    	m_modoLineas = modoLineas;
    	m_modoPagos = modoPagos;
    	
    	m_lineas = instanciaLineasTableModel();
    	m_pagos = instanciaPagosTableModel();
    	
        initToConciliarTableModel(); 
        
    }
    
    public TableModel getToConciliar() {
        return VModelHelper.GetInstance().new HideColumnsTableModel( m_toConciliar );
    }
    
    public TableModel getLineas() {
        return VModelHelper.GetInstance().new HideColumnsTableModel( m_lineas );
    }
    
    public TableModel getPagos() {
        return VModelHelper.GetInstance().new HideColumnsTableModel( m_pagos );
    }
    
    public int getPagoRealMode(int rowIdx) {
    	try {
    		return getPagoRealMode(payData.get(rowIdx));
    	} catch (Exception e) {
    		return -1;
    	}
    }
    
    protected int getPagoRealMode(ResultItem ri) {
    	return getPagoRealMode((String)ri.getItem(((ConciliacionColumnsData)m_pagos).getRowIdxTipo()));
    }
    
    public static int getPagoRealMode(String tipo) {
    	if (tipo.equals("P"))
    		return MODOPAGOS_PAGOS;
    	else if (tipo.equals("B"))
    		return MODOPAGOS_BOLETAS;
    	return -1;
    }
    
    public void setBankAccountID(int C_BankAccount_ID) {
    	m_bankAccountId = C_BankAccount_ID;
    }
    
    public void setBankStatement(MBankStatement bankStatement) {
    	m_bankStatement = bankStatement;
    	
    	m_ctx = m_bankStatement.getCtx();
    	m_trxName = m_bankStatement.get_TrxName();
    }
    
    public int getBankAccountID() {
    	return m_bankAccountId;
    }
    
    public MBankStatement getBankStatement() {
    	return m_bankStatement;
    }
    
    public void init(Timestamp statementDate, int mainCurrencyID) {
    	
    	m_statementDate = statementDate;
    	m_mainCurrencyID = mainCurrencyID;
    	
        if( m_statementDate == null ) 
            m_statementDate = new Timestamp( System.currentTimeMillis());

        resetPagos();
        resetLineas();
        
    }
    
    public void concilData(int[] importedLinesItems, int[] paymentsItems) throws Exception {
    	if (importedLinesItems.length > 1 && paymentsItems.length > 1)
    		throw new Exception("concilData: 1 <-> n || n <-> 1");
    	
    	if (importedLinesItems.length == 0 && paymentsItems.length == 0)
    		return;
    	
    	Arrays.sort(importedLinesItems);
    	Arrays.sort(paymentsItems);
    	
    	Vector<ResultItem> importedLinesVector = new Vector<ResultItem>();
    	Vector<ResultItem> paymentsVector = new Vector<ResultItem>();
    	
    	// Me guardo los ResultItems seleccionados de ambas tablas
    	
    	getItems(importedLinesItems, importedLinesVector, importedData);
    	getItems(paymentsItems, paymentsVector, payData);
    	
    	// Lo hago en dos pasos para que sea Exception-safe
    	
    	// Elimino los elementos seleccionados de las tablas 
    	
    	int i;
    	
    	for (i=0; i<importedLinesItems.length; i++)
    		importedData.remove(importedLinesItems[i] - i);
    	
    	for (i=0; i<paymentsItems.length; i++)
    		payData.remove(paymentsItems[i] - i);

    	// Imported Lines, Payments
    	
    	// 1..n ó n..1
    	
    	if (importedLinesVector.size() > 1 || paymentsVector.size() == 0)
    	{
    		ResultItem rr = paymentsVector.size() > 0 ? paymentsVector.get(0) : VModelHelper.ResultItemFactory(null);
    		
    		for (i=0; i<importedLinesVector.size(); i++) {
    			toConciliarData.get(0).add(importedLinesVector.get(i));
    			toConciliarData.get(1).add(rr);
    		}
    	}
    	else 
    	{
    		ResultItem rr = importedLinesVector.size() > 0 ? importedLinesVector.get(0) : VModelHelper.ResultItemFactory(null);
    		
    		for (i=0; i<paymentsVector.size(); i++) {
    			toConciliarData.get(0).add(rr);
    			toConciliarData.get(1).add(paymentsVector.get(i));
    		}
    	}
    	
    	m_lineas.fireTableDataChanged();
    	m_pagos.fireTableDataChanged();
    	m_toConciliar.updated();
    }
    
    public void unconcilData(int[] toConcilItems) throws Exception {
    	Arrays.sort(toConcilItems);
    	// IdentityHashMap<ResultItem, Object> conj = new IdentityHashMap<ResultItem, Object>();
    	
    	for (int i = 0; i < toConcilItems.length; i++) {
    		ResultItem r1 = toConciliarData.get(0).remove(toConcilItems[i] - i);
    		ResultItem r2 = toConciliarData.get(1).remove(toConcilItems[i] - i);
    		
    		if (r1 != null && r1.isValid() && !importedData.contains(r1)) 
    			importedData.add(r1);
    		
    		if (r2 != null && r2.isValid() && !payData.contains(r2)) 
    			payData.add(r2);
    		
    	}
    	
    	m_lineas.fireTableDataChanged();
    	m_pagos.fireTableDataChanged();
    	m_toConciliar.fireTableStructureChanged();
    	
    	Collections.sort(payData, sortComparator);
    	Collections.sort(importedData, sortComparator);
    }
    
    private MBankStatementLine crearLinea( int I_BankStatement_ID ) throws Exception{
    	X_I_BankStatement imp = null;
    	MBankStatementLine line = null;
    	try{
	    	// I_BankStatement_ID indica el ID de la tabla I; ya deben estar los datos allí
	    	imp = new X_I_BankStatement( m_ctx, I_BankStatement_ID, m_trxName ) ;
				
			line = new MBankStatementLine(m_bankStatement);
			
			boolean saveOk = false;
			
			// Solo linea, o Ambos
			
			line.setStatementLineDate(m_statementDate);
			
			saveOk = ImportBankStatement.assignImpToLine(m_bankStatement, line, imp);
	
			if (saveOk)
				return line;
		
    	} catch(Exception e){
    		throw new Exception(e);
    	}
		return null;
    }
    
    /*
    protected void procesarLineasImpConPagos() throws Exception {
    	
    	if (toConciliarData.size() == 0)
    		return;
    	
    	if (toConciliarData.get(0).size() != toConciliarData.get(1).size())
    		throw new Exception("VCociliacionTableModel.procesar()");
    	
    	int i;
    	
    	Properties ctx = m_ctx;
    	String trxName = m_trxName;
    	
    	// Para no procesar items repetidos, y de acá saco los items repetidos 
    	// en caso que los necesite de nuevo.
    	HashMap<ResultItem, MBankStatementLine> conj = new HashMap<ResultItem, MBankStatementLine>();
    	
    	
    	 //----//
    	// ** //
    	
//    	 *
//    	 * 1. Crear las BankStatementLines
//    	 *
//    	 * Si tengo solo un Payment, inserto el BSL a partir del payment.
//    	 * 
//    	 * Si tengo solo un I_BankS, inserto el BSL a partir del I_BankStatement
//    	 * 
//    	 * Si tengo de los dos, inserto el BSL a parir del I_BankStatement de forma unica sin repeticion
//    	 * 
//    	 * TODO: Faltan realizar las validaciones de I_BankStatement que estan en ImportBankStatement
//    	 * 
//     	 * 2. Crear relación de conciliación
//    	 * 
//    	 *
    	
    	for (i = 0; i < toConciliarData.get(0).size(); i++)
    	{
    		ResultItem riLinea = toConciliarData.get(0).get(i);
    		ResultItem riPago = toConciliarData.get(1).get(i);
    		
    		boolean lineaValid = riLinea != null && riLinea.isValid();
    		boolean pagoValid = riPago != null && riPago.isValid();
    		
    		int I_BankStatement_ID = lineaValid ? ((Integer)riLinea.getItem(0)).intValue() : -1;
    		int C_Payment_ID = pagoValid ? ((Integer)riPago.getItem(0)).intValue() : -1;
    		
    		try 
    		{
	    		MPayment pay = 
	    			pagoValid ? new MPayment( ctx, C_Payment_ID, trxName) : null;
	    		
	    		MBankStatementLine line = 
	    			(lineaValid && conj.containsKey(riLinea)) ? conj.get(riLinea) : crearLinea( I_BankStatement_ID );
		    			
	    		boolean saveOk = (line != null);
	    		
	    		//
	    		//
	    		// Crear las BankStatementLines
	    		//
	    		//
	
	    		if (!saveOk) {
	                log.log( Level.SEVERE, "Line not created #" + i );
	    		}
	
	    		//
	    		// 
	    		//  CREAR EL OBJETO DE CONCILIACION //
	    		//  
	    		//
	    		
	    		if (crearConciliacion(i, lineaValid, pagoValid, line, pay)) {
	    			// Si se concilió correctamente, elimina el registro de
	    			// la lista de conciliación.
	    			toConciliarData.get(0).remove(i);
	    			toConciliarData.get(1).remove(i);
	    			i--;
	    		}
    		} catch (Exception e) {
    			log.log(Level.SEVERE, "No se pudo conciliar: ", e);
    		}
    	}
    	
    	m_toConciliar.fireTableDataChanged();
    	
    }
    */
    
    /*
    protected void procesarLineasExistentesConPagos() throws Exception {
    	
    	if (toConciliarData.size() == 0)
    		return;
    	
    	if (toConciliarData.get(0).size() != toConciliarData.get(1).size())
    		throw new Exception("VCociliacionTableModel.procesar()");
    	
    	int i;
    	
    	Properties ctx = m_ctx;
    	String trxName = m_trxName;
    	
    	for (i = 0; i < toConciliarData.get(0).size(); i++) 
    	{
    		ResultItem riLinea = toConciliarData.get(0).get(i);
    		ResultItem riPago = toConciliarData.get(1).get(i);
    		
    		boolean lineaValid = riLinea != null && riLinea.isValid();
    		boolean pagoValid = riPago != null && riPago.isValid();
    		
    		int C_BankStatementLine_ID = lineaValid ? ((Integer)riLinea.getItem(0)).intValue() : -1;
    		int C_Payment_ID = pagoValid ? ((Integer)riPago.getItem(0)).intValue() : -1;
    		
    		try {
	    		MBankStatementLine line = 
	    			lineaValid ? new MBankStatementLine(ctx, C_BankStatementLine_ID, trxName) : null;
	    			
	    		MPayment pay = 
	    			pagoValid ? new MPayment( ctx, C_Payment_ID, trxName) : null;
	
	    		if (crearConciliacion(i, lineaValid, pagoValid, line, pay)) {
	    			// Si se concilió correctamente, elimina el registro de
	    			// la lista de conciliación.
	    			toConciliarData.get(0).remove(i);
	    			toConciliarData.get(1).remove(i);
	    			i--;
	    		}
    		} catch (Exception e) {
    			log.log(Level.SEVERE, "No se pudo conciliar: ", e);
    		}
    	}
    	
    	m_toConciliar.fireTableDataChanged();
    }
    */
    
    protected void procesarConciliacionGenerica() throws Exception {
    	ConciliacionColumnsData lineasColumnsData = (ConciliacionColumnsData)m_lineas;
    	ConciliacionColumnsData pagosColumnsData = (ConciliacionColumnsData)m_pagos;
    	
    	if (toConciliarData.size() == 0)
    		return;
    	
    	if (toConciliarData.get(0).size() != toConciliarData.get(1).size())
    		throw new Exception("VCociliacionTableModel.procesar()");
    	
    	int i;
    	
    	for (i = 0; i < toConciliarData.get(0).size(); i++) 
    	{
    		ResultItem riLinea = toConciliarData.get(0).get(i);
    		ResultItem riPago = toConciliarData.get(1).get(i);
    		
    		boolean lineaValid = riLinea != null && riLinea.isValid();
    		boolean pagoValid = riPago != null && riPago.isValid();
    		
    		// int linea_ID = lineaValid ? ((Integer)riLinea.getItem(lineasColumnsData.getRowIdxID())).intValue() : -1;
    		// int pago_ID = pagoValid ? ((Integer)riPago.getItem(pagosColumnsData.getRowIdxID())).intValue() : -1;
    		
    		try {
    			
    			if (lineasColumnsData.conciliarLinea(i, lineaValid, pagoValid, riLinea, riPago, pagosColumnsData)) {
    				// Si se concilió correctamente, elimina el registro de
	    			// la lista de conciliación.
	    			toConciliarData.get(0).remove(i);
	    			toConciliarData.get(1).remove(i);
	    			i--;
    			}
    			
    		} catch (Exception e) {
    			throw new Exception(e);
    		}
    	}
    	
    	m_toConciliar.fireTableDataChanged();
    }

    public void procesarConciliacion() throws Exception {
    	
    	procesarConciliacionGenerica();
    	
    }
    
    protected boolean conciliarPagoReal(ConciliacionColumnsData colsData, int i, boolean lineaValid, boolean pagoValid, MBankStatementLine line, ResultItem riPago) {
    	int pago_ID = pagoValid ? ((Integer)riPago.getItem(colsData.getRowIdxID())) : -1;
		MPayment pay = pagoValid ? new MPayment(m_ctx, pago_ID, m_trxName) : null;
		
		boolean saveok = true;
		
		if (pagoValid && lineaValid) {
			line.setPayment(pay);
			saveok = line.save();
		}
		
		if (saveok)
			saveok = crearConciliacion(i, lineaValid, pagoValid, line, pay);
		
		return saveok;
    }
    
    protected boolean conciliarBoletaReal(ConciliacionColumnsData colsData, int i, boolean lineaValid, boolean pagoValid, MBankStatementLine line, ResultItem riPago) throws Exception {
		int boleta_ID = pagoValid ? ((Integer)riPago.getItem(colsData.getRowIdxID())) : -1;
		MBoletaDeposito boleta = pagoValid ? new MBoletaDeposito(m_ctx, boleta_ID, m_trxName) : null;

		boolean saveok = true;
		
		if (lineaValid && pagoValid) {
			line.setBoletaDeposito(boleta);
			saveok = line.save();
		}
		
		if (saveok)
			saveok = crearConciliacion(i, lineaValid, pagoValid, line, boleta);
		
		return saveok;
	}
    
    private boolean crearConciliacion(int i, boolean lineaValid, boolean pagoValid, MBankStatementLine line, MBoletaDeposito boleta) {
    	return crearConciliacion_LowLevel(i, lineaValid, pagoValid, line, null, boleta);
    }

    private boolean crearConciliacion(int i, boolean lineaValid, boolean pagoValid, MBankStatementLine line, MPayment pay) {
    	return crearConciliacion_LowLevel(i, lineaValid, pagoValid, line, pay, null);
    }
    
    /** Invocar cuando un pago es inválido, y solo hay linea a conciliar
     * 
     * @param i numero de linea
     * @param lineaValid si la linea es válida
     * @param line linea a conciliar
     * @return si se pudo crear la conciliación
     */
    private boolean crearConciliacion(int i, MBankStatementLine line) {
    	return crearConciliacion_LowLevel(i, true, false, line, null, null);
    }
    
    private boolean crearConciliacion_LowLevel(int i, boolean lineaValid, boolean pagoValid, MBankStatementLine line, MPayment pay, MBoletaDeposito boleta) {

    	// Al menos uno de los dos debe ser valido
    	if (!lineaValid && !pagoValid)
    		return false;
    	
    	// Idem
    	if (pagoValid && (pay == null && boleta == null)) 
    		return false;
    	
    	Properties ctx = m_ctx;
    	String trxName = m_trxName;
    	
    	boolean saveOk = true ;
    	
		if (lineaValid) {
			
			// Solo creo el registro de conciliacion si existe una linea.
			
			X_C_BankStatLine_Reconcil brec = new X_C_BankStatLine_Reconcil(ctx, 0, trxName);

			brec.setC_Currency_ID(line.getC_Currency_ID());
			brec.setTrxAmt(line.getTrxAmt());
			
			brec.setC_BankStatementLine_ID(line.getC_BankStatementLine_ID());
			
			brec.setReferenceNo(line.getReferenceNo());
			line.setIsReconciled(true);
			
			saveOk = line.save();
			
			brec.setIsReconciled(true);
			brec.setDocStatus(X_C_BankStatLine_Reconcil.DOCSTATUS_Completed);
			brec.setDocAction(X_C_BankStatLine_Reconcil.DOCACTION_Close);
			
			if (pagoValid) {
				if (pay != null) {
					brec.setC_Payment_ID(pay.getC_Payment_ID());
					brec.setC_Currency_ID(pay.getC_Currency_ID());
					brec.setTrxAmt(pay.getPayAmt());
				} else if (boleta != null) { 
					brec.setM_BoletaDeposito_ID(boleta.getM_BoletaDeposito_ID());
					brec.setC_Currency_ID(boleta.getC_Currency_ID());
					brec.setTrxAmt(boleta.getGrandTotal());
				}
			}

			if (saveOk)
				saveOk = brec.save();
		}

		if (pagoValid && saveOk) {
		    if (pay != null && !pay.isReconciled()) {
				pay.setIsReconciled(true);
				saveOk = pay.save();
			} else if (boleta != null && !boleta.isReconciled()) {
				boleta.setConciliado(true);
				saveOk = boleta.save();
			}
		}
		
		if (!saveOk) {
            log.log( Level.SEVERE, "C_BankStatLine_Reconcil not created #" + i );
		}
		
		return saveOk;
    }
    
    
    public boolean precheckConcilData(int[] linesItems, int[] paymentsItems) {
    	ConciliacionColumnsData lineasColumnsData = ((ConciliacionColumnsData)m_lineas);
    	ConciliacionColumnsData pagosColumnsData = ((ConciliacionColumnsData)m_pagos);
    	
    	Vector<ResultItem> linesVector = new Vector<ResultItem>();
    	Vector<ResultItem> paymentsVector = new Vector<ResultItem>();
    	
    	// Me guardo los ResultItems seleccionados de ambas tablas
    	
    	getItems(linesItems, linesVector, importedData);
    	getItems(paymentsItems, paymentsVector, payData);
   	
    	BigDecimal sum1 = new BigDecimal(0);
    	BigDecimal sum2 = new BigDecimal(0);
    	
    	// Lineas
    	
    	for (int i = 0; i < linesVector.size(); i++)
    		sum1 = sum1.add((BigDecimal)linesVector.get(i).getItem(lineasColumnsData.getRowIdxAmt()));

    	// Pagos
    	
    	for (int i = 0; i < paymentsVector.size(); i++)
    		sum2 = sum2.add((BigDecimal)paymentsVector.get(i).getItem(pagosColumnsData.getRowIdxAmt()));
    	
    	return DiferenciaMenor(sum1, sum2, 10.0);
    }
    
    public int[] findMatches(int sideNum, int selIdx) throws Exception {
    	Vector<Integer> matches = new Vector<Integer>();
    	
    	Vector<ResultItem> thisSide = null;
    	Vector<ResultItem> otherSide = null;

    	ConciliacionColumnsData thisSideColumnsData = null; 
    	ConciliacionColumnsData otherSideColumnsData = null;
    	
    	int otherSideNum = 1 - sideNum;
    	
    	if (sideNum == 0) {
    		thisSide = importedData;
    		otherSide = payData;
    		thisSideColumnsData = (ConciliacionColumnsData) m_lineas;
    		otherSideColumnsData = (ConciliacionColumnsData) m_pagos;
    	} else if (sideNum == 1) {
    		thisSide = payData;
    		otherSide = importedData;
    		thisSideColumnsData = (ConciliacionColumnsData) m_pagos;
    		otherSideColumnsData = (ConciliacionColumnsData) m_lineas;
    	} else {
    		throw new IllegalArgumentException("side: " + sideNum);
    	}
    	
    	// BY DATE // TODO: ¿Algo mas?
    	
    	// Selecciona el que tiene misma fecha
    	/*
    	Date selected = (Date)(thisSide.get(selIdx).getItem(m_dateColIdx[sideNum]));
    	
    	for (int i = 0; i<otherSide.size(); i++) {
    		Date d = (Date)otherSide.get(i).getItem(m_dateColIdx[otherSideNum]);
    		if ( selected.equals(d) ) {
    			matches.add(i);
    			break;
    		}
    	}
    	*/
    	
    	
    	BigDecimal selectedAmt = (BigDecimal)(thisSide.get(selIdx).getItem(thisSideColumnsData.getRowIdxAmt()));
    	
    	for (int i = 0; i<otherSide.size(); i++) {
    		BigDecimal otherAmt = (BigDecimal)(otherSide.get(i).getItem(otherSideColumnsData.getRowIdxAmt()));
    		
    		if ( ! DiferenciaMenor(selectedAmt, otherAmt, 10.0 ) ) {
    			otherSide.remove(i);
    			i--;
    		}
    	}
    	
    	m_lineas.fireTableDataChanged();
    	m_pagos.fireTableDataChanged();
    	
    	int[] ret = new int[matches.size()];
    	for (int i=0; i<matches.size();i++) ret[i] = matches.get(i).intValue();
    	return ret;
    }
    
    protected void doResetPayments() {
        boolean clean = payData == null;
        
        payData = new Vector<ResultItem>();
        m_pagos.setResultItem(payData);

        StringBuffer sql  = new StringBuffer( " SELECT p.C_Payment_ID,p.DocumentNo, p.DateTrx, currencyConvert(p.PayAmt, p.C_Currency_ID, ?, ?, null, p.AD_Client_ID, p.AD_Org_ID) AS ConvertedPayAmt, "    // #1
        + " bp.Name, p.CheckNo " + 
        " FROM C_BankAccount ba" + 
        " INNER JOIN C_Payment_v p ON (p.C_BankAccount_ID=ba.C_BankAccount_ID)" + 
        " INNER JOIN C_Currency c ON (p.C_Currency_ID=c.C_Currency_ID)" + 
        " INNER JOIN C_BPartner bp ON (p.C_BPartner_ID=bp.C_BPartner_ID) " + 
        " WHERE p.Processed='Y' AND p.IsReconciled='N'" + " AND p.DocStatus IN ('CO','CL','RE') AND p.PayAmt<>0" + 
        " AND p.C_BankAccount_ID = ? " );

        ResultItem[] items = getToConcilNotNullResultItems(1);
        if (items.length > 0) {
        	StringBuffer elems = new StringBuffer();
        	int i;
        	int colIdx = 0;
        	for (i = 0; i < items.length - 1; i++) {
        		elems.append((items[i].getItem(colIdx)).toString());
        		elems.append(",");
        	}
        	elems.append((items[i].getItem(colIdx)).toString());
        	
        	sql.append(" AND p.C_Payment_ID NOT IN (").append(elems).append(") ");
        }
        
        // Si cambias el criterio de ordenamiento hacelo tambien en sortComparator
        sql.append(" ORDER BY p.C_Payment_ID ");
        
        // Atencion: Se espera que el primer parametro sea Timestamp y el segundo int

        // executeQuery( sql.toString(), payData, m_statementDate, m_bankAccountId );
        
    	try {
    		PreparedStatement pstmt = DB.prepareStatement( sql.toString() );
    		
    		pstmt.setInt(1, m_mainCurrencyID);
    		pstmt.setTimestamp(2, m_statementDate);
    		pstmt.setInt(3, m_bankAccountId);
    		
    		ResultSet rs = pstmt.executeQuery();
    		
    		while(rs.next()) {
    			payData.add(VModelHelper.ResultItemFactory(rs));
            }
    	} catch (Exception e) {
    		log.log( Level.SEVERE, sql.toString(), e );
    	}
        
        m_pagos.fireChanged(clean);
    }

    protected void doResetImportedLines() {
    	
    	boolean clean = importedData == null;
    	
    	importedData = new Vector<ResultItem>();
    	m_lineas.setResultItem(importedData);

    	StringBuffer sql = new StringBuffer( "SELECT ii.I_BankStatement_ID, coalesce(ii.StatementDate, ii.StatementLineDate) as StatementDate, ii.LineDescription, currencyConvert( ii.StmtAmt, ii.C_Currency_ID, ?, ?, null, ii.AD_Client_ID, ii.AD_Org_ID) as ConvertedStmtAmt " +
		" FROM I_BankStatement ii  " +
		" INNER JOIN C_BankAccount ba ON (ii.C_BankAccount_ID = ba.C_BankAccount_ID) " + 
		" INNER JOIN C_Currency c ON (ii.C_Currency_ID = c.C_Currency_ID)  " +
		" LEFT JOIN C_BPartner bp ON (ii.C_BPartner_ID = bp.C_BPartner_ID)  " +
    	" WHERE ii.I_IsImported = 'N' AND ii.C_BankAccount_ID = ? " );

    	
    	ResultItem[] items = getToConcilNotNullResultItems(0);
        if (items.length > 0) {
        	StringBuffer elems = new StringBuffer();
        	int i;
        	int colIdx = 0;
        	for (i = 0; i < items.length - 1; i++) {
        		elems.append((items[i].getItem(colIdx)).toString());
        		elems.append(",");
        	}
        	elems.append((items[i].getItem(colIdx)).toString());
        	
        	sql.append(" AND ii.I_BankStatement_ID NOT IN (").append(elems).append(") ");
        }
        
        // Si cambias el criterio de ordenamiento hacelo tambien en sortComparator
        sql.append(" ORDER BY ii.I_BankStatement_ID ");
        
//   	 Atencion: Se espera que el primer parametro sea Timestamp y el segundo int
    	
    	// executeQuery( sql.toString(), importedData, m_statementDate, m_bankAccountId );
        
        try {
    		PreparedStatement pstmt = DB.prepareStatement( sql.toString() );
    		
    		pstmt.setInt(1, m_mainCurrencyID);
    		pstmt.setTimestamp(2, m_statementDate);
    		pstmt.setInt(3, m_bankAccountId);
    		
    		ResultSet rs = pstmt.executeQuery();
    		
    		while(rs.next()) {
    			importedData.add(VModelHelper.ResultItemFactory(rs));
            }
    	} catch (Exception e) {
    		log.log( Level.SEVERE, sql.toString(), e );
    	}
    	
    	
   		m_lineas.fireChanged(clean);
    }
    
    protected void doResetLineasExistentes() {
    	
    	boolean clean = importedData == null;
    	
    	importedData = new Vector<ResultItem>();
    	m_lineas.setResultItem(importedData);

    	StringBuffer sql = new StringBuffer( "SELECT bsl.C_BankStatementLine_ID, bsl.dateacct, bsl.Description, currencyConvert( bsl.StmtAmt, bsl.C_Currency_ID, ?, ?, null, bsl.AD_Client_ID, bsl.AD_Org_ID) " +  
			" FROM C_BankStatementLine bsl  " + 
			" INNER JOIN C_BankStatement bs ON (bs.C_BankStatement_ID = bsl.C_BankStatement_ID) " + 
			" INNER JOIN C_BankAccount ba ON (bs.C_BankAccount_ID = ba.C_BankAccount_ID)  " + 
			" INNER JOIN C_Currency c ON (bsl.C_Currency_ID = c.C_Currency_ID)  " + 
			" WHERE " /* c_bankaccount_id = ? AND */ + " bs.C_BankStatement_ID = ? AND " +
			"bsl.isReconciled='N' AND" +
			// Para que solo me devuelva los que no estan Conciliados
			"  NOT EXISTS (SELECT * FROM c_bankstatline_reconcil rec1 WHERE rec1.IsReconciled = 'Y' AND rec1.C_BankStatementLine_ID = bsl.C_BankStatementLine_ID)  " );

    	
    	ResultItem[] items = getToConcilNotNullResultItems(0);
        if (items.length > 0) {
        	StringBuffer elems = new StringBuffer();
        	int i;
        	int colIdx = 0;
        	for (i = 0; i < items.length - 1; i++) {
        		elems.append((items[i].getItem(colIdx)).toString());
        		elems.append(",");
        	}
        	elems.append((items[i].getItem(colIdx)).toString());
        	
        	// Para que no me devuelva los que ya estan en la tabla de Conciliacion
        	sql.append(" AND bsl.C_BankStatementLine_ID NOT IN (").append(elems).append(") ");
        }
        
        // Si cambias el criterio de ordenamiento hacelo tambien en sortComparator
        sql.append(" ORDER BY bsl.C_BankStatementLine_ID ");
        
//   	 Atencion: Se espera que el primer parametro sea Timestamp y el segundo int
    	
        log.config( "C_BankAccount_ID=" + m_bankAccountId );

    	try {
    		PreparedStatement pstmt = DB.prepareStatement( sql.toString() );
    		
    		pstmt.setInt(1, m_mainCurrencyID);
    		pstmt.setTimestamp(2, m_statementDate);
    		// pstmt.setInt(3, m_bankAccountId);
    		pstmt.setInt(3, m_bankStatement.getC_BankStatement_ID());
    		
    		ResultSet rs = pstmt.executeQuery();
    		
    		while(rs.next()) {
    			importedData.add(VModelHelper.ResultItemFactory(rs));
            }
    	} catch (Exception e) {
    		log.log( Level.SEVERE, sql.toString(), e );
    	}
    	
   		m_lineas.fireChanged(clean);
    }
    
    protected void doResetBoletas() {
        boolean clean = payData == null;
        
        payData = new Vector<ResultItem>();
        m_pagos.setResultItem(payData);

        StringBuffer sql = new StringBuffer( "" );
        
        sql.append("  SELECT b.M_BoletaDeposito_ID, b.FechaDeposito, b.FechaAcreditacion, b.DocumentNo, currencyConvert(b.GrandTotal, b.C_Currency_ID, ?, ?, null, b.AD_Client_ID, b.AD_Org_ID) AS ConvertedGrandTotal " ); 
    	sql.append(" FROM M_BoletaDeposito b " );   
    	sql.append(" WHERE b.Processed='Y' AND b.IsReconciled <> 'Y' " );  
    	sql.append(" AND b.DocStatus IN ('CO','CL','RE') AND b.GrandTotal <> 0  AND b.C_BankAccount_ID = ? " );  
    	sql.append("  ");
        // sql.append("  ");
        
        ResultItem[] items = getToConcilNotNullResultItems(1);
        if (items.length > 0) {
        	StringBuffer elems = new StringBuffer();
        	int i;
        	int colIdx = 0;
        	for (i = 0; i < items.length - 1; i++) {
        		elems.append((items[i].getItem(colIdx)).toString());
        		elems.append(",");
        	}
        	elems.append((items[i].getItem(colIdx)).toString());
        	
        	sql.append(" AND b.C_BoletaDeposito_ID NOT IN (").append(elems).append(") ");
        }
        
        // Si cambias el criterio de ordenamiento hacelo tambien en sortComparator
        sql.append(" ORDER BY b.M_BoletaDeposito_ID ");
        
        // Atencion: Se espera que el primer parametro sea Timestamp y el segundo int

        // executeQuery( sql.toString(), payData, m_statementDate, m_bankAccountId );
        
    	try {
    		PreparedStatement pstmt = DB.prepareStatement( sql.toString() );
    		
    		pstmt.setInt(1, m_mainCurrencyID);
    		pstmt.setTimestamp(2, m_statementDate);
    		pstmt.setInt(3, m_bankAccountId);
    		
    		ResultSet rs = pstmt.executeQuery();
    		
    		while(rs.next()) {
    			payData.add(VModelHelper.ResultItemFactory(rs));
            }
    	} catch (Exception e) {
    		log.log( Level.SEVERE, sql.toString(), e );
    	}
        
       	m_pagos.fireChanged(clean);
    }
    
    protected void doResetPaymentsUnionBoletas() {
        boolean clean = payData == null;
        
        payData = new Vector<ResultItem>();
        m_pagos.setResultItem(payData);

        ConciliacionColumnsData pagosColumnsData = (ConciliacionColumnsData)m_pagos;
        
        StringBuffer sql  = new StringBuffer( "" );
      
		/**
		 * Incidencia 4250: No deben mostrarse las boletas de deposito en ventana de conciliación
		 * 					Es por este motivo que el query cambia, y se comenta el union con la
		 * 					tabla de boletas de depósito, visualizando solo los pagos relacionados
		 */        
        
        // -- PAGOS --
        
 //       sql.append(" ( ");		<- Comentado: Incidencia 4250
        
        sql.append( "SELECT 'P', p.C_Payment_ID, p.DocumentNo, p.DateTrx, currencyConvert(p.PayAmt, p.C_Currency_ID, ?, ?, null, p.AD_Client_ID, p.AD_Org_ID) AS ConvertedPayAmt, "    // #1
        + " bp.Name, p.CheckNo " + 
        " FROM C_BankAccount ba" + 
        " INNER JOIN C_Payment_v p ON (p.C_BankAccount_ID=ba.C_BankAccount_ID) " + 
        " INNER JOIN C_Payment ppr ON (p.C_Payment_ID = ppr.C_Payment_ID) " +
        " INNER JOIN C_Currency c ON (p.C_Currency_ID=c.C_Currency_ID)" + 
        " INNER JOIN C_BPartner bp ON (p.C_BPartner_ID=bp.C_BPartner_ID) " + 
        " WHERE p.Processed='Y' AND p.IsReconciled='N'" + " AND p.DocStatus IN ('CO','CL','RE') AND p.PayAmt <> 0 " +
        " AND (ppr.M_BoletaDeposito_ID = 0 OR ppr.M_BoletaDeposito_ID IS NULL) " +
        " AND p.C_BankAccount_ID = ? " );
        
        ResultItem[] itemsPag = getToConcilNotNullResultItems(1);
        if (itemsPag.length > 0) {
        	StringBuffer elems = new StringBuffer();
        	int i;
        	int colIdx = pagosColumnsData.getRowIdxID();
        	for (i = 0; i < itemsPag.length - 1; i++) {
        		elems.append((itemsPag[i].getItem(colIdx)).toString());
        		elems.append(",");
        	}
        	elems.append((itemsPag[i].getItem(colIdx)).toString());
        	
        	sql.append(" AND p.C_Payment_ID NOT IN (").append(elems).append(") ");
        }
        
        // Si cambias el criterio de ordenamiento hacelo tambien en sortComparator
        sql.append(" ORDER BY p.C_Payment_ID ");
        
        // -- UNION --

/*	Inicio Comentario: Incidencia 4250 
         
        sql.append(" ) UNION ( ");
        
        // -- BOLETAS --
        
        sql.append("  SELECT 'B', b.M_BoletaDeposito_ID, b.DocumentNo, b.FechaDeposito, currencyConvert(b.GrandTotal, b.C_Currency_ID, ?, ?, null, b.AD_Client_ID, b.AD_Org_ID) AS ConvertedGrandTotal " );
//        sql.append(" bp.Name, p.CheckNo ");
        sql.append(" ,'' , '' ");
    	sql.append(" FROM M_BoletaDeposito b " );   
//    	sql.append(" INNER JOIN C_BPartner bp ON (b.C_BPartner_ID=bp.C_BPartner_ID) ");
    	sql.append(" WHERE b.Processed='Y' AND b.IsReconciled <> 'Y' " );  
    	sql.append(" AND b.DocStatus IN ('CO','CL','RE') AND b.GrandTotal <> 0  AND b.C_BankAccount_ID = ? " );  
    	//sql.append(" AND NOT EXISTS (SELECT * FROM C_BankStatementLine l WHERE b.M_BoletaDeposito_ID = l.M_BoletaDeposito_ID AND l.StmtAmt <> 0) " );   
		// sql.append("  ");
        // sql.append("  ");
          
        
        ResultItem[] itemsBol = getToConcilNotNullResultItems(1);
        if (itemsBol.length > 0) {
        	StringBuffer elems = new StringBuffer();
        	int i;
        	int colIdx = pagosColumnsData.getRowIdxID();
        	for (i = 0; i < itemsBol.length - 1; i++) {
        		elems.append((itemsBol[i].getItem(colIdx)).toString());
        		elems.append(",");
        	}
        	elems.append((itemsBol[i].getItem(colIdx)).toString());
        	
        	sql.append(" AND b.M_BoletaDeposito_ID NOT IN (").append(elems).append(") ");
        }
        
        // Si cambias el criterio de ordenamiento hacelo tambien en sortComparator
        sql.append(" ORDER BY b.M_BoletaDeposito_ID ");
        
 		sql.append(" ) ");

Fin Comentario: Incidencia 4250 */         
        
        
        // Atencion: Se espera que el primer parametro sea Timestamp y el segundo int

        // executeQuery( sql.toString(), payData, m_statementDate, m_bankAccountId );
        
    	try {
    		PreparedStatement pstmt = DB.prepareStatement( sql.toString() );

    		// Pagos
    		
    		pstmt.setInt(1, m_mainCurrencyID);
    		pstmt.setTimestamp(2, m_statementDate);
    		pstmt.setInt(3, m_bankAccountId);


    		// Boletas
/* Comentado: Incidencia 4250	
    		pstmt.setInt(4, m_mainCurrencyID);
    		pstmt.setTimestamp(5, m_statementDate);
    		pstmt.setInt(6, m_bankAccountId);
*/
    		ResultSet rs = pstmt.executeQuery();
    		
    		while(rs.next()) {
    			payData.add(VModelHelper.ResultItemFactory(rs));
            }
    	} catch (Exception e) {
    		log.log( Level.SEVERE, sql.toString(), e );
    	}
        
        m_pagos.fireChanged(clean);
    }
    
    public void resetLineas() {
    	if (m_modoLineas == MODOLINEAS_IMPORTADAS)
    		doResetImportedLines();
    	else if (m_modoLineas == MODOLINEAS_EXISTENTES)
    		doResetLineasExistentes();
    }
    
    public void resetPagos() {
    	if (m_modoPagos == MODOPAGOS_PAGOS)
    		doResetPayments();
    	else if (m_modoPagos == MODOPAGOS_BOLETAS)
    		doResetBoletas();
    	else if (m_modoPagos == MODOPAGOS_PAGOSUNIONBOLETAS)
    		doResetPaymentsUnionBoletas();
    		
    }
    
    private ResultItem[] getToConcilNotNullResultItems(int n) {
        
        Vector<ResultItem> items = new Vector<ResultItem>();
        HashSet<ResultItem> conj = new HashSet<ResultItem>();
        
        if (n < toConciliarData.size()) {
	        for (ResultItem x : toConciliarData.get(n))
	        	if (x != null && x.isValid() && !conj.contains(x)) { 
	        		conj.add(x);
	        		items.add(x);
	        	}
	    
	        return items.toArray(new ResultItem[items.size()]);
        }
        
        return new ResultItem[0];
    }
    
    private void getItems(int[] items, Vector<ResultItem> vec, Vector<ResultItem> data) {
    	/*
    	for (i=0; i<importedLinesItems.length; i++) 
    		importedLinesVector.add(importedData.get(importedLinesItems[i]));
    	
    	for (i=0; i<paymentsItems.length; i++)
    		paymentsVector.add(payData.get(paymentsItems[i]));
    	*/
    	
    	for (int i : items)
    		vec.add(data.get(i));
    }
    
    private void executeQuery(String sql, Vector<ResultItem> data, Timestamp statementDate, int bankAccountId)
    {
        log.config( "C_BankAccount_ID=" + bankAccountId );

    	try {
    		PreparedStatement pstmt = DB.prepareStatement( sql );
    		
    		pstmt.setTimestamp(1, statementDate);
    		pstmt.setInt(2, bankAccountId);
    		
    		ResultSet rs = pstmt.executeQuery();
    		
    		while(rs.next()) {
    			data.add(VModelHelper.ResultItemFactory(rs));
            }
    	} catch (Exception e) {
    		log.log( Level.SEVERE,sql,e );
    	}
    }
    
    private ResultItemTableModel instanciaLineasTableModel() 
    {
    	if (m_modoLineas == MODOLINEAS_IMPORTADAS)
			return new LineasImportadasTableModel();
		else if (m_modoLineas == MODOLINEAS_EXISTENTES)
			return new LineasExistentesTableModel();

    	return null; // asi explota por si alguien mete mal el dedo
    }
    
    private ResultItemTableModel instanciaPagosTableModel()
    {
    	if (m_modoPagos == MODOPAGOS_PAGOS)
    		return new PagosTableModel();
    	else if (m_modoPagos == MODOPAGOS_BOLETAS)
    		return new BoletasDepositoTableModel() ;
    	else if (m_modoPagos == MODOPAGOS_PAGOSUNIONBOLETAS)
    		return new PagoUnionBoletaTableModel();
    	
    	return null;
    }
    
    private void initToConciliarTableModel() {
    	if (toConciliarData.size() == 0) {
    		toConciliarData.add(new Vector<ResultItem>());
    		toConciliarData.add(new Vector<ResultItem>());
    		
    		Vector<ResultItemTableModel> cm = new Vector<ResultItemTableModel>();
    		
    		/*
    		 * Tengo que volver a preguntar el modo, y crear
    		 * otra instancia de los models
    		 *  
    		 */
    		
    		cm.add(instanciaLineasTableModel());
    		cm.add(instanciaPagosTableModel());
    		
    		m_toConciliar.setColsModels(cm);    		
    	}
    }
    
    public Object getPayID(int rowIdx) {
    	int colIdx = ((ConciliacionColumnsData)m_pagos).getRowIdxID();
    	return payData.get(rowIdx).getItem(colIdx);
    }

    public Object getLineaID(int rowIdx) {
    	int colIdx = ((ConciliacionColumnsData)m_lineas).getRowIdxID();
    	return importedData.get(rowIdx).getItem(colIdx);
    }

    
    
    //
    private static final CLogger log = CLogger.getCLogger(VConciliacionTableModel.class);
    
    // C_BankStatement_Reconcil
    private ToConciliarTableModel m_toConciliar = new ToConciliarTableModel();
    private Vector<Vector<ResultItem>> toConciliarData = new Vector<Vector<ResultItem>>(); // Imported Lines
    
    // I_BankStatement, C_BankStatement
    private ResultItemTableModel m_lineas = null;
    private Vector<ResultItem> importedData;
    
    // Payments, Bolets
    private ResultItemTableModel m_pagos = null;
    private Vector<ResultItem> payData;
    
    //
    private int m_mainCurrencyID;
    private int m_bankAccountId ;
    private MBankStatement m_bankStatement;
    private Timestamp m_statementDate;

	Properties m_ctx ;
	String m_trxName ;

    //
    private int m_modoLineas ;
    private int m_modoPagos ;

    //
    private Comparator<ResultItem> sortComparator = new Comparator<ResultItem>() {
		public int compare(ResultItem arg0, ResultItem arg1) {
			// Si cambiás el criterio de orden, hacelo tambien en los ORDER BY SQL
			return ((Integer)arg0.getItem(0)).compareTo((Integer)arg1.getItem(0));
		}
	};
}

