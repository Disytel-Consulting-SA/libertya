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

import java.util.logging.Level;

import org.openXpertya.impexp.BankStatementMatchInfo;
import org.openXpertya.model.MBankStatement;
import org.openXpertya.model.MBankStatementLine;
import org.openXpertya.model.MBankStatementMatcher;
import org.openXpertya.model.X_I_BankStatement;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class BankStatementMatcher extends SvrProcess {

    /** Descripción de Campos */

    MBankStatementMatcher[] m_matchers = null;

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
            } else {
                log.log( Level.SEVERE,"prepare - Unknown Parameter: " + name );
            }
        }

        m_matchers = MBankStatementMatcher.getMatchers( getCtx(),get_TrxName());
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
        int Table_ID  = getTable_ID();
        int Record_ID = getRecord_ID();

        if( (m_matchers == null) || (m_matchers.length == 0) ) {
            throw new IllegalStateException( "No Matchers found" );
        }

        //

        log.info( "doIt - Table_ID=" + Table_ID + ", Record_ID=" + Record_ID + ", Matchers=" + m_matchers.length );

        if( Table_ID == X_I_BankStatement.Table_ID ) {
            return match( new X_I_BankStatement( getCtx(),Record_ID,get_TrxName()));
        } else if( Table_ID == MBankStatement.Table_ID ) {
            return match( new MBankStatement( getCtx(),Record_ID,get_TrxName()));
        } else if( Table_ID == MBankStatementLine.Table_ID ) {
            return match( new MBankStatementLine( getCtx(),Record_ID,get_TrxName()));
        }

        return "??";
    }    // doIt

    /**
     * Descripción de Método
     *
     *
     * @param ibs
     *
     * @return
     */

    private String match( X_I_BankStatement ibs ) {
        if( (m_matchers == null) || (ibs == null) || (ibs.getC_Payment_ID() != 0) ) {
            return "--";
        }

        log.fine( "match - " + ibs );

        BankStatementMatchInfo info = null;

        for( int i = 0;i < m_matchers.length;i++ ) {
            if( m_matchers[ i ].isMatcherValid()) {
                info = m_matchers[ i ].getMatcher().findMatch( ibs );

                if( (info != null) && info.isMatched()) {
                    if( info.getC_Payment_ID() > 0 ) {
                        ibs.setC_Payment_ID( info.getC_Payment_ID());
                    }

                    if( info.getC_Invoice_ID() > 0 ) {
                        ibs.setC_Invoice_ID( info.getC_Invoice_ID());
                    }

                    if( info.getC_BPartner_ID() > 0 ) {
                        ibs.setC_BPartner_ID( info.getC_BPartner_ID());
                    }

                    ibs.save();

                    return "OK";
                }
            }
        }    // for all matchers

        return "--";
    }    // match

    /**
     * Descripción de Método
     *
     *
     * @param bsl
     *
     * @return
     */

    private String match( MBankStatementLine bsl ) {
        if( (m_matchers == null) || (bsl == null) || (bsl.getC_Payment_ID() != 0) ) {
            return "--";
        }

        log.fine( "match - " + bsl );

        BankStatementMatchInfo info = null;

        for( int i = 0;i < m_matchers.length;i++ ) {
            if( m_matchers[ i ].isMatcherValid()) {
                info = m_matchers[ i ].getMatcher().findMatch( bsl );

                if( (info != null) && info.isMatched()) {
                    if( info.getC_Payment_ID() > 0 ) {
                        bsl.setC_Payment_ID( info.getC_Payment_ID());
                    }

                    if( info.getC_Invoice_ID() > 0 ) {
                        bsl.setC_Invoice_ID( info.getC_Invoice_ID());
                    }

                    if( info.getC_BPartner_ID() > 0 ) {
                        bsl.setC_BPartner_ID( info.getC_BPartner_ID());
                    }

                    bsl.save();

                    return "OK";
                }
            }
        }    // for all matchers

        return "--";
    }    // match

    /**
     * Descripción de Método
     *
     *
     * @param bs
     *
     * @return
     */

    private String match( MBankStatement bs ) {
        if( (m_matchers == null) || (bs == null) ) {
            return "--";
        }

        log.fine( "match - " + bs );

        int                  count = 0;
        MBankStatementLine[] lines = bs.getLines( false );

        for( int i = 0;i < lines.length;i++ ) {
            if( lines[ i ].getC_Payment_ID() == 0 ) {
                match( lines[ i ] );
                count++;
            }
        }

        return String.valueOf( count );
    }    // match
}    // BankStatementMatcher



/*
 *  @(#)BankStatementMatcher.java   02.07.07
 * 
 *  Fin del fichero BankStatementMatcher.java
 *  
 *  Versión 2.2
 *
 */
