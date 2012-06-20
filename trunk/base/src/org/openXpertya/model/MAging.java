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
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Properties;

import org.openXpertya.util.Env;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MAging extends X_T_Aging {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param AD_PInstance_ID
     * @param C_BPartner_ID
     * @param C_Currency_ID
     * @param C_Invoice_ID
     * @param C_InvoicePaySchedule_ID
     * @param C_BP_Group_ID
     * @param DueDate
     * @param IsSOTrx
     * @param trxName
     */

    public MAging( Properties ctx,int AD_PInstance_ID,int C_BPartner_ID,int C_Currency_ID,int C_Invoice_ID,int C_InvoicePaySchedule_ID,int C_BP_Group_ID,Timestamp DueDate,boolean IsSOTrx,String trxName ) {
        super( ctx,0,trxName );
        setAD_PInstance_ID( AD_PInstance_ID );
        setC_BPartner_ID( C_BPartner_ID );
        setC_Currency_ID( C_Currency_ID );
        setC_Invoice_ID( C_Invoice_ID );
        setC_InvoicePaySchedule_ID( C_InvoicePaySchedule_ID );
        setIsListInvoices( C_Invoice_ID != 0 );

        //

        setC_BP_Group_ID( C_BP_Group_ID );

        if( DueDate == null ) {
            setDueDate( new Timestamp( System.currentTimeMillis()));
        } else {
            setDueDate( DueDate );
        }

        setIsSOTrx( IsSOTrx );

        //

        setDueAmt( Env.ZERO );
        setDue0( Env.ZERO );
        setDue0_7( Env.ZERO );
        setDue0_30( Env.ZERO );
        setDue1_7( Env.ZERO );
        setDue31_60( Env.ZERO );
        setDue31_Plus( Env.ZERO );
        setDue61_90( Env.ZERO );
        setDue61_Plus( Env.ZERO );
        setDue8_30( Env.ZERO );
        setDue91_Plus( Env.ZERO );

        //

        setPastDueAmt( Env.ZERO );
        setPastDue1_7( Env.ZERO );
        setPastDue1_30( Env.ZERO );
        setPastDue31_60( Env.ZERO );
        setPastDue31_Plus( Env.ZERO );
        setPastDue61_90( Env.ZERO );
        setPastDue61_Plus( Env.ZERO );
        setPastDue8_30( Env.ZERO );
        setPastDue91_Plus( Env.ZERO );

        //

        setOpenAmt( Env.ZERO );
        setInvoicedAmt( Env.ZERO );
    }    // MAging

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MAging( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MAging

    /**
     * Descripción de Método
     *
     *
     * @param daysDue
     * @param invoicedAmt
     * @param openAmt
     */

    public void add( int daysDue,BigDecimal invoicedAmt,BigDecimal openAmt ) {
        if( invoicedAmt == null ) {
            invoicedAmt = Env.ZERO;
        }

        if( openAmt == null ) {
            openAmt = Env.ZERO;
        }

        setInvoicedAmt( getInvoicedAmt().add( invoicedAmt ));
        setOpenAmt( getOpenAmt().add( openAmt ));

        BigDecimal amt = openAmt;

        // Not due - negative

        if( daysDue <= 0 ) {
            setDueAmt( getDueAmt().add( amt ));

            if( daysDue == 0 ) {
                setDue0( getDue0().add( amt ));
            }

            if( daysDue >= -7 ) {
                setDue0_7( getDue0_7().add( amt ));
            }

            if( daysDue >= -30 ) {
                setDue0_30( getDue0_30().add( amt ));
            }

            if( (daysDue <= -1) && (daysDue >= -7) ) {
                setDue1_7( getDue1_7().add( amt ));
            }

            if( (daysDue <= -8) && (daysDue >= -30) ) {
                setDue8_30( getDue8_30().add( amt ));
            }

            if( (daysDue <= -31) && (daysDue >= -60) ) {
                setDue31_60( getDue31_60().add( amt ));
            }

            if( daysDue <= -31 ) {
                setDue31_Plus( getDue31_Plus().add( amt ));
            }

            if( (daysDue <= -61) && (daysDue >= -90) ) {
                setDue61_90( getDue61_90().add( amt ));
            }

            if( daysDue <= -61 ) {
                setDue61_Plus( getDue61_Plus().add( amt ));
            }

            if( daysDue <= -91 ) {
                setDue91_Plus( getDue91_Plus().add( amt ));
            }
        } else    // Due = positive (> 1)
        {
            setPastDueAmt( getPastDueAmt().add( amt ));

            if( daysDue <= 7 ) {
                setPastDue1_7( getPastDue1_7().add( amt ));
            }

            if( daysDue <= 30 ) {
                setPastDue1_30( getPastDue1_30().add( amt ));
            }

            if( (daysDue >= 8) && (daysDue <= 30) ) {
                setPastDue8_30( getPastDue8_30().add( amt ));
            }

            if( (daysDue >= 31) && (daysDue <= 60) ) {
                setPastDue31_60( getPastDue31_60().add( amt ));
            }

            if( daysDue >= 31 ) {
                setPastDue31_Plus( getPastDue31_Plus().add( amt ));
            }

            if( (daysDue >= 61) && (daysDue <= 90) ) {
                setPastDue61_90( getPastDue61_90().add( amt ));
            }

            if( daysDue >= 61 ) {
                setPastDue61_Plus( getPastDue61_Plus().add( amt ));
            }

            if( daysDue >= 91 ) {
                setPastDue91_Plus( getPastDue91_Plus().add( amt ));
            }
        }
    }             // add

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer( "MAging[" );

        sb.append( "AD_PInstance_ID=" ).append( getAD_PInstance_ID()).append( ",C_BPartner_ID=" ).append( getC_BPartner_ID()).append( ",C_Currency_ID=" ).append( getC_Currency_ID()).append( ",C_Invoice_ID=" ).append( getC_Invoice_ID());
        sb.append( "]" );

        return sb.toString();
    }    // toString
}    // MAging



/*
 *  @(#)MAging.java   02.07.07
 * 
 *  Fin del fichero MAging.java
 *  
 *  Versión 2.2
 *
 */
