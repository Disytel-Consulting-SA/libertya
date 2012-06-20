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

import org.openXpertya.util.DB;
import org.openXpertya.util.DisplayType;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MAttribute extends X_M_Attribute {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param M_Attribute_ID
     * @param trxName
     */

    public MAttribute( Properties ctx,int M_Attribute_ID,String trxName ) {
        super( ctx,M_Attribute_ID,trxName );

        if( M_Attribute_ID == 0 ) {
            setAttributeValueType( ATTRIBUTEVALUETYPE_StringMax40 );
            setIsInstanceAttribute( false );
            setIsMandatory( false );
        }
    }    // MAttribute

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MAttribute( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MAttribute

    /** Descripción de Campos */

    private MAttributeValue[] m_values = null;

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MAttributeValue[] getMAttributeValues() {
        if( (m_values == null) && ATTRIBUTEVALUETYPE_List.equals( getAttributeValueType())) {
            ArrayList list = new ArrayList();

            if( !isMandatory()) {
                list.add( null );
            }

            //

            String sql = "SELECT * FROM M_AttributeValue " + "WHERE M_Attribute_ID=? " + "ORDER BY Value";
            PreparedStatement pstmt = null;

            try {
                pstmt = DB.prepareStatement( sql );
                pstmt.setInt( 1,getM_Attribute_ID());

                ResultSet rs = pstmt.executeQuery();

                while( rs.next()) {
                    list.add( new MAttributeValue( getCtx(),rs,null ));
                }

                rs.close();
                pstmt.close();
                pstmt = null;
            } catch( SQLException ex ) {
                log.log( Level.SEVERE,sql,ex );
            }

            try {
                if( pstmt != null ) {
                    pstmt.close();
                }
            } catch( SQLException ex1 ) {
            }

            pstmt    = null;
            m_values = new MAttributeValue[ list.size()];
            list.toArray( m_values );
        }

        return m_values;
    }    // getValues

    /**
     * Descripción de Método
     *
     *
     * @param M_AttributeSetInstance_ID
     *
     * @return
     */

    public MAttributeInstance getMAttributeInstance( int M_AttributeSetInstance_ID ) {
        MAttributeInstance retValue = null;
        String             sql      = "SELECT * " + "FROM M_AttributeInstance " + "WHERE M_Attribute_ID=? AND M_AttributeSetInstance_ID=?";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql,get_TrxName());
            pstmt.setInt( 1,getM_Attribute_ID());
            pstmt.setInt( 2,M_AttributeSetInstance_ID );

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                retValue = new MAttributeInstance( getCtx(),rs,get_TrxName());
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( SQLException ex ) {
            log.log( Level.SEVERE,sql,ex );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }
        } catch( SQLException ex1 ) {
        }

        pstmt = null;

        return retValue;
    }    // getAttributeInstance

    /**
     * Descripción de Método
     *
     *
     * @param M_AttributeSetInstance_ID
     * @param value
     */

    public void setMAttributeInstance( int M_AttributeSetInstance_ID,MAttributeValue value ) {
        MAttributeInstance instance = getMAttributeInstance( M_AttributeSetInstance_ID );

        if( instance == null ) {
            if( value != null ) {
                instance = new MAttributeInstance( getCtx(),getM_Attribute_ID(),M_AttributeSetInstance_ID,value.getM_AttributeValue_ID(),value.getName(),get_TrxName());    // Cached !!
            } else {
                instance = new MAttributeInstance( getCtx(),getM_Attribute_ID(),M_AttributeSetInstance_ID,0,null,get_TrxName());
            }
        } else {
            if( value != null ) {
                instance.setM_AttributeValue_ID( value.getM_AttributeValue_ID());
                instance.setValue( value.getName());    // Cached !!
            } else {
                instance.setM_AttributeValue_ID( 0 );
                instance.setValue( null );
            }
        }

        instance.save();
    }    // setAttributeInstance

    /**
     * Descripción de Método
     *
     *
     * @param M_AttributeSetInstance_ID
     * @param value
     */

    public void setMAttributeInstance( int M_AttributeSetInstance_ID,String value ) {
        MAttributeInstance instance = getMAttributeInstance( M_AttributeSetInstance_ID );

        if( instance == null ) {
            instance = new MAttributeInstance( getCtx(),getM_Attribute_ID(),M_AttributeSetInstance_ID,value,get_TrxName());
        } else {
            instance.setValue( value );
        }

        instance.save();
    }    // setAttributeInstance

    /**
     * Descripción de Método
     *
     *
     * @param M_AttributeSetInstance_ID
     * @param value
     */

    public void setMAttributeInstance( int M_AttributeSetInstance_ID,BigDecimal value ) {
        MAttributeInstance instance = getMAttributeInstance( M_AttributeSetInstance_ID );

        if( instance == null ) {
            instance = new MAttributeInstance( getCtx(),getM_Attribute_ID(),M_AttributeSetInstance_ID,value,get_TrxName());
        } else {
            instance.setValueNumber( value );
            instance.setValue( DisplayType.getNumberFormat(DisplayType.Number).format(value));    // for display
        }

        instance.save();
    }    // setAttributeInstance

    public void setMAttributeInstance( int M_AttributeSetInstance_ID,Timestamp value ) {
        MAttributeInstance instance = getMAttributeInstance( M_AttributeSetInstance_ID );

        if( instance == null ) {
            instance = new MAttributeInstance( getCtx(),getM_Attribute_ID(),M_AttributeSetInstance_ID, value,get_TrxName());
        } else {
        	instance.setValueDate( value );
            instance.setValue( DisplayType.getDateFormat().format(value));
        }

        instance.save();
    }    // setAttributeInstance
    
    /**
     * Descripción de Método
     *
     *
     * @param newRecord
     * @param success
     *
     * @return
     */

    protected boolean afterSave( boolean newRecord,boolean success ) {

        // Changed to Instance Attribute

        if( !newRecord && is_ValueChanged( "IsInstanceAttribute" ) && isInstanceAttribute()) {
            String sql = "UPDATE M_AttributeSet mas " + "SET IsInstanceAttribute='Y' " + "WHERE IsInstanceAttribute='N'" + " AND EXISTS (SELECT * FROM M_AttributeUse mau " + "WHERE mas.M_AttributeSet_ID=mau.M_AttributeSet_ID" + " AND mau.M_Attribute_ID=" + getM_Attribute_ID() + ")";
            int no = DB.executeUpdate( sql,get_TrxName());

            log.fine( "AttributeSet Instance set #" + no );
        }

        return success;
    }    // afterSave
}    // MAttribute



/*
 *  @(#)MAttribute.java   02.07.07
 * 
 *  Fin del fichero MAttribute.java
 *  
 *  Versión 2.2
 *
 */
