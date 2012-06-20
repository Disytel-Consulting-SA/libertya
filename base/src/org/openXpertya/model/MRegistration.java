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
import java.util.Arrays;
import java.util.Properties;
import java.util.logging.Level;

import javax.servlet.http.HttpServletRequest;

import org.openXpertya.util.DB;
import org.openXpertya.util.WebUtil;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MRegistration extends X_A_Registration {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param A_Registration_ID
     * @param trxName
     */

    public MRegistration( Properties ctx,int A_Registration_ID,String trxName ) {
        super( ctx,A_Registration_ID,trxName );

        if( A_Registration_ID == 0 ) {
            setIsRegistered( true );
        }
    }    // MRegistration

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param Name
     * @param IsAllowPublish
     * @param IsInProduction
     * @param AssetServiceDate
     * @param trxName
     */

    public MRegistration( Properties ctx,String Name,boolean IsAllowPublish,boolean IsInProduction,Timestamp AssetServiceDate,String trxName ) {
        this( ctx,0,trxName );
        setName( Name );
        setIsAllowPublish( IsAllowPublish );
        setIsInProduction( IsInProduction );
        setAssetServiceDate( AssetServiceDate );
    }    // MRegistration

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MRegistration( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MRegistration

    /** Descripción de Campos */

    private MRegistrationAttribute[] m_allAttributes = null;

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MRegistrationAttribute[] getAttributes() {
        if( m_allAttributes == null ) {
            m_allAttributes = MRegistrationAttribute.getAll( getCtx());
        }

        return m_allAttributes;
    }    // getAttributes

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MRegistrationValue[] getValues() {
        return getValues( true );
    }    // getValues

    /**
     * Descripción de Método
     *
     *
     * @param onlySelfService
     *
     * @return
     */

    public MRegistrationValue[] getValues( boolean onlySelfService ) {
        createMissingValues();

        //

        String sql = "SELECT * FROM A_RegistrationValue rv " + "WHERE A_Registration_ID=?";

        if( onlySelfService ) {
            sql += " AND EXISTS (SELECT * FROM A_RegistrationAttribute ra WHERE rv.A_RegistrationAttribute_ID=ra.A_RegistrationAttribute_ID" + " AND ra.IsActive='Y' AND ra.IsSelfService='Y')";
        }

        // sql += " ORDER BY A_RegistrationAttribute_ID";

        ArrayList         list  = new ArrayList();
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql );
            pstmt.setInt( 1,getA_Registration_ID());

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                list.add( new MRegistrationValue( getCtx(),rs,get_TrxName()));
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"getValues",e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        // Convert and Sort

        MRegistrationValue[] retValue = new MRegistrationValue[ list.size()];

        list.toArray( retValue );
        Arrays.sort( retValue );

        return retValue;
    }    // getValues

    /**
     * Descripción de Método
     *
     */

    private void createMissingValues() {
        String sql = "SELECT ra.A_RegistrationAttribute_ID " + "FROM A_RegistrationAttribute ra" + " LEFT OUTER JOIN A_RegistrationProduct rp ON (rp.A_RegistrationAttribute_ID=ra.A_RegistrationAttribute_ID)" + " LEFT OUTER JOIN A_Registration r ON (r.M_Product_ID=rp.M_Product_ID) " + "WHERE r.A_Registration_ID=?"

        // Not in Registration

        + " AND NOT EXISTS (SELECT A_RegistrationAttribute_ID FROM A_RegistrationValue v " + "WHERE ra.A_RegistrationAttribute_ID=v.A_RegistrationAttribute_ID AND r.A_Registration_ID=v.A_Registration_ID)";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql );
            pstmt.setInt( 1,getA_Registration_ID());

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                MRegistrationValue v = new MRegistrationValue( this,rs.getInt( 1 ),"?" );

                v.save();
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"createMissingValues",e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }
    }    // createMissingValues

    /**
     * Descripción de Método
     *
     *
     * @param request
     *
     * @return
     */

    public int loadAttributeValues( HttpServletRequest request ) {

        // save if not saved

        if( getID() == 0 ) {
            save();
        }

        int count = 0;

        // read values for all attributes

        MRegistrationAttribute[] attributes = getAttributes();

        for( int i = 0;i < attributes.length;i++ ) {
            MRegistrationAttribute attribute = attributes[ i ];
            String                 value     = WebUtil.getParameter( request,attribute.getName());

            if( value == null ) {
                continue;
            }

            MRegistrationValue regValue = new MRegistrationValue( this,attribute.getA_RegistrationAttribute_ID(),value );

            if( regValue.save()) {
                count++;
            }
        }

        log.fine( "loadAttributeValues - #" + count + " (of " + attributes.length + ")" );

        return count;
    }    // loadAttrubuteValues

    /**
     * Descripción de Método
     *
     *
     * @param request
     *
     * @return
     */

    public int updateAttributeValues( HttpServletRequest request ) {

        // save if not saved

        if( getID() == 0 ) {
            save();
        }

        int count = 0;

        // Get All Values

        MRegistrationValue[] regValues = getValues( false );

        for( int i = 0;i < regValues.length;i++ ) {
            MRegistrationValue regValue      = regValues[ i ];
            String             attributeName = regValue.getRegistrationAttribute();

            //

            String dataValue = WebUtil.getParameter( request,attributeName );

            if( dataValue == null ) {
                continue;
            }

            regValue.setDescription( "Previous=" + regValue.getName());
            regValue.setName( dataValue );

            if( regValue.save()) {
                count++;
            }
        }

        log.fine( "updateAttributeValues - #" + count + " (of " + regValues.length + ")" );

        return count;
    }    // updateAttrubuteValues
}    // MRegistration



/*
 *  @(#)MRegistration.java   02.07.07
 * 
 *  Fin del fichero MRegistration.java
 *  
 *  Versión 2.2
 *
 */
