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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.impexp.BankStatementMatcherInterface;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MBankStatementMatcher extends X_C_BankStatementMatcher {

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param trxName
     *
     * @return
     */

    public static MBankStatementMatcher[] getMatchers( Properties ctx,String trxName ) {
        ArrayList list = new ArrayList();
        String    sql  = MRole.getDefault( ctx,false ).addAccessSQL( "SELECT * FROM C_BankStatementMatcher ORDER BY SeqNo","C_BankStatementMatcher",MRole.SQL_NOTQUALIFIED,MRole.SQL_RO );
        int               AD_Client_ID = Env.getAD_Client_ID( ctx );
        PreparedStatement pstmt        = null;

        try {
            pstmt = DB.prepareStatement( sql,trxName );

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                list.add( new MBankStatementMatcher( ctx,rs,trxName ));
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            s_log.log( Level.SEVERE,"getMatchers",e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        // Convert

        MBankStatementMatcher[] retValue = new MBankStatementMatcher[ list.size()];

        list.toArray( retValue );

        return retValue;
    }    // getMatchers

    /** Descripción de Campos */

    private static CLogger s_log = CLogger.getCLogger( MBankStatementMatcher.class );

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param C_BankStatementMatcher_ID
     * @param trxName
     */

    public MBankStatementMatcher( Properties ctx,int C_BankStatementMatcher_ID,String trxName ) {
        super( ctx,C_BankStatementMatcher_ID,trxName );
    }    // MBankStatementMatcher

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MBankStatementMatcher( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MBankStatementMatcher

    /** Descripción de Campos */

    private BankStatementMatcherInterface m_matcher = null;

    /** Descripción de Campos */

    private Boolean m_matcherValid = null;

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isMatcherValid() {
        if( m_matcherValid == null ) {
            getMatcher();
        }

        return m_matcherValid.booleanValue();
    }    // isMatcherValid

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public BankStatementMatcherInterface getMatcher() {
        if( (m_matcher != null) || ( (m_matcherValid != null) && m_matcherValid.booleanValue())) {
            return m_matcher;
        }

        String className = getClassname();

        if( (className == null) || (className.length() == 0) ) {
            return null;
        }

        try {
            Class matcherClass = Class.forName( className );

            m_matcher = ( BankStatementMatcherInterface )matcherClass.newInstance();
            m_matcherValid = Boolean.TRUE;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"getMatcher",e );
            m_matcher      = null;
            m_matcherValid = Boolean.FALSE;
        }

        return m_matcher;
    }    // getMatcher
}    // MBankStatementMatcher



/*
 *  @(#)MBankStatementMatcher.java   02.07.07
 * 
 *  Fin del fichero MBankStatementMatcher.java
 *  
 *  Versión 2.2
 *
 */
