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

import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MBPartnerLocation extends X_C_BPartner_Location {

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param C_BPartner_ID
     *
     * @return
     */

    public static MBPartnerLocation[] getForBPartner( Properties ctx,int C_BPartner_ID ) {
        ArrayList         list  = new ArrayList();
        String            sql   = "SELECT * FROM C_BPartner_Location WHERE C_BPartner_ID=?";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql );
            pstmt.setInt( 1,C_BPartner_ID );

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                list.add( new MBPartnerLocation( ctx,rs,null ));
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            s_log.log( Level.SEVERE,"getForBPartner",e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        MBPartnerLocation[] retValue = new MBPartnerLocation[ list.size()];

        list.toArray( retValue );

        return retValue;
    }    // getForBPartner

    /** Descripción de Campos */

    private static CLogger s_log = CLogger.getCLogger( MBPartnerLocation.class );

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param C_BPartner_Location_ID
     * @param trxName
     */

    public MBPartnerLocation( Properties ctx,int C_BPartner_Location_ID,String trxName ) {
        super( ctx,C_BPartner_Location_ID,trxName );

        if( C_BPartner_Location_ID == 0 ) {
            setName( "." );

            //

            setIsShipTo( true );
            setIsRemitTo( true );
            setIsPayFrom( true );
            setIsBillTo( true );
        }
    }    // MBPartner_Location

    /**
     * Constructor de la clase ...
     *
     *
     * @param bp
     */

    public MBPartnerLocation( MBPartner bp ) {
        this( bp.getCtx(),0,bp.get_TrxName());
        setClientOrg( bp );
        setC_BPartner_ID( bp.getC_BPartner_ID());
    }    // MBPartner_Location

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MBPartnerLocation( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MBPartner_Location

    /** Descripción de Campos */

    private MLocation m_location = null;

    /** Descripción de Campos */

    private String m_uniqueName = null;

    /** Descripción de Campos */

    private int m_unique = 0;

    /**
     * Descripción de Método
     *
     *
     * @param requery
     *
     * @return
     */

    public MLocation getLocation( boolean requery ) {
        if( m_location == null ) {
            m_location = MLocation.get( getCtx(),getC_Location_ID(),get_TrxName());
        }

        return m_location;
    }    // getLoaction

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer( "MBPartner_Location[ID=" ).append( getID()).append( ",C_Location_ID=" ).append( getC_Location_ID()).append( ",Name=" ).append( getName()).append( "]" );

        return sb.toString();
    }    // toString

    /**
     * Descripción de Método
     *
     *
     * @param newRecord
     *
     * @return
     */

    protected boolean beforeSave( boolean newRecord ) {
        if( getC_Location_ID() == 0 ) {
            return false;
        }

        // Set New Name

        if( !newRecord ) {
            return true;
        }

        MLocation address = getLocation( true );

        m_uniqueName = getName();

        if( (m_uniqueName != null) && m_uniqueName.equals( "." )) {    // default
            m_uniqueName = null;
        }

        m_unique = 0;
        makeUnique( address );

        // Check uniqueness

        MBPartnerLocation[] locations = MBPartnerLocation.getForBPartner( getCtx(),getC_BPartner_ID());
        boolean unique = locations.length == 0;

        while( !unique ) {
            unique = true;

            for( int i = 0;i < locations.length;i++ ) {
                MBPartnerLocation location = locations[ i ];

                if( location.getC_BPartner_Location_ID() == getID()) {
                    continue;
                }

                if( m_uniqueName.equals( location.getName())) {
                    makeUnique( address );
                    unique = false;

                    break;
                }
            }
        }

        setName( m_uniqueName );

        return true;
    }    // beforeSave

    /**
     * Descripción de Método
     *
     *
     * @param address
     */

    private void makeUnique( MLocation address ) {
        if( m_uniqueName == null ) {
            m_uniqueName = "";
        }

        m_unique++;

        // 0 - City

        if( m_uniqueName.length() == 0 ) {
            String xx = address.getCity();

            if( (xx != null) && (xx.length() > 0) ) {
                m_uniqueName = xx;
            }

            m_unique = 0;
        }

        // 1 + Address1

        if( (m_unique == 1) || (m_uniqueName.length() == 0) ) {
            String xx = address.getAddress1();

            if( (xx != null) && (xx.length() > 0) ) {
                if( m_uniqueName.length() > 0 ) {
                    m_uniqueName += " ";
                }

                m_uniqueName += xx;
            }

            m_unique = 1;
        }

        // 2 + Address2

        if( (m_unique == 2) || (m_uniqueName.length() == 0) ) {
            String xx = address.getAddress2();

            if( (xx != null) && (xx.length() > 0) ) {
                if( m_uniqueName.length() > 0 ) {
                    m_uniqueName += " ";
                }

                m_uniqueName += xx;
            }

            m_unique = 2;
        }

        // 3 - Region

        if( (m_unique == 3) || (m_uniqueName.length() == 0) ) {
            String xx = address.getRegionName( true );

            {
                if( m_uniqueName.length() > 0 ) {
                    m_uniqueName += " ";
                }

                m_uniqueName += xx;
            }

            m_unique = 3;
        }

        // 4 - ID

        if( (m_unique == 4) || (m_uniqueName.length() == 0) ) {
            int id = getID();

            if( id == 0 ) {
                id = address.getID();
            }

            m_uniqueName += "#" + id;
            m_unique     = 4;
        }
    }    // makeUnique
}    // MBPartnerLocation



/*
 *  @(#)MBPartnerLocation.java   02.07.07
 * 
 *  Fin del fichero MBPartnerLocation.java
 *  
 *  Versión 2.2
 *
 */
