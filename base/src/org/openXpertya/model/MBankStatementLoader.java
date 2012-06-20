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
import java.util.Properties;

import org.openXpertya.impexp.BankStatementLoaderInterface;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MBankStatementLoader extends X_C_BankStatementLoader {

    /** Descripción de Campos */

    private int loadCount = 0;

    /** Descripción de Campos */

    private String errorMessage = "";

    /** Descripción de Campos */

    private String errorDescription = "";

    /** Descripción de Campos */

    private BankStatementLoaderInterface m_loader = null;

    /** Descripción de Campos */

    private String localFileName = null;

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param C_BankStatementLoader_ID
     * @param trxName
     */

    public MBankStatementLoader( Properties ctx,int C_BankStatementLoader_ID,String trxName ) {
        super( ctx,C_BankStatementLoader_ID,trxName );
        init( null );
    }    // MBankStatementLoader

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param C_BankStatementLoader_ID
     * @param fileName
     * @param trxName
     */

    public MBankStatementLoader( Properties ctx,int C_BankStatementLoader_ID,String fileName,String trxName ) {
        super( ctx,C_BankStatementLoader_ID,trxName );
        init( fileName );
    }    // MBankStatementLoader

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MBankStatementLoader( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
        init( null );
    }    // MBankStatementLoader

    /**
     * Descripción de Método
     *
     *
     * @param fileName
     */

    private void init( String fileName ) {
        localFileName = fileName;

        try {
            log.info( "MBankStatementLoader Class Name=" + getStmtLoaderClass());

            Class bsrClass = Class.forName( getStmtLoaderClass());

            m_loader = ( BankStatementLoaderInterface )bsrClass.newInstance();
        } catch( Exception e ) {
            errorMessage     = "ClassNotLoaded";
            errorDescription = e.getMessage();
        }
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer( "MBankStatementLoader[" ).append( getID()).append( "-" ).append( getName()).append( "]" );

        return sb.toString();
    }    // toString

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getLocalFileName() {
        return localFileName;
    }    // getLocalFileName

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean loadLines() {
        boolean result = false;

        log.info( "MBankStatementLoader.loadLines" );

        if( m_loader == null ) {
            errorMessage = "ClassNotLoaded";

            return result;
        }

        // Initialize the Loader

        if( !m_loader.init( this )) {
            errorMessage     = m_loader.getLastErrorMessage();
            errorDescription = m_loader.getLastErrorDescription();

            return result;
        }

        // Verify whether the data structure is valid

        if( !m_loader.isValid()) {
            errorMessage     = m_loader.getLastErrorMessage();
            errorDescription = m_loader.getLastErrorDescription();

            return result;
        }

        // Load statement lines

        if( !m_loader.loadLines()) {
            errorMessage     = m_loader.getLastErrorMessage();
            errorDescription = m_loader.getLastErrorDescription();

            return result;
        }

        result = true;

        return result;
    }    // loadLines

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean saveLine() {
        log.info( "MBankStatementLoader.importLine" );

        boolean           result = false;
        X_I_BankStatement imp    = new X_I_BankStatement( getCtx(),0,get_TrxName());

        if( m_loader == null ) {
            errorMessage = "LoadError";

            return result;
        }

        // Bank Account fields

        log.config( "MBankStatementLoader.importLine Bank Account=" + m_loader.getBankAccountNo());
        imp.setBankAccountNo( m_loader.getBankAccountNo());
        log.config( "MBankStatementLoader.importLine Routing No=" + m_loader.getRoutingNo());
        imp.setRoutingNo( m_loader.getRoutingNo());

        // Statement fields

        log.config( "MBankStatementLoader.importLine EFT Statement Reference No=" + m_loader.getStatementReference());
        imp.setEftStatementReference( m_loader.getStatementReference());
        log.config( "MBankStatementLoader.importLine EFT Statement Date=" + m_loader.getStatementDate());
        imp.setEftStatementDate( m_loader.getStatementDate());
        log.config( "MBankStatementLoader.importLine Statement Date=" + m_loader.getStatementDate());
        imp.setStatementDate( m_loader.getStatementDate());

        // Statement Line fields

        log.config( "MBankStatementLoader.importLine EFT Transaction ID=" + m_loader.getTrxID());
        imp.setEftTrxID( m_loader.getTrxID());
        log.config( "MBankStatementLoader.importLine Statement Line Date=" + m_loader.getStatementLineDate());
        imp.setStatementLineDate( m_loader.getStatementLineDate());
        imp.setStatementLineDate( m_loader.getStatementLineDate());
        imp.setEftStatementLineDate( m_loader.getStatementLineDate());
        log.config( "MBankStatementLoader.importLine Valuta Date=" + m_loader.getValutaDate());
        imp.setValutaDate( m_loader.getValutaDate());
        imp.setEftValutaDate( m_loader.getValutaDate());
        log.config( "MBankStatementLoader.importLine Statement Amount=" + m_loader.getStmtAmt());
        imp.setStmtAmt( m_loader.getStmtAmt());
        imp.setEftAmt( m_loader.getStmtAmt());
        log.config( "MBankStatementLoader.importLine Transaction Amount=" + m_loader.getTrxAmt());
        imp.setTrxAmt( m_loader.getTrxAmt());
        log.config( "MBankStatementLoader.importLine Interest Amount=" + m_loader.getInterestAmt());
        imp.setInterestAmt( m_loader.getInterestAmt());
        log.config( "MBankStatementLoader.importLine Reference No=" + m_loader.getReference());
        imp.setReferenceNo( m_loader.getReference());
        imp.setEftReference( m_loader.getReference());
        log.config( "MBankStatementLoader.importLine Check No=" + m_loader.getReference());
        imp.setEftCheckNo( m_loader.getCheckNo());
        log.config( "MBankStatementLoader.importLine Memo=" + m_loader.getMemo());
        imp.setMemo( m_loader.getMemo());
        imp.setEftMemo( m_loader.getMemo());
        log.config( "MBankStatementLoader.importLine Payee Name=" + m_loader.getPayeeName());
        imp.setEftPayee( m_loader.getPayeeName());
        log.config( "MBankStatementLoader.importLine Payee Account No=" + m_loader.getPayeeAccountNo());
        imp.setEftPayeeAccount( m_loader.getPayeeAccountNo());
        log.config( "MBankStatementLoader.importLine EFT Transaction Type=" + m_loader.getTrxType());
        imp.setEftTrxType( m_loader.getTrxType());
        log.config( "MBankStatementLoader.importLine Currency=" + m_loader.getCurrency());
        imp.setEftCurrency( m_loader.getCurrency());
        imp.setISO_Code( m_loader.getCurrency());
        log.config( "MBankStatementLoader.importLine Charge Name=" + m_loader.getChargeName());
        imp.setChargeName( m_loader.getChargeName());
        log.config( "MBankStatementLoader.importLine Charge Amount=" + m_loader.getChargeAmt());
        imp.setChargeAmt( m_loader.getChargeAmt());
        imp.setProcessed( false );
        imp.setI_IsImported( false );
        result = imp.save();

        if( result ) {
            loadCount++;
        } else {
            errorMessage = "LoadError";
        }

        imp = null;

        return result;
    }    // importLine

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getErrorMessage() {
        return errorMessage;
    }    // getErrorMessage

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getErrorDescription() {
        return errorDescription;
    }    // getErrorDescription

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getLoadCount() {
        return loadCount;
    }    // getLoadCount
}    // MBankStatementLoader



/*
 *  @(#)MBankStatementLoader.java   02.07.07
 * 
 *  Fin del fichero MBankStatementLoader.java
 *  
 *  Versión 2.2
 *
 */
