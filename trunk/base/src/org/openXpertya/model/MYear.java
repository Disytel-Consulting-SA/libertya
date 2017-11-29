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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.util.CCache;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.ITime;
import org.openXpertya.util.Language;
import org.openXpertya.util.Msg;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MYear extends X_C_Year implements ITime{

	private static CCache s_cache = new CCache( "C_Year",10 );
	
	public static MYear get(Properties ctx, Integer yearID){
		MYear retValue = ( MYear )s_cache.get( yearID );
		MCalendar cal = MCalendar.getDefault(ctx);
		
        if( retValue != null && cal.isCacheEnabled()) {
            return retValue;
        }
       
        retValue = new MYear(ctx, yearID, null);
        
        if( retValue != null && cal.isCacheEnabled()) {
            s_cache.put(yearID, retValue);
        }
        
        return retValue;
	}
	
	/**
	 * @param ctx
	 *            contexto
	 * @param date
	 *            fecha
	 * @param trxName
	 *            transacción actual
	 * @return la instancia a partir del año de la fecha parámetro
	 */
	public static MYear get(Properties ctx, Timestamp date, String trxName){
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(date.getTime());
		return (MYear) PO.findFirst(ctx, X_C_Year.Table_Name, "year = '"
				+ calendar.get(Calendar.YEAR) + "' AND ad_client_id = ?",
				new Object[] { Env.getAD_Client_ID(ctx) }, null, trxName);
	}
	
	/** Períodos de este año */
	private List<MPeriod> m_periods = null;
	
    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param C_Year_ID
     * @param trxName
     */

    public MYear( Properties ctx,int C_Year_ID,String trxName ) {
        super( ctx,C_Year_ID,trxName );

        if( C_Year_ID == 0 ) {

            // setC_Calendar_ID (0);
            // setYear (null);

            setProcessing( false );    // N
        }
    }                                  // MYear

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MYear( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MYear

    /**
     * Constructor de la clase ...
     *
     *
     * @param calendar
     */

    public MYear( MCalendar calendar ) {
        this( calendar.getCtx(),0,calendar.get_TrxName());
        setClientOrg( calendar );
        setC_Calendar_ID( calendar.getC_Calendar_ID());
        setYear();
    }    // MYear

    /**
     * Descripción de Método
     *
     */

    private void setYear() {
        GregorianCalendar cal = new GregorianCalendar( Language.getLoginLanguage().getLocale());
        String Year = String.valueOf( cal.get( Calendar.YEAR ));

        super.setYear( Year );
    }    // setYear

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getYearAsInt() {
        try {
            return Integer.parseInt( getYear());
        } catch( Exception e ) {
            log.log( Level.SEVERE,"getYearAsInt - " + e.toString());
        }

        return 0;
    }    // getYearAsInt

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getYY() {
        String year = getYear();

        return year.substring( 2,4 );
    }    // getYY

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer( "MYear[" );

        sb.append( getID()).append( "-" ).append( getYear()).append( "]" );

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
        if( getYearAsInt() == 0 ) {
        	log.saveError("SaveError", Msg.translate(getCtx(), "YearMustBeNumeric"));
        	return false;
        }
        
        // Validación de año duplicado
        boolean yearExists = 0 < (Long)DB.getSQLObject(get_TrxName(), 
        		" SELECT COALESCE(COUNT(*),0) AS YearCount " +
        		" FROM C_Year WHERE Year ILIKE ? AND C_Year_ID <> ? AND C_Calendar_ID = ? ",
        		new Object[] {getYear(), getC_Year_ID(), getC_Calendar_ID()});
        if (yearExists) {
        	log.saveError("SaveError", Msg.getMsg(getCtx(), "CalendarYearExists", new Object[]{getYear()}));
        	return false;
        }
        
        return true;
    }    // beforeSave

    /**
     * Descripción de Método
     *
     *
     * @param locale
     *
     * @return
     */
    public boolean createStdPeriods( Locale locale ) {
    	return createStdPeriods(locale, false);
    }

    /**
     * Descripción de Método
     *
     *
     * @param locale
     * @param openPeriods: si está activo, los períodos creados se abren
     *
     * @return
     */
    public boolean createStdPeriods( Locale locale, boolean openPeriods ) {
        if( locale == null ) {
            MClient client = MClient.get( getCtx());

            locale = client.getLocale();
        }

        if( (locale == null) && (Language.getLoginLanguage() != null) ) {
            locale = Language.getLoginLanguage().getLocale();
        }

        if( locale == null ) {
            locale = Env.getLanguage( getCtx()).getLocale();
        }

        //

        DateFormatSymbols symbols = new DateFormatSymbols( locale );
        String[]          months  = symbols.getShortMonths();

        //

        int               year = getYearAsInt();
        GregorianCalendar cal  = new GregorianCalendar( locale );

        cal.set( Calendar.HOUR_OF_DAY,0 );
        cal.set( Calendar.MINUTE,0 );
        cal.set( Calendar.SECOND,0 );
        cal.set( Calendar.MILLISECOND,0 );

        //

        for( int month = 0;month < 12;month++ ) {
            cal.set( Calendar.YEAR,year );
            cal.set( Calendar.MONTH,month );
            cal.set( Calendar.DAY_OF_MONTH,1 );

            Timestamp start = new Timestamp( cal.getTimeInMillis());
            String    name  = months[ month ] + "-" + getYY();

            //

            cal.add( Calendar.MONTH,1 );
            cal.add( Calendar.DAY_OF_YEAR,-1 );

            Timestamp end = new Timestamp( cal.getTimeInMillis());

            //

            MPeriod period = new MPeriod( this,month + 1,name,start,end );

            if( !period.save( get_TrxName())) {    // Creates Period Control
                return false;
            }
            
            if (openPeriods) {
            	try {
            		period.changeStatus(MPeriodControl.PERIODACTION_OpenPeriod);
            	} catch (Exception e) {
            		return false;
            	}
            }
        }

        return true;
    }    // createStdPeriods
    
    /**
     * Crea los controles de períodos de todos los períodos que tiene este año.
     */
    public void createControlsOfAllPeriods() {
    	// Para cada período de este año, se crean los controles de períodos
    	// de los tipos de documento base.
    	for (MPeriod period : getPeriods(true)) {
			period.createPeriodControls();
		}    	
    }
    
    /**
     * Retorna los Periodos que tiene asociado este Año.
     * @param reload Indica si se deben recargar los datos a partir de la BD.
     * @return Lista de <code>MPeriod</code>
     */
    public List<MPeriod> getPeriods(boolean reload) {
    	if (reload || m_periods == null) {
    		// Se recarga la lista de pedidos.
    		m_periods = new ArrayList<MPeriod>();
    		
    		String sql = "SELECT * FROM C_Period WHERE C_Year_ID = ?";
    		PreparedStatement pstmt = null;
    		ResultSet rs = null;
    		
    		try {
    			pstmt = DB.prepareStatement(sql, get_TrxName());
    			pstmt.setInt(1, getC_Year_ID());
    			rs = pstmt.executeQuery();
    			MPeriod period = null;
    			while (rs.next()) {
    				period = new MPeriod(getCtx(), rs, get_TrxName());
    				m_periods.add(period);
    			}
    			
        	} catch (SQLException e) {
        		log.log(Level.SEVERE, "Error loading Periods. C_Year_ID = " + getC_Year_ID(), e);
    		} finally {
    			try {
    				if (rs != null) rs.close();
    				if (pstmt != null) pstmt.close();
    			} catch (Exception e) {	}
    		}
    	}
    	
    	return m_periods;
    }

    /**
     * Retorna los Periodos que tiene asociado este Año.
     * @return Lista de <code>MPeriod</code>
     */
    public List<MPeriod> getPeriods() {
    	MCalendar calendar = MCalendar.get(getCtx(), getC_Calendar_ID());
    	return getPeriods(!calendar.isCacheEnabled());
    }

	@Override
	public Date getDateFrom() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DATE, 1);
		calendar.set(Calendar.MONTH, 0);
		calendar.set(Calendar.YEAR, getYearAsInt());
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return new Timestamp(calendar.getTimeInMillis());
	}

	@Override
	public int getDateField() {
		return Calendar.YEAR;
	}

	@Override
	public Date getDateTo() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DATE, 31);
		calendar.set(Calendar.MONTH, 11);
		calendar.set(Calendar.YEAR, getYearAsInt());
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}

	@Override
	public String getITimeDescription() {
		return getYear();
	}

	@Override
	public boolean isIncludedInPeriod(Timestamp date) {
		// Si la fecha de inicio del periodo es menor o igual que la fecha actual y si la fecha de fin es mayor o igual 
		// que la fecha actual retorna true, en caso contrario retorna false.  
		return ( (getDateFrom().compareTo(date) <= 0) && (date.compareTo(getDateTo()) <= 0) );
	}

	@Override
	public Integer getDaysCount() {
		// FIXME: Está bien que se tome siempre 365 días para cada año? Y para
		// bisiestos?
		return 365;
	}

	@Override
	public int getDayField() {
		return Calendar.DAY_OF_YEAR;
	}
}    // MYear



/*
 *  @(#)MYear.java   02.07.07
 * 
 *  Fin del fichero MYear.java
 *  
 *  Versión 2.2
 *
 */
