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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;
import org.openXpertya.util.TimeUtil;
import org.openXpertya.util.Util;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MPaymentTerm extends X_C_PaymentTerm {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param C_PaymentTerm_ID
     * @param trxName
     */

    public MPaymentTerm( Properties ctx,int C_PaymentTerm_ID,String trxName ) {
        super( ctx,C_PaymentTerm_ID,trxName );

        if( C_PaymentTerm_ID == 0 ) {
            setAfterDelivery( false );
            setNetDays( 0 );
            setDiscount( Env.ZERO );
            setDiscount2( Env.ZERO );
            setDiscountDays( 0 );
            setDiscountDays2( 0 );
            setGraceDays( 0 );
            setIsDueFixed( false );
            setIsValid( false );
        }
    }    // MPaymentTerm

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MPaymentTerm( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MPaymentTerm

    /**
     * Get payment terms of client 
     * @param ctx
     * @param trxName
     * @return
     */
    public static List<MPaymentTerm> getOfClient(Properties ctx,String trxName){
    	//script sql
    	String sql = "SELECT * FROM c_paymentterm WHERE ad_client_id = ? "; 
    		
    	List<MPaymentTerm> list = new ArrayList<MPaymentTerm>();
    	PreparedStatement ps = null;
    	ResultSet rs = null;
    	
    	try {
			ps = DB.prepareStatement(sql, trxName);
			//set ad_client
			ps.setInt(1, Env.getAD_Client_ID(ctx));
			rs = ps.executeQuery();
			
			while(rs.next()){
				list.add(new MPaymentTerm(ctx,rs,trxName));				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			try {
				ps.close();
				rs.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return list;
    } //	getOfClient
    
    
    /** Descripción de Campos */

    private final static BigDecimal HUNDRED = new BigDecimal( 100 );

    /** Descripción de Campos */

    private MPaySchedule[] m_schedule;

    /**
     * Descripción de Método
     *
     *
     * @param requery
     *
     * @return
     */

    public MPaySchedule[] getSchedule( boolean requery ) {
        if( (m_schedule != null) &&!requery ) {
            return m_schedule;
        }

        String sql = "SELECT * FROM C_PaySchedule WHERE C_PaymentTerm_ID=? AND isactive = 'Y' ORDER BY NetDays";
        ArrayList         list  = new ArrayList();
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql,get_TrxName());
            pstmt.setInt( 1,getC_PaymentTerm_ID());

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                MPaySchedule ps = new MPaySchedule( getCtx(),rs,get_TrxName());

                ps.setParent( this );
                list.add( ps );
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"getSchedule",e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        m_schedule = new MPaySchedule[ list.size()];
        list.toArray( m_schedule );

        return m_schedule;
    }    // getSchedule

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String validate() {
        getSchedule( true );

        if( m_schedule.length == 0 ) {
            setIsValid( true );

            return "@OK@";
        }
        //Cambiado por ConSerTi para que deje vencimientos de una sola fecha
      /*  if( m_schedule.length == 1 ) {
            setIsValid( false );

            return "@Invalid@ @Count@ # = 1 (@C_PaySchedule_ID@)";
        }*/
        //Fin modificacion
        // Add up

        BigDecimal total = Env.ZERO;

        for( int i = 0;i < m_schedule.length;i++ ) {
            BigDecimal percent = m_schedule[ i ].getPercentage();

            if( percent != null ) {
                total = total.add( percent );
            }
        }

        boolean valid = total.compareTo( HUNDRED ) == 0;

        setIsValid( valid );

        for( int i = 0;i < m_schedule.length;i++ ) {
            if( m_schedule[ i ].isValid() != valid ) {
                m_schedule[ i ].setIsValid( valid );
                m_schedule[ i ].save();
            }
        }

        String msg = "@OK@";

        if( !valid ) {
            msg = "@Total@ = " + total + " - @Difference@ = " + HUNDRED.subtract( total );
        }

        return Msg.parseTranslation( getCtx(),msg );
    }    // validate

    /**
     * Descripción de Método
     *
     *
     * @param C_Invoice_ID
     *
     * @return
     */

    public boolean apply( int C_Invoice_ID ) {
        MInvoice invoice = new MInvoice( getCtx(),C_Invoice_ID,get_TrxName());

        if( (invoice == null) || (invoice.getID() == 0) ) {
            log.log( Level.SEVERE,"apply - Not valid C_Invoice_ID=" + C_Invoice_ID );

            return false;
        }

        return apply( invoice );
    }    // apply

    /**
     * Descripción de Método
     *
     *
     * @param invoice
     *
     * @return
     */

    public boolean apply( MInvoice invoice ) {
        if( (invoice == null) || (invoice.getID() == 0) ) {
            log.log( Level.SEVERE,"No valid invoice - " + invoice );

            return false;
        }

        if( !isValid()) {
			log.log(Level.SEVERE, Msg.getMsg(getCtx(), "InvalidPaymentTerm")
					+ " : " + getName());
            return applyNoSchedule( invoice );
        }

        //

        getSchedule( false );
        //Cambiado por ConSerTi para que acepte las facturas con un solo pago en los vencimientos
        log.fine("La longitud de los vencimientos en esta forma de pago es:"+m_schedule.length);
        return applySchedule( invoice );
    }               // apply

    /**
     * Descripción de Método
     *
     *
     * @param invoice
     *
     * @return
     */

    private boolean applyNoSchedule( MInvoice invoice ) {
    	try{
    		deleteInvoicePaySchedule( invoice.getC_Invoice_ID(),invoice.get_TrxName() );
    	} catch(Exception e){
    		log.saveError("Error", e);
    		return false;
    	}

        // updateInvoice

        if( invoice.getC_PaymentTerm_ID() != getC_PaymentTerm_ID()) {
            invoice.setC_PaymentTerm_ID( getC_PaymentTerm_ID());
        }

        if( invoice.isPayScheduleValid()) {
            invoice.setIsPayScheduleValid( false );
        }

        return false;
    }    // applyNoSchedule

    /**
     * Descripción de Método
     *
     *
     * @param invoice
     *
     * @return
     */

    private boolean applySchedule( MInvoice invoice ) {
        try{
        	deleteInvoicePaySchedule( invoice.getC_Invoice_ID(),invoice.get_TrxName());
        } catch(Exception e){
    		log.saveError("Error", e);
    		return false;
    	}

        // Create Schedule

        MInvoicePaySchedule ips       = null;
        BigDecimal          remainder = invoice.getGrandTotal();

        for( int i = 0;i < m_schedule.length;i++ ) {
//            ips = new MInvoicePaySchedule( invoice,m_schedule[ i ] );
        	ips = createInvoicePaySchedule(getCtx(), invoice, m_schedule[i], this, invoice.get_TrxName());
            if(!ips.save()){
            	log.saveError("SaveError", CLogger.retrieveErrorAsString());
            	return false;
            }
            remainder = remainder.subtract( ips.getDueAmt());
        }    // for all schedules

		// Si no hay esquema de pagos configurado, verifico la configuración del
		// esquema de vencimiento para crear los posteriores esquemas de pagos
		// de facturas  
        if(m_schedule.length == 0){
        	ips = createInvoicePaySchedule(getCtx(), invoice, null, this, invoice.get_TrxName());
        	if(!ips.save()){
            	log.saveError("SaveError", CLogger.retrieveErrorAsString());
            	return false;
            }
            remainder = BigDecimal.ZERO;
        }
        
        // Remainder - update last
        if( (remainder.compareTo( BigDecimal.ZERO ) != 0) && (ips != null) ) {
            ips.setDueAmt( ips.getDueAmt().add( remainder ));
            if(!ips.save( invoice.get_TrxName())){
            	log.saveError("SaveError", CLogger.retrieveErrorAsString());
            	return false;
            }
            log.fine( "Remainder=" + remainder + " - " + ips );
        }

        // updateInvoice

        if( invoice.getC_PaymentTerm_ID() != getC_PaymentTerm_ID()) {
            invoice.setC_PaymentTerm_ID( getC_PaymentTerm_ID());
        }

        return invoice.validatePaySchedule();
    }    // applySchedule

    /**
     * Descripción de Método
     *
     *
     * @param C_Invoice_ID
     * @param trxName
     */

    private void deleteInvoicePaySchedule( int C_Invoice_ID,String trxName ) throws Exception{
        String sql = "DELETE FROM C_InvoicePaySchedule WHERE C_Invoice_ID=" + C_Invoice_ID;
        PreparedStatement ps = null;
        try {
			ps = DB.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE, trxName);
			ps.executeUpdate();
		} catch (Exception e) {
			throw e;
		} finally{
			try{
				if(ps != null) ps.close();
			} catch(Exception e2){
				throw e2;
			}			
		}
    }    // deleteInvoicePaySchedule

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer( "MPaymentTerm[" );

        sb.append( getID()).append( "-" ).append( getName()).append( ",Valid=" ).append( isValid()).append( "]" );

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
        if( isDueFixed()) {
            int dd = getFixMonthDay();

            if( (dd < 1) || (dd > 31) ) {
                log.saveError( "Error",Msg.parseTranslation( getCtx(),"@Invalid@ @FixMonthDay@" ));

                return false;
            }

            dd = getFixMonthCutoff();

            if( (dd < 1) || (dd > 31) ) {
                log.saveError( "Error",Msg.parseTranslation( getCtx(),"@Invalid@ @FixMonthCutoff@" ));

                return false;
            }
        }

        if( !newRecord ||!isValid()) {
            validate();
        }

		// Si existe un descuento o un descuento 2 configurado entonces debe
		// existir un tipo de aplicación para ellos
		if ((getDiscount().compareTo(BigDecimal.ZERO) != 0 && getDiscountApplicationType() == null)
				|| (getDiscount2().compareTo(BigDecimal.ZERO) != 0 && getDiscountApplicationType2() == null)) {
			log.saveError("NotExistDiscountApplicationType", "");
			return false;
        }
				
        return true;
    }    // beforeSave

	/**
	 * Creo el esquema de pago de factura a partir de un esquema de pago o, si
	 * es null, a partir del esquema de vencimiento parámetro. Siempre la
	 * configuración de la fecha de vencimiento se toma desde el esquema de
	 * vencimientos parámetro o tomado del esquema de pago parámetro, luego de
	 * adicionar los días neto del esquema de pagos, si es que existe este
	 * último.
	 * 
	 * @param ctx
	 *            contexto
	 * @param invoice
	 *            factura a la que se debe crear el esquema
	 * @param paySchedule
	 *            esquema de pago
	 * @param paymentTerm
	 *            esquema de vencimiento
	 * @param trxName
	 *            nombre de la transacción
	 * @return esquema de pago de factura, null si el esquema de pago y esquema
	 *         de vencimiento parámetros son null
	 */
	public static MInvoicePaySchedule createInvoicePaySchedule(Properties ctx,
			MInvoice invoice, MPaySchedule paySchedule,
			MPaymentTerm paymentTerm, String trxName) {
		// Si el esquema de pagos y el de vencimiento son null, no hay nada que
		// hacer 
		if(paySchedule == null && paymentTerm == null){
			return null;
		}
		// Creo el esquema de pago de la factura
		MInvoicePaySchedule ips = new MInvoicePaySchedule(ctx, 0, trxName);
		ips.setClientOrg( invoice );
        ips.setC_Invoice_ID( invoice.getC_Invoice_ID());
        if(paySchedule != null){
            ips.setC_PaySchedule_ID( paySchedule.getC_PaySchedule_ID());
        }
		ips.setParent(invoice);
		
		// Montos
		
        int scale = MCurrency.getStdPrecision( ctx,invoice.getC_Currency_ID(), trxName);
        BigDecimal due = invoice.getGrandTotal();
        // Determino el monto a partir del porcentaje del esquema de pago o
		// 100 % en caso que no poseamos esquema de pagos
		due = due
				.multiply(
						paySchedule != null ? paySchedule.getPercentage()
								: HUNDRED).divide(HUNDRED, scale,
						BigDecimal.ROUND_HALF_UP);
        ips.setDueAmt( due );
        // Descuentos
		BigDecimal discount = due.multiply(
				paySchedule != null ? paySchedule.getDiscount() : paymentTerm
						.getDiscount()).divide(HUNDRED, scale,
				BigDecimal.ROUND_HALF_UP);
        ips.setDiscountAmt( discount );
        if( due.compareTo( BigDecimal.ZERO ) == 0 ) {
            ips.setIsValid( false );
        } else {			
        	ips.setIsValid( true );
        }
        
        // Vencimiento
        
		// Verificar cada tipo de vencimiento dependiendo de los parámetros que
		// tiene el esquema de vencimientos parámetro, sino tomo el que tiene el esquema de pagos
		MPaymentTerm realPaymentTerm = paymentTerm != null ? paymentTerm
				: new MPaymentTerm(ctx, paySchedule.getC_PaymentTerm_ID(),
						trxName);
		// Determinar la fecha de vencimiento, si existe esquema de pagos, le
		// sumo la cantidad de días configuradas a la fecha de la factura o a la
		// fecha de recepción dependiendo de la configuración de fecha de
		// aplicación
		Timestamp invoiceDate = MPaymentTerm.APPLICATIONDATE_InvoiceDate
				.equals(realPaymentTerm.getApplicationDate()) ? invoice
				.getDateInvoiced()
				: (invoice.getDateRecepted() != null ? invoice
						.getDateRecepted() : invoice.getDateInvoiced());
		Timestamp dueDate = paySchedule != null ? TimeUtil.addDays(invoiceDate,
				paySchedule.getNetDays()) : invoiceDate;
		ips.setDueDate(getRealDueDate(dueDate, realPaymentTerm));
		
		// Día de descuento
		Timestamp discountDate = TimeUtil.addDays(invoice.getDateInvoiced(),
				paySchedule != null ? paySchedule.getDiscountDays()
						: paymentTerm.getDiscountDays());
        ips.setDiscountDate( discountDate );
        
        return ips;
    }

	/**
	 * Obtener la fecha de vencimiento real a partir de la configuración del
	 * esquema de vencimientos
	 * 
	 * @param dueDate
	 *            fecha de vencimiento
	 * @param paymentTerm
	 *            esquema de vencimientos
	 * @return fecha de vencimiento real surgido de la aplicación de la
	 *         configuración del esquema de vencimentos
	 */
	public static Timestamp getRealDueDate(Timestamp dueDate, MPaymentTerm paymentTerm){
		Timestamp realDueDate = dueDate;
		Calendar dueCalendar = Calendar.getInstance();
		dueCalendar.setTimeInMillis(realDueDate.getTime());
		boolean changed = false;
		// Si es fecha de vencimiento fija, realizo los cálculos necesarios 
		if(paymentTerm.isDueFixed()){
			// Le sumo los meses 
			int month = paymentTerm.getFixMonthOffset();
			int cuteDay = paymentTerm.getFixMonthCutoff();
			// Si el día de de corte es menor al día del vencimiento, entonces sumo un mes más 
			if(dueCalendar.get(Calendar.DAY_OF_MONTH) >= cuteDay){
				month++;
			}
			dueCalendar.add(Calendar.MONTH, month);
			try{
				// Le seteo el día de vencimiento fijo
				dueCalendar.set(Calendar.DAY_OF_MONTH, paymentTerm.getFixMonthDay());
			} catch(ArrayIndexOutOfBoundsException aioobe){
				// Si se disparó esta exception verificar es porque el dia
				// sobrepasa los días del mes
				// Crear el calendario con el último día de mes
				Timestamp lastDayMonth = TimeUtil
						.getMonthLastDay(new Timestamp(dueCalendar
								.getTimeInMillis()));
				Calendar lastDayMonthCalendar = Calendar.getInstance();
				lastDayMonthCalendar.setTimeInMillis(lastDayMonth.getTime());
				int lastDay = lastDayMonthCalendar.get(Calendar.DAY_OF_MONTH);
				// Si sobrepasó los días del mes, determino la diferencia entre
				// el último día de mes y el día de vencimiento fijo, esa
				// diferencia se la sumo a la fecha de vencimiento. Ejemplo: Si
				// el día de vencimiento fijo es 30 y estamos en Febrero (28
				// días), el último día de mes es 28 y la diferencia es 2,
				// entonces le seteo el día 28 al calendario del vencimiento
				// actual y le sumo la diferencia para que automáticamente
				// proponga el offset de meses
				if(paymentTerm.getFixMonthDay() > lastDay){
					int diffDays = paymentTerm.getFixMonthDay() - lastDay;
					dueCalendar.set(Calendar.DAY_OF_MONTH, lastDay);
					dueCalendar.add(Calendar.DATE, diffDays);
				}
			}
			changed = true;
		}
		// Si la cantidad de días neto es mayor a 0, se las sumo a la fecha de
		// vencimiento
		else if(paymentTerm.getNetDays() > 0){
			dueCalendar.add(Calendar.DATE, paymentTerm.getNetDays());
			changed = true;
		}
		// Si es siguiente día hábil verifico el siguiente día hábil
		if(paymentTerm.isNextBusinessDay()){
			// Le sumo 1 día en caso que sea solo esa configuración, si la fecha
			// cambió en el resto del método entonces no porque ya se configuró
			// otra fecha 
			if(!changed){
				dueCalendar.add(Calendar.DATE, 1);
			}
			// Día Hábil
			for (; dueCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY
					|| dueCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY;)
				dueCalendar.add(Calendar.DATE, 1);
		}
		realDueDate = new Timestamp(dueCalendar.getTimeInMillis());
		return realDueDate;
	}

	/**
	 * Obtengo el monto de descuento/recargo del esquema de pagos parámetro. Si
	 * es null, se tomará la configuración del esquema de vencimientos.
	 * 
	 * @param ps
	 *            esquema de pago
	 * @param invoiceDate
	 *            fecha de facturación
	 * @param dueDate
	 *            fecha de vencimiento para esta factura o cuota
	 * @param compareDate
	 *            fecha de comparación para validar si es posible aplicar el
	 *            descuento o no
	 * @param baseAmt
	 *            monto base a aplicar el descuento/recargo
	 * @return monto de descuento/recargo teniendo en cuenta los criterios
	 *         descritos
	 */
	public BigDecimal getDiscount(MPaySchedule ps, Timestamp invoiceDate, Timestamp dueDate, Timestamp compareDate, BigDecimal baseAmt){
		// Tomar la configuración de descuentos/recargos desde el esquema de
		// vencimientos o del esquema de pago
		String discountApplicationType = null;  
		BigDecimal discountPerc = BigDecimal.ZERO;
		Integer toleranceDays = 0;
		Integer discountDays = 0;
		BigDecimal discount = BigDecimal.ZERO;
		Integer diffToUse = 0;
		// Días de diferencia entre la fecha de comparación y la fecha de
		// facturación. Si es positivo la fecha de comparación es mayor a la de
		// facturación, 0 es igual y negativo lo contrario
		Integer diffInvoicedDays = TimeUtil.getDiffDays(invoiceDate, compareDate);
		// Días de diferencia entre la fecha de comparación y la fecha de
		// vencimiento. Si es positivo la fecha de comparación es mayor a la de
		// vencimiento, 0 es igual y negativo lo contrario
		Integer diffDueDays = TimeUtil.getDiffDays(dueDate, compareDate);
		// Lo tomo del esquema de pagos, sino del esquema de vencimientos
		if(ps != null){
			discountApplicationType = ps.getDiscountApplicationType();
			discountPerc = ps.getDiscount();
			discountDays = ps.getDiscountDays();
			toleranceDays = ps.getGraceDays();
			diffToUse = diffDueDays;
		}
		else{
			// Tomar descuento 1 o 2? Eso se determina las fechas parámetro, si
			// la fecha de comparación está después de la de vencimiento,
			// significa que estoy vencido por lo que se debe tomar el descuento
			// 2, sino se toma descuento 1
			if(diffDueDays > 0){
				discountApplicationType = getDiscountApplicationType2();
				discountPerc = getDiscount2();
				discountDays = getDiscountDays2();
				toleranceDays = getGraceDays2();
				diffToUse = diffDueDays;
			}
			else if(diffInvoicedDays > 0){
				discountApplicationType = getDiscountApplicationType();
				discountPerc = getDiscount();
				discountDays = getDiscountDays();
				toleranceDays = getGraceDays();
				diffToUse = diffInvoicedDays;
			}			
		}
		// Si hay descuento aplicable entonces aplico
		if(discountApplicationType != null){
			// Si es incremento diario se debe multiplicar por la cantidad de
			// días de diferencia, sino es de una vez el descuento
			// Primero verifico si lo puedo aplicar, dependiendo los días
			// Si la diferencia de días es mayor a la cantidad de días de
			// descuento + los días de tolerancia, entonces se aplica
			Integer daysToBeat = discountDays+toleranceDays;
			if(diffToUse > daysToBeat){
				Integer dailyIncreaseDays = discountApplicationType
						.equals(DISCOUNTAPPLICATIONTYPE_DailyIncrease) ? diffToUse
						: 1;
				// Saco el monto de descuento/recargo, descuentos son positivos,
				// recargos negativos
				discount = (baseAmt.multiply(discountPerc).divide(
						new BigDecimal(100), 20, BigDecimal.ROUND_HALF_EVEN))
						.multiply(new BigDecimal(dailyIncreaseDays));
			}
		}
		return discount;
	}

	/**
	 * Obtengo el monto de descuento/recargo del esquema de pagos del esquema de
	 * pago de factura parámetro. Si es null, se tomará la configuración del
	 * esquema de vencimientos.
	 * 
	 * @param ips
	 *            esquema de pago de factura
	 * @param invoiceDate
	 *            fecha de facturación
	 * @param dueDate
	 *            fecha de vencimiento para esta factura o cuota
	 * @param compareDate
	 *            fecha de comparación para validar si es posible aplicar el
	 *            descuento o no
	 * @param baseAmt
	 *            monto base a aplicar el descuento/recargo
	 * @return monto de descuento/recargo teniendo en cuenta los criterios
	 *         descritos
	 */
	public BigDecimal getDiscount(MInvoicePaySchedule ips, Timestamp invoiceDate, Timestamp dueDate, Timestamp compareDate, BigDecimal baseAmt){
		// Obtener el esquema de pago del esquema de vencimiento que posee el
		// esquema de pago de la factura parámetro
		MPaySchedule ps = null;
		if (ips != null && !Util.isEmpty(ips.getC_PaySchedule_ID(), true)) {
			ps = new MPaySchedule(getCtx(), ips.getC_PaySchedule_ID(), get_TrxName());			
		}
		return getDiscount(ps, invoiceDate, dueDate, compareDate, baseAmt);
	}
	
}    // MPaymentTerm



/*
 *  @(#)MPaymentTerm.java   02.07.07
 * 
 *  Fin del fichero MPaymentTerm.java
 *  
 *  Versión 2.2
 *
 */
