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

import org.openXpertya.model.MBPartner;
import org.openXpertya.model.MInvoice;
import org.openXpertya.model.MInvoiceLine;
import org.openXpertya.model.MRequest;
import org.openXpertya.model.MRequestType;
import org.openXpertya.model.MRequestUpdate;
import org.openXpertya.util.DB;
import org.openXpertya.util.ErrorOXPSystem;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class RequestInvoice extends SvrProcess {

    /** Descripción de Campos */

    private int p_R_RequestType_ID = 0;

    /** Descripción de Campos */

    private int p_R_Group_ID = 0;

    /** Descripción de Campos */

    private int p_R_Category_ID = 0;

    /** Descripción de Campos */

    private int p_C_BPartner_ID = 0;

    /** Descripción de Campos */

    private int p_M_Product_ID = 0;

    /** Descripción de Campos */

    private MInvoice m_invoice = null;

    /** Descripción de Campos */

    private int m_linecount = 0;

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
            } else if( name.equals( "R_RequestType_ID" )) {
                p_R_RequestType_ID = para[ i ].getParameterAsInt();
            } else if( name.equals( "R_Group_ID" )) {
                p_R_Group_ID = para[ i ].getParameterAsInt();
            } else if( name.equals( "R_Category_ID" )) {
                p_R_Category_ID = para[ i ].getParameterAsInt();
            } else if( name.equals( "C_BPartner_ID" )) {
                p_C_BPartner_ID = para[ i ].getParameterAsInt();
            } else if( name.equals( "M_Product_ID" )) {
                p_M_Product_ID = para[ i ].getParameterAsInt();
            } else {
                log.log( Level.SEVERE,"Unknown Parameter: " + name );
            }
        }
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
        log.info( "R_RequestType_ID=" + p_R_RequestType_ID + ", R_Group_ID=" + p_R_Group_ID + ", R_Category_ID=" + p_R_Category_ID + ", C_BPartner_ID=" + p_C_BPartner_ID + ", p_M_Product_ID=" + p_M_Product_ID );

        MRequestType type = MRequestType.get( getCtx(),p_R_RequestType_ID );

        if( type.getID() == 0 ) {
            throw new ErrorOXPSystem( "@R_RequestType_ID@ @NotFound@ " + p_R_RequestType_ID );
        }

        if( !type.isInvoiced()) {
            throw new ErrorOXPSystem( "@R_RequestType_ID@ <> @IsInvoiced@" );
        }

        String sql = "SELECT * FROM R_Request r" + " INNER JOIN R_Status s ON (r.R_Status_ID=s.R_Status_ID) " + "WHERE s.IsClosed='Y'" + " AND r.R_RequestType_ID=?";

        if( p_R_Group_ID != 0 ) {
            sql += " AND r.R_Group_ID=?";
        }

        if( p_R_Category_ID != 0 ) {
            sql += " AND r.R_Category_ID=?";
        }

        if( p_C_BPartner_ID != 0 ) {
            sql += " AND r.C_BPartner_ID=?";
        }

        sql += " AND r.IsInvoiced='Y' " + "ORDER BY C_BPartner_ID";

        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql );

            int index = 1;

            pstmt.setInt( index++,p_R_RequestType_ID );

            if( p_R_Group_ID != 0 ) {
                pstmt.setInt( index++,p_R_Group_ID );
            }

            if( p_R_Category_ID != 0 ) {
                pstmt.setInt( index++,p_R_Category_ID );
            }

            if( p_C_BPartner_ID != 0 ) {
                pstmt.setInt( index++,p_C_BPartner_ID );
            }

            ResultSet rs               = pstmt.executeQuery();
            int       oldC_BPartner_ID = 0;

            while( rs.next()) {
                MRequest request = new MRequest( getCtx(),rs,null );

                if( !request.isInvoiced()) {
                    continue;
                }

                if( oldC_BPartner_ID != request.getC_BPartner_ID()) {
                    invoiceDone();
                }

                if( m_invoice == null ) {
                    invoiceNew( request );
                    oldC_BPartner_ID = request.getC_BPartner_ID();
                }

                invoiceLine( request );
            }

            invoiceDone();

            //

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

        // R_Category_ID

        return null;
    }    // doIt

    /**
     * Descripción de Método
     *
     */

    private void invoiceDone() {

        // Close Old

        if( m_invoice != null ) {
            if( m_linecount == 0 ) {
                m_invoice.delete( false );
            } else {
                m_invoice.processIt( MInvoice.ACTION_Prepare );
                m_invoice.save();
                addLog( 0,null,m_invoice.getGrandTotal(),m_invoice.getDocumentNo());
            }
        }

        m_invoice = null;
    }    // invoiceDone

    /**
     * Descripción de Método
     *
     *
     * @param request
     */

    private void invoiceNew( MRequest request ) {
        m_invoice = new MInvoice( getCtx(),0,get_TrxName());
        m_invoice.setIsSOTrx( true );

        MBPartner partner = new MBPartner( getCtx(),request.getC_BPartner_ID(),get_TrxName());

        m_invoice.setBPartner( partner );
        m_invoice.save();
        m_linecount = 0;
    }    // invoiceNew

    /**
     * Descripción de Método
     *
     *
     * @param request
     */

    private void invoiceLine( MRequest request ) {
        MRequestUpdate[] updates = request.getUpdates( null );

        for( int i = 0;i < updates.length;i++ ) {
            BigDecimal qty = updates[ i ].getQtyInvoiced();

            if( (qty == null) || (qty.signum() == 0) ) {
                continue;
            }

            MInvoiceLine il = new MInvoiceLine( m_invoice );

            m_linecount++;
            il.setLine( m_linecount * 10 );

            //

            il.setQty( qty );

            // Product

            int M_Product_ID = updates[ i ].getM_ProductSpent_ID();

            if( M_Product_ID == 0 ) {
                M_Product_ID = p_M_Product_ID;
            }

            il.setM_Product_ID( M_Product_ID );

            //

            il.setPrice();
            il.save();
        }
    }    // invoiceLine
}    // RequestInvoice



/*
 *  @(#)RequestInvoice.java   02.07.07
 * 
 *  Fin del fichero RequestInvoice.java
 *  
 *  Versión 2.2
 *
 */
