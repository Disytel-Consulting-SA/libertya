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
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.model.MBankStatementLoader;
import org.openXpertya.util.Env;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class LoadBankStatement extends SvrProcess {

    /**
     * Constructor de la clase ...
     *
     */

    public LoadBankStatement() {
        super();
        log.info( "LoadBankStatement" );
    }    // LoadBankStatement

    /** Descripción de Campos */

    private int m_AD_Client_ID = 0;

    /** Descripción de Campos */

    private int m_AD_Org_ID = 0;

    /** Descripción de Campos */

    private int m_C_BankStmtLoader_ID = 0;

    /** Descripción de Campos */

    private String fileName = "";

    /** Descripción de Campos */

    private Properties m_ctx;

    /** Descripción de Campos */

    private MBankStatementLoader m_controller = null;

    /**
     * Descripción de Método
     *
     */

    protected void prepare() {
        log.info( "" );
        m_ctx = Env.getCtx();

        ProcessInfoParameter[] para = getParameter();

        for( int i = 0;i < para.length;i++ ) {
            String name = para[ i ].getParameterName();

            if( name.equals( "C_BankStatementLoader_ID" )) {
                m_C_BankStmtLoader_ID = (( BigDecimal )para[ i ].getParameter()).intValue();
            } else if( name.equals( "FileName" )) {
                fileName = ( String )para[ i ].getParameter();
            } else {
                log.log( Level.SEVERE,"Unknown Parameter: " + name );
            }
        }

        m_AD_Client_ID = Env.getAD_Client_ID( m_ctx );
        log.info( "AD_Client_ID=" + m_AD_Client_ID );
        m_AD_Org_ID = Env.getAD_Org_ID( m_ctx );
        log.info( "AD_Org_ID=" + m_AD_Org_ID );
        log.info( "C_BankStatementLoader_ID=" + m_C_BankStmtLoader_ID );
    }    // prepare

    /**
     * Descripción de Método
     *
     *
     * @return
     *
     * @throws java.lang.Exception
     */

    protected String doIt() throws java.lang.Exception {
        log.info( "LoadBankStatement.doIt" );

        String message = "@Error@";

        m_controller = new MBankStatementLoader( m_ctx,m_C_BankStmtLoader_ID,fileName,get_TrxName());
        log.info( m_controller.toString());

        if( (m_controller == null) || (m_controller.getID() == 0) ) {
            log.log( Level.SEVERE,"Invalid Loader" );

            // Start loading bank statement lines

        } else if( !m_controller.loadLines()) {
            log.log( Level.SEVERE,m_controller.getErrorMessage() + " - " + m_controller.getErrorDescription());
        } else {
            log.info( "Imported=" + m_controller.getLoadCount());
            addLog( 0,null,new BigDecimal( m_controller.getLoadCount()),"@Loaded@" );
            message = "@OK@";
        }

        return message;
    }    // doIt
}    // LoadBankStatement



/*
 *  @(#)LoadBankStatement.java   02.07.07
 * 
 *  Fin del fichero LoadBankStatement.java
 *  
 *  Versión 2.2
 *
 */
