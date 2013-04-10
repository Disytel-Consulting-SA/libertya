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



package org.openXpertya.print;

import java.math.BigDecimal;
import java.sql.Timestamp;

import org.openXpertya.model.MLocation;
import org.openXpertya.util.DisplayType;
import org.openXpertya.util.Env;
import org.openXpertya.util.Language;
import org.openXpertya.util.Msg;
import org.openXpertya.util.NamePair;

/**
 * Descripción de Clase
 *
 *
 * @versión    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class PrintDataElement {

    /**
     * Constructor de la clase ...
     *
     *
     * @param columnName
     * @param value
     * @param displayType
     * @param isPKey
     * @param isPageBreak
     */

    public PrintDataElement( String columnName,Object value,int displayType,boolean isPKey,boolean isPageBreak ) {
        if( columnName == null ) {
            throw new IllegalArgumentException( "PrintDataElement - Name cannot be null" );
        }

        m_columnName  = columnName;
        m_value       = value;
        m_displayType = displayType;
        if (DisplayType.isText(m_displayType)) {
        	m_value = Msg.parseTranslation(Env.getCtx(), (String)m_value);
        }
        m_isPKey      = isPKey;
        m_isPageBreak = isPageBreak;
    }    // PrintDataElement

    /**
     * Constructor de la clase ...
     *
     *
     * @param columnName
     * @param value
     * @param displayType
     */

    public PrintDataElement( String columnName,Object value,int displayType ) {
        this( columnName,value,displayType,false,false );
    }    // PrintDataElement

    /** Descripción de Campos */

    private String m_columnName;

    /** Descripción de Campos */

    private Object m_value;

    /** Descripción de Campos */

    private int m_displayType;

    /** Descripción de Campos */

    private boolean m_isPKey;

    /** Descripción de Campos */

    private boolean m_isPageBreak;

    /** Descripción de Campos */

    public static final String XML_TAG = "element";

    /** Descripción de Campos */

    public static final String XML_ATTRIBUTE_NAME = "name";

    /** Descripción de Campos */

    public static final String XML_ATTRIBUTE_KEY = "key";

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getColumnName() {
        return m_columnName;
    }    // getName

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Object getValue() {
        return m_value;
    }    // getValue

    
	/**
	 * 	Get Node Data Value as String
	 * 	@return data value
	 */
	public String getValueAsString()
	{
		if (m_value == null)
			return "";
		String retValue = m_value.toString();
		if (m_value instanceof NamePair)
			retValue = ((NamePair)m_value).getID();
		return retValue;
	}	//	getValueDisplay

    
    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public BigDecimal getFunctionValue() {
        if( m_value == null ) {
            return Env.ZERO;
        }

        // Numbers - return number value

        if( m_value instanceof BigDecimal ) {
            return( BigDecimal )m_value;
        }

        if( m_value instanceof Number ) {
            return new BigDecimal((( Number )m_value ).doubleValue());
        }

        // Boolean - return 1 for true 0 for false

        if( m_value instanceof Boolean ) {
            if((( Boolean )m_value ).booleanValue()) {
                return Env.ONE;
            } else {
                return Env.ZERO;
            }
        }

        // Return Length

        String s = m_value.toString();

        return new BigDecimal( s.length());
    }    // getFunctionValue

    /**
     * Descripción de Método
     *
     *
     * @param language
     *
     * @return
     */

    public String getValueDisplay( Language language ) {
        if( m_value == null ) {
            return "";
        }

        String retValue = m_value.toString();

        if( m_displayType == DisplayType.Location ) {
            return getValueDisplay_Location();
        } else if( m_columnName.equals( "C_BPartner_Location_ID" ) || m_columnName.equals( "Bill_Location_ID" )) {
            return getValueDisplay_BPLocation();
        } else if( (m_displayType == 0) || (m_value instanceof String) || (m_value instanceof NamePair) ) {
            ;
        } else if( language != null )    // Optional formatting of Numbers and Dates
        {
            if( DisplayType.isNumeric( m_displayType )) {
                retValue = DisplayType.getNumberFormat( m_displayType,language ).format( m_value );
            } else if( DisplayType.isDate( m_displayType )) {
                retValue = DisplayType.getDateFormat( m_displayType,language ).format( m_value );
            }
        }

        return retValue;
    }    // getValueDisplay

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private String getValueDisplay_BPLocation() {
        try {
            int C_BPartner_Location_ID = Integer.parseInt( getValueKey());

            if( C_BPartner_Location_ID != 0 ) {
                MLocation loc = MLocation.getBPLocation( Env.getCtx(),C_BPartner_Location_ID,null );

                if( loc != null ) {
                    return loc.toStringCR();
                }
            }
        } catch( Exception ex ) {
        }

        return m_value.toString();
    }    // getValueDisplay_BPLocation

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private String getValueDisplay_Location() {
        try {
            int C_Location_ID = Integer.parseInt( getValueKey());

            if( C_Location_ID != 0 ) {
                MLocation loc = new MLocation( Env.getCtx(),C_Location_ID,null );

                if( loc != null ) {
                    return loc.toStringCR();
                }
            }
        } catch( Exception ex ) {
        }

        return m_value.toString();
    }    // getValueDisplay_Location

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getValueKey() {
        if( m_value == null ) {
            return "";
        }

        if( m_value instanceof NamePair ) {
            return(( NamePair )m_value ).getID();
        }

        return "";
    }    // getValueKey

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isNull() {
        return m_value == null;
    }    // isNull

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getDisplayType() {
        return m_displayType;
    }    // getDisplayType

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isNumeric() {
        if( m_displayType == 0 ) {
            return m_value instanceof BigDecimal;
        }

        return DisplayType.isNumeric( m_displayType );
    }    // isNumeric

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isDate() {
        if( m_displayType == 0 ) {
            return m_value instanceof Timestamp;
        }

        return DisplayType.isDate( m_displayType );
    }    // isDate

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isID() {
        return DisplayType.isID( m_displayType );
    }    // isID

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isYesNo() {
        if( m_displayType == 0 ) {
            return m_value instanceof Boolean;
        }

        return DisplayType.YesNo == m_displayType;
    }    // isYesNo

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isPKey() {
        return m_isPKey;
    }    // isPKey

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isPageBreak() {
        return m_isPageBreak;
    }    // isPageBreak

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int hashCode() {
        if( m_value == null ) {
            return m_columnName.hashCode();
        }

        return m_columnName.hashCode() + m_value.hashCode();
    }    // hashCode

    /**
     * Descripción de Método
     *
     *
     * @param compare
     *
     * @return
     */

    public boolean equals( Object compare ) {
        if( compare instanceof PrintDataElement ) {
            PrintDataElement pde = ( PrintDataElement )compare;

            if( pde.getColumnName().equals( m_columnName )) {
                if( (pde.getValue() != null) && pde.getValue().equals( m_value )) {
                    return true;
                }

                if( (pde.getValue() == null) && (m_value == null) ) {
                    return true;
                }
            }
        }

        return false;
    }    // equals

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer( m_columnName ).append( "=" ).append( m_value );

        if( m_isPKey ) {
            sb.append( "(PK)" );
        }

        return sb.toString();
    }    // toString

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean hasKey() {
        return m_value instanceof NamePair;
    }    // hasKey

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toStringX() {
        if( m_value instanceof NamePair ) {
            NamePair     pp = ( NamePair )m_value;
            StringBuffer sb = new StringBuffer( m_columnName );

            sb.append( "(" ).append( pp.getID()).append( ")" ).append( "=" ).append( pp.getName());

            if( m_isPKey ) {
                sb.append( "(PK)" );
            }

            return sb.toString();
        } else {
            return toString();
        }
    }    // toStringX
}    // PrintDataElement



/*
 *  @(#)PrintDataElement.java   23.03.06
 * 
 *  Fin del fichero PrintDataElement.java
 *  
 *  Versión 2.2
 *
 */
