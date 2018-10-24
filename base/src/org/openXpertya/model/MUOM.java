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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.util.CCache;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Ini;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MUOM extends X_C_UOM {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param C_UOM_ID
     * @param trxName
     */

    public MUOM( Properties ctx,int C_UOM_ID,String trxName ) {
        super( ctx,C_UOM_ID,trxName );
    }    // UOM

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MUOM( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // UOM
    
    /**
     * Get uoms of client 
     * @param ctx
     * @param trxName
     * @return
     */
    public static List<MUOM> getOfClient(Properties ctx, String trxName){
    	//script sql
    	String sql = "SELECT * FROM c_uom WHERE ad_client_id = ?"; 
    		
    	List<MUOM> list = new ArrayList<MUOM>();
    	PreparedStatement ps = null;
    	ResultSet rs = null;
    	
    	try {
			ps = DB.prepareStatement(sql, trxName);
			//set ad_client
			ps.setInt(1, Env.getAD_Client_ID(ctx));
			rs = ps.executeQuery();
			
			while(rs.next()){
				list.add(new MUOM(ctx,rs,trxName));				
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
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer( "UOM[" );

        sb.append( "ID=" ).append( getID()).append( ", Name=" ).append( getName());

        return sb.toString();
    }    // toString

    /**
     * Descripción de Método
     *
     *
     * @param qty
     * @param stdPrecision
     *
     * @return
     */

    public BigDecimal round( BigDecimal qty,boolean stdPrecision ) {
        int precision = getStdPrecision();

        if( !stdPrecision ) {
            precision = getCostingPrecision();
        }

        if( qty.scale() > precision ) {
            return qty.setScale( getStdPrecision(),BigDecimal.ROUND_HALF_UP );
        }

        return qty;
    }    // round

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isMinute() {
        return X12_MINUTE.equals( getX12DE355());
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isHour() {
        return X12_HOUR.equals( getX12DE355());
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isDay() {
        return X12_DAY.equals( getX12DE355());
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isWorkDay() {
        return X12_DAY_WORK.equals( getX12DE355());
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isWeek() {
        return X12_WEEK.equals( getX12DE355());
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isMonth() {
        return X12_MONTH.equals( getX12DE355());
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isWorkMonth() {
        return X12_MONTH_WORK.equals( getX12DE355());
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isYear() {
        return X12_YEAR.equals( getX12DE355());
    }

    /** Descripción de Campos */

    static final String X12_MINUTE = "MJ";

    /** Descripción de Campos */

    static final String X12_HOUR = "HR";

    /** Descripción de Campos */

    static final String X12_DAY = "DA";

    /** Descripción de Campos */

    static final String X12_DAY_WORK = "WD";

    /** Descripción de Campos */

    static final String X12_WEEK = "WK";

    /** Descripción de Campos */

    static final String X12_MONTH = "MO";

    /** Descripción de Campos */

    static final String X12_MONTH_WORK = "WM";

    /** Descripción de Campos */

    static final String X12_YEAR = "YR";

    /** Descripción de Campos */

    private static CLogger s_log = CLogger.getCLogger( MUOM.class );

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     *
     * @return
     */

    public static int getMinute_UOM_ID( Properties ctx ) {
        if( Ini.isClient()) {
            Iterator it = s_cache.values().iterator();

            while( it.hasNext()) {
                MUOM uom = ( MUOM )it.next();

                if( uom.isMinute()) {
                    return uom.getC_UOM_ID();
                }
            }
        }

        // Server

        int    C_UOM_ID = 0;
        String sql      = "SELECT C_UOM_ID FROM C_UOM " + "WHERE IsActive='Y' AND X12DE355='MJ'";    // HardCoded

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql );
            ResultSet         rs    = pstmt.executeQuery();

            if( rs.next()) {
                C_UOM_ID = rs.getInt( 1 );
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            s_log.log( Level.SEVERE,"getMinute_UOM_ID",e );
        }

        return C_UOM_ID;
    }    // getMinute_UOM_ID

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     *
     * @return
     */

    public static int getDefault_UOM_ID( Properties ctx ) {
        String sql = "SELECT C_UOM_ID " + "FROM C_UOM " + "WHERE AD_Client_ID IN (0,?) " + "ORDER BY IsDefault DESC, AD_Client_ID DESC, C_UOM_ID";

        return DB.getSQLValue( null,sql,Env.getAD_Client_ID( ctx ));
    }    // getDefault_UOM_ID

    /** Descripción de Campos */

    private static CCache s_cache = new CCache( "C_UOM",30 );

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param C_UOM_ID
     *
     * @return
     */

    public static MUOM get( Properties ctx,int C_UOM_ID ) {
        if( s_cache.size() == 0 ) {
            loadUOMs( ctx );
        }

        //

        Integer ii  = new Integer( C_UOM_ID );
        MUOM    uom = ( MUOM )s_cache.get( ii );

        if( uom != null ) {
            return uom;
        }

        //

        uom = new MUOM( ctx,C_UOM_ID,null );
        s_cache.put( new Integer( C_UOM_ID ),uom );

        return uom;
    }    // getUOMfromCache

    /**
	 * @param ctx
	 * @param uomID
	 * @param qty
	 * @param trxName
	 * @return true si se permite ingresar decimales en la cantidad parámetro o
	 *         no se permite pero no tiene decimaes mayores a 0, false caso
	 *         contrario
	 */
    public static boolean isAllowedQty(Properties ctx, Integer uomID, BigDecimal qty, String trxName){
    	boolean allowedQty = true;
		// Si no permite decimales, entonces verificar si existen decimales en
		// la cantidad parámetro
		int intQty = qty.intValue();
		if (qty.subtract(new BigDecimal(intQty)).compareTo(BigDecimal.ZERO) > 0) {
        	MUOM uom = MUOM.get(ctx, uomID);
        	allowedQty = uom.isAllowDecimals();
    	}
    	return allowedQty;
    }
    
    /**
     * Descripción de Método
     *
     *
     * @param ctx
     */

    private static void loadUOMs( Properties ctx ) {
        String sql = MRole.getDefault( ctx,false ).addAccessSQL( "SELECT * FROM C_UOM " + "WHERE IsActive='Y'","C_UOM",MRole.SQL_NOTQUALIFIED,MRole.SQL_RO );

        PreparedStatement pstmt    = null;
        ResultSet         rs       = null;
        
        try {
            pstmt = DB.prepareStatement( sql );
            rs = pstmt.executeQuery();

            while( rs.next()) {
                MUOM uom = new MUOM( ctx,rs,null );
                s_cache.put( new Integer( uom.getC_UOM_ID()),uom );
            }
        } catch( SQLException e ) {
            s_log.log( Level.SEVERE,"UOM.loadUOMs",e );
        } finally {
        	try {
        		if (rs != null) rs.close();
        		if (pstmt != null) pstmt.close();
        	} catch (Exception e) {	}
        }
    }    // loadUOMs
    
    /**
     * Carga la instancia de la traducción para el idioma del contexto, para
     * esta unidad de medida.
     */
    private void loadUOMTransaltion() {
    	X_C_UOM_Trl trl = null;
        if(!Env.isBaseLanguage(Env.getCtx(),"C_UOM")) {
	    	// Consulta que obtiene la traducción para la UM según el idioma
	    	// seteado en el contexto.
	    	String language = Env.getAD_Language(getCtx());
	    	String sql = "SELECT * FROM C_UOM_Trl WHERE C_UOM_ID = ? AND AD_Language = ?"; 
	    	
	    	PreparedStatement pstmt = null;
	        ResultSet         rs    = null;
	        try {
	        	pstmt = DB.prepareStatement(sql);
	        	pstmt.setInt(1, getC_UOM_ID());
	        	pstmt.setString(2, language);
	        	rs = pstmt.executeQuery();
	        	if (rs.next()) {
	        		trl = new X_C_UOM_Trl(getCtx(), rs, get_TrxName());
	        	}
	        } catch (Exception e) {
	        	s_log.log( Level.SEVERE,"Cannot get UOM Translation. C_UOM_ID=" + getC_UOM_ID() + " Language=" + language,e );
	        } finally {
	        	try {
	        		if (rs != null) rs.close();
	        		if (pstmt != null) pstmt.close();
	        	} catch (Exception e) {	}
	        }
        }
        uomTranslation = trl;
    }
    
    /** Traducción de esta UM en el idioma del contexto. Si no existe la 
     * traducción este atributo es <code>null</code>*/
    private X_C_UOM_Trl uomTranslation = null;

    /**
     * @return Devuelve la traducción al idioma del contexto para
     * esta UM.
     */
    private X_C_UOM_Trl getTrl() {
    	if (uomTranslation == null) {
    		loadUOMTransaltion();
    	}
    	return uomTranslation;
    }

	@Override
	public String getDescription() {
		String description = super.getDescription();
		// Si existe la traducción se devuelve la descripción según la traducción
		if (getTrl() != null) {
			description = getTrl().getDescription();
		}
		return description;
	}

	@Override
	public String getName() {
		String name = super.getName();
		// Si existe la traducción se devuelve el nombre según la traducción
		if (getTrl() != null) {
			name = getTrl().getName();
		}
		return name;
	}

	@Override
	public String getUOMSymbol() {
		String uomSymbol = super.getUOMSymbol();
		// Si existe la traducción se devuelve el símbolo según la traducción
		if (getTrl() != null) {
			uomSymbol = getTrl().getUOMSymbol();
		}
		return uomSymbol;
	}

	
	/**
	 * 	Get Precision
	 * 	@param ctx context
	 *	@param C_UOM_ID ID
	 * 	@return Precision
	 */
	public static int getPrecision (Properties ctx, int C_UOM_ID)
	{
		MUOM uom = get(ctx, C_UOM_ID);
		return uom.getStdPrecision();
	}	//	getPrecision
	

}    // MUOM



/*
 *  @(#)MUOM.java   02.07.07
 * 
 *  Fin del fichero MUOM.java
 *  
 *  Versión 2.2
 *
 */
