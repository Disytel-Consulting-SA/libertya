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



package org.openXpertya.wf;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.model.MProcessPara;
import org.openXpertya.model.X_AD_WF_Node_Para;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MWFNodePara extends X_AD_WF_Node_Para {

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param AD_WF_Node_ID
     *
     * @return
     */

    public static MWFNodePara[] getParameters( Properties ctx,int AD_WF_Node_ID ) {
        ArrayList list = new ArrayList();
        String    sql  = "SELECT * FROM AD_WF_Node_Para " + "WHERE AD_WF_Node_ID=?";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql );
            pstmt.setInt( 1,AD_WF_Node_ID );

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                list.add( new MWFNodePara( ctx,rs,null ));
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            s_log.log( Level.SEVERE,"getParameters",e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        MWFNodePara[] retValue = new MWFNodePara[ list.size()];

        list.toArray( retValue );

        return retValue;
    }    // getParameters

    /** Descripción de Campos */

    private static CLogger s_log = CLogger.getCLogger( MWFNodePara.class );

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param id
     * @param trxName
     */

    public MWFNodePara( Properties ctx,int id,String trxName ) {
        super( ctx,id,trxName );
    }    // MWFNodePara

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MWFNodePara( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MWFNodePara

    /** Descripción de Campos */

    private MProcessPara m_processPara = null;

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MProcessPara getProcessPara() {
        if( m_processPara == null ) {
            m_processPara = new MProcessPara( getCtx(),getAD_Process_Para_ID(),get_TrxName());
        }

        return m_processPara;
    }    // getProcessPara

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getAttributeName() {
        String an = super.getAttributeName();

        if( (an == null) || ((an.length() == 0) && (getAD_Process_Para_ID() != 0)) ) {
            an = getProcessPara().getColumnName();
            setAttributeName( an );
            save();
        }

        return an;
    }    // getAttributeName

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getDisplayType() {
        return getProcessPara().getAD_Reference_ID();
    }    // getDisplayType

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isMandatory() {
        return getProcessPara().isMandatory();
    }    // isMandatory

    /**
     * Descripción de Método
     *
     *
     * @param AD_Process_Para_ID
     */

    public void setAD_Process_Para_ID( int AD_Process_Para_ID ) {
        super.setAD_Process_Para_ID( AD_Process_Para_ID );
        setAttributeName( null );
    }
}    // MWFNodePara



/*
 *  @(#)MWFNodePara.java   02.07.07
 * 
 *  Fin del fichero MWFNodePara.java
 *  
 *  Versión 2.2
 *
 */
