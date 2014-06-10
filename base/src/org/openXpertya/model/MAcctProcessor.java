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

import java.sql.*;

import java.util.*;
import java.util.logging.*;

import org.openXpertya.model.MClient;
import org.openXpertya.model.X_C_AcctProcessor;
import org.openXpertya.util.*;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MAcctProcessor extends X_C_AcctProcessor implements ProcesadorOXP {

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     *
     * @return
     */

    public static MAcctProcessor[] getActive( Properties ctx ) {
        ArrayList         list  = new ArrayList();
        String            sql   = "SELECT * FROM C_AcctProcessor WHERE IsActive='Y'";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql );

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                list.add( new MAcctProcessor( ctx,rs,null ));
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            s_log.log( Level.SEVERE,"getActive",e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        MAcctProcessor[] retValue = new MAcctProcessor[ list.size()];

        list.toArray( retValue );

        return retValue;
    }    // getActive

    /** Descripción de Campos */

    private static CLogger s_log = CLogger.getCLogger( MAcctProcessor.class );

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param C_AcctProcessor_ID
     * @param trxName
     */

    public MAcctProcessor( Properties ctx,int C_AcctProcessor_ID,String trxName ) {
        super( ctx,C_AcctProcessor_ID,trxName );

        if( C_AcctProcessor_ID == 0 ) {

            // setName (null);
            // setSupervisor_ID (0);

            setFrequencyType( FREQUENCYTYPE_Hour );
            setFrequency( 1 );
            setKeepLogDays( 7 );    // 7
        }
    }                               // MAcctProcessor

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MAcctProcessor( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MAcctProcessor

    /**
     * Constructor de la clase ...
     *
     *
     * @param client
     * @param Supervisor_ID
     */

    public MAcctProcessor( MClient client,int Supervisor_ID ) {
        this( client.getCtx(),0,client.get_TrxName());
        setClientOrg( client );
        setName( client.getName() + " - " + Msg.translate( getCtx(),"C_AcctProcessor_ID" ));
        setSupervisor_ID( Supervisor_ID );
    }    // MAcctProcessor

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getServerID() {
        return "AcctProcessor" + getID();
    }    // getServerID

    /**
     * Descripción de Método
     *
     *
     * @param requery
     *
     * @return
     */

    public Timestamp getDateNextRun( boolean requery ) {
        if( requery ) {
            load( get_TrxName());
        }

        return getDateNextRun();
    }    // getDateNextRun

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public ProcesadorLogOXP[] getLogs() {
        ArrayList list = new ArrayList();
        String    sql  = "SELECT * " + "FROM C_AcctProcessorLog " + "WHERE C_AcctProcessor_ID=? " + "ORDER BY Created DESC";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql,get_TrxName());
            pstmt.setInt( 1,getC_AcctProcessor_ID());

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                list.add( new MAcctProcessorLog( getCtx(),rs,get_TrxName()));
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,sql,e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        MAcctProcessorLog[] retValue = new MAcctProcessorLog[ list.size()];

        list.toArray( retValue );

        return retValue;
    }    // getLogs

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int deleteLog() {
        if( getKeepLogDays() < 1 ) {
            return 0;
        }

		String sql = 	" DELETE FROM C_AcctProcessorLog " +
						" WHERE C_AcctProcessor_ID_ID=" + getC_AcctProcessor_ID() + 
						" AND Created < ('now'::text)::timestamp(6) - interval '" + getKeepLogDays() + " days'";
        int no = DB.executeUpdate( sql,get_TrxName());

        return no;
    }    // deleteLog
    
    
    
	public void setClientOrg(int AD_Client_ID, int AD_Org_ID) {
		super.setClientOrg(AD_Client_ID, AD_Org_ID);
	} // setClientOrg
    
}    // MAcctProcessor



/*
 *  @(#)MAcctProcessor.java   02.07.07
 * 
 *  Fin del fichero MAcctProcessor.java
 *  
 *  Versión 2.2
 *
 */
