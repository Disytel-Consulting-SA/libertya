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
import org.openXpertya.model.MPayment;
import org.openXpertya.util.DB;
import org.openXpertya.util.ErrorUsuarioOXP;
import org.openXpertya.util.Msg;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class BPartnerValidate extends SvrProcess {

    /** Descripción de Campos */

    int p_C_BPartner_ID = 0;

    /** Descripción de Campos */

    int p_C_BP_Group_ID = 0;

    /**
     * Descripción de Método
     *
     */

    protected void prepare() {
        p_C_BPartner_ID = getRecord_ID();

        ProcessInfoParameter[] para = getParameter();

        for( int i = 0;i < para.length;i++ ) {
            String name = para[ i ].getParameterName();

            if( para[ i ].getParameter() == null ) {
                ;
            } else if( name.equals( "C_BPartner_ID" )) {
                p_C_BPartner_ID = para[ i ].getParameterAsInt();
            } else if( name.equals( "C_BP_Group_ID" )) {
                p_C_BP_Group_ID = para[ i ].getParameterAsInt();
            } else {
                log.log( Level.SEVERE,"prepare - Unknown Parameter: " + name );
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
        log.info( "C_BPartner_ID=" + p_C_BPartner_ID + ", C_BP_Group_ID=" + p_C_BP_Group_ID );

        if( (p_C_BPartner_ID == 0) && (p_C_BP_Group_ID == 0) ) {
            throw new ErrorUsuarioOXP( "No Business Partner/Group selected" );
        }

        if( p_C_BP_Group_ID == 0 ) {
            MBPartner bp = new MBPartner( getCtx(),p_C_BPartner_ID,get_TrxName());

            if( bp.getID() == 0 ) {
                throw new ErrorUsuarioOXP( "Business Partner not found - C_BPartner_ID=" + p_C_BPartner_ID );
            }

            checkBP( bp );
        } else {
            String sql = "SELECT * FROM C_BPartner WHERE C_BP_Group_ID=? AND IsActive='Y'";
            PreparedStatement pstmt = null;

            try {
                pstmt = DB.prepareStatement( sql,get_TrxName());
                pstmt.setInt( 1,p_C_BP_Group_ID );

                ResultSet rs = pstmt.executeQuery();

                while( rs.next()) {
                    MBPartner bp = new MBPartner( getCtx(),rs,get_TrxName());

                    checkBP( bp );
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
        }

        //

        return "OK";
    }    // doIt

    /**
     * Descripción de Método
     *
     *
     * @param bp
     */

    private void checkBP( MBPartner bp ) {

        // See also VMerge.postMerge

        checkPayments( bp );
        checkInvoices( bp );

        //

        bp.setTotalOpenBalance();
        bp.save();
        addLog( 0,null,null,bp.getName() + ":" );
        addLog( 0,null,bp.getSO_CreditUsed(),Msg.getElement( getCtx(),"SO_CreditUsed" ));
        addLog( 0,null,bp.getTotalOpenBalance(),Msg.getElement( getCtx(),"TotalOpenBalance" ));
        addLog( 0,null,bp.getActualLifeTimeValue(),Msg.getElement( getCtx(),"ActualLifeTimeValue" ));
    }    // checkBP

    /**
     * Descripción de Método
     *
     *
     * @param bp
     */

    private void checkPayments( MBPartner bp ) {

        // See also VMerge.postMerge

        int        changed  = 0;
        MPayment[] payments = MPayment.getOfBPartner( getCtx(),bp.getC_BPartner_ID(),get_TrxName());

        for( int i = 0;i < payments.length;i++ ) {
            MPayment payment = payments[ i ];

            if( payment.testAllocation()) {
                payment.save();
                changed++;
            }
        }

        addLog( 0,null,new BigDecimal( payments.length ),Msg.getElement( getCtx(),"C_Payment_ID" ));
        addLog( 0,null,new BigDecimal( changed ),Msg.getElement( getCtx(),"Updated" ));
    }    // checkPayments

    /**
     * Descripción de Método
     *
     *
     * @param bp
     */

    private void checkInvoices( MBPartner bp ) {

        // See also VMerge.postMerge

        int        changed  = 0;
        MInvoice[] invoices = MInvoice.getOfBPartner( getCtx(),bp.getC_BPartner_ID(),get_TrxName());

        for( int i = 0;i < invoices.length;i++ ) {
            MInvoice invoice = invoices[ i ];

            if( invoice.testAllocation()) {
                invoice.save();
                changed++;
            }
        }

        addLog( 0,null,new BigDecimal( invoices.length ),Msg.getElement( getCtx(),"C_Invoice_ID" ));
        addLog( 0,null,new BigDecimal( changed ),Msg.getElement( getCtx(),"Updated" ));
    }    // checkInvoices
}    // BPartnerValidate



/*
 *  @(#)BPartnerValidate.java   02.07.07
 * 
 *  Fin del fichero BPartnerValidate.java
 *  
 *  Versión 2.2
 *
 */
