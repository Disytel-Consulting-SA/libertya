package org.openXpertya.process;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import org.openXpertya.model.FiscalDocumentExport;
import org.openXpertya.model.MInvoice;
import org.openXpertya.model.MPeriod;
import org.openXpertya.util.DB;

public class ExportElectronicInvoiceToTables extends SvrProcess {

	private int p_C_Period_ID;
	
	@Override
	protected String doIt() throws Exception {
		
		// Recupero el periodo para calcular el mes completo
		MPeriod periodo = new MPeriod(getCtx(), p_C_Period_ID, get_TrxName());
		
		// Borro los datos para ese período de la tabla e_electronicinvoice y e_electronicinvoiceline
		String	sqlDeleteLines	= " delete from e_electronicinvoiceline where e_electronicinvoice_id in " +
							  "		(select e_electronicinvoice_id from e_electronicinvoice " +
							  " 	 where date_trunc('day',dateinvoiced) >= '"+periodo.getStartDate()+"' and date_trunc('day',dateinvoiced) <= '"+periodo.getEndDate()+"' and (AD_Client_ID = "+ getAD_Client_ID() +") and (createdby = "+ getAD_User_ID() + ")) ";
        DB.executeUpdate(sqlDeleteLines, get_TrxName());
		
        String	sqlDelete	= " delete from e_electronicinvoice where date_trunc('day',dateinvoiced) >= '"+periodo.getStartDate()+"' and date_trunc('day',dateinvoiced) <= '"+periodo.getEndDate()+"' and (AD_Client_ID = "+ getAD_Client_ID() +") and (createdby = "+ getAD_User_ID() +") ";
        DB.executeUpdate(sqlDelete, get_TrxName());
               
		// Consulta con todos los datos
		StringBuffer sql = new StringBuffer();
		sql.append(" select distinct(i.c_invoice_id) from c_invoice i " +
				   " inner join C_Doctype d ON i.C_Doctype_id = d.C_Doctype_id " +
				   " left join e_electronicinvoice e on (i.c_invoice_id = e.c_invoice_id) " +
				   " where /*e.c_invoice_id is null " +
				   " and i.numerodedocumento is not null" +
				   " and*/ date_trunc('day',i.dateinvoiced) >= '"+periodo.getStartDate()+"' and date_trunc('day',i.dateinvoiced) <= '"+periodo.getEndDate()+"'" +
				   " and (docstatus = 'CO' or docstatus = 'CL' or docstatus = 'VO' or docstatus = 'RE') " +
				   " and (case when (d.isfiscal = 'Y') then (i.FiscalAlreadyPrinted = 'Y') else true end) " +
				   " and (i.AD_Client_ID = "+ getAD_Client_ID() +") " +
				   " and (d.isfiscaldocument = 'Y')");
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			pstmt = DB.prepareStatement(sql.toString(), get_TrxName());
			rs = pstmt.executeQuery();
			// Exportación Resolución 1361
			FiscalDocumentExport createFD = new FiscalDocumentExport();
			while (rs.next()) {
				// Instancio la Factura a exportar
				MInvoice inv = new MInvoice(getCtx(), rs.getInt("C_Invoice_ID"), get_TrxName());
				if (inv.isFiscalAlreadyPrinted()){
					// Exporto la factura fiscal y sus lineas para satisfacer la resolución 1361 de duplicados electrónicos
					createFD.fiscalPrintingExport( getCtx(), inv );
				}
				else {
					// Exporto la factura no fiscal y sus lineas para satisfacer la resolución 1361 de duplicados electrónicos
					createFD.noFiscalPrintingExport( getCtx(), inv );
				}
			}
		} catch (SQLException e) {
			log.log(Level.SEVERE, "Export To Table E_ElectronicInvoice error", e);
			throw new Exception("Export Electronic Invoice To Table Error",e);
		}
		
//		// Se actualizan los montos de las facturas anuladas a 0
//		String	sqlUpdateInvoiced	= " UPDATE E_ElectronicInvoice " +
//				" SET grandtotal= 0.00, taxbaseamt = 0.00, totallines = 0.00, taxamt = 0.00, rni = 0.00, operacionesexentas = 0.00, importepercepciones = 0.00, percepcionesiibb = 0.00, impuestosmunicipales = 0.00, impuestosinternos = 0.00, transporte = 0.00" +
//				" WHERE datevoid IS NOT NULL";
//        DB.executeUpdate(sqlUpdateInvoiced, get_TrxName());
		return "Generalización finalizada correctamente";
	}

	@Override
	protected void prepare() {
		ProcessInfoParameter[] para = getParameter();

        for( int i = 0;i < para.length;i++ ) {
            String name = para[ i ].getParameterName();

            if( para[ i ].getParameter() == null ) ;
            else if( name.equalsIgnoreCase( "C_Period_ID" )) {
            	p_C_Period_ID = ((BigDecimal)para[ i ].getParameter()).intValue();
            } else {
                log.log( Level.SEVERE,"prepare - Unknown Parameter: " + name );
            }
        }
	}

}
