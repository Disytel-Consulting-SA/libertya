package org.openXpertya.process.afip;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;

import org.openXpertya.process.ProcessInfoParameter;
import org.openXpertya.process.SvrProcess;
import org.openXpertya.util.DB;

public class ExportCityCompras extends SvrProcess {

	public ExportCityCompras() {
		super();
		// TODO Auto-generated constructor stub
	}

   
	    private Timestamp date_from;
	    private Timestamp date_to;
	    private String transaction;
	    private String directorio="";
	    private int ad_org_id;
	    
		protected void prepare() {
			
			ProcessInfoParameter[] para = getParameter();
			 for( int i = 0;i < para.length;i++ ) {
		            log.fine( "prepare - " + para[ i ] );

		            String name = para[ i ].getParameterName();

		            if( para[ i ].getParameter() == null ) {
		                ;
		            } else if( name.equalsIgnoreCase( "DateFrom" )) {
		                date_from = ( Timestamp )para[ i ].getParameter();
		            } else if( name.equalsIgnoreCase( "DateTo" )) {
		                date_to = ( Timestamp )para[ i ].getParameter();
		            } else if( name.equalsIgnoreCase( "transactiontype" )) {
		            	transaction = (String)para[ i ].getParameter();
		            } else if( name.equalsIgnoreCase( "AD_Org_ID" )) {
		            	ad_org_id = para[ i ].getParameterAsInt();	            	
		            } else if( name.equalsIgnoreCase( "Directorio" )) {
		            	directorio = (String)para[ i ].getParameter();
		            }  else {
		                log.log( Level.SEVERE,"prepare - Unknown Parameter: " + name );
		            }
		        }
			// veo que no falten los parametros !!!!!!
			
		}
		
		/* (non-Javadoc)
		 * @see org.openXpertya.process.SvrProcess#doIt()
		 */
		protected String doIt() throws java.lang.Exception {
			
			//Control del directorio de destino
			File targetDir = new File (directorio);
			if (!targetDir.exists())
					targetDir.mkdir();
			
			String filename = directorio + "/citi_compras_agrupado_" + getDate() + ".txt";
			String result ="";
			String lineSeparator = System.getProperty("line.separator");
	        String sql = null;
	        FileWriter fw=null;
	        PreparedStatement pstmt = null;
	        ResultSet rs = null;
	 		try
	 		{
	 			long inicia = System.currentTimeMillis();
	 			
	 			//Agrupados por ventas menores a 500$
	 			sql = getSqlComprasAgrupado();	        	         	 		
	 			pstmt = DB.prepareStatement(sql);			
	 			pstmt.setTimestamp(1,date_from);
	 			pstmt.setTimestamp(2,date_to);
	 			pstmt.setInt(3, getAD_Client_ID());	 			
	 			if (ad_org_id!=0)
	 				pstmt.setInt(4,ad_org_id);
	 			rs = pstmt.executeQuery();
	 			
	 			fw = new FileWriter(filename);
	 			int agrupado = putRsInFile(fw,rs);
	 			fw.close();
	 			
	 			
	 			
	 			// Detalle de ventas mayores a 500$
	 			sql = getSqlComprasDetalle();	        	         	 		
	 			pstmt = DB.prepareStatement(sql);			
	 			pstmt.setTimestamp(1,date_from);
	 			pstmt.setTimestamp(2,date_to);
	 			pstmt.setInt(3, getAD_Client_ID());	 			
	 			if (ad_org_id!=0)
	 				pstmt.setInt(4,ad_org_id);
	 			rs = pstmt.executeQuery();
	 			
	 			filename = directorio + "/citi_compras_detalle_" +  getDate() + ".txt";
	 			fw = new FileWriter(filename);
	 			int detalle = putRsInFile(fw,rs);
	 			fw.close();
	 			 		
	 			
	 			long termina = System.currentTimeMillis();
	 			long segundos = (termina-inicia)/1000;
	 			result = "Registros agrupados generados:" + agrupado +  " Detalles generados:" + detalle +   " Tiempo empleado:" + segundos + " segs.";
	 			log.log(Level.SEVERE,"Generar libro IVA Tardo: "+segundos+" segundos");
	 			rs.close();
	 			pstmt.close();
	 			pstmt = null;
	 		}
	 		catch (Exception e)
	 		{ 
	 			log.saveError("Exportacion Citi Compras - Prepare", e);
	 			e.printStackTrace();
	 			
	 			try {
					if(fw != null)fw.close();
					if(pstmt != null)pstmt.close();
					if(rs != null)rs.close();					
				} catch (IOException e1) {
					result = "Error al generar los archivos Citi Compras " +lineSeparator + e.getLocalizedMessage();
					e1.printStackTrace();
				}
	 			
	 		}	
	 		return result;
		}		

		

		/*
		 * Retorna el dia actual en formato anio mes dia
		 */
	    private String getDate() {
	        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
	        Date date = new Date();
	        return dateFormat.format(date);
	    }
		
		
		/*
		 * Pasa el resultset a el archivo
		 * @Param
		 * FileWriter fw writer del archivo a escribir
		 * ResultSet rs  el resultset a pasar al archivo
		 */
		private int putRsInFile(FileWriter fw , ResultSet rs) throws IOException, SQLException
		{
			String lineSeparator = System.getProperty("line.separator");
		
			int cant=0;
			StringBuffer s=new StringBuffer();
 			while(rs.next())
 			{
 				s=new StringBuffer();
 				s.append(rs.getString(1));
 				s.append(rs.getString(2));
 				s.append(rs.getString(3));
 				s.append(rs.getString(4));
 				s.append(rs.getString(5));
 				s.append(rs.getString(6));
 				s.append(rs.getString(7));
 				s.append(rs.getString(8));
 				s.append(rs.getString(9));
 				s.append(lineSeparator);
 				fw.write(s.toString());
 				s=null;
 				cant++;
 				
 			}
		
			return cant;
				
		}
		
		/*
		 * Retorna el sql para obtener el city compras de operaciones 
		 * menores a $500. Estas se consolidan por punto de venta.
		 */
		private String getSqlComprasAgrupado()
		{
			StringBuffer sqlReal = new StringBuffer();
//			sqlReal.append("SELECT   '00' AS TIPO_COMP, -- VER DE DONDE SALEN LOS CODIGOS \n");
			sqlReal.append("SELECT   '00' AS TIPO_COMP, ");
			sqlReal.append("         LPAD(0,4,0) ");
			sqlReal.append("                  || lpad(0,12,0) ");
			sqlReal.append("                  || LPAD(0,8,0)    AS NUM_COMP, ");
			sqlReal.append("         To_char(c_invoice.dateacct,'DDMMYYYY')   AS FECHA   , ");
			sqlReal.append("        Lpad(Replace(c_bpartner.taxid,'-',''),11,0)  AS CUIT    , ");
//			sqlReal.append("         taxid  , ");
			sqlReal.append("         RPAD(c_bpartner.name,25,' ')                                           AS NOMBRE      , ");
			sqlReal.append("         LPAD(REPLACE(trim( TO_CHAR(SUM(taxamt),'999999999.99')),'.','' ),12,0) AS IMP_LIQ     , ");
			sqlReal.append("         RPAD(' ' , 11,' ')                                                     AS CUIT_VEND   , ");
			sqlReal.append("         RPAD(' ' , 25,' ')                                                     AS NOMBRE_VEND , ");
			sqlReal.append("         RPAD(' ' , 12,' ')                                                     AS IVA_COMISION, ");
			sqlReal.append("         TO_CHAR(c_invoice.dateacct,'MMYYYY')                                   AS FILTRO ");
			sqlReal.append(" FROM     c_invoice ");
			sqlReal.append(" INNER JOIN c_bpartner  ON (c_invoice.c_bpartner_id = c_bpartner.c_bpartner_id) ");
			sqlReal.append(" INNER JOIN ad_client  ON (c_invoice.ad_client_id  = ad_client.ad_client_id) ");
			sqlReal.append(" INNER JOIN c_invoicetax ON (c_invoice.c_invoice_id  = c_invoicetax.c_invoice_id) ");
			sqlReal.append(" INNER JOIN c_doctype ON (c_invoice.c_doctype_id  = c_doctype.c_doctype_id) ");
			sqlReal.append(" WHERE ( c_invoice.docstatus = 'CO' OR c_invoice.docstatus = 'CL' ) ");
			sqlReal.append("     AND c_doctype.isfiscaldocument   ='Y'  and  docbasetype in ('ARI','API','ARC') and signo_issotrx=-1 ");
			sqlReal.append("     AND c_invoice.IsSOTrx    ='N' ");
			sqlReal.append("     AND c_invoice.isactive   = 'Y' ");
			sqlReal.append("     AND c_invoicetax.taxamt <= 500 ");
			sqlReal.append("     AND c_invoicetax.taxamt  > 0 ");
			sqlReal.append(" 	 AND c_invoice.dateacct between ? and ? ");
			sqlReal.append(" 	 AND c_invoice.ad_client_id=? ");
			if(ad_org_id != 0)
				sqlReal.append(" 	 AND c_invoice.ad_org_id=? ");
			
			sqlReal.append(" GROUP BY c_bpartner.duns , ");
			sqlReal.append("         c_bpartner.taxid, ");
			sqlReal.append("         c_bpartner.name , ");
			sqlReal.append("         TO_CHAR(c_invoice.dateacct, 'MMYYYY') , To_char(c_invoice.dateacct,'DDMMYYYY') ");
			sqlReal.append(" ORDER BY NUM_COMP ASC");
			return sqlReal.toString();
		}
		
		/*
		 * Retorna el query para obtener las operaciones mayores a $500. 
		 * Se retorna el detalle de las mismas, segun disposicion fiscal
		 */
		private String getSqlComprasDetalle()
		{
			StringBuffer  sqlReal = new StringBuffer();
			sqlReal.append("SELECT   '01' AS tipo_comp,  ");
			sqlReal.append("         Lpad(Max(c_invoice.puntodeventa),4,0) ||Lpad(0,12,0) ||Lpad(Max(c_invoice.numerocomprobante),8,0) AS num_comp, ");
			sqlReal.append("         To_char(Max(c_invoice.dateacct),'DDMMYYYY') AS fecha, ");
			sqlReal.append("         Lpad(Replace(c_bpartner.taxid,'-',''),11,0)   AS cuit, ");
			sqlReal.append("         Rpad(Max(c_bpartner.NAME),25,' ') AS nombre, ");
			sqlReal.append("         Lpad(Replace(Trim(To_char(Sum(taxamt),'999999999.99')),'.',''),12,0) AS imp_liq, ");
			sqlReal.append("         Rpad(0,11,0)      AS cuit_vend, ");
			sqlReal.append("         Rpad(' ',25,' ')  AS nombre_vend, ");
			sqlReal.append("         Rpad(0,12,0)      AS iva_comision ");
			sqlReal.append(" FROM     c_invoice ");
			sqlReal.append(" INNER JOIN c_bpartner  ON (c_invoice.c_bpartner_id = c_bpartner.c_bpartner_id) ");
			sqlReal.append(" INNER JOIN ad_client  ON (c_invoice.ad_client_id  = ad_client.ad_client_id) ");
			sqlReal.append(" INNER JOIN c_invoicetax ON (c_invoice.c_invoice_id  = c_invoicetax.c_invoice_id) ");
			sqlReal.append(" INNER JOIN c_doctype ON (c_invoice.c_doctype_id  = c_doctype.c_doctype_id) ");
			sqlReal.append(" WHERE  (c_invoice.docstatus = 'CO' OR c_invoice.docstatus = 'CL') ");
			sqlReal.append("         AND c_doctype.isfiscaldocument = 'Y' and  docbasetype in ('ARI','API','ARC') and signo_issotrx=-1  ");
			sqlReal.append("         AND c_invoice.issotrx = 'N' ");
			sqlReal.append("         AND c_invoice.isactive = 'Y' ");
			sqlReal.append("         AND c_invoicetax.taxamt > 500 ");
			sqlReal.append("         AND Length(c_invoice.numerocomprobante) > 0 ");			
			sqlReal.append(" 	 	  AND c_invoice.dateacct between ? and ? ");
			sqlReal.append(" 	      AND c_invoice.ad_client_id=? ");
			if(ad_org_id != 0)
				sqlReal.append(" 	 AND c_invoice.ad_org_id=? ");			
			sqlReal.append(" GROUP BY Lpad(c_invoice.puntodeventa,4,0) ||Lpad(0,12,0) ||Lpad(c_invoice.numerocomprobante,8,0), Replace(c_bpartner.taxid,'-','') ");
			sqlReal.append(" ORDER BY num_comp ASC");
						
			return sqlReal.toString();

		}

}
