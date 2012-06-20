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
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.DisplayType;
import org.openXpertya.util.KeyNamePair;
import org.openXpertya.util.TimeUtil;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MAttributeSetInstance extends X_M_AttributeSetInstance {

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param M_AttributeSetInstance_ID
     * @param M_Product_ID
     *
     * @return
     */

    public static MAttributeSetInstance get( Properties ctx,int M_AttributeSetInstance_ID,int M_Product_ID ) {
        MAttributeSetInstance retValue = null;

        // Load Instance if not 0

        if( M_AttributeSetInstance_ID != 0 ) {
            s_log.fine( "From M_AttributeSetInstance_ID=" + M_AttributeSetInstance_ID );

            return new MAttributeSetInstance( ctx,M_AttributeSetInstance_ID,null );
        }

        // Get new from Product

        s_log.fine( "From M_Product_ID=" + M_Product_ID );

        if( M_Product_ID == 0 ) {
            return null;
        }

        String sql = "SELECT M_AttributeSet_ID, M_AttributeSetInstance_ID " + "FROM M_Product " + "WHERE M_Product_ID=?";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql );
            pstmt.setInt( 1,M_Product_ID );

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                int M_AttributeSet_ID = rs.getInt( 1 );

                // M_AttributeSetInstance_ID = rs.getInt(2);       //      needed ?
                //

                retValue = new MAttributeSetInstance( ctx,0,M_AttributeSet_ID,null );
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( SQLException ex ) {
            s_log.log( Level.SEVERE,"get",ex );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }
        } catch( SQLException ex1 ) {
        }

        pstmt = null;

        //

        return retValue;
    }    // get

    /** Descripción de Campos */

    private static CLogger s_log = CLogger.getCLogger( MAttributeSetInstance.class );

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param M_AttributeSetInstance_ID
     * @param trxName
     */

    public MAttributeSetInstance( Properties ctx,int M_AttributeSetInstance_ID,String trxName ) {
        super( ctx,M_AttributeSetInstance_ID,trxName );

        if( M_AttributeSetInstance_ID == 0 ) {}
    }    // MAttributeSetInstance

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MAttributeSetInstance( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MAttributeSetInstance

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param M_AttributeSetInstance_ID
     * @param M_AttributeSet_ID
     * @param trxName
     */

    public MAttributeSetInstance( Properties ctx,int M_AttributeSetInstance_ID,int M_AttributeSet_ID,String trxName ) {
        this( ctx,M_AttributeSetInstance_ID,trxName );
        setM_AttributeSet_ID( M_AttributeSet_ID );
    }    // MAttributeSetInstance

    /** Descripción de Campos */

    private MAttributeSet m_mas = null;

    /** Descripción de Campos */

    private DateFormat m_dateFormat = DisplayType.getDateFormat( DisplayType.Date );

    /**
     * Descripción de Método
     *
     *
     * @param mas
     */

    public void setMAttributeSet( MAttributeSet mas ) {
        m_mas = mas;
        setM_AttributeSet_ID( mas.getM_AttributeSet_ID());
    }    // setAttributeSet

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MAttributeSet getMAttributeSet() {
        if( (m_mas == null) && (getM_AttributeSet_ID() != 0) ) {
            m_mas = new MAttributeSet( getCtx(),getM_AttributeSet_ID(),get_TrxName());
        }

        return m_mas;
    }    // getMAttributeSet

    /**
     * Descripción de Método
     *
     */

    public void setDescription() {

        // Make sure we have a Attribute Set

        getMAttributeSet();

        if( m_mas == null ) {
            setDescription( "" );

            return;
        }

        StringBuffer sb = new StringBuffer();

        // SerNo

        if( m_mas.isSerNo() && (getSerNo() != null) ) {
            sb.append( "#" ).append( getSerNo());
        }

        // Lot

        if( m_mas.isLot() && (getLot() != null) ) {
            sb.append( "[" ).append( getLot()).append( "]" );
        }

        // GuaranteeDate

        if( m_mas.isGuaranteeDate() && (getGuaranteeDate() != null) ) {
            if( sb.length() > 0 ) {
                sb.append( "_" );
            }

            sb.append( m_dateFormat.format( getGuaranteeDate()));
        }

        // Instance Attribute Values

        MAttribute[] attributes = m_mas.getMAttributes( false );

        for( int i = 0;i < attributes.length;i++ ) {
            if( (sb.length() > 0) && (i == 0) ) {
                sb.append( "_" );
            }

            if( i != 0 ) {
                sb.append( "-" );
            }

            MAttributeInstance mai = attributes[ i ].getMAttributeInstance( getM_AttributeSetInstance_ID());

            if( (mai != null) && (mai.getValue() != null) ) {
                sb.append( mai.getValue());
            }
        }

        // Product Attribute Values

        attributes = m_mas.getMAttributes( true );

        for( int i = 0;i < attributes.length;i++ ) {
            if( (sb.length() > 0) && (i == 0) ) {
                sb.append( "_" );
            }

            if( i != 0 ) {
                sb.append( "-" );
            }

            MAttributeInstance mai = attributes[ i ].getMAttributeInstance( getM_AttributeSetInstance_ID());

            if( (mai != null) && (mai.getValue() != null) ) {
                sb.append( mai.getValue());
            }
        }

        //
        // log.fine("setDescription - " + sb.toString());

        setDescription( sb.toString());
    }    // setDescription

    /**
     * Descripción de Método
     *
     *
     * @param getNew
     *
     * @return
     */

    public Timestamp getGuaranteeDate( boolean getNew ) {
        if( getNew ) {
            int days = getMAttributeSet().getGuaranteeDays();

            if( days > 0 ) {
                Timestamp ts = TimeUtil.addDays( new Timestamp( System.currentTimeMillis()),days );

                setGuaranteeDate( ts );
            }
        }

        return getGuaranteeDate();
    }    // getGuaranteeDate

    public Timestamp getDueDate( boolean getNew ) {
    	if (getNew) {
    		setGuaranteeDate(getMAttributeSet().getDueDate());
    	}
    	return getGuaranteeDate();
    }
    
    /**
     * Descripción de Método
     *
     *
     * @param getNew
     * @param M_Product_ID
     *
     * @return
     */

    public String getLot( boolean getNew,int M_Product_ID ) {
        if( getNew ) {
            createLot( M_Product_ID );
        }

        return getLot();
    }    // getLot

    /**
     * Descripción de Método
     *
     *
     * @param M_Product_ID
     *
     * @return
     */

    public KeyNamePair createLot( int M_Product_ID ) {
        KeyNamePair retValue    = null;
        int         M_LotCtl_ID = getMAttributeSet().getM_LotCtl_ID();

        if( M_LotCtl_ID != 0 ) {
            MLotCtl ctl = new MLotCtl( getCtx(),M_LotCtl_ID,null );
            MLot    lot = ctl.createLot( M_Product_ID );

            setM_Lot_ID( lot.getM_Lot_ID());
            setLot( lot.getName());
            retValue = new KeyNamePair( lot.getM_Lot_ID(),lot.getName());
        }

        return retValue;
    }    // createLot

    /**
     * Descripción de Método
     *
     *
     * @param getNew
     *
     * @return
     */

    public String getSerNo( boolean getNew ) {
        if( getNew ) {
            int M_SerNoCtl_ID = getMAttributeSet().getM_SerNoCtl_ID();

            if( M_SerNoCtl_ID != 0 ) {
                MSerNoCtl ctl = new MSerNoCtl( getCtx(),M_SerNoCtl_ID,get_TrxName());

                setSerNo( ctl.createSerNo());
            }
        }

        return getSerNo();
    }    // getSerNo
}    // MAttributeSetInstance



/*
 *  @(#)MAttributeSetInstance.java   02.07.07
 * 
 *  Fin del fichero MAttributeSetInstance.java
 *  
 *  Versión 2.2
 *
 */
