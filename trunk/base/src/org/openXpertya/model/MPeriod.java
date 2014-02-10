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

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.util.CCache;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.ITime;
import org.openXpertya.util.TimeUtil;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MPeriod extends X_C_Period implements ITime{

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param C_Period_ID
     * @param trxName
     *
     * @return
     */

    public static MPeriod get( Properties ctx,int C_Period_ID,String trxName ) {
        Integer key      = new Integer( C_Period_ID );
        MPeriod retValue = ( MPeriod )s_cache.get( key );

        if( retValue != null ) {
            return retValue;
        }

        //

        retValue = new MPeriod( ctx,C_Period_ID,trxName );

        if( retValue.getID() != 0 ) {
            s_cache.put( key,retValue );
        }

        return retValue;
    }    // get

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param DateAcct
     *
     * @return
     */

    public static MPeriod get( Properties ctx,Timestamp DateAcct ) {
        if( DateAcct == null ) {
            return null;
        }

        // Search in Cache first

        Iterator it = s_cache.values().iterator();

        while( it.hasNext()) {
            MPeriod period = ( MPeriod )it.next();

            if( period.isStandardPeriod() && period.isInPeriod( DateAcct )) {
                return period;
            }
        }

        // Get it from DB

        MPeriod retValue     = null;
        int     AD_Client_ID = Env.getAD_Client_ID( ctx );
        String  sql          = "SELECT * " + "FROM C_Period " + "WHERE C_Year_ID IN " + "(SELECT C_Year_ID FROM C_Year WHERE C_Calendar_ID= " + "(SELECT C_Calendar_ID FROM AD_ClientInfo WHERE AD_Client_ID=?))" + " AND ?::date BETWEEN date_trunc('day',StartDate) AND date_trunc('day',EndDate)" + " AND PeriodType='S'";

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql,null, true );

            pstmt.setInt( 1,AD_Client_ID );
            pstmt.setTimestamp( 2, DateAcct );

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                MPeriod period = new MPeriod( ctx,rs,null );
                Integer key    = new Integer( period.getC_Period_ID());

                s_cache.put( key,period );

                if( period.isStandardPeriod()) {
                    retValue = period;
                }
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( SQLException e ) {
            s_log.log( Level.SEVERE,"(DateAcct)",e );
        }

        if( retValue == null ) {
            s_log.warning( "No Standard Period for " + DateAcct + " (AD_Client_ID=" + AD_Client_ID + ")" );
        }

        return retValue;
    }    // get

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param DateAcct
     *
     * @return
     */

    public static int getC_Period_ID( Properties ctx,Timestamp DateAcct ) {
        MPeriod period = get( ctx,DateAcct );

        if( period == null ) {
            return 0;
        }

        return period.getC_Period_ID();
    }    // getC_Period_ID

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param DateAcct
     * @param DocBaseType
     *
     * @return
     */

    public static boolean isOpen( Properties ctx,Timestamp DateAcct,String DocBaseType ) {
    	return isOpen(ctx, DateAcct, DocBaseType, 0);
//        if( DateAcct == null ) {
//            s_log.warning( "No DateAcct" );
//
//            return false;
//        }
//
//        if( DocBaseType == null ) {
//            s_log.warning( "No DocBaseType" );
//
//            return false;
//        }
//
//        MPeriod period = MPeriod.get( ctx,DateAcct );
//
//        if( period == null ) {
//            s_log.warning( "No Period for " + DateAcct + " (" + DocBaseType + ")" );
//
//            return false;
//        }
//
//        boolean open = period.isOpen( DocBaseType );
//
//        if( !open ) {
//            s_log.warning( period.getName() + ": Not open for " + DocBaseType + " (" + DateAcct + ")" );
//        }
//
//        return open;
    }    // isOpen

    
    
    public static boolean isOpen( Properties ctx,Timestamp DateAcct,String DocBaseType, Integer warehouseID ) {
        return isOpen(ctx, DateAcct, DocBaseType, warehouseID, false);
    }    // isOpen

    public static boolean isOpen( Properties ctx,Timestamp DateAcct,String DocBaseType, Integer warehouseID, boolean bypassWarehouseCloseValidation ) {
        if( DateAcct == null ) {
            s_log.warning( "No DateAcct" );

            return false;
        }

        if( DocBaseType == null ) {
            s_log.warning( "No DocBaseType" );

            return false;
        }

        MPeriod period = MPeriod.get( ctx,DateAcct );

        if( period == null ) {
            s_log.warning( "No Period for " + DateAcct + " (" + DocBaseType + ")" );

            return false;
        }

        Calendar calendarAcct = new GregorianCalendar();
        calendarAcct.setTimeInMillis(DateAcct.getTime());
		boolean open = period.isOpen(DocBaseType,
				calendarAcct.get(Calendar.DAY_OF_MONTH), warehouseID,
				bypassWarehouseCloseValidation);

        if( !open ) {
            s_log.warning( period.getName() + ": Not open for " + DocBaseType + " (" + DateAcct + ")" );
        }

        return open;
    }    // isOpen

    
    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param DateAcct
     *
     * @return
     */

    public static MPeriod getFirstInYear( Properties ctx,Timestamp DateAcct ) {
        MPeriod retValue     = null;
        int     AD_Client_ID = Env.getAD_Client_ID( ctx );
        String  sql          = "SELECT * " + "FROM C_Period " + "WHERE C_Year_ID IN " + "(SELECT p.C_Year_ID " + "FROM AD_ClientInfo c" + " INNER JOIN C_Year y ON (c.C_Calendar_ID=y.C_Calendar_ID)" + " INNER JOIN C_Period p ON (y.C_Year_ID=p.C_Year_ID) " + "WHERE c.AD_Client_ID=?" + "     AND ? BETWEEN StartDate AND EndDate)" + " AND PeriodType='S' " + "ORDER BY StartDate";

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql,null );

            pstmt.setInt( 1,AD_Client_ID );
            pstmt.setTimestamp( 2,DateAcct );

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {    // first only
                retValue = new MPeriod( ctx,rs,null );
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            s_log.log( Level.SEVERE,"getFirstinYear",e );
        }

        return retValue;
    }    // getFirstInYear

    /** Descripción de Campos */

    private static CCache s_cache = new CCache( "C_Period",10 );

    /** Descripción de Campos */

    private static CLogger s_log = CLogger.getCLogger( MPeriod.class );

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param C_Period_ID
     * @param trxName
     */

    public MPeriod( Properties ctx,int C_Period_ID,String trxName ) {
        super( ctx,C_Period_ID,trxName );

        if( C_Period_ID == 0 ) {

            // setC_Period_ID (0);             //      PK
            // setC_Year_ID (0);           //      Parent
            // setName (null);
            // setPeriodNo (0);
            // setStartDate (new Timestamp(System.currentTimeMillis()));

            setPeriodType( PERIODTYPE_StandardCalendarPeriod );
        }
    }    // MPeriod

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MPeriod( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MPeriod

    /**
     * Constructor de la clase ...
     *
     *
     * @param year
     * @param PeriodNo
     * @param name
     * @param startDate
     * @param endDate
     */

    public MPeriod( MYear year,int PeriodNo,String name,Timestamp startDate,Timestamp endDate ) {
        this( year.getCtx(),0,year.get_TrxName());
        setClientOrg( year );
        setC_Year_ID( year.getC_Year_ID());
        setPeriodNo( PeriodNo );
        setName( name );
        setStartDate( startDate );
        setEndDate( endDate );
    }    // MPeriod

    /** Descripción de Campos */

    private MPeriodControl[] m_controls = null;

    /**
     * Descripción de Método
     *
     *
     * @param requery
     *
     * @return
     */

    public MPeriodControl[] getPeriodControls( boolean requery ) {
        if( (m_controls != null) &&!requery ) {
            return m_controls;
        }

        //

        ArrayList         list  = new ArrayList();
        String            sql   = "SELECT * FROM C_PeriodControl " + "WHERE C_Period_ID=?";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql,null );
            pstmt.setInt( 1,getC_Period_ID());

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                list.add( new MPeriodControl( getCtx(),rs,null ));
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"getPeriodControls",e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        //

        m_controls = new MPeriodControl[ list.size()];
        list.toArray( m_controls );

        return m_controls;
    }    // getPeriodControls

    /**
     * Descripción de Método
     *
     *
     * @param DocBaseType
     *
     * @return
     */

    public MPeriodControl getPeriodControl( String DocBaseType ) {
        if( DocBaseType == null ) {
            return null;
        }

        getPeriodControls( false );

        for( int i = 0;i < m_controls.length;i++ ) {

            // log.fine("getPeriodControl - " + 1 + " - " + m_controls[i]);

            if( DocBaseType.equals( m_controls[ i ].getDocBaseType())) {
                return m_controls[ i ];
            }
        }

        return null;
    }    // getPeriodControl

    /**
     * Descripción de Método
     *
     *
     * @param date
     *
     * @return
     */

    public boolean isInPeriod( Timestamp date ) {
        if( date == null ) {
            return false;
        }

        Timestamp dateOnly = TimeUtil.getDay( date );
        Timestamp from     = TimeUtil.getDay( getStartDate());

        if( dateOnly.before( from )) {
            return false;
        }

        Timestamp to = TimeUtil.getDay( getEndDate());

        if( dateOnly.after( to )) {
            return false;
        }

        return true;
    }    // isInPeriod

    /**
     * Descripción de Método
     *
     *
     * @param DocBaseType
     *
     * @return
     */

    public boolean isOpen( String DocBaseType ) {
    	return isOpen(DocBaseType, 1, 0);
//        MAcctSchema as = MClient.get( getCtx(),getAD_Client_ID()).getAcctSchema();
//
//        if( (as != null) && as.isAutoPeriodControl()) {
//            if( as.getC_Period_ID() == getC_Period_ID()) {
//                return true;
//            }
//
//            // Future --| Period |-- History
//
//            Timestamp first = TimeUtil.addDays( getStartDate(),-as.getPeriod_OpenFuture());
//            Timestamp last = TimeUtil.addDays( getEndDate(),as.getPeriod_OpenHistory());
//            Timestamp today = new Timestamp( System.currentTimeMillis());
//
//            if( today.before( first )) {
//                log.warning( "Today before first day - " + first );
//
//                return false;
//            }
//
//            if( today.after( last )) {
//                log.warning( "Today after last day - " + first );
//
//                return false;
//            }
//
//            // We are OK
//
//            if( today.after( getStartDate()) && today.before( getEndDate())) {
//                as.setC_Period_ID( getC_Period_ID());
//                as.save();
//            }
//
//            return true;
//        }
//
//        // Standard Period Control
//
//        if( DocBaseType == null ) {
//            log.warning( getName() + " - No DocBaseType" );
//
//            return false;
//        }
//
//        MPeriodControl pc = getPeriodControl( DocBaseType );
//
//        if( pc == null ) {
//            log.warning( getName() + " - Period Control not found for " + DocBaseType );
//
//            return false;
//        }
//
//        log.fine( getName() + ": " + DocBaseType );
//
//        return pc.isOpen();
    }    // isOpen

    
    public boolean isOpen( String DocBaseType, Integer dayOfMonth, Integer warehouseID ) {
    	return isOpen(DocBaseType, dayOfMonth, warehouseID, false);
    }    // isOpen
    

    public boolean isOpen( String DocBaseType, Integer dayOfMonth, Integer warehouseID, boolean bypassWarehouseCloseValidation ) {
    	// Control de cierres de almacenes
		if (!isOpenWarehouseClosePeriod(DocBaseType, dayOfMonth, warehouseID,
				bypassWarehouseCloseValidation)) {
        	log.warning("Period closed for warehouse close control");
        	return false;
        }
        
        MAcctSchema as = MClient.get( getCtx(),getAD_Client_ID()).getAcctSchema();

        if( (as != null) && as.isAutoPeriodControl()) {
            if( as.getC_Period_ID() == getC_Period_ID()) {
                return true;
            }

            // Future --| Period |-- History

            Timestamp first = TimeUtil.addDays( getStartDate(),-as.getPeriod_OpenFuture());
            Timestamp last = TimeUtil.addDays( getEndDate(),as.getPeriod_OpenHistory());
            Timestamp today = new Timestamp( System.currentTimeMillis());

            if( today.before( first )) {
                log.warning( "Today before first day - " + first );

                return false;
            }

            if( today.after( last )) {
                log.warning( "Today after last day - " + first );

                return false;
            }

            // We are OK

            if( today.after( getStartDate()) && today.before( getEndDate())) {
                as.setC_Period_ID( getC_Period_ID());
                as.save();
            }

            return true;
        }

        // Standard Period Control

        if( DocBaseType == null ) {
            log.warning( getName() + " - No DocBaseType" );

            return false;
        }

        MPeriodControl pc = getPeriodControl( DocBaseType );

        if( pc == null ) {
            log.warning( getName() + " - Period Control not found for " + DocBaseType );

            return false;
        }

        log.fine( getName() + ": " + DocBaseType );      

        return pc.isOpen();
    }    // isOpen

    
    /**
     * Período de control de cierres de depósito.
     * @param DocBaseType
     * @param dayOfMonth
     * @param warehouseID
     * @return
     */
    private boolean isOpenWarehouseClosePeriod(String DocBaseType, Integer dayOfMonth, Integer warehouseID ){
    	return isOpenWarehouseClosePeriod(DocBaseType, dayOfMonth, warehouseID, false);
    }
    
    
    /**
     * Período de control de cierres de depósito.
     * @param DocBaseType
     * @param dayOfMonth
     * @param warehouseID
     * @param bypassValidation
     * @return
     */
    private boolean isOpenWarehouseClosePeriod(String DocBaseType, Integer dayOfMonth, Integer warehouseID, boolean bypassValidation ){
    	GregorianCalendar closeDate = new GregorianCalendar();
        closeDate.setTimeInMillis(getStartDate().getTime());
        try{
        	closeDate.set(GregorianCalendar.DATE, dayOfMonth);
        } catch(Exception e){
        	log.severe("Fecha invalida para el dia del mes "+dayOfMonth+" para el mes "+closeDate.get(Calendar.MONTH)+1+" y año "+closeDate.get(Calendar.YEAR));
        	return false;
        }
        // Control de cierre de almacén 
        if(MWarehouseClose.isClosed(DocBaseType, new Date(closeDate.getTimeInMillis()), warehouseID, bypassValidation)){
        	log.severe("Control de periodo de cierres de almacen: No existe periodo abierto del almacen para la fecha "+closeDate.getTime());
        	return false;
        }
        return true;
    }
    
    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isStandardPeriod() {
        return PERIODTYPE_StandardCalendarPeriod.equals( getPeriodType());
    }    // isStandardPeriod

    /**
     * Descripción de Método
     *
     *
     * @param newRecord
     *
     * @return
     */

    protected boolean beforeSave( boolean newRecord ) {

        // Truncate Dates

        Timestamp date = getStartDate();

        if( date != null ) {
            setStartDate( TimeUtil.getDay( date ));
        } else {
            return false;
        }

        //

        date = getEndDate();

        if( date != null ) {
            setEndDate( TimeUtil.getDay( date ));
        } else {
            setEndDate( TimeUtil.getMonthLastDay( getStartDate()));
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

    protected boolean afterSave( boolean newRecord,boolean success ) {
        if( newRecord ) {

            // SELECT Value FROM AD_Ref_List WHERE AD_Reference_ID=183
        	
        	createPeriodControls();
        }

        return success;
    }    // afterSave
    
    /**
     * Crea los controles de período de todos los tipos de documentos base que
     * esten asociados con los tipos de documento de la compañía.
     * En caso de que existan controles de período para algún tipo de documento base,
     * entonces no se modifica este control previamente creado ni tampoco se crea uno
     * nuevo.
     */
    public void createPeriodControls() {
    	
    	// Se obtienen todos los tipos de documento de la compañía para obtener los tipos
    	// de documento base a los que hay que crearle el control de período.
        MDocType[] types = MDocType.getOfClient(getCtx(), get_TrxName());
        int        count = 0;
        // Se obtienen los controles de período existentes, a fin de no repetir controles
        // para similares tipos de documento base.
        List<MPeriodControl> existentControls = MPeriodControl.getOfPeriod(getC_Period_ID(), get_TrxName()); 
        // Aquí se guardan los tipos de documento base qua ya tienen un control de período.
        List<String> existentDocBaseControl = new ArrayList<String>();
        for (MPeriodControl periodControl : existentControls) {
			existentDocBaseControl.add(periodControl.getDocBaseType());
		}

        // Por cada tipo de documento, se crea en caso de no existir el control de período
        // para el tipo de documento base.
        for( int i = 0;i < types.length;i++ ) {
            MDocType       type = types[ i ];
            String docBaseType = type.getDocBaseType();
            // Solo si no existe se crea...
            if (!existentDocBaseControl.contains(docBaseType)) {
                MPeriodControl pc   = new MPeriodControl( this, docBaseType);
                if( pc.save()) {
                    count++;
                    existentDocBaseControl.add(docBaseType);
                }
            }
        }

        log.fine( "PeriodControl #" + count );
    }

	@Override
	public Date getDateFrom() {
		return new Date(getStartDate().getTime());
	}

	@Override
	public int getDateField() {
		return Calendar.MONTH;
	}

	@Override
	public Date getDateTo() {
		return new Date(getEndDate().getTime());
	}

	@Override
	public String getITimeDescription() {
		return getName();
	}

	@Override
	public boolean isIncludedInPeriod(Timestamp date) {
		// Si la fecha de inicio del periodo es menor o igual que la fecha actual y si la fecha de fin es mayor o igual 
		// que la fecha actual retorna true, en caso contrario retorna false.
		return ( (getDateFrom().compareTo(date) <= 0) && (date.compareTo(getDateTo()) <= 0) );
	}

	@Override
	public Integer getDaysCount() {
		// FIXME: Está bien que se tome mensualmente la cantidad de días a 30
		// días o tiene que ser la cantidad de días del mes actual?
		return 30;
	}

	@Override
	public int getDayField() {
		return Calendar.DAY_OF_MONTH;
	}
}    // MPeriod



/*
 *  @(#)MPeriod.java   02.07.07
 * 
 *  Fin del fichero MPeriod.java
 *  
 *  Versión 2.2
 *
 */
