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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.logging.Level;

import org.openXpertya.model.MPaySelection;
import org.openXpertya.model.MPaySelectionCheck;
import org.openXpertya.model.MPaySelectionLine;
import org.openXpertya.util.DB;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class PaySelectionCreateCheck extends SvrProcess {

    /** Descripción de Campos */

    private String p_PaymentRule = null;

    /** Descripción de Campos */

    private int p_C_PaySelection_ID = 0;

    /** Descripción de Campos */

    private ArrayList m_list = new ArrayList();

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
            } else if( name.equals( "PaymentRule" )) {
                p_PaymentRule = ( String )para[ i ].getParameter();
            } else {
                log.log( Level.SEVERE,"prepare - Unknown Parameter: " + name );
            }
        }

        p_C_PaySelection_ID = getRecord_ID();
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
        log.info( "doIt - C_PaySelection_ID=" + p_C_PaySelection_ID + ", PaymentRule=" + p_PaymentRule );

        MPaySelection psel = new MPaySelection( getCtx(),p_C_PaySelection_ID,get_TrxName());

        if( psel.getID() == 0 ) {
            throw new IllegalArgumentException( "Not found C_PaySelection_ID=" + p_C_PaySelection_ID );
        }

        if( psel.isProcessed()) {
            throw new IllegalArgumentException( "@Processed@" );
        }

        if( p_PaymentRule == null ) {
            throw new IllegalArgumentException( "No PaymentRule" );
        }

        //

        MPaySelectionLine[] lines = psel.getLines( false );

        for( int i = 0;i < lines.length;i++ ) {
            MPaySelectionLine line = lines[ i ];

            if( !line.isActive() || line.isProcessed()) {
                continue;
            }

            createCheck( line );
        }

        //

        psel.setProcessed( true );
        psel.save();
        //Añadido por Conserti para que el check de pagado se ponga automaticamente en la factura de la/las factuaras 
        //de las que acabamos de generar el pago.
        String sql="Select c_invoice_id from c_payselectionline where c_payselection_id="+p_C_PaySelection_ID;
        PreparedStatement pstmt           = null;
        try {
            pstmt = DB.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            int no=0;
            while( rs.next()) {
            	//Para cada factura de la que acabamos de generar el pago, modificamos en la tabla c_invoice , el "Check"
            	//que indica si esta pagada o no.
            	String updateInvoice= "Update c_invoice set ispaid='Y', Updated=Sysdate where c_invoice_id="+rs.getInt("C_Invoice_ID");
            	no = DB.executeUpdate(updateInvoice);
            }
        } catch( Exception e ) {
            log.log( Level.SEVERE,"doIt - " + sql,e );
        }

        return "@C_PaySelectionCheck_ID@ - #" + m_list.size();
    }    // doIt

    /**
     * Descripción de Método
     *
     *
     * @param line
     */

    private void createCheck( MPaySelectionLine line ) {

        // Try to find one

        for( int i = 0;i < m_list.size();i++ ) {
            MPaySelectionCheck check = ( MPaySelectionCheck )m_list.get( i );

            // Add to existing

            if( check.getC_BPartner_ID() == line.getInvoice().getC_BPartner_ID()) {
                check.addLine( line );
                check.setProcessed(true);
                check.setIsReceipt(true);
                if( !check.save()) {
                    throw new IllegalStateException( "Cannot Save MPaySelectionCheck" );
                }
                
                line.setC_PaySelectionCheck_ID( check.getC_PaySelectionCheck_ID());
                line.setProcessed( true );

                if( !line.save()) {
                    throw new IllegalStateException( "Cannot Save MPaySelectionLine" );
                }

                return;
            }
        }

        // Create new

        MPaySelectionCheck check = new MPaySelectionCheck( line,p_PaymentRule );
        check.setProcessed(true);
        check.setIsReceipt(true);
        if( !check.save()) {
            throw new IllegalStateException( "Cannot Save MPaySelectionCheck" );
        }
        
        line.setC_PaySelectionCheck_ID( check.getC_PaySelectionCheck_ID());
        line.setProcessed( true );

        if( !line.save()) {
            throw new IllegalStateException( "Cannot Save MPaySelectionLine" );
        }

        m_list.add( check );
        // createCheck
    
    }
 
}    // PaySelectionCreateCheck



/*
 *  @(#)PaySelectionCreateCheck.java   02.07.07
 * 
 *  Fin del fichero PaySelectionCreateCheck.java
 *  
 *  Versión 2.2
 *
 */
