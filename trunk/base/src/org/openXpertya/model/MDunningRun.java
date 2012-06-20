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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.util.DB;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MDunningRun extends X_C_DunningRun {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param C_DunningRun_ID
     * @param trxName
     */

    public MDunningRun( Properties ctx,int C_DunningRun_ID,String trxName ) {
        super( ctx,C_DunningRun_ID,trxName );

        if( C_DunningRun_ID == 0 ) {

            // setC_DunningLevel_ID (0);

            setDunningDate( new Timestamp( System.currentTimeMillis()));
            setProcessed( false );
        }
    }    // MDunningRun

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MDunningRun( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MDunningRun

    /** Descripción de Campos */

    private MDunningLevel m_level = null;

    /** Descripción de Campos */

    private MDunningRunEntry[] m_entries = null;

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MDunningLevel getLevel() {
        if( m_level == null ) {
            m_level = new MDunningLevel( getCtx(),getC_DunningLevel_ID(),get_TrxName());
        }

        return m_level;
    }    // getLevel

    /**
     * Descripción de Método
     *
     *
     * @param requery
     *
     * @return
     */

    public MDunningRunEntry[] getEntries( boolean requery ) {
        if( (m_entries != null) &&!requery ) {
            return m_entries;
        }

        String            sql   = "SELECT * FROM C_DunningRunEntry WHERE C_DunningRun_ID=?";
        ArrayList         list  = new ArrayList();
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql,get_TrxName());
            pstmt.setInt( 1,getC_DunningRun_ID());

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                list.add( new MDunningRunEntry( getCtx(),rs,get_TrxName()));
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"getEntries",e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        m_entries = new MDunningRunEntry[ list.size()];
        list.toArray( m_entries );

        return m_entries;
    }    // getEntries

    /**
     * Descripción de Método
     *
     *
     * @param force
     *
     * @return
     */

    public boolean deleteEntries( boolean force ) {
        getEntries( true );

        for( int i = 0;i < m_entries.length;i++ ) {
            MDunningRunEntry entry = m_entries[ i ];

            entry.delete( force );
        }

        boolean ok = getEntries( true ).length == 0;

        if( ok ) {
            m_entries = null;
        }

        return ok;
    }    // deleteEntries

    /**
     * Descripción de Método
     *
     *
     * @param C_BPartner_ID
     * @param C_Currency_ID
     * @param SalesRep_ID
     *
     * @return
     */

    public MDunningRunEntry getEntry( int C_BPartner_ID,int C_Currency_ID,int SalesRep_ID ) {

        // TODO: Related BP

        int C_BPartnerRelated_ID = C_BPartner_ID;

        //

        getEntries( false );

        for( int i = 0;i < m_entries.length;i++ ) {
            MDunningRunEntry entry = m_entries[ i ];

            if( entry.getC_BPartner_ID() == C_BPartnerRelated_ID ) {
                return entry;
            }
        }

        // New Entry

        MDunningRunEntry entry = new MDunningRunEntry( this );
        MBPartner        bp    = new MBPartner( getCtx(),C_BPartnerRelated_ID,get_TrxName());

        entry.setBPartner( bp,true );    // AR hardcoded

        //

        if( entry.getSalesRep_ID() == 0 ) {
            entry.setSalesRep_ID( SalesRep_ID );
        }

        entry.setC_Currency_ID( C_Currency_ID );

        //

        m_entries = null;

        return entry;
    }    // getEntry
}    // MDunningRun



/*
 *  @(#)MDunningRun.java   02.07.07
 * 
 *  Fin del fichero MDunningRun.java
 *  
 *  Versión 2.2
 *
 */
