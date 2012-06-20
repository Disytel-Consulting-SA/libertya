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
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MMatchInv extends X_M_MatchInv {

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param M_InOutLine_ID
     * @param C_InvoiceLine_ID
     * @param trxName
     *
     * @return
     */

    public static MMatchInv[] get( Properties ctx,int M_InOutLine_ID,int C_InvoiceLine_ID,String trxName ) {
        if( (M_InOutLine_ID == 0) || (C_InvoiceLine_ID == 0) ) {
            return new MMatchInv[]{};
        }

        //

        String sql = "SELECT * FROM M_MatchInv WHERE M_InOutLine_ID=? AND C_InvoiceLine_ID=?";
        ArrayList         list  = new ArrayList();
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql,trxName );
            pstmt.setInt( 1,M_InOutLine_ID );
            pstmt.setInt( 2,C_InvoiceLine_ID );

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                list.add( new MMatchInv( ctx,rs,trxName ));
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            s_log.log( Level.SEVERE,"get",e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        MMatchInv[] retValue = new MMatchInv[ list.size()];

        list.toArray( retValue );

        return retValue;
    }    // get

    /** Descripción de Campos */

    private static CLogger s_log = CLogger.getCLogger( MMatchInv.class );

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param M_MatchInv_ID
     * @param trxName
     */

    public MMatchInv( Properties ctx,int M_MatchInv_ID,String trxName ) {
        super( ctx,M_MatchInv_ID,trxName );

        if( M_MatchInv_ID == 0 ) {

            // setDateTrx (new Timestamp(System.currentTimeMillis()));
            // setC_InvoiceLine_ID (0);
            // setM_InOutLine_ID (0);
            // setM_Product_ID (0);
            // setQty (Env.ZERO);

            setPosted( false );
            setProcessed( false );
            setProcessing( false );
        }
    }    // MMatchInv

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MMatchInv( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MMatchInv

    /**
     * Constructor de la clase ...
     *
     *
     * @param iLine
     * @param dateTrx
     * @param qty
     */

    public MMatchInv( MInvoiceLine iLine,Timestamp dateTrx,BigDecimal qty ) {
        this( iLine.getCtx(),0,iLine.get_TrxName());
        setClientOrg( iLine );
        setC_InvoiceLine_ID( iLine.getC_InvoiceLine_ID());
        setM_InOutLine_ID( iLine.getM_InOutLine_ID());

        if( dateTrx != null ) {
            setDateTrx( dateTrx );
        }
        setC_Project_ID(iLine.getC_Project_ID());
        setM_Product_ID( iLine.getM_Product_ID());
        setQty( qty );
        setProcessed( true );    // auto
    }                            // MMatchPO

    /**
     * Descripción de Método
     *
     *
     * @param newRecord
     *
     * @return
     */

    protected boolean beforeSave( boolean newRecord ) {

        // Set Trx Date

        if( getDateTrx() == null ) {
            setDateTrx( new Timestamp( System.currentTimeMillis()));
        }

        // Set Acct Date

        if( getDateAcct() == null ) {
            Timestamp ts = getNewerDateAcct();

            if( ts == null ) {
                ts = getDateTrx();
            }

            setDateAcct( ts );
        }

        return true;
    }    // beforeSave

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private Timestamp getNewerDateAcct() {
        Timestamp invoiceDate = null;
        Timestamp shipDate    = null;
        String    sql         = "SELECT i.DateAcct " + "FROM C_InvoiceLine il" + " INNER JOIN C_Invoice i ON (i.C_Invoice_ID=il.C_Invoice_ID) " + "WHERE C_InvoiceLine_ID=?";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql );
            pstmt.setInt( 1,getC_InvoiceLine_ID());

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                invoiceDate = rs.getTimestamp( 1 );
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,sql,e );
        }

        sql = "SELECT io.DateAcct " + "FROM M_InOutLine iol" + " INNER JOIN M_InOut io ON (io.M_InOut_ID=iol.M_InOut_ID) " + "WHERE iol.M_InOutLine_ID=?";

        try {
            pstmt = DB.prepareStatement( sql );
            pstmt.setInt( 1,getM_InOutLine_ID());

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                shipDate = rs.getTimestamp( 1 );
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,sql,e );
        }

        //

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        if( invoiceDate == null ) {
            return shipDate;
        }

        if( shipDate == null ) {
            return invoiceDate;
        }

        if( invoiceDate.after( shipDate )) {
            return invoiceDate;
        }

        return shipDate;
    }    // getNewerDateAcct

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    protected boolean beforeDelete() {
        if( isPosted()) {
            if( !MPeriod.isOpen( getCtx(),getDateTrx(),MDocType.DOCBASETYPE_MatchInvoice )) {
                return false;
            }

            setPosted( false );

            return MFactAcct.delete( Table_ID,getID(),get_TrxName()) >= 0;
        }

        return true;
    }    // beforeDelete

    /**
     * Descripción de Método
     *
     *
     * @param success
     *
     * @return
     */

    protected boolean afterDelete( boolean success ) {
        if( success ) {

            // Get Order and decrease invoices

            MInvoiceLine iLine = new MInvoiceLine( getCtx(),getC_InvoiceLine_ID(),get_TrxName());
            int C_OrderLine_ID = iLine.getC_OrderLine_ID();

            if( C_OrderLine_ID == 0 ) {
                MInOutLine ioLine = new MInOutLine( getCtx(),getM_InOutLine_ID(),get_TrxName());

                C_OrderLine_ID = ioLine.getC_OrderLine_ID();
            }

            // No Order Found

            if( C_OrderLine_ID == 0 ) {
                return success;
            }

            // Find MatchPO

            MMatchPO[] matches = MMatchPO.get( getCtx(),C_OrderLine_ID,getC_InvoiceLine_ID(),get_TrxName());

            for( int i = 0;i < matches.length;i++ ) {
                MMatchPO matchPO = matches[ i ];

                matchPO.delete( true );
            }
        }

        return success;
    }    // afterDelete
}    // MMatchInv



/*
 *  @(#)MMatchInv.java   02.07.07
 * 
 *  Fin del fichero MMatchInv.java
 *  
 *  Versión 2.2
 *
 */
