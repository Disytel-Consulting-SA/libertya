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
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MPeriodControl extends X_C_PeriodControl {

	private static CLogger		s_log = CLogger.getCLogger (MPeriodControl.class);
	
    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param C_PeriodControl_ID
     * @param trxName
     */

    public MPeriodControl( Properties ctx,int C_PeriodControl_ID,String trxName ) {
        super( ctx,C_PeriodControl_ID,trxName );

        if( C_PeriodControl_ID == 0 ) {

            // setC_Period_ID (0);
            // setDocBaseType (null);

            setPeriodAction( PERIODACTION_NoAction );
            setPeriodStatus( PERIODSTATUS_NeverOpened );
        }
    }    // MPeriodControl

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MPeriodControl( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MPeriodControl

    /**
     * Constructor de la clase ...
     *
     *
     * @param period
     * @param DocBaseType
     */

    public MPeriodControl( MPeriod period,String DocBaseType ) {
        this( period.getCtx(),period.getAD_Client_ID(),period.getC_Period_ID(),DocBaseType,period.get_TrxName());
    }    // MPeriodControl

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param AD_Client_ID
     * @param C_Period_ID
     * @param DocBaseType
     * @param trxName
     */

    public MPeriodControl( Properties ctx,int AD_Client_ID,int C_Period_ID,String DocBaseType,String trxName ) {
        this( ctx,0,trxName );
        setClientOrg( AD_Client_ID,0 );
        setC_Period_ID( C_Period_ID );
        setDocBaseType( DocBaseType );
    }    // MPeriodControl

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isOpen() {
        return PERIODSTATUS_Open.equals( getPeriodStatus());
    }    // isOpen

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer( "MPeriodControl[" );

        sb.append( getID()).append( "," ).append( getDocBaseType()).append( ",Status=" ).append( getPeriodStatus()).append( "]" );

        return sb.toString();
    }    // toString
    
    /**
     * Retorna una lista con todos los controles de períodos que tiene asociado un
     * dterminado período.
     * @param c_Period_ID Período al que pertenecen los controles
     * @param trxName Transacción a utilizar.
     * @return Lista de <code>MPeriodControl</code>
     */
    public static List<MPeriodControl> getOfPeriod(int c_Period_ID, String trxName) {
    	List<MPeriodControl> list = new ArrayList<MPeriodControl>();
    	String sql = "SELECT * FROM C_PeriodControl WHERE C_Period_ID = ?";
    	PreparedStatement pstmt = null;
    	ResultSet rs = null;
    	
    	try {
    		pstmt = DB.prepareStatement(sql, trxName);
    		pstmt.setInt(1, c_Period_ID);
    		rs = pstmt.executeQuery();
    		MPeriodControl periodControl = null;
    		// Se recorren todos los controles del período y se agregan a la lista.
    		while (rs.next()) {
    			periodControl = new MPeriodControl(Env.getCtx(), rs, trxName);
    			list.add(periodControl);
    		}
    		
    	} catch (SQLException e) {
    		s_log.log(Level.SEVERE, "Error loading PeriodControls. C_Period_ID = " + c_Period_ID, e);
		} finally {
			try {
				if (rs != null) rs.close();
				if (pstmt != null) pstmt.close();
			} catch (Exception e) {	}
		}
		
		return list;
    }
}    // MPeriodControl



/*
 *  @(#)MPeriodControl.java   02.07.07
 * 
 *  Fin del fichero MPeriodControl.java
 *  
 *  Versión 2.2
 *
 */
