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



package org.openXpertya.www;

import org.apache.ecs.AlignType;
import org.apache.ecs.Element;
import org.apache.ecs.xhtml.input;
import org.apache.ecs.xhtml.label;
import org.apache.ecs.xhtml.option;
import org.apache.ecs.xhtml.select;
import org.apache.ecs.xhtml.td;
import org.apache.ecs.xhtml.textarea;
import org.openXpertya.model.Lookup;
import org.openXpertya.model.MLocator;
import org.openXpertya.util.DisplayType;
import org.openXpertya.util.KeyNamePair;
import org.openXpertya.util.ValueNamePair;
import org.openXpertya.util.WebEnv;
import org.openXpertya.util.WebSessionCtx;

/**
 * Descripción de Clase
 *
 *
 * @versión    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class WebField {

    /**
     * Constructor de la clase ...
     *
     *
     * @param wsc
     * @param columnName
     * @param name
     * @param description
     * @param displayType
     * @param fieldLength
     * @param displayLength
     * @param longField
     * @param readOnly
     * @param mandatory
     * @param error
     * @param hasDependents
     * @param hasCallout
     */

    public WebField( WebSessionCtx wsc,String columnName,String name,String description,int displayType,int fieldLength,int displayLength,boolean longField,boolean readOnly,boolean mandatory,boolean error,boolean hasDependents,boolean hasCallout ) {
        super();
        m_wsc        = wsc;
        m_columnName = columnName;

        if( (name == null) || (name.length() == 0) ) {
            m_name = columnName;
        } else {
            m_name = name;
        }

        if( (description != null) && (description.length() > 0) ) {
            m_description = description;
        }

        //

        m_displayType   = displayType;
        m_fieldLength   = fieldLength;
        m_displayLength = displayLength;

        if( m_displayLength == 0 ) {
            m_displayLength = 20;    // default length
        }

        m_longField = longField;

        //

        m_readOnly      = readOnly;
        m_mandatory     = mandatory;
        m_error         = error;
        m_hasDependents = hasDependents;
        m_hasCallout    = hasCallout;

        //

    }    // WebField

    /** Descripción de Campos */

    public static final String C_MANDATORY = "Cmandatory";

    /** Descripción de Campos */

    public static final String C_ERROR = "Cerror";

    /** Descripción de Campos */

    private WebSessionCtx m_wsc;

    /** Descripción de Campos */

    private String m_columnName;

    /** Descripción de Campos */

    private String m_name;

    /** Descripción de Campos */

    private String m_description;

    //

    /** Descripción de Campos */

    private int m_displayType;

    /** Descripción de Campos */

    private int m_fieldLength;

    /** Descripción de Campos */

    private int m_displayLength;

    /** Descripción de Campos */

    private boolean m_longField;

    //

    /** Descripción de Campos */

    private boolean m_readOnly;

    /** Descripción de Campos */

    private boolean m_mandatory;

    /** Descripción de Campos */

    private boolean m_error;

    /** Descripción de Campos */

    private boolean m_hasDependents;

    /** Descripción de Campos */

    private boolean m_hasCallout;

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public td getLabel() {
        if( m_displayType == DisplayType.YesNo ) {
            return new td( WebEnv.NBSP );
        }

        //

        label myLabel = new label( m_columnName + "F",null,m_name );

        myLabel.setID( m_columnName + "L" );

        if( m_description != null ) {
            myLabel.setTitle( m_description );
        }

        //

        td td = new td().addElement( myLabel ).setAlign( AlignType.RIGHT ).setVAlign( AlignType.TOP );

        return td;
    }    // getLabel

    /**
     * Descripción de Método
     *
     *
     * @param element
     *
     * @return
     */

    private td createTD( Element element ) {
        td td = new td().addElement( element ).setAlign( AlignType.LEFT ).setVAlign( AlignType.TOP );

        if( m_longField ) {
            td.setColSpan( 3 );
        }

        return td;
    }    // createTD

    /**
     * Descripción de Método
     *
     *
     * @param lookup
     * @param data
     *
     * @return
     */

    public td getField( Lookup lookup,Object data ) {
        String dataValue = ( data == null )
                           ?""
                           :data.toString();

        //

        if( (m_displayType == DisplayType.Search) || (m_displayType == DisplayType.Location) || (m_displayType == DisplayType.Account) ) {
            String dataDisplay = "";

            if( (lookup != null) && (data != null) ) {
                dataDisplay = lookup.getDisplay( data );
            }

            return getPopupField( dataDisplay,dataValue );
        }

        if( DisplayType.isLookup( m_displayType ) || (m_displayType == DisplayType.Locator) ) {
            return getSelectField( lookup,dataValue );
        }

        if( m_displayType == DisplayType.YesNo ) {
            return getCheckField( dataValue );
        }

        if( m_displayType == DisplayType.Button ) {
            return getButtonField();
        }

        if( DisplayType.isDate( m_displayType )) {
            return getDateField( data );
        } else if( DisplayType.isNumeric( m_displayType )) {
            return getNumberField( data );
        }

        // Strings

        if( m_displayType == DisplayType.Text ) {
            return getTextField( dataValue,3 );
        } else if( m_displayType == DisplayType.TextLong ) {
            return getTextField( dataValue,10 );
        } else if( m_displayType == DisplayType.Memo ) {
            return getTextField( dataValue,15 );
        }

        return getStringField( dataValue );
    }    // getField

    /**
     * Descripción de Método
     *
     *
     * @param data
     *
     * @return
     */

    private td getStringField( String data ) {
        input string = new input( input.TYPE_TEXT,m_columnName,data );

        string.setID( m_columnName + "F" );
        string.setSize( m_displayLength );

        if( m_fieldLength > 0 ) {
            string.setMaxlength( m_fieldLength );
        }

        //

        string.setDisabled( m_readOnly );

        if( m_error ) {
            string.setClass( C_ERROR );
        } else if( m_mandatory ) {
            string.setClass( C_MANDATORY );
        }

        //

        if( m_hasDependents || m_hasCallout ) {
            string.setOnChange( "startUpdate(this);" );
        }

//                      string.setOnChange("dynDisplay();");
        //

        return createTD( string );
    }    // getStringField

    /**
     * Descripción de Método
     *
     *
     * @param data
     * @param rows
     *
     * @return
     */

    private td getTextField( String data,int rows ) {
        textarea text = new textarea( m_columnName,rows,m_displayLength ).addElement( data );

        text.setID( m_columnName + "F" );
        text.setDisabled( m_readOnly );

        if( m_error ) {
            text.setClass( C_ERROR );
        } else if( m_mandatory ) {
            text.setClass( C_MANDATORY );
        }

        //

        if( m_hasDependents || m_hasCallout ) {
            text.setOnChange( "startUpdate(this);" );
        }

        //

        return createTD( text );
    }    // getTextField

    /**
     * Descripción de Método
     *
     *
     * @param data
     *
     * @return
     */

    private td getDateField( Object data ) {
        String formattedData = "";

        if( data == null ) {
            ;
        } else if( data instanceof String ) {
        	formattedData = (String)data;
        } else if( m_displayType == DisplayType.DateTime ) {
            formattedData = m_wsc.dateTimeFormat.format( data );
        } else {
            formattedData = m_wsc.dateFormat.format( data );
        }

        input string = new input( input.TYPE_TEXT,m_columnName,formattedData );

        string.setID( m_columnName + "F" );
        string.setSize( m_displayLength );

        if( m_fieldLength > 0 ) {
            string.setMaxlength( m_fieldLength );
        }

        //

        string.setDisabled( m_readOnly );

        if( m_error ) {
            string.setClass( C_ERROR );
        } else if( m_mandatory ) {
            string.setClass( C_MANDATORY );
        }

        //

        if( m_hasDependents || m_hasCallout ) {
            string.setOnChange( "startUpdate(this);" );
        }

        //

        return createTD( string );
    }    // getDateField

    /**
     * Descripción de Método
     *
     *
     * @param data
     *
     * @return
     */

    private td getNumberField( Object data ) {
        String formattedData = "";

        if( data == null ) {
            ;
        } else if( data instanceof String ) {
        	formattedData = (String)data;
        } else if( m_displayType == DisplayType.Amount ) {
            formattedData = m_wsc.amountFormat.format( data );
        } else if( (m_displayType == DisplayType.Number) || (m_displayType == DisplayType.CostPrice) ) {
            formattedData = m_wsc.numberFormat.format( data );
        } else if( m_displayType == DisplayType.Quantity ) {
            formattedData = m_wsc.quantityFormat.format( data );
        } else if( m_displayType == DisplayType.Integer ) {
            formattedData = m_wsc.integerFormat.format( data );
        } else {
            formattedData = data.toString();
        }

        //

        input string = new input( input.TYPE_TEXT,m_columnName,formattedData );

        string.setID( m_columnName + "F" );
        string.setSize( m_displayLength );

        if( m_fieldLength > 0 ) {
            string.setMaxlength( m_fieldLength );
        }

        //

        string.setDisabled( m_readOnly );

        if( m_error ) {
            string.setClass( C_ERROR );
        } else if( m_mandatory ) {
            string.setClass( C_MANDATORY );
        }

        //

        if( m_hasDependents || m_hasCallout ) {
            string.setOnChange( "startUpdate(this);" );
        }

        //

        return createTD( string );
    }    // getNumberField

    /**
     * Descripción de Método
     *
     *
     * @param data
     *
     * @return
     */

    private td getCheckField( String data ) {
        boolean check = (data != null) && ( data.equals( "true" ) || data.equals( "Y" ));

        //

        input cb = new input( input.TYPE_CHECKBOX,m_columnName,"true" ).setChecked( check ).addElement( m_name );

        cb.setID( m_columnName + "F" );
        cb.setDisabled( m_readOnly );

        if( m_error ) {
            cb.setClass( C_ERROR );
        }

        // else if (m_mandatory)             //  looks odd
        // cb.setClass(C_MANDATORY);
        //

        if( m_hasDependents || m_hasCallout ) {
            cb.setOnChange( "startUpdate(this);" );
        }

        //

        return createTD( cb );
    }    // getCheckField

    /**
     * Descripción de Método
     *
     *
     * @param dataDisplay
     * @param dataValue
     *
     * @return
     */

    private td getPopupField( String dataDisplay,String dataValue ) {

        // The hidden data field        Name=columnName

        input hidden = new input( input.TYPE_HIDDEN,m_columnName,dataValue );

        hidden.setID( m_columnName + "D" );

        // The display field       Name=columnName, ID=FcolumnName

        input display = new input( input.TYPE_TEXT,m_columnName,dataDisplay );

        // display.setSize(field.getDisplayLength()).setMaxlength(field.getFieldLength());

        display.setID( m_columnName + "F" );
        display.setReadOnly( true );

        // The button              Name=columnName, ID=BcolumnName

        input button = new input( input.TYPE_IMAGE,m_columnName,"x" );

        button.setID( m_columnName + "B" );

        String gif = "PickOpen10.gif";

        if( m_displayType == DisplayType.Location ) {
            gif = "Location10.gif";
        } else if( m_displayType == DisplayType.Account ) {
            gif = "Account10.gif";
        } else if( m_columnName.equals( "C_BPartner_ID" )) {
            gif = "BPartner10.gif";
        } else if( m_columnName.equals( "M_Product_ID" )) {
            gif = "Product10.gif";
        }

        button.setSrc( WebEnv.getImageDirectory( gif ));
        button.setBorder( 1 );

        if( m_displayType == DisplayType.Location ) {
            button.setOnClick( "startLocation('" + m_columnName + "');return false;" );
        } else if( m_displayType == DisplayType.Account ) {
            button.setOnClick( "startAccount('" + m_columnName + "');return false;" );
        } else {
            button.setOnClick( "startLookup('" + m_columnName + "');return false;" );
        }

        //

        if( m_error ) {
            display.setClass( C_ERROR );
        } else if( m_mandatory ) {
            display.setClass( C_MANDATORY );
        }

        //

        if( m_hasDependents || m_hasCallout ) {

            // hidden.setOnChange("startUpdate(this);");

            display.setOnChange( "startUpdate(this);" );
        }

        //

        return createTD( hidden ).addElement( display ).addElement( button );
    }    // getPopupField

    /**
     * Descripción de Método
     *
     *
     * @param lookup
     * @param dataValue
     *
     * @return
     */

    private td getSelectField( Lookup lookup,String dataValue ) {
        select sel = new select( m_columnName,getOptions( lookup,dataValue ));

        sel.setID( m_columnName );
        sel.setDisabled( m_readOnly );

        if( m_error ) {
            sel.setClass( C_ERROR );
        } else if( m_mandatory ) {
            sel.setClass( C_MANDATORY );
        }

        //

        if( m_hasDependents || m_hasCallout ) {
            sel.setOnChange( "startUpdate(this);" );
        }

        //

        return createTD( sel );
    }    // getSelectField

    /**
     * Descripción de Método
     *
     *
     * @param lookup
     * @param dataValue
     *
     * @return
     */

    private option[] getOptions( Lookup lookup,String dataValue ) {
        if( lookup == null ) {
            return new option[ 0 ];
        }

        //

        Object[] list = lookup.getData( m_mandatory,true,!m_readOnly,false ).toArray();    // if r/o also inactive
        int      size    = list.length;
        option[] options = new option[ size ];

        for( int i = 0;i < size;i++ ) {
            boolean isNumber = list[ 0 ] instanceof KeyNamePair;
            String  key      = null;

            if( m_displayType == DisplayType.Locator ) {
                MLocator loc = ( MLocator )list[ i ];

                key          = String.valueOf( loc.getM_Locator_ID());
                options[ i ] = new option( key ).addElement( loc.getValue());
            } else if( isNumber ) {
                KeyNamePair p = ( KeyNamePair )list[ i ];

                key          = String.valueOf( p.getKey());
                options[ i ] = new option( key ).addElement( p.getName());
            } else {
                ValueNamePair p = ( ValueNamePair )list[ i ];

                key = p.getValue();

                if( (key == null) || (key.length() == 0) ) {
                    key = "??";
                }

                String name = p.getName();

                if( (name == null) || (name.length() == 0) ) {
                    name = "???";
                }

                options[ i ] = new option( key ).addElement( name );
            }

            if( dataValue.equals( key )) {
                options[ i ].setSelected( true );
            }
        }

        return options;
    }    // getOptions

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private td getButtonField() {
        input button = new input( input.TYPE_BUTTON,m_columnName,m_name );

        button.setID( m_columnName );
        button.setDisabled( m_readOnly );
        button.setOnClick( "startButton(this);" );

        return createTD( button ).setAlign( AlignType.LEFT );    // overwrite
    }                                                            // getButtonField
}    // WebField



/*
 *  @(#)WebField.java   23.03.06
 * 
 *  Fin del fichero WebField.java
 *  
 *  Versión 2.2
 *
 */
