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
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.sla.SLACriteria;
import org.openXpertya.util.CCache;
import org.openXpertya.util.DB;
import org.openXpertya.util.ErrorOXPSystem;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MSLACriteria extends X_PA_SLA_Criteria {

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param PA_SLA_Criteria_ID
     * @param trxName
     *
     * @return
     */

    public static MSLACriteria get( Properties ctx,int PA_SLA_Criteria_ID,String trxName ) {
        Integer      key      = new Integer( PA_SLA_Criteria_ID );
        MSLACriteria retValue = ( MSLACriteria )s_cache.get( key );

        if( retValue != null ) {
            return retValue;
        }

        retValue = new MSLACriteria( ctx,PA_SLA_Criteria_ID,trxName );

        if( retValue.getID() != 0 ) {
            s_cache.put( key,retValue );
        }

        return retValue;
    }    // get

    /** Descripción de Campos */

    private static CCache s_cache = new CCache( "PA_SLA_Criteria",20 );

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param PA_SLA_Criteria_ID
     * @param trxName
     */

    public MSLACriteria( Properties ctx,int PA_SLA_Criteria_ID,String trxName ) {
        super( ctx,PA_SLA_Criteria_ID,trxName );
    }    // MSLACriteria

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MSLACriteria( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MSLACriteria

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MSLAGoal[] getGoals() {
        String sql = "SELECT * FROM PA_SLA_Goal " + "WHERE PA_SLA_Criteria_ID=?";
        ArrayList         list  = new ArrayList();
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql );
            pstmt.setInt( 1,getPA_SLA_Criteria_ID());

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                list.add( new MSLAGoal( getCtx(),rs,get_TrxName()));
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"getGoals",e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        MSLAGoal[] retValue = new MSLAGoal[ list.size()];

        list.toArray( retValue );

        return retValue;
    }    // getGoals

    /**
     * Descripción de Método
     *
     *
     * @return
     *
     * @throws Exception
     */

    public SLACriteria newInstance() throws Exception {
        if( (getClassname() == null) || (getClassname().length() == 0) ) {
            throw new ErrorOXPSystem( "No SLA Criteria Classname" );
        }

        try {
            Class       clazz    = Class.forName( getClassname());
            SLACriteria retValue = ( SLACriteria )clazz.newInstance();

            return retValue;
        } catch( Exception e ) {
            throw new ErrorOXPSystem( "Could not intsnciate SLA Criteria",e );
        }
    }    // newInstance
}    // MSLACriteria



/*
 *  @(#)MSLACriteria.java   02.07.07
 * 
 *  Fin del fichero MSLACriteria.java
 *  
 *  Versión 2.2
 *
 */
