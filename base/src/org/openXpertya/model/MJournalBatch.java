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
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.process.DocAction;
import org.openXpertya.process.DocumentEngine;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;

/**
 * Descripción de Clase
 *
 *
 * @version    2.1, 02.07.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MJournalBatch extends X_GL_JournalBatch implements DocAction {

	private static CLogger s_log = CLogger.getCLogger(MJournalBatch.class);
	
    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param GL_JournalBatch_ID
     * @param dateDoc
     * @param trxName
     *
     * @return
     */

    public static MJournalBatch copyFrom( Properties ctx,int GL_JournalBatch_ID,Timestamp dateDoc,String trxName ) {
        MJournalBatch from = new MJournalBatch( ctx,GL_JournalBatch_ID,trxName );

        if( from.getGL_JournalBatch_ID() == 0 ) {
            throw new IllegalArgumentException( "From Journal Batch not found GL_JournalBatch_ID=" + GL_JournalBatch_ID );
        }

        //

        MJournalBatch to = new MJournalBatch( ctx,0,trxName );

        PO.copyValues( from,to,from.getAD_Client_ID(),from.getAD_Org_ID());
        to.set_ValueNoCheck( "DocumentNo",null );
        to.setDateAcct( dateDoc );
        to.setDateDoc( dateDoc );
        to.setDocStatus( DOCSTATUS_Drafted );
        to.setDocAction( DOCACTION_Complete );
        to.setIsApproved( false );

        //

        if( !to.save()) {
            throw new IllegalStateException( "Could not create Journal Batch" );
        }

        if( to.copyDetailsFrom( from ) == 0 ) {
            throw new IllegalStateException( "Could not create Journal Batch Details" );
        }

        return to;
    }    // copyFrom

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param GL_JournalBatch_ID
     * @param trxName
     */

    public MJournalBatch( Properties ctx,int GL_JournalBatch_ID,String trxName ) {
        super( ctx,GL_JournalBatch_ID,trxName );

        if( GL_JournalBatch_ID == 0 ) {

            // setGL_JournalBatch_ID (0);      PK
            // setDescription (null);
            // setDocumentNo (null);

            setPostingType( POSTINGTYPE_Actual );
            setDocAction( DOCACTION_Complete );
            setDocStatus( DOCSTATUS_Drafted );
            setTotalCr( Env.ZERO );
            setTotalDr( Env.ZERO );
            setProcessed( false );
            setProcessing( false );
            setIsApproved( false );
        }
    }    // MJournalBatch

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MJournalBatch( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MJournalBatch

    /**
     * Constructor de la clase ...
     *
     *
     * @param original
     */

    public MJournalBatch( MJournalBatch original ) {
        this( original.getCtx(),0,original.get_TrxName());
        setClientOrg( original );
        setGL_JournalBatch_ID( original.getGL_JournalBatch_ID());

        //
        // setC_AcctSchema_ID(original.getC_AcctSchema_ID());
        // setGL_Budget_ID(original.getGL_Budget_ID());

        setGL_Category_ID( original.getGL_Category_ID());
        setPostingType( original.getPostingType());
        setDescription( original.getDescription());
        setC_DocType_ID( original.getC_DocType_ID());
        setControlAmt( original.getControlAmt());

        //

        setC_Currency_ID( original.getC_Currency_ID());

        // setC_ConversionType_ID(original.getC_ConversionType_ID());
        // setCurrencyRate(original.getCurrencyRate());

        // setDateDoc(original.getDateDoc());
        // setDateAcct(original.getDateAcct());
        // setC_Period_ID(original.getC_Period_ID());

    }    // MJournal

	/**
	 * @author: Horacio Alvarez
	 * @fecha: 25-08-08
	 */
	public static MJournalBatch getBatch (String documentNo, String docStatus, String trxName) {
		try
		{
			StringBuffer sql = new StringBuffer("SELECT * ");
			sql.append("FROM GL_JournalBatch ");
			sql.append("WHERE DocumentNo like ? AND DocStatus like ? ");
			PreparedStatement pstmt = DB.prepareStatement(sql.toString(), trxName);
			pstmt.setString(1,documentNo);
			pstmt.setString(2,docStatus);
			ResultSet rs = pstmt.executeQuery();
			if(rs.next())
				return new MJournalBatch(Env.getCtx(),rs.getInt("GL_JournalBatch_ID"), trxName);
		}
		catch(Exception ex)
		{
			s_log.severe("Error al buscar el lote de asientos."+ex);
		}
		return null;
	}
	
    /**
     * Descripción de Método
     *
     *
     * @param AD_Client_ID
     * @param AD_Org_ID
     */
    public void setClientOrg( int AD_Client_ID,int AD_Org_ID ) {
        super.setClientOrg( AD_Client_ID,AD_Org_ID );
    }    // setClientOrg

    /**
     * Descripción de Método
     *
     *
     * @param DateAcct
     */

    public void setDateAcct( Timestamp DateAcct ) {
        super.setDateAcct( DateAcct );

        if( DateAcct == null ) {
            return;
        }

        if( getC_Period_ID() != 0 ) {
            return;
        }

        int C_Period_ID = MPeriod.getC_Period_ID( getCtx(),DateAcct );

        if( C_Period_ID == 0 ) {
            log.warning( "setDateAcct - Period not found" );
        } else {
            setC_Period_ID( C_Period_ID );
        }
    }    // setDateAcct

    /**
     * Descripción de Método
     *
     *
     * @param requery
     *
     * @return
     */

    public MJournal[] getJournals( boolean requery ) {
        ArrayList list = new ArrayList();
        String    sql  = "SELECT * FROM GL_Journal WHERE GL_JournalBatch_ID=? ORDER BY DocumentNo";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql,get_TrxName());
            pstmt.setInt( 1,getGL_JournalBatch_ID());

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                list.add( new MJournal( getCtx(),rs,get_TrxName()));
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( SQLException ex ) {
            log.log( Level.SEVERE,"getJournals",ex );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }
        } catch( SQLException ex1 ) {
        }

        pstmt = null;

        //

        MJournal[] retValue = new MJournal[ list.size()];

        list.toArray( retValue );

        return retValue;
    }    // getJournals

    /**
     * Descripción de Método
     *
     *
     * @param jb
     *
     * @return
     */

    public int copyDetailsFrom( MJournalBatch jb ) {
        if( isProcessed() || (jb == null) ) {
            return 0;
        }

        int        count        = 0;
        int        lineCount    = 0;
        MJournal[] fromJournals = jb.getJournals( false );

        for( int i = 0;i < fromJournals.length;i++ ) {
            MJournal toJournal = new MJournal( getCtx(),0,jb.get_TrxName());

            PO.copyValues( fromJournals[ i ],toJournal,getAD_Client_ID(),getAD_Org_ID());
            toJournal.setGL_JournalBatch_ID( getGL_JournalBatch_ID());
            toJournal.set_ValueNoCheck( "DocumentNo",null );    // create new
            toJournal.setC_Period_ID( 0 );
            toJournal.setDateDoc( getDateDoc());                // dates from this Batch
            toJournal.setDateAcct( getDateAcct());
            toJournal.setDocStatus( MJournal.DOCSTATUS_Drafted );
            toJournal.setDocAction( MJournal.DOCACTION_Complete );
            toJournal.setTotalCr( Env.ZERO );
            toJournal.setTotalDr( Env.ZERO );
            toJournal.setIsApproved( false );
            toJournal.setIsPrinted( false );
            toJournal.setPosted( false );
            toJournal.setProcessed( false );

            if( toJournal.save()) {
                count++;
                lineCount += toJournal.copyLinesFrom( fromJournals[ i ],getDateAcct(),'x' );
            }
        }

        if( fromJournals.length != count ) {
            log.log( Level.SEVERE,"copyDetailsFrom - Line difference - Journals=" + fromJournals.length + " <> Saved=" + count );
        }

        return count + lineCount;
    }    // copyLinesFrom

    /**
     * Descripción de Método
     *
     *
     * @param processAction
     *
     * @return
     */

    public boolean processIt( String processAction ) {
        m_processMsg = null;

        DocumentEngine engine = new DocumentEngine( this,getDocStatus());

        return engine.processIt( processAction,getDocAction(),log );
    }    // process


    /** Descripción de Campos */

    private boolean m_justPrepared = false;

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean unlockIt() {
        log.info( "unlockIt - " + toString());
        setProcessing( false );

        return true;
    }    // unlockIt

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean invalidateIt() {
        log.info( "invalidateIt - " + toString());

        return true;
    }    // invalidateIt

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String prepareIt() {
        log.info( toString());
        m_processMsg = ModelValidationEngine.get().fireDocValidate( this,ModelValidator.TIMING_BEFORE_PREPARE );

        if( m_processMsg != null ) {
            return DocAction.STATUS_Invalid;
        }

        MDocType dt = MDocType.get( getCtx(),getC_DocType_ID());

        // Std Period open?

        if( !MPeriod.isOpen( getCtx(),getDateAcct(),dt.getDocBaseType())) {
            m_processMsg = "@PeriodClosed@";

            return DocAction.STATUS_Invalid;
        }

        // Add up Amounts & prepare them

        MJournal[] journals = getJournals( false );

        if( journals.length == 0 ) {
            m_processMsg = "@NoLines@";

            return DocAction.STATUS_Invalid;
        }

        BigDecimal TotalDr = Env.ZERO;
        BigDecimal TotalCr = Env.ZERO;

        for( int i = 0;i < journals.length;i++ ) {
            MJournal journal = journals[ i ];

            if( !journal.isActive()) {
                continue;
            }

            // Prepare if not closed

            if( DOCSTATUS_Closed.equals( journal.getDocStatus()) || DOCSTATUS_Voided.equals( journal.getDocStatus()) || DOCSTATUS_Reversed.equals( journal.getDocStatus()) || DOCSTATUS_Completed.equals( journal.getDocStatus())) {
                ;
            } else {
                String status = journal.prepareIt();

                if( !DocAction.STATUS_InProgress.equals( status )) {
                    journal.setDocStatus( status );
                    journal.save();
                    m_processMsg = journal.getProcessMsg();

                    return status;
                }

                journal.setDocStatus( DOCSTATUS_InProgress );
                journal.save();
            }

            //

            TotalDr = TotalDr.add( journal.getTotalDr());
            TotalCr = TotalCr.add( journal.getTotalCr());
        }

        setTotalDr( TotalDr );
        setTotalCr( TotalCr );

        // Control Amount

        if( (Env.ZERO.compareTo( getControlAmt()) != 0) && (getControlAmt().compareTo( getTotalDr()) != 0) ) {
            m_processMsg = "@ControlAmtError@";

            return DocAction.STATUS_Invalid;
        }

        // Add up Amounts

        if (getTotalCr().compareTo(getTotalDr()) != 0) {
        	m_processMsg = "@NoBalance@";
        	return DocAction.STATUS_Invalid;
        }
        
        m_justPrepared = true;

        return DocAction.STATUS_InProgress;
    }    // prepareIt

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean approveIt() {
        log.info( "approveIt - " + toString());
        setIsApproved( true );

        return true;
    }    // approveIt

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean rejectIt() {
        log.info( "rejectIt - " + toString());
        setIsApproved( false );

        return true;
    }    // rejectIt

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String completeIt() {
        log.info( "completeIt - " + toString());

        // Re-Check

        if( !m_justPrepared ) {
            String status = prepareIt();

            if( !DocAction.STATUS_InProgress.equals( status )) {
                return status;
            }
        }

        // Implicit Approval

        approveIt();

        // Add up Amounts & complete them

        MJournal[] journals = getJournals( true );
        BigDecimal TotalDr  = Env.ZERO;
        BigDecimal TotalCr  = Env.ZERO;

        for( int i = 0;i < journals.length;i++ ) {
            MJournal journal = journals[ i ];

            if( !journal.isActive()) {
                journal.setProcessed( true );
                journal.setDocStatus( DOCSTATUS_Voided );
                journal.setDocAction( DOCACTION_None );
                journal.save();

                continue;
            }

            // Complete if not closed

            if( DOCSTATUS_Closed.equals( journal.getDocStatus()) || DOCSTATUS_Voided.equals( journal.getDocStatus()) || DOCSTATUS_Reversed.equals( journal.getDocStatus()) || DOCSTATUS_Completed.equals( journal.getDocStatus())) {
                ;
            } else {
                String status = journal.completeIt();

                if( !DocAction.STATUS_Completed.equals( status )) {
                    journal.setDocStatus( status );
                    journal.save();
                    m_processMsg = journal.getProcessMsg();

                    return status;
                }

                journal.setDocStatus( DOCSTATUS_Completed );
                journal.save();
            }

            //

            TotalDr = TotalDr.add( journal.getTotalDr());
            TotalCr = TotalCr.add( journal.getTotalCr());
        }

        setTotalDr( TotalDr );
        setTotalCr( TotalCr );

        // User Validation

        String valid = ModelValidationEngine.get().fireDocValidate( this,ModelValidator.TIMING_AFTER_COMPLETE );

        if( valid != null ) {
            m_processMsg = valid;

            return DocAction.STATUS_Invalid;
        }

        //Modificado por ConSerTi para el restablecimiento de los elementos contables
        //
        // Inicio codigo comentado por Antonio@Disytel ya que no existe la tabla: acct_temp
        /*
        String sql = null;
		sql="SELECT c_elementvalue_id,value from acct_temp ";
		
		PreparedStatement pstmt = null;
		try
		{
			pstmt = DB.prepareStatement (sql);
			ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
            	log.fine("entro en el select de elementvalue, y el valor a meter es:"+rs.getString(2));
                boolean DocControl;
                if(rs.getString(2).equalsIgnoreCase("Y")){
                	DocControl=true;
                }else{
                	DocControl=false;
                }
            	MElementValue element = new MElementValue(getCtx(),rs.getInt(1),null);             
                element.setIsDocControlled(DocControl);
                element.save();    
            }
            rs.close();
            pstmt.close();
            pstmt = null;
		}
		catch (Exception e)
		{
			//log.error ("doIt - " + sql, e);
			log.saveError("doIt - " + sql, e);
		}
        //Fin modificacion
		String sql2 = "DELETE FROM acct_temp";
        int no = DB.executeUpdate(sql2,get_TrxName());
        */
        // Fin Codigo comentado Antonio@Disytel
        setProcessed( true );
        setDocAction( DOCACTION_Close );

        return DocAction.STATUS_Completed;
    }    // completeIt

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean postIt() {
        log.info( "postIt - " + toString());

        return false;
    }    // postIt

    /**
     * Descripción de Método
     *
     *
     * @return
     */
	public boolean voidIt()
	{
		MJournal[] journals = getJournals(true);
		for (int i = 0; i < journals.length; i++)
		{
			MJournal journal = journals[i];
			if (journal.voidIt())
				journal.save();
			else
			{
				m_processMsg = journal.getProcessMsg();
        		return false;
			}
		}
		setProcessed(true);
		setDocAction(DOCACTION_None);
		return true;
    }    // voidIt

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean closeIt() {
        log.info( "closeIt - " + toString());

        MJournal[] journals = getJournals( true );

        for( int i = 0;i < journals.length;i++ ) {
            MJournal journal = journals[ i ];

            if( !journal.isActive() &&!journal.isProcessed()) {
                journal.setProcessed( true );
                journal.setDocStatus( DOCSTATUS_Voided );
                journal.setDocAction( DOCACTION_None );
                journal.save();

                continue;
            }

            if( DOCSTATUS_Drafted.equals( journal.getDocStatus()) || DOCSTATUS_InProgress.equals( journal.getDocStatus()) || DOCSTATUS_Invalid.equals( journal.getDocStatus())) {
                m_processMsg = "Journal not Completed: " + journal.getSummary();

                return false;
            }

            // Close if not closed

            if( DOCSTATUS_Closed.equals( journal.getDocStatus()) || DOCSTATUS_Voided.equals( journal.getDocStatus()) || DOCSTATUS_Reversed.equals( journal.getDocStatus())) {
                ;
            } else {
                if( !journal.closeIt()) {
                    m_processMsg = "Cannot close: " + journal.getSummary();

                    return false;
                }

                journal.save();
            }
        }

        return true;
    }    // closeIt

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean reverseCorrectIt() {
        log.info( "reverseCorrectIt - " + toString());

        MJournal[] journals = getJournals( true );

        // check prerequisites

        for( int i = 0;i < journals.length;i++ ) {
            MJournal journal = journals[ i ];

            if( !journal.isActive()) {
                continue;
            }

            // All need to be closed/Completed

            if( DOCSTATUS_Completed.equals( journal.getDocStatus())) {
                ;
            } else {
                m_processMsg = "All Journals need to be Compleded: " + journal.getSummary();

                return false;
            }
        }

        // Reverse it

        MJournalBatch reverse = new MJournalBatch( this );

        reverse.setDateDoc( getDateDoc());
        reverse.setC_Period_ID( getC_Period_ID());
        reverse.setDateAcct( getDateAcct());

        // Reverse indicator

        String description = reverse.getDescription();

        if( description == null ) {
            description = "** " + getDocumentNo() + " **";
        } else {
            description += " ** " + getDocumentNo() + " **";
        }

        reverse.setDescription( description );
        reverse.save();

        //

        // Reverse Journals

        for( int i = 0;i < journals.length;i++ ) {
            MJournal journal = journals[ i ];

            if( !journal.isActive()) {
                continue;
            }

            if( journal.reverseCorrectIt( reverse.getGL_JournalBatch_ID()) == null ) {
                m_processMsg = "Could not reverse " + journal;

                return false;
            }

            journal.save();
        }

        return true;
    }    // reverseCorrectionIt

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean reverseAccrualIt() {
        log.info( "reverseAccrualIt - " + toString());

        MJournal[] journals = getJournals( true );

        // check prerequisites

        for( int i = 0;i < journals.length;i++ ) {
            MJournal journal = journals[ i ];

            if( !journal.isActive()) {
                continue;
            }

            // All need to be closed/Completed

            if( DOCSTATUS_Completed.equals( journal.getDocStatus())) {
                ;
            } else {
                m_processMsg = "All Journals need to be Compleded: " + journal.getSummary();

                return false;
            }
        }

        // Reverse it

        MJournalBatch reverse = new MJournalBatch( this );

        reverse.setC_Period_ID( 0 );
        reverse.setDateDoc( new Timestamp( System.currentTimeMillis()));
        reverse.setDateAcct( reverse.getDateDoc());

        // Reverse indicator

        String description = reverse.getDescription();

        if( description == null ) {
            description = "** " + getDocumentNo() + " **";
        } else {
            description += " ** " + getDocumentNo() + " **";
        }

        reverse.setDescription( description );
        reverse.save();

        // Reverse Journals

        for( int i = 0;i < journals.length;i++ ) {
            MJournal journal = journals[ i ];

            if( !journal.isActive()) {
                continue;
            }

            if( journal.reverseAccrualIt( reverse.getGL_JournalBatch_ID()) == null ) {
                m_processMsg = "Could not reverse " + journal;

                return false;
            }

            journal.save();
        }

        return true;
    }    // reverseAccrualIt

    
	protected boolean beforeSave(boolean newRecord) {
		if (getControlAmt().compareTo(Env.ZERO) < 0) {
			log.saveError("ControlAmtUnderZero", "");
			return false;
		}
		
		return true;
	}
    
    
    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean reActivateIt() {
        log.info( "reActivateIt - " + toString());

        // setProcessed(false);

        if( reverseCorrectIt()) {
            return true;
        }

        return false;
    }    // reActivateIt

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getSummary() {
        StringBuffer sb = new StringBuffer();

        sb.append( getDocumentNo());

        // : Total Lines = 123.00 (#1)

        sb.append( ": " ).append( Msg.translate( getCtx(),"TotalDr" )).append( "=" ).append( getTotalDr()).append( " " ).append( Msg.translate( getCtx(),"TotalCR" )).append( "=" ).append( getTotalCr()).append( " (#" ).append( getJournals( false ).length ).append( ")" );

        // - Description

        if( (getDescription() != null) && (getDescription().length() > 0) ) {
            sb.append( " - " ).append( getDescription());
        }

        return sb.toString();
    }    // getSummary

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer( "MJournalBatch[" );

        sb.append( getID()).append( "," ).append( getDescription()).append( ",DR=" ).append( getTotalDr()).append( ",CR=" ).append( getTotalCr()).append( "]" );

        return sb.toString();
    }    // toString


    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getDoc_User_ID() {
        return getCreatedBy();
    }    // getDoc_User_ID

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public BigDecimal getApprovalAmt() {
        return getTotalDr();
    }    // getApprovalAmt
}    // MJournalBatch



/*
 *  @(#)MJournalBatch.java   02.07.07
 * 
 *  Fin del fichero MJournalBatch.java
 *  
 *  Versión 2.1
 *
 */
