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

import java.awt.Color;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;

import org.compiere.plaf.CompiereColor;
import org.openXpertya.util.DB;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MColor extends X_AD_Color {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param AD_Color_ID
     * @param trxName
     */

    public MColor( Properties ctx,int AD_Color_ID,String trxName ) {
        super( ctx,AD_Color_ID,trxName );

        if( AD_Color_ID == 0 ) {
            setName( "-/-" );
        }
    }    // MColor

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        return "MColor[ID=" + getID() + " - " + getName() + "]";
    }    // toString

    /**
     * Descripción de Método
     *
     *
     * @param rs
     * @param index
     *
     * @return
     *
     * @throws SQLException
     */

    protected Object loadSpecial( ResultSet rs,int index ) throws SQLException {
        log.config( p_info.getColumnName( index ));

        if( index == get_ColumnIndex( "ColorType" )) {
            return rs.getString( index + 1 );
        }

        return null;
    }    // loadSpecial

    /**
     * Descripción de Método
     *
     *
     * @param value
     * @param index
     *
     * @return
     */

    protected String saveNewSpecial( Object value,int index ) {
        String colName  = p_info.getColumnName( index );
        String colValue = (value == null)
                          ?"null"
                          :value.getClass().toString();

        log.fine( colName + "=" + colValue );

        if( value == null ) {
            return "NULL";
        }

        return value.toString();
    }    // saveNewSpecial

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public CompiereColor getCompiereColor() {
        if( getID() == 0 ) {
            return null;
        }

        // Color Type

        String ColorType = ( String )getColorType();

        if( ColorType == null ) {
            log.log( Level.SEVERE,"MColor.getCompiereColor - No ColorType" );

            return null;
        }

        CompiereColor cc = null;

        //

        if( ColorType.equals( CompiereColor.TYPE_FLAT )) {
            cc = new CompiereColor( getColor( true ),true );
        } else if( ColorType.equals( CompiereColor.TYPE_GRADIENT )) {
            int    RepeatDistance = getRepeatDistance();
            String StartPoint     = getStartPoint();
            int    startPoint     = (StartPoint == null)
                                    ?0
                                    :Integer.parseInt( StartPoint );

            cc = new CompiereColor( getColor( true ),getColor( false ),startPoint,RepeatDistance );
        } else if( ColorType.equals( CompiereColor.TYPE_LINES )) {
            int LineWidth    = getLineWidth();
            int LineDistance = getLineDistance();

            cc = new CompiereColor( getColor( false ),getColor( true ),LineWidth,LineDistance );
        } else if( ColorType.equals( CompiereColor.TYPE_TEXTURE )) {
            int    AD_Image_ID = getAD_Image_ID();
            String url         = getURL( AD_Image_ID );

            if( url == null ) {
                return null;
            }

            BigDecimal ImageAlpha     = getImageAlpha();
            float      compositeAlpha = (ImageAlpha == null)
                                        ?0.7f
                                        :ImageAlpha.floatValue();

            cc = new CompiereColor( url,getColor( true ),compositeAlpha );
        }

        return cc;
    }    // getCompiereColor

    /**
     * Descripción de Método
     *
     *
     * @param primary
     *
     * @return
     */

    private Color getColor( boolean primary ) {
        int red   = primary
                    ?getRed()
                    :getRed_1();
        int green = primary
                    ?getGreen()
                    :getGreen_1();
        int blue  = primary
                    ?getBlue()
                    :getBlue_1();

        //

        return new Color( red,green,blue );
    }    // getColor

    /**
     * Descripción de Método
     *
     *
     * @param AD_Image_ID
     *
     * @return
     */

    private String getURL( int AD_Image_ID ) {
        if( AD_Image_ID == 0 ) {
            return null;
        }

        //

        String retValue = null;
        String sql      = "SELECT ImageURL FROM AD_Image WHERE AD_Image_ID=?";

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql );

            pstmt.setInt( 1,AD_Image_ID );

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                retValue = rs.getString( 1 );
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"MColor.getURL",e );
        }

        return retValue;
    }    // getURL
    
    /**************************************************************************
	 *  Get AdempiereColor.
	 *  see org.compiere.grid.ed.VColor#getAdempiereColor
	 *  @return AdempiereColor
	 */
	public CompiereColor getOpenXpertyaColor()
	{
		if (getID() == 0)
			return null;

		//  Color Type
		String ColorType = (String)getColorType();
		if (ColorType == null)
		{
			log.log(Level.SEVERE, "MColor.getAdempiereColor - No ColorType");
			return null;
		}
		CompiereColor cc = null;
		//
		if (ColorType.equals(CompiereColor.TYPE_FLAT))
		{
			cc = new CompiereColor(getColor(true), true);
		}
		else if (ColorType.equals(CompiereColor.TYPE_GRADIENT))
		{
			int RepeatDistance = getRepeatDistance();
			String StartPoint = getStartPoint();
			int startPoint = StartPoint == null ? 0 : Integer.parseInt(StartPoint);
			cc = new CompiereColor(getColor(true), getColor(false), startPoint, RepeatDistance);
		}
		else if (ColorType.equals(CompiereColor.TYPE_LINES))
		{
			int LineWidth = getLineWidth();
			int LineDistance = getLineDistance();
			cc = new CompiereColor(getColor(false), getColor(true), LineWidth, LineDistance);
		}
		else if (ColorType.equals(CompiereColor.TYPE_TEXTURE))
		{
			int AD_Image_ID = getAD_Image_ID();
			String url = getURL(AD_Image_ID);
			if (url == null)
				return null;
			BigDecimal ImageAlpha = getImageAlpha();
			float compositeAlpha = ImageAlpha == null ? 0.7f : ImageAlpha.floatValue();
			cc = new CompiereColor(url, getColor(true), compositeAlpha);
		}
		return cc;
	}   //  getAdempiereColor

}    // MColor



/*
 *  @(#)MColor.java   02.07.07
 * 
 *  Fin del fichero MColor.java
 *  
 *  Versión 2.2
 *
 */
