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

import org.openXpertya.process.DocAction;
import org.openXpertya.process.DocumentEngine;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Util;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MBankStatement extends X_C_BankStatement implements DocAction {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param C_BankStatement_ID
     * @param trxName
     */

    public MBankStatement( Properties ctx,int C_BankStatement_ID,String trxName ) {
        super( ctx,C_BankStatement_ID,trxName );

        if( C_BankStatement_ID == 0 ) {

            // setC_BankAccount_ID (0);        //      parent

            setStatementDate( new Timestamp( System.currentTimeMillis()));    // @Date@
            setDocAction( DOCACTION_Complete );    // CO
            setDocStatus( DOCSTATUS_Drafted );     // DR
            setBeginningBalance( Env.ZERO );
            setStatementDifference( Env.ZERO );
            setEndingBalance( Env.ZERO );
            setIsApproved( false );                // N
            setIsManual( true );                   // Y
            setPosted( false );                    // N
            super.setProcessed( false );
        }
    }                                              // MBankStatement

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MBankStatement( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MBankStatement

    /**
     * Constructor de la clase ...
     *
     *
     * @param account
     * @param isManual
     */

    public MBankStatement( MBankAccount account,boolean isManual ) {
        this( account.getCtx(),0,account.get_TrxName());
        setClientOrg( account );
        setC_BankAccount_ID( account.getC_BankAccount_ID());
        setStatementDate( new Timestamp( System.currentTimeMillis()));
        setBeginningBalance( account.getCurrentBalance());
        setName( getStatementDate().toString());
        setIsManual( isManual );
    }    // MBankStatement

    /**
     * Constructor de la clase ...
     *
     *
     * @param account
     */

    public MBankStatement( MBankAccount account ) {
        this( account,false );
    }    // MBankStatement

    /** Descripción de Campos */

    private MBankStatementLine[] m_lines = null;

    /**
     * Descripción de Método
     *
     *
     * @param requery
     *
     * @return
     */

    public MBankStatementLine[] getLines( boolean requery ) {
        if( (m_lines != null) &&!requery ) {
            return m_lines;
        }

        //

        ArrayList list = new ArrayList();
        String    sql  = "SELECT * FROM C_BankStatementLine " + "WHERE C_BankStatement_ID=?" + "ORDER BY Line";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql,get_TrxName());
            pstmt.setInt( 1,getC_BankStatement_ID());

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                list.add( new MBankStatementLine( getCtx(),rs,get_TrxName()));
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

        MBankStatementLine[] retValue = new MBankStatementLine[ list.size()];

        list.toArray( retValue );

        return retValue;
    }    // getLines

    public X_C_BankStatLine_Reconcil[] getAllReconcils() {

        ArrayList list = new ArrayList();
        String    sql  = "SELECT * FROM C_BankStatLine_Reconcil " + " WHERE C_BankStatement_ID=? ";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql,get_TrxName());
            pstmt.setInt( 1,getC_BankStatement_ID());

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                list.add( new X_C_BankStatLine_Reconcil( getCtx(), rs, get_TrxName()));
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

        X_C_BankStatLine_Reconcil[] retValue = new X_C_BankStatLine_Reconcil[ list.size()];

        list.toArray( retValue );

        return retValue;
    }    // getLines
    
    /**
     * Descripción de Método
     *
     *
     * @param description
     */

    public void addDescription( String description ) {
        String desc = getDescription();

        if( desc == null ) {
            setDescription( description );
        } else {
            setDescription( desc + " | " + description );
        }
    }    // addDescription

    /**
     * Descripción de Método
     *
     *
     * @param processed
     */

    public void setProcessed( boolean processed ) {
        super.setProcessed( processed );

        if( getID() == 0 ) {
            return;
        }

        String sql = "UPDATE C_BankStatementLine SET Processed='" + ( processed
                ?"Y"
                :"N" ) + "' WHERE C_BankStatement_ID=" + getC_BankStatement_ID();
        int noLine = DB.executeUpdate( sql,get_TrxName());

        m_lines = null;
        log.fine( "setProcessed - " + processed + " - Lines=" + noLine );
    }    // setProcessed

    /**
     * Descripción de Método
     *
     *
     * @param newRecord
     *
     * @return
     */

    protected boolean beforeSave( boolean newRecord ) {
    	
    	// NO es posible guardar un extracto bancario para la org *
    	if(Util.isEmpty(getAD_Org_ID(), true)){
    		log.saveError("SaveError", Msg.getMsg(getCtx(), "InvalidOrg"));
    		return false;
    	}
    	
        if( getBeginningBalance().compareTo( Env.ZERO ) == 0 ) {
            MBankAccount ba = MBankAccount.get( getCtx(),getC_BankAccount_ID());

            setBeginningBalance( ba.getCurrentBalance());
        }

        setEndingBalance( getBeginningBalance().add( getStatementDifference()));

        return true;
    }    // beforeSave

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
    }    // processIt

    /** Descripción de Campos */

    private String m_processMsg = null;

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
        setDocAction( DOCACTION_Prepare );

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

        // Std Period open?

        if( !MPeriod.isOpen( getCtx(),getStatementDate(),MDocType.DOCBASETYPE_BankStatement )) {
            m_processMsg = "@PeriodClosed@";

            return DocAction.STATUS_Invalid;
        }

        MBankStatementLine[] lines = getLines( true );

        if( lines.length == 0 ) {
            m_processMsg = "@NoLines@";

            return DocAction.STATUS_Invalid;
        }

        // Lines

        BigDecimal total   = Env.ZERO;
        Timestamp  minDate = getStatementDate();
        Timestamp  maxDate = minDate;

        for( int i = 0;i < lines.length;i++ ) {
            MBankStatementLine line = lines[ i ];

            total = total.add( line.getStmtAmt());

            if( line.getDateAcct().before( minDate )) {
                minDate = line.getDateAcct();
            }

            if( line.getDateAcct().after( maxDate )) {
                maxDate = line.getDateAcct();
            }
        }

        setStatementDifference( total );
        setEndingBalance( getBeginningBalance().add( total ));

        if( !MPeriod.isOpen( getCtx(),minDate,MDocType.DOCBASETYPE_BankStatement ) ||!MPeriod.isOpen( getCtx(),maxDate,MDocType.DOCBASETYPE_BankStatement )) {
            m_processMsg = "@PeriodClosed@";

            return DocAction.STATUS_Invalid;
        }

        m_justPrepared = true;

        if( !DOCACTION_Complete.equals( getDocAction())) {
            setDocAction( DOCACTION_Complete );
        }

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

        // Re-Check

    	if (!m_justPrepared	&& !existsJustPreparedDoc()) {
            String status = prepareIt();

            if( !DocAction.STATUS_InProgress.equals( status )) {
                return status;
            }
        }

        // Implicit Approval

        if( !isApproved()) {
            approveIt();
        }

        log.info( "completeIt - " + toString());

        // Set Payment reconciled

        MBankStatementLine[] lines = getLines( false );

        for( int i = 0;i < lines.length;i++ ) {
            MBankStatementLine line = lines[ i ];

            if( line.getC_Payment_ID() != 0 ) {
                MPayment payment = new MPayment( getCtx(),line.getC_Payment_ID(),get_TrxName());
                payment.setIsReconciled( true );
                if(!payment.save()){
                	setProcessMsg(CLogger.retrieveErrorAsString());
                	return DOCSTATUS_Invalid;
                }
            }
            
            if ( line.getM_BoletaDeposito_ID() != 0 ) {
            	MBoletaDeposito boleta = new MBoletaDeposito( getCtx(),line.getM_BoletaDeposito_ID(),get_TrxName());
            	if ( ! boleta.isReconciled() ) {
            		boleta.setConciliado(true);
            		if(!boleta.save()){
            			setProcessMsg(CLogger.retrieveErrorAsString());
                    	return DOCSTATUS_Invalid;
            		}
            	}
            }
            // Se concilian las líneas de extracto bancario
            line.setIsReconciled(true);
            if(!line.save()){
            	setProcessMsg(CLogger.retrieveErrorAsString());
            	return DOCSTATUS_Invalid;
            }
        }

        // Update Bank Account

        MBankAccount ba = MBankAccount.get( getCtx(),getC_BankAccount_ID());

        ba.setCurrentBalance( getEndingBalance());
        if(!ba.save( get_TrxName())){
        	setProcessMsg(CLogger.retrieveErrorAsString());
        	return DOCSTATUS_Invalid;
        }

        // User Validation

        String valid = ModelValidationEngine.get().fireDocValidate( this,ModelValidator.TIMING_AFTER_COMPLETE );

        if( valid != null ) {
            m_processMsg = valid;

            return DocAction.STATUS_Invalid;
        }

        //

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

    public boolean voidIt() {
        log.info( "voidIt - " + toString());

        if( DOCSTATUS_Closed.equals( getDocStatus()) || DOCSTATUS_Reversed.equals( getDocStatus()) || DOCSTATUS_Voided.equals( getDocStatus())) {
            m_processMsg = "Document Closed: " + getDocStatus();
            setDocAction( DOCACTION_None );

            return false;
        }

        // Not Processed

        if( DOCSTATUS_Drafted.equals( getDocStatus()) || DOCSTATUS_Invalid.equals( getDocStatus()) || DOCSTATUS_InProgress.equals( getDocStatus()) || DOCSTATUS_Approved.equals( getDocStatus()) || DOCSTATUS_NotApproved.equals( getDocStatus())) {
            ;

            // Std Period open?

        } else {
            if( !MPeriod.isOpen( getCtx(),getStatementDate(),MDocType.DOCBASETYPE_BankStatement )) {
                m_processMsg = "@PeriodClosed@";

                return false;
            }

            if( MFactAcct.delete( Table_ID,getC_BankStatement_ID(),get_TrxName()) < 0 ) {
                return false;    // could not delete
            }
        }

        // Set lines to 0

        MBankStatementLine[] lines = getLines( true );

        for( int i = 0;i < lines.length;i++ ) {
            MBankStatementLine line = lines[ i ];
            BigDecimal         old  = line.getStmtAmt();

            if( line.getStmtAmt().compareTo( Env.ZERO ) != 0 ) {
                String description = Msg.getMsg( getCtx(),"Voided" ) + " (" + Msg.translate( getCtx(),"StmtAmt" ) + "=" + line.getStmtAmt();

                if( line.getTrxAmt().compareTo( Env.ZERO ) != 0 ) {
                    description += ", " + Msg.translate( getCtx(),"TrxAmt" ) + "=" + line.getTrxAmt();
                }

                if( line.getChargeAmt().compareTo( Env.ZERO ) != 0 ) {
                    description += ", " + Msg.translate( getCtx(),"ChargeAmt" ) + "=" + line.getChargeAmt();
                }

                if( line.getInterestAmt().compareTo( Env.ZERO ) != 0 ) {
                    description += ", " + Msg.translate( getCtx(),"InterestAmt" ) + "=" + line.getInterestAmt();
                }

                description += ")";
                line.addDescription( description );

                //

                line.setStmtAmt( Env.ZERO );
                line.setTrxAmt( Env.ZERO );
                line.setChargeAmt( Env.ZERO );
                line.setInterestAmt( Env.ZERO );
                if(!line.save( get_TrxName())){
                	setProcessMsg(CLogger.retrieveErrorAsString());
                	return false;
                }

                //

                if( line.getC_Payment_ID() != 0 ) {
                    MPayment payment = new MPayment( getCtx(),line.getC_Payment_ID(),get_TrxName());

                    payment.setIsReconciled( false );
                    if(!payment.save()){
                    	setProcessMsg(CLogger.retrieveErrorAsString());
                    	return false;
                    }
                }
                if ( line.getM_BoletaDeposito_ID() != 0 ) {
                	MBoletaDeposito boleta = new MBoletaDeposito( getCtx(),line.getM_BoletaDeposito_ID(),get_TrxName());
                	if ( ! boleta.isReconciled() ) {
                		boleta.setConciliado(false);
                		if(!boleta.save()){
                			setProcessMsg(CLogger.retrieveErrorAsString());
                        	return false;
                		}
                	}
                }                
            }
        }
        
        /**
         * SEGÚN LineasExistentesTableModel.java - Linea 201
         * la tabla C_BankStatLine_Reconcil ya no se utiliza mas.
         */
        
/*        X_C_BankStatLine_Reconcil[] reconLines = getAllReconcils();
        
        for (int i=0; i<reconLines.length; i++) {
        	X_C_BankStatLine_Reconcil line = reconLines[i];
        	
        	if (line.getC_Payment_ID() != 0) {
        		MPayment payment = new MPayment( getCtx(),line.getC_Payment_ID(),get_TrxName());
        		
        		if (payment.isReconciled()) {
        			payment.setIsReconciled( false );
        			payment.save( get_TrxName());
        		}
        	}
        }*/
        
        addDescription( Msg.getMsg( getCtx(),"Voided" ));
        setStatementDifference( Env.ZERO );
        setProcessed( true );
        setDocAction( DOCACTION_None );

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
        setDocAction( DOCACTION_None );

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

        return false;
    }    // reverseCorrectionIt

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean reverseAccrualIt() {
        log.info( "reverseAccrualIt - " + toString());

        return false;
    }    // reverseAccrualIt

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean reActivateIt() {
        log.info( "reActivateIt - " + toString());

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

        sb.append( getName());

        // : Total Lines = 123.00 (#1)

        sb.append( ": " ).append( Msg.translate( getCtx(),"StatementDifference" )).append( "=" ).append( getStatementDifference()).append( " (#" ).append( getLines( false ).length ).append( ")" );

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

    public String getProcessMsg() {
        return m_processMsg;
    }    // getProcessMsg

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getDoc_User_ID() {
        return getUpdatedBy();
    }    // getDoc_User_ID

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public BigDecimal getApprovalAmt() {
        return getStatementDifference();
    }    // getApprovalAmt

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getC_Currency_ID() {

        // MPriceList pl = MPriceList.get(getCtx(), getM_PriceList_ID());
        // return pl.getC_Currency_ID();

        return 0;
    }    // getC_Currency_ID
}    // MBankStatement



/*
 *  @(#)MBankStatement.java   02.07.07
 * 
 *  Fin del fichero MBankStatement.java
 *  
 *  Versión 2.2
 *
 */
