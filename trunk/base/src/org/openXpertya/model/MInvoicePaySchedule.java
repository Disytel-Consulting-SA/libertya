/*
 *    El contenido de este fichero está sujeto a la  Licencia Pública openXpertya versión 1.1 (LPO)
 * en tanto en cuanto forme parte íntegra del total del producto denominado:  openXpertya, solución 
 * empresarial global , y siempre según los términos de dicha licencia LPO.
 *    Una copia  íntegra de dicha  licencia está incluida con todas  las fuentes del producto.
 *    Partes del código son CopyRight (c) 2002-2007 de Ingeniería Informática Integrada S.L., otras 
 * partes son  CopyRight (c) 2002-2007 de  Consultoría y  Soporte en  Redes y  Tecnologías  de  la
 * Información S.L.,  otras partes son  adaptadas, ampliadas,  traducidas, revisadas  y/o mejoradas
 * a partir de código original de  terceros, recogidos en el  ADDENDUM  A, sección 3 (A.3) de dicha
 * licencia  LPO,  y si dicho código es extraido como parte del total del producto, estará sujeto a
 * su respectiva licencia original.  
 *     Más información en http://www.openxpertya.org/ayuda/Licencia.html
 */



package org.openXpertya.model;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.TimeUtil;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MInvoicePaySchedule extends X_C_InvoicePaySchedule {

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param C_Invoice_ID
     * @param C_InvoicePaySchedule_ID
     * @param trxName
     *
     * @return
     */
	//Añadido por ConSerTi
	public static MInvoicePaySchedule[] getInvoicePaySchedule(Properties ctx, 
			MRemesaLine line)
		{
			ArrayList list = new ArrayList();
			PreparedStatement pstmt = null;
			s_log.fine("En getInvoicePayschedule con remesa="+line.get_Value("C_Remesa_ID"));
			if (line != null)
			{
				String sql = "SELECT * FROM C_InvoicePaySchedule ips ";
				sql += " WHERE ips.C_Invoice_ID in (SELECT cin.C_Invoice_ID FROM C_Invoice cin WHERE cin.C_BPartner_ID=?)";
				sql += " AND ips.Processed='N'";
				sql += " AND ips.DueDate<(?)";
				sql += " AND ips.C_Remesa_ID="+line.get_Value("C_Remesa_ID");
				sql += " ORDER BY DueDate";
				/*String sql = "SELECT * FROM C_InvoicePaySchedule ips ";
					sql += " WHERE ips.C_Invoice_ID in (SELECT cin.C_Invoice_ID FROM C_Invoice cin WHERE cin.C_BPartner_ID=?)";
					//Esta parte de la sentencia no se cambia nunca en la base de datos
					//	sql += " AND ips.IsSelected='Y'";
					sql += " AND ips.Processed='N'";
					sql += " AND ips.DueDate<(?)";
					sql += " ORDER BY DueDate";*/
					//
					
				try
				{
					s_log.fine("Esta es la SQL de la discordia1:"+sql);
					
					pstmt = DB.prepareStatement(sql);
					pstmt.setInt(1, line.getC_BPartner_ID());
					 
					MRemesa remesa = new MRemesa(Env.getCtx(), line.getC_Remesa_ID(),null);
					pstmt.setTimestamp(2, remesa.getExecuteDate());
					s_log.fine("Esta es la SQL de la discordia2:"+pstmt.toString());
					ResultSet rs = pstmt.executeQuery();
					while (rs.next())
						list.add (new MInvoicePaySchedule(ctx, rs, null));
					rs.close();
					pstmt.close();
					pstmt = null;
				}
				catch (Exception e)
				{
					s_log.saveError("getInvoicePaySchedule", e);
				}
				try
				{
					if (pstmt != null)
						pstmt.close();
					pstmt = null;
				}
				catch (Exception e)
				{
					pstmt = null;
				}
			}	
			
			MInvoicePaySchedule[] retValue = new MInvoicePaySchedule[list.size()];
			list.toArray(retValue);
			return retValue;
		}	//	getSchedule
	//Fin Añadido
	
    public static MInvoicePaySchedule[] getInvoicePaySchedule( Properties ctx,int C_Invoice_ID,int C_InvoicePaySchedule_ID,String trxName ) {
        String sql = "SELECT * FROM C_InvoicePaySchedule ips ";

        if( C_Invoice_ID != 0 ) {
            sql += "WHERE C_Invoice_ID=? ";
        } else {
            sql += "WHERE EXISTS (SELECT * FROM C_InvoicePaySchedule x" + " WHERE x.C_InvoicePaySchedule_ID=? AND ips.C_Invoice_ID=x.C_Invoice_ID) ";
        }

        sql += "ORDER BY DueDate";

        //

        ArrayList         list  = new ArrayList();
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql,trxName );

            if( C_Invoice_ID != 0 ) {
                pstmt.setInt( 1,C_Invoice_ID );
            } else {
                pstmt.setInt( 1,C_InvoicePaySchedule_ID );
            }

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                list.add( new MInvoicePaySchedule( ctx,rs,trxName ));
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            s_log.log( Level.SEVERE,"getInvoicePaySchedule",e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        MInvoicePaySchedule[] retValue = new MInvoicePaySchedule[ list.size()];

        list.toArray( retValue );

        return retValue;
    }    // getSchedule

    /** Descripción de Campos */

    private static CLogger s_log = CLogger.getCLogger( MInvoicePaySchedule.class );

    /** Descripción de Campos */

    private final static BigDecimal HUNDRED = new BigDecimal( 100 );

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param C_InvoicePaySchedule_ID
     * @param trxName
     */

    public MInvoicePaySchedule( Properties ctx,int C_InvoicePaySchedule_ID,String trxName ) {
        super( ctx,C_InvoicePaySchedule_ID,trxName );

        if( C_InvoicePaySchedule_ID == 0 ) {

            // setC_Invoice_ID (0);
            // setDiscountAmt (Env.ZERO);
            // setDiscountDate (new Timestamp(System.currentTimeMillis()));
            // setDueAmt (Env.ZERO);
            // setDueDate (new Timestamp(System.currentTimeMillis()));

            setIsValid( false );
        }
    }    // MInvoicePaySchedule

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MInvoicePaySchedule( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MInvoicePaySchedule

    /**
     * Constructor de la clase ...
     *
     *
     * @param invoice
     * @param paySchedule
     */

    public MInvoicePaySchedule( MInvoice invoice,MPaySchedule paySchedule ) {
        super( invoice.getCtx(),0,invoice.get_TrxName());
        m_parent = invoice;
        setClientOrg( invoice );
        setC_Invoice_ID( invoice.getC_Invoice_ID());
        setC_PaySchedule_ID( paySchedule.getC_PaySchedule_ID());
        
        

        // Amounts

        int scale = MCurrency.getStdPrecision( getCtx(),invoice.getC_Currency_ID());
        BigDecimal due = invoice.getGrandTotal();

        if( due.compareTo( Env.ZERO ) == 0 ) {
            setDueAmt( Env.ZERO );
            setDiscountAmt( Env.ZERO );
            setIsValid( false );
        } else {
            due = due.multiply( paySchedule.getPercentage()).divide( HUNDRED,scale,BigDecimal.ROUND_HALF_UP );
            setDueAmt( due );

            BigDecimal discount = due.multiply( paySchedule.getDiscount()).divide( HUNDRED,scale,BigDecimal.ROUND_HALF_UP );

            setDiscountAmt( discount );
            setIsValid( true );
        }
        //Dia fijo de pago
        MPaymentTerm TerminoDePago = new MPaymentTerm( getCtx(),paySchedule.getC_PaymentTerm_ID(),null );
        log.fine("El dia fijo d pago es el = "+TerminoDePago.get_Value("FixMonthDay")+", y el segundo es:"+TerminoDePago.get_Value("FixMonthDay2"));
        int dias=0;
		int dias2=0;
		if(TerminoDePago.get_Value("FixMonthDay")!=null){
			dias=Integer.valueOf(String.valueOf(TerminoDePago.get_Value("FixMonthDay"))).intValue();
        }
		if(TerminoDePago.get_Value("FixMonthDay2")!=null){
		
			dias2=Integer.valueOf(String.valueOf(TerminoDePago.get_Value("FixMonthDay2"))).intValue();
        }
		MBPartner partneraux = new MBPartner(getCtx(),invoice.getC_BPartner_ID(),null);
        
        //Dates
        Timestamp dueDate = TimeUtil.addDays( invoice.getDateInvoiced(),paySchedule.getNetDays());
        log.fine("Lo que usa para calcular la fecha al principio es dateinvoiced="+invoice.getDateInvoiced()+", y payschedule="+paySchedule.getNetDays());
        log.fine("La fecha de factura inicial que calcula es = "+ dueDate);
        Timestamp s1=partneraux.getStartHolidays();
    	Timestamp e1=partneraux.getEndHolidays();
    	Timestamp s2=partneraux.getStartHolidays2();
    	Timestamp e2=partneraux.getEndHolidays2();
        //Si hay dia fijo de pago, calcular las fechas de pago
        if (TerminoDePago.isDueFixed())
        {	
        	//Calculamos fecha de pago. Ver la que saca automaticamente. Cogemos el dia.	
        	int dia= Integer.valueOf(String.valueOf(dueDate).substring(8,10)).intValue();
        	if (dia>dias && dia>dias2)
        	{
        		
        		log.fine("Tiene que se el dia 25 del mes siguiente de la fecha de factura");
        		//Calcular el ultimo dia del mes de la fecha de la factuar.
        		Timestamp ultimodiames= TimeUtil.getMonthLastDay(dueDate);
        		log.fine("El ultimo dia del mes de la fecha de factura... es = "+ ultimodiames);
        		//Calculamos la nueva fecha. Ultimo dia del mes + dia fijo de pago.
        		dueDate = TimeUtil.addDays( ultimodiames,dias);
        		
        		log.fine("La nueva fecha es =  "+ dueDate);
        	}else if(dia>dias && dia<=dias2){
        		log.fine("Entro en el caso en que vale el segundo dia de vencimiento");
//        		Tiene que se el "25", dia fijo de pago, de ese mes. Del dueDate.
        		//Cogemos el primer dia y le sumamos "25", el dia fijo de pago
        		int aux= Integer.valueOf(String.valueOf(dueDate).substring(8,10)).intValue();
        		Timestamp primerdia= TimeUtil.addDays(dueDate, -aux);
        		log.fine("Primer dia del mes par sumarle el 25 = "+ primerdia);
        		//Le sumamos el dia fijo de pago al primer dia del mes.
        		dueDate = TimeUtil.addDays(primerdia, dias2);
        		log.fine("La fecha en el mismo mes es= "+ dueDate);
        	}
        	else
        	{
        		//Tiene que se el "25", dia fijo de pago, de ese mes. Del dueDate.
        		//Cogemos el primer dia y le sumamos "25", el dia fijo de pago
        		int aux= Integer.valueOf(String.valueOf(dueDate).substring(8,10)).intValue();
        		Timestamp primerdia= TimeUtil.addDays(dueDate, -aux);
        		log.fine("Primer dia del mes par sumarle el 25 = "+ primerdia);
        		//Le sumamos el dia fijo de pago al primer dia del mes.
        		dueDate = TimeUtil.addDays(primerdia, dias);
        		log.fine("La fecha en el mismo mes es= "+ dueDate);
        	}
        	//Gestión de vacaciones
        	
        	Timestamp aux1,aux2,aux3,aux4;
        	if((s1!=null && e1!=null )||(s2!=null && e2!=null) ){
        		log.fine("Entra en el caso de vacaciones");
            	if(s1==null){
            		log.fine("Segundas vacaciones");
            		aux1=s2;
            		aux2=e2;
            		aux3=s2;
            		aux4=e2;
            	}else if(s2!=null){
            		log.fine("Dos vacaciones");
            		aux1=s1;
            		aux2=e1;
            		aux3=s2;
            		aux4=e2;
            	}else{
            		log.fine("Primeras vacaciones");
            		aux1=s1;
            		aux2=e1;
            		aux3=s1;
            		aux4=e1;
            	}
            	Timestamp auxi2=null;
            	String SQL = "SELECT max(duedate) from c_invoicepayschedule where c_invoice_id="+invoice.getC_Invoice_ID();
            	try {
                    PreparedStatement pstmt = DB.prepareStatement( SQL );                 
                    ResultSet rs = pstmt.executeQuery();
                    if( rs.next()) {
                    	auxi2=rs.getTimestamp(1);
                    }                   
                    rs.close();
                    pstmt.close();
            	} catch( SQLException e ) {
                    log.log( Level.SEVERE,"Error ",e );
                   
                }
            	log.fine("Antes de la comprobacion, auxi2="+auxi2+", y duedate="+dueDate);
        		if(((dueDate.after(aux1)&& dueDate.before(aux2))||(dueDate.after(aux3)&& dueDate.before(aux4)))||dueDate.equals(auxi2)){
            		 dia= Integer.valueOf(String.valueOf(dueDate).substring(8,10)).intValue();
            	//	if (dia>dias){
            			log.fine("(V)Tiene que se el dia 25 del mes siguiente de la fecha de factura");
            			Timestamp ultimodiames= TimeUtil.getMonthLastDay(dueDate);
            			log.fine("(V)El ultimo dia del mes de la fecha de factura... es = "+ ultimodiames);
            			dueDate = TimeUtil.addDays( ultimodiames,dias);
            			log.fine("(V)La nueva fecha es =  "+ dueDate);
            	//	}
            	/*	else{
              			int aux= Integer.valueOf(String.valueOf(dueDate).substring(8,10)).intValue();
              			Timestamp primerdia;
              			
            			primerdia= TimeUtil.addDays(dueDate, -aux);
            			
              			log.fine("(V)Primer dia del mes par sumarle el 25 = "+ primerdia);
    	        		dueDate = TimeUtil.addDays(primerdia, dias);
    	        		log.fine("(V)La fecha en el mismo mes es= "+ dueDate);
            		}*/
            	}
            }
        }else{
        	if((s1!=null && e1!=null )||(s2!=null && e2!=null) ){
        		log.fine("No dia fijo de pago");
        		Timestamp aux1,aux2,aux3,aux4;
        		int caso=0;
            	if(s1==null){
            		log.fine("Segundas vacaciones");
            		aux1=s2;
            		aux2=e2;
            		aux3=s2;
            		aux4=e2;
            		caso=1;
            	}else if(s2!=null){
            		log.fine("Dos vacaciones");
            		aux1=s1;
            		aux2=e1;
            		aux3=s2;
            		aux4=e2;
            		caso=2;
            	}else{
            		log.fine("Primeras vacaciones");
            		aux1=s1;
            		aux2=e1;
            		aux3=s1;
            		aux4=e1;
            		caso=3;
            	}
            	if((dueDate.after(aux1)&& dueDate.before(aux2))||(dueDate.after(aux3)&& dueDate.before(aux4))){
            		if (caso==1){	
            			dueDate = TimeUtil.addDays(partneraux.getEndHolidays2(), 1); 		
            		}else if(caso==3){
            			dueDate = TimeUtil.addDays(partneraux.getEndHolidays(), 1);
            		}else{
            			if(dueDate.after(aux1)&& dueDate.before(aux2)){
            				dueDate = TimeUtil.addDays(partneraux.getEndHolidays(), 1);
            			}else{
            				dueDate = TimeUtil.addDays(partneraux.getEndHolidays2(), 1);
            			}
            		}
            	}
        	}
        }
        
        //Fin calcular fechas de pago.        
        setDueDate( dueDate );
        Timestamp discountDate = TimeUtil.addDays( invoice.getDateInvoiced(),paySchedule.getDiscountDays());
        setDiscountDate( discountDate );
        
    }    // MInvoicePaySchedule

    /** Descripción de Campos */

    private MInvoice m_parent = null;

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MInvoice getParent() {
        if( m_parent == null ) {
            m_parent = new MInvoice( getCtx(),getC_Invoice_ID(),get_TrxName());
        }

        return m_parent;
    }    // getParent

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getFormaDePago() {
        MPaySchedule ProgramaDePago = new MPaySchedule( getCtx(),getC_PaySchedule_ID(),null );
        MPaymentTerm TerminoDePago = new MPaymentTerm( getCtx(),ProgramaDePago.getC_PaymentTerm_ID(),null );
        String nombre = TerminoDePago.getName();

        return nombre;
    }    // getFormaDePago

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getFactura_N() {
        MInvoice Factura   = new MInvoice( getCtx(),getC_Invoice_ID(),null );
        String   Factura_N = Factura.getDocumentNo();

        return Factura_N;
    }    // getFactura_N

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public double getImporteTotal() {
        double importe = ( getDueAmt()).doubleValue();

        return importe;
    }    // getImporteTotal

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isVencido() {
    	return isVencido(Env.getDate());
    }

	/**
	 * Determina si la fecha parámetro se encuentra después de la fecha de
	 * vencimiento
	 * 
	 * @param compareDate
	 *            fecha de comparación
	 * @return true si la fecha parámetro se encuentra después a la fecha de
	 *         vencimiento
	 */
    public boolean isVencido(Timestamp compareDate) {
		// Lo paso a date y después a timestamp para solo verificar la fecha y
		// no la hora
        java.util.Date date = new java.util.Date(compareDate.getTime());
        // fecha parámetro es posterior a la de vencimiento
        return date.after( getDueDate());
    }
    
	/**
	 * Determina si la fecha parámetro se encuentra después de la fecha de
	 * vencimiento
	 * 
	 * @param compareDate
	 *            fecha de comparación
	 * @return true si la fecha parámetro se encuentra después a la fecha de
	 *         vencimiento
	 */
    public boolean isVencido(Date compareDate) {
        // fecha parámetro es posterior a la de vencimiento
        return compareDate.after( getDueDate());
    }

	/**
	 * Determina la diferencia de días que existen entre la fecha parámetro y la
	 * fecha de vencimiento. Si la cantidad de días es positiva entonces la
	 * fecha parámetro es mayor al vencimiento, si es 0 estamos en la fecha de
	 * vencimiento y si es negativa la fecha de vencimiento es mayor a la fecha
	 * parámetro.
	 * 
	 * @param compareDate
	 *            fecha de comparación
	 * @return cantidad de días de diferencia entre la fecha parámetro y la
	 *         fecha de vencimiento
	 */
    public int diffDueDays(Date compareDate){
		return TimeUtil.getDiffDays(getDueDate(), new Timestamp(compareDate.getTime()));
    }
    
    /**
     * Descripción de Método
     *
     *
     * @param parent
     */

    public void setParent( MInvoice parent ) {
        m_parent = parent;
    }    // setParent

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer( "Vencimientos[" );

        sb.append( getID()).append( "-Fecha=" + getDueDate() + "/" + getDueAmt()).append( ";Descuento=" ).append( getDiscountDate() + "/" + getDiscountAmt()).append( "]" );

        return sb.toString();
    }    // toString

    /**
     * Descripción de Método
     *
     *
     * @param newRecord
     *
     * @return
     */

    protected boolean beforeSave( boolean newRecord ) {
        if( is_ValueChanged( "DueAmt" )) {
            log.fine( "beforeSave" );
            setIsValid( false );
        }

        return true;
    }    // beforeSave

    /**
     * Descripción de Método
     *
     *
     * @param newRecord
     * @param success
     *
     * @return
     */
//Añadido por ConSerTi
    public static MInvoicePaySchedule[] getIPSRemesados(Properties ctx, 
    		MRemesaLine line)
    	{
    		ArrayList list = new ArrayList();
    		PreparedStatement pstmt = null;

    		if (line != null)
    		{
    			String sql = "SELECT * FROM C_InvoicePaySchedule ips ";
    				sql += " WHERE ips.C_Invoice_ID in (SELECT cin.C_Invoice_ID FROM C_Invoice cin WHERE cin.C_BPartner_ID=?)";
    				//Comentado ya que no modifica ese campo nunca
    				//sql += " AND ips.IsSelected='Y'";
    				sql += " AND ips.Processed='N'";
    				//sql += " AND ips.DueDate<(?)";
    				sql += " AND ips.C_Remesa_ID=?";
    				sql += " ORDER BY DueDate";
    				//
    			try
    			{
    				pstmt = DB.prepareStatement(sql);
    				pstmt.setInt(1, line.getC_BPartner_ID());
    				
    				MRemesa remesa = new MRemesa(Env.getCtx(), line.getC_Remesa_ID(),null);
    				//pstmt.setTimestamp(2, remesa.getExecuteDate());
    				pstmt.setInt(2, remesa.getC_Remesa_ID());
    				
    				ResultSet rs = pstmt.executeQuery();
    				while (rs.next())
    					list.add (new MInvoicePaySchedule(ctx, rs,null));
    				rs.close();
    				pstmt.close();
    				pstmt = null;
    			}
    			catch (Exception e)
    			{
    				s_log.saveError("getInvoicePaySchedule", e);
    			}
    			try
    			{
    				if (pstmt != null)
    					pstmt.close();
    				pstmt = null;
    			}
    			catch (Exception e)
    			{
    				pstmt = null;
    			}
    		}	
    		
    		MInvoicePaySchedule[] retValue = new MInvoicePaySchedule[list.size()];
    		list.toArray(retValue);
    		return retValue;
    	}	//	getSchedule
    
//Fin añadido
    protected boolean afterSave( boolean newRecord,boolean success ) {
        if( is_ValueChanged( "DueAmt" )) {
            log.fine( "afterSave" );
            getParent();
            m_parent.validatePaySchedule();
            m_parent.setSkipApplyPaymentTerm(true);
            m_parent.save();
        }

        return success;
    }    // afterSave
    
    public BigDecimal getOpenAmount() {
    	MInvoice invoice = new MInvoice(getCtx(), getC_Invoice_ID(), get_TrxName()); 
    	String sql = "SELECT invoiceopen(?, ?, ?, null) as openAmount";
		PreparedStatement ps = null;
		ResultSet rs = null;
		BigDecimal openAmount = null;
		try {
			ps = DB.prepareStatement(sql, get_TrxName());
			ps.setInt(1, getC_Invoice_ID());
			ps.setInt(2, getID());
			ps.setInt(3, invoice.getC_Currency_ID());
			rs = ps.executeQuery();
			while (rs.next()) {
				openAmount = rs.getBigDecimal("openAmount");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (ps != null)
					ps.close();
				if (rs != null)
					rs.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return openAmount;
    }
}    // MInvoicePaySchedule



/*
 *  @(#)MInvoicePaySchedule.java   02.07.07
 * 
 *  Fin del fichero MInvoicePaySchedule.java
 *  
 *  Versión 2.2
 *
 */
