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



package org.openXpertya.process;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Level;

import org.openXpertya.model.MInOutLineConfirm;
import org.openXpertya.model.X_I_InOutLineConfirm;
import org.openXpertya.util.DB;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class ImportInOutConfirm extends SvrProcess {

    /** Descripción de Campos */

    private int p_AD_Client_ID = 0;

    /** Descripción de Campos */

    private boolean p_DeleteOldImported = false;

    /** Descripción de Campos */

    private int p_I_InOutLineConfirm_ID = 0;

    /**
     * Descripción de Método
     *
     */

    protected void prepare() {
        ProcessInfoParameter[] para = getParameter();

        for( int i = 0;i < para.length;i++ ) {
            String name = para[ i ].getParameterName();

            if( para[ i ].getParameter() == null ) {
                ;
            } else if( name.equals( "AD_Client_ID" )) {
                p_AD_Client_ID = (( BigDecimal )para[ i ].getParameter()).intValue();
            } else if( name.equals( "DeleteOldImported" )) {
                p_DeleteOldImported = "Y".equals( para[ i ].getParameter());
            } else {
                log.log( Level.SEVERE,"prepare - Unknown Parameter: " + name );
            }
        }

        p_I_InOutLineConfirm_ID = getRecord_ID();
    }    // prepare

    /**
     * Descripción de Método
     *
     *
     * @return
     *
     * @throws Exception
     */

    protected String doIt() throws Exception {
        log.info( "" );

        StringBuffer sql         = null;
        int          no          = 0;
        String       clientCheck = " AND AD_Client_ID=" + p_AD_Client_ID;

        // Delete Old Imported

        if( p_DeleteOldImported ) {
            sql = new StringBuffer( "DELETE I_InOutLineConfirm " + "WHERE I_IsImported='Y'" ).append( clientCheck );
            no = DB.executeUpdate( sql.toString());
            log.fine( "doIt - Delete Old Impored =" + no );
        }

        // Set IsActive, Created/Updated

        sql = new StringBuffer( "UPDATE I_InOutLineConfirm " + "SET IsActive = COALESCE (IsActive, 'Y')," + " Created = COALESCE (Created, SysDate)," + " CreatedBy = COALESCE (CreatedBy, 0)," + " Updated = COALESCE (Updated, SysDate)," + " UpdatedBy = COALESCE (UpdatedBy, 0)," + " I_ErrorMsg = NULL," + " I_IsImported = 'N' " + "WHERE I_IsImported<>'Y' OR I_IsImported IS NULL" );
        no = DB.executeUpdate( sql.toString());
        log.info( "doIt - Reset=" + no );

        // Set Client from Name

        sql = new StringBuffer( "UPDATE I_InOutLineConfirm i " + "SET AD_Client_ID=COALESCE (AD_Client_ID," ).append( p_AD_Client_ID ).append( ") " + "WHERE (AD_Client_ID IS NULL OR AD_Client_ID=0)" + " AND I_IsImported<>'Y'" );
        no = DB.executeUpdate( sql.toString());
        log.fine( "doIt - Set Client from Value=" + no );

        // Error Confirmation Line

        sql = new StringBuffer( "UPDATE I_InOutLineConfirm i " + "SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg||'ERR=Invalid Confirmation Line, '" + "WHERE (M_InOutLineConfirm_ID IS NULL OR M_InOutLineConfirm_ID=0" + " OR NOT EXISTS (SELECT * FROM M_InOutLineConfirm c WHERE i.M_InOutLineConfirm_ID=c.M_InOutLineConfirm_ID))" + " AND I_IsImported<>'Y'" ).append( clientCheck );
        no = DB.executeUpdate( sql.toString());

        if( no != 0 ) {
            log.warning( "doIt - Invalid InOutLineConfirm=" + no );
        }

        // Error Confirmation No

        sql = new StringBuffer( "UPDATE I_InOutLineConfirm i " + "SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg||'ERR=Missing Confirmation No, '" + "WHERE (ConfirmationNo IS NULL OR ConfirmationNo='')" + " AND I_IsImported<>'Y'" ).append( clientCheck );
        no = DB.executeUpdate( sql.toString());

        if( no != 0 ) {
            log.warning( "doIt - Invalid ConfirmationNo=" + no );
        }

        // Qty

        sql = new StringBuffer( "UPDATE I_InOutLineConfirm i " + "SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg||'ERR=Target<>(Confirmed+Difference+Scrapped), ' " + "WHERE EXISTS (SELECT * FROM M_InOutLineConfirm c " + "WHERE i.M_InOutLineConfirm_ID=c.M_InOutLineConfirm_ID" + " AND c.TargetQty<>(i.ConfirmedQty+i.ScrappedQty+i.DifferenceQty))" + " AND I_IsImported<>'Y'" ).append( clientCheck );
        no = DB.executeUpdate( sql.toString());

        if( no != 0 ) {
            log.warning( "doIt - Invalid Qty=" + no );
        }

        PreparedStatement pstmt = null;

        sql = new StringBuffer( "SELECT * FROM I_InOutLineConfirm " + "WHERE I_IsImported='N'" ).append( clientCheck ).append( " ORDER BY I_InOutLineConfirm_ID, I_InOutLineConfirm_ID" );
        no = 0;

        try {
            pstmt = DB.prepareStatement( sql.toString());

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                X_I_InOutLineConfirm importLine = new X_I_InOutLineConfirm( getCtx(),rs,get_TrxName());
                MInOutLineConfirm confirmLine = new MInOutLineConfirm( getCtx(),importLine.getM_InOutLineConfirm_ID(),null );

                confirmLine.setConfirmationNo( importLine.getConfirmationNo());
                confirmLine.setConfirmedQty( importLine.getConfirmedQty());
                confirmLine.setDifferenceQty( importLine.getDifferenceQty());
                confirmLine.setScrappedQty( importLine.getScrappedQty());
                confirmLine.setDescription( importLine.getDescription());

                if( confirmLine.save()) {

                    // Import

                    importLine.setI_IsImported( true );

                    if( importLine.save()) {
                        no++;
                    }
                }
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"doIt",e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        return "@Inserted@ #" + no;
    }    // doIt
}    // ImportInOutConfirm



/*
 *  @(#)ImportInOutConfirm.java   02.07.07
 * 
 *  Fin del fichero ImportInOutConfirm.java
 *  
 *  Versión 2.2
 *
 */
