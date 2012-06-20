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

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Properties;

import org.openXpertya.util.DB;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MRecurring extends X_C_Recurring {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param C_Recurring_ID
     * @param trxName
     */

    public MRecurring( Properties ctx,int C_Recurring_ID,String trxName ) {
        super( ctx,C_Recurring_ID,trxName );

        if( C_Recurring_ID == 0 ) {

            // setC_Recurring_ID (0);          //      PK

            setDateNextRun( new Timestamp( System.currentTimeMillis()));
            setFrequencyType( FREQUENCYTYPE_Monthly );
            setFrequency( 1 );

            // setName (null);
            // setRecurringType (null);

            setRunsMax( 1 );
            setRunsRemaining( 0 );
        }
    }    // MRecurring

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MRecurring( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MRecurring

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer( "MRecurring[" ).append( getID()).append( "," ).append( getName());

        if( getRecurringType().equals( MRecurring.RECURRINGTYPE_Order )) {
            sb.append( ",C_Order_ID=" ).append( getC_Order_ID());
        } else if( getRecurringType().equals( MRecurring.RECURRINGTYPE_Invoice )) {
            sb.append( ",C_Invoice_ID=" ).append( getC_Invoice_ID());
        } else if( getRecurringType().equals( MRecurring.RECURRINGTYPE_Project )) {
            sb.append( ",C_Project_ID=" ).append( getC_Project_ID());
        } else if( getRecurringType().equals( MRecurring.RECURRINGTYPE_GLJournal )) {
            sb.append( ",GL_JournalBatch_ID=" ).append( getGL_JournalBatch_ID());
        }

        sb.append( ",Fequency=" ).append( getFrequencyType()).append( "*" ).append( getFrequency());
        sb.append( "]" );

        return sb.toString();
    }    // toString

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String executeRun() {
        Timestamp dateDoc = getDateNextRun();

        if( !calculateRuns()) {
            throw new IllegalStateException( "No Runs Left" );
        }

        // log

        MRecurringRun run = new MRecurringRun( getCtx(),this );
        String        msg = "@Created@ ";

        // Copy

        if( getRecurringType().equals( MRecurring.RECURRINGTYPE_Order )) {
            MOrder from  = new MOrder( getCtx(),getC_Order_ID(),get_TrxName());
            MOrder order = MOrder.copyFrom( from,dateDoc,from.getC_DocType_ID(),from.isSOTrx(),false,false,get_TrxName());

            run.setC_Order_ID( order.getC_Order_ID());
            msg += order.getDocumentNo();
        } else if( getRecurringType().equals( MRecurring.RECURRINGTYPE_Invoice )) {
            MInvoice from = new MInvoice( getCtx(),getC_Invoice_ID(),get_TrxName());
            MInvoice invoice = MInvoice.copyFrom( from,dateDoc,from.getC_DocType_ID(),from.isSOTrx(),false,get_TrxName(),false );

            run.setC_Invoice_ID( invoice.getC_Invoice_ID());
            msg += invoice.getDocumentNo();
        } else if( getRecurringType().equals( MRecurring.RECURRINGTYPE_Project )) {
            MProject project = MProject.copyFrom( getCtx(),getC_Project_ID(),dateDoc,get_TrxName());

            run.setC_Project_ID( project.getC_Project_ID());
            msg += project.getValue();
        } else if( getRecurringType().equals( MRecurring.RECURRINGTYPE_GLJournal )) {
            MJournalBatch journal = MJournalBatch.copyFrom( getCtx(),getGL_JournalBatch_ID(),dateDoc,get_TrxName());

            run.setGL_JournalBatch_ID( journal.getGL_JournalBatch_ID());
            msg += journal.getDocumentNo();
        } else {
            return "Invalid @RecurringType@ = " + getRecurringType();
        }

        run.save( get_TrxName());

        //

        setDateLastRun( run.getUpdated());
        setRunsRemaining( getRunsRemaining() - 1 );
        setDateNextRun();
        save( get_TrxName());

        return msg;
    }    // execureRun

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private boolean calculateRuns() {
        String sql = "SELECT COUNT(*) FROM C_Recurring_Run WHERE C_Recurring_ID=?";
        int current   = DB.getSQLValue( get_TrxName(),sql,getC_Recurring_ID());
        int remaining = getRunsMax() - current;

        setRunsRemaining( remaining );
        save();

        return remaining > 0;
    }    // calculateRuns

    /**
     * Descripción de Método
     *
     */

    private void setDateNextRun() {
        if( getFrequency() < 1 ) {
            setFrequency( 1 );
        }

        int      frequency = getFrequency();
        Calendar cal       = Calendar.getInstance();

        cal.setTime( getDateNextRun());

        //

        if( getFrequencyType().equals( FREQUENCYTYPE_Daily )) {
            cal.add( Calendar.DAY_OF_YEAR,frequency );
        } else if( getFrequencyType().equals( FREQUENCYTYPE_Weekly )) {
            cal.add( Calendar.WEEK_OF_YEAR,frequency );
        } else if( getFrequencyType().equals( FREQUENCYTYPE_Monthly )) {
            cal.add( Calendar.MONTH,frequency );
        } else if( getFrequencyType().equals( FREQUENCYTYPE_Quarterly )) {
            cal.add( Calendar.MONTH,3 * frequency );
        }

        Timestamp next = new Timestamp( cal.getTimeInMillis());

        setDateNextRun( next );
    }    // setDateNextRun
}    // MRecurring



/*
 *  @(#)MRecurring.java   02.07.07
 * 
 *  Fin del fichero MRecurring.java
 *  
 *  Versión 2.2
 *
 */
