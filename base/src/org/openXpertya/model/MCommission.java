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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.util.DB;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MCommission extends X_C_Commission {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param C_Commission_ID
     * @param trxName
     */

    public MCommission( Properties ctx,int C_Commission_ID,String trxName ) {
        super( ctx,C_Commission_ID,trxName );

        if( C_Commission_ID == 0 ) {

            // setName (null);
            // setC_BPartner_ID (0);
            // setC_Charge_ID (0);
            // setC_Commission_ID (0);
            // setC_Currency_ID (0);
            //

            setDocBasisType( DOCBASISTYPE_Invoice );      // I
            setFrequencyType( FREQUENCYTYPE_Monthly );    // M
            setListDetails( false );
        }
    }                                                     // MCommission

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MCommission( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MCommission

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MCommissionLine[] getLines() {
        String sql = "SELECT * FROM C_CommissionLine WHERE C_Commission_ID=? ORDER BY Line";
        ArrayList         list  = new ArrayList();
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql,get_TrxName());
            pstmt.setInt( 1,getC_Commission_ID());

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                list.add( new MCommissionLine( getCtx(),rs,get_TrxName()));
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"getLines",e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        // Convert

        MCommissionLine[] retValue = new MCommissionLine[ list.size()];

        list.toArray( retValue );

        return retValue;
    }    // getLines

    /**
     * Descripción de Método
     *
     *
     * @param DateLastRun
     */

    public void setDateLastRun( Timestamp DateLastRun ) {
        if( DateLastRun != null ) {
            super.setDateLastRun( DateLastRun );
        }
    }    // setDateLastRun

    /**
     * Descripción de Método
     *
     *
     * @param otherCom
     *
     * @return
     */

    public int copyLinesFrom( MCommission otherCom ) {
        if( otherCom == null ) {
            return 0;
        }

        MCommissionLine[] fromLines = otherCom.getLines();
        int               count     = 0;

        for( int i = 0;i < fromLines.length;i++ ) {
            MCommissionLine line = new MCommissionLine( getCtx(),0,get_TrxName());

            PO.copyValues( fromLines[ i ],line,getAD_Client_ID(),getAD_Org_ID());
            line.setC_Commission_ID( getC_Commission_ID());
            line.setC_CommissionLine_ID( 0 );    // new

            if( line.save()) {
                count++;
            }
        }

        if( fromLines.length != count ) {
            log.log( Level.SEVERE,"copyLinesFrom - Line difference - From=" + fromLines.length + " <> Saved=" + count );
        }

        return count;
    }    // copyLinesFrom
}    // MCommission



/*
 *  @(#)MCommission.java   02.07.07
 * 
 *  Fin del fichero MCommission.java
 *  
 *  Versión 2.2
 *
 */
