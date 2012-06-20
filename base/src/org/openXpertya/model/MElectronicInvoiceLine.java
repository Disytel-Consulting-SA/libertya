package org.openXpertya.model;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;

public class MElectronicInvoiceLine extends X_E_ElectronicInvoiceLine {
	
	public MElectronicInvoiceLine(Properties ctx, int E_ElectronicInvoice_ID,	String trxName) {
		super(ctx, E_ElectronicInvoice_ID, trxName);
	}
	
	public MElectronicInvoiceLine(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}

	public void createLine(MInvoiceLine line, int hdr_id, String dateVoid) throws Exception{
		setUnidad_Medida(getRefTablaUnidadMedida(line.getC_UOM_ID())); 			// Según tabla 9
		setLineNetAmt(line.getLineNetAmt());
		setImporte_Bonificacion(BigDecimal.ZERO);										// No se usa
		setLineTotalAmt(line.getLineTotalAmt());
		setRate(getLineRate(line.getC_Tax_ID()));
		setIsTaxExempt(getLineIsTaxExempt(line.getC_Tax_ID()));
		if (line.getDescription() != null)
			setDiseno_Libre(line.getDescription().substring(0, line.getDescription().length()));
		if (dateVoid != null)
			if (dateVoid.equalsIgnoreCase(""))
				setIndica_Anulacion("A");
		setE_ElectronicInvoice_ID(hdr_id);
		setQtyInvoiced(line.getQtyInvoiced());

		if (!save()){
			throw new Exception(CLogger.retrieveErrorAsString());
		} 
	}
	
	private String getRefTablaUnidadMedida(int id) throws SQLException{
		MUOM uom = new MUOM(getCtx(), id, get_TrxName());
		String	sql = " SELECT * FROM E_ElectronicInvoiceRef Where tabla_ref = '"+FiscalDocumentExport.TABLAREF_UnidadesMedida +"' and clave_busqueda = '"+uom.getUOMSymbol()+"'";
		PreparedStatement pstmt = DB.prepareStatement(sql, get_TrxName());
		ResultSet rs = pstmt.executeQuery();
		
		if (rs.next()){
			X_E_ElectronicInvoiceRef ref = new X_E_ElectronicInvoiceRef(getCtx(), rs, get_TrxName());
			return ref.getCodigo();
		}
		
		return null;
	}
	
	private BigDecimal getLineRate(int id){
		MTax tax = new MTax(getCtx(),id, get_TrxName());
		return tax.getRate();
	}
	
	private boolean getLineIsTaxExempt(int id){
		MTax tax = new MTax(getCtx(),id, get_TrxName());
		return tax.isTaxExempt();
	}
}
