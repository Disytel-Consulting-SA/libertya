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



package org.openXpertya.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public final class DisplayType {

    /** Descripción de Campos */

    public static final int String = 10;

    /** Descripción de Campos */

    public static final int Integer = 11;

    /** Descripción de Campos */

    public static final int Amount = 12;

    /** Descripción de Campos */

    public static final int ID = 13;

    /** Descripción de Campos */

    public static final int Text = 14;

    /** Descripción de Campos */

    public static final int Date = 15;

    /** Descripción de Campos */

    public static final int DateTime = 16;

    /** Descripción de Campos */

    public static final int List = 17;

    /** Descripción de Campos */

    public static final int Table = 18;

    /** Descripción de Campos */

    public static final int TableDir = 19;

    /** Descripción de Campos */

    public static final int YesNo = 20;

    /** Descripción de Campos */

    public static final int Location = 21;

    /** Descripción de Campos */

    public static final int Number = 22;

    /** Descripción de Campos */

    public static final int Binary = 23;

    /** Descripción de Campos */

    public static final int Time = 24;

    /** Descripción de Campos */

    public static final int Account = 25;

    /** Descripción de Campos */

    public static final int RowID = 26;

    /** Descripción de Campos */

    public static final int Color = 27;

    /** Descripción de Campos */

    public static final int Button = 28;

    /** Descripción de Campos */

    public static final int Quantity = 29;

    /** Descripción de Campos */

    public static final int Search = 30;

    /** Descripción de Campos */

    public static final int Locator = 31;

    /** Descripción de Campos */

    public static final int Image = 32;

    /** Descripción de Campos */

    public static final int Assignment = 33;

    /** Descripción de Campos */

    public static final int Memo = 34;

    /** Descripción de Campos */

    public static final int PAttribute = 35;

    /** Descripción de Campos */

    public static final int TextLong = 36;

    /** Descripción de Campos */

    public static final int CostPrice = 37;
    
	/** Display Type 38	File Path	*/
    
	public static final int FilePath  = 38;
	
	/** Display Type 39 File Name	*/
	
	public static final int FileName  = 39;
	
	/** Display Type 40	URL	*/
	
	public static final int URL  = 40;
	
	/** Display Type 42	PrinterName	*/
	
	public static final int PrinterName  = 42;


    // Candidates: FileName, PrinterName

    // See DBA_DisplayType.sql ----------------------------------------------

    /** Descripción de Campos */

    private static final int MAX_DIGITS = 28;    // Oracle Standard Limitation 38 digits

    /** Descripción de Campos */

    private static final int INTEGER_DIGITS = 10;

    /** Descripción de Campos */

    private static final int MAX_FRACTION = 12;

    /** Descripción de Campos */

    private static final int AMOUNT_FRACTION = 2;

    /**
     * Descripción de Método
     *
     *
     * @param displayType
     *
     * @return
     */

    public static boolean isID( int displayType ) {
        if( (displayType == ID) || (displayType == Table) || (displayType == TableDir) || (displayType == Search) || (displayType == Location) || (displayType == Locator) || (displayType == Account) || (displayType == Assignment) || (displayType == PAttribute) ) {
            return true;
        }

        return false;
    }    // isID

    /**
     * Descripción de Método
     *
     *
     * @param displayType
     *
     * @return
     */

    public static boolean isNumeric( int displayType ) {
        if( (displayType == Amount) || (displayType == Number) || (displayType == CostPrice) || (displayType == Integer) || (displayType == Quantity) ) {
            return true;
        }

        return false;
    }    // isNumeric
    
    /**
     * @param displayType 
     * @return true si el tipo de dato referencia a una tabla, else cc
     */
    public static boolean isTableReference( int displayType ) {
        if( (displayType == Table) || (displayType == TableDir) || (displayType == Search) || (displayType == Location) || (displayType == Locator) || (displayType == Account) || (displayType == Assignment) || (displayType == PAttribute) ) {
            return true;
        }

        return false;
    }    // isTableReference

    /**
     * Descripción de Método
     *
     *
     * @param displayType
     *
     * @return
     */

    public static int getDefaultPrecision( int displayType ) {
        if( displayType == Amount ) {
            return 2;
        }

        if( displayType == Number ) {
            return 6;
        }

        if( (displayType == CostPrice) || (displayType == Quantity) ) {
            return 4;
        }

        return 0;
    }    // getDefaultPrecision

    /**
     * Descripción de Método
     *
     *
     * @param displayType
     *
     * @return
     */

    public static boolean isText( int displayType ) {
        if( (displayType == String) || (displayType == Text) || (displayType == TextLong) || (displayType == Memo) ) {
            return true;
        }

        return false;
    }    // isText

    /**
     * Descripción de Método
     *
     *
     * @param displayType
     *
     * @return
     */

    public static boolean isDate( int displayType ) {
        if( (displayType == Date) || (displayType == DateTime) || (displayType == Time) ) {
            return true;
        }

        return false;
    }    // isDate

    /**
     * Descripción de Método
     *
     *
     * @param displayType
     *
     * @return
     */

    public static boolean isLookup( int displayType ) {
        if( (displayType == List) || (displayType == Table) || (displayType == TableDir) || (displayType == Search) ) {
            return true;
        }

        return false;
    }    // isLookup

    /**
     * Descripción de Método
     *
     *
     * @param displayType
     *
     * @return
     */

    public static boolean isLOB( int displayType ) {
        if( (displayType == Binary) || (displayType == TextLong) || (displayType == Image) ) {
            return true;
        }

        return false;
    }

    /**
     * Descripción de Método
     *
     *
     * @param displayType
     * @param language
     *
     * @return
     */

    public static DecimalFormat getNumberFormat( int displayType,Language language ) {
        Language myLanguage = language;

        if( myLanguage == null ) {
            myLanguage = Language.getLoginLanguage();
        }

        Locale        locale = myLanguage.getLocale();
        DecimalFormat format = null;

        if( locale != null ) {
            format = ( DecimalFormat )NumberFormat.getNumberInstance( locale );
        } else {
            format = ( DecimalFormat )NumberFormat.getNumberInstance( Locale.US );
        }

        //

        if( displayType == Integer ) {
            format.setParseIntegerOnly( true );
            format.setMaximumIntegerDigits( INTEGER_DIGITS );
            format.setMaximumFractionDigits( 0 );
        } else if( displayType == Quantity ) {
            format.setMaximumIntegerDigits( MAX_DIGITS );
            format.setMaximumFractionDigits( MAX_FRACTION );
        } else if( displayType == Amount ) {
            format.setMaximumIntegerDigits( MAX_DIGITS );
            format.setMaximumFractionDigits( AMOUNT_FRACTION );
            format.setMinimumFractionDigits( AMOUNT_FRACTION );
        } else if( displayType == CostPrice ) {
            format.setMaximumIntegerDigits( MAX_DIGITS );
            format.setMaximumFractionDigits( MAX_FRACTION );
            format.setMinimumFractionDigits( AMOUNT_FRACTION );
        } else    // if (displayType == Number)
        {
            format.setMaximumIntegerDigits( MAX_DIGITS );
            format.setMaximumFractionDigits( MAX_FRACTION );
            format.setMinimumFractionDigits( 1 );
        }

        return format;
    }    // getDecimalFormat

    /**
     * Descripción de Método
     *
     *
     * @param displayType
     *
     * @return
     */

    public static DecimalFormat getNumberFormat( int displayType ) {
        return getNumberFormat( displayType,null );
    }    // getNumberFormat

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public static SimpleDateFormat getDateFormat() {
        return getDateFormat( DisplayType.Date,null );
    }    // getDateFormat

    /**
     * Descripción de Método
     *
     *
     * @param language
     *
     * @return
     */

    public static SimpleDateFormat getDateFormat( Language language ) {
        return getDateFormat( DisplayType.Date,language );
    }    // getDateFormat

    /**
     * Descripción de Método
     *
     *
     * @param displayType
     *
     * @return
     */

    public static SimpleDateFormat getDateFormat( int displayType ) {
        return getDateFormat( displayType,null );
    }    // getDateFormat

    /**
     * Descripción de Método
     *
     *
     * @param displayType
     * @param language
     *
     * @return
     */

    public static SimpleDateFormat getDateFormat( int displayType,Language language ) {
        Language myLanguage = language;

        if( myLanguage == null ) {
            myLanguage = Language.getLoginLanguage();
        }

        //

        if( displayType == DateTime ) {
            return myLanguage.getDateTimeFormat();
        } else if( displayType == Time ) {
            return myLanguage.getTimeFormat();
        }

        // else if (displayType == Date)

        return myLanguage.getDateFormat();    // default
    }                                         // getDateFormat

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    static public SimpleDateFormat getDateFormat_JDBC() {
        return new SimpleDateFormat( "yyyy-MM-dd" );
    }    // getDateFormat_JDBC

    /**
     * Descripción de Método
     *
     *
     * @param displayType
     * @param yesNoAsBoolean
     *
     * @return
     */

    public static Class getClass( int displayType,boolean yesNoAsBoolean ) {
        if( (displayType == String) || (displayType == Text) || (displayType == TextLong) || (displayType == List) || (displayType == Memo) ) {
            return String.class;
        } else if( isID( displayType ) || (displayType == Integer) ) {    // note that Integer is stored as BD
            return Integer.class;
        } else if( isNumeric( displayType )) {
            return java.math.BigDecimal.class;
        } else if( isDate( displayType )) {
            return java.sql.Timestamp.class;
        } else if( displayType == YesNo ) {
            return yesNoAsBoolean
                   ?Boolean.class
                   :String.class;
        } else if( displayType == Button ) {
            return String.class;
        } else if( isLOB( displayType )) {    // CLOB is String
            return byte[].class;
        }

        //

        return Object.class;
    }    // getClass
    
    

    /**
     * Determina que tipos requieren el uso de comillas para contener el dato
     * @param displayType 
     * @return true si requiere comillas, false si no require o null en caso de no encontrar el tipo en los sets
     */
    public static Boolean requiresQuotes(int displayType)
    {
    	if (withQuotes.contains(displayType))
    		return true;
    	if (withoutQuotes.contains(displayType))
    		return false;
    	return null;
    }
    
    private static HashSet<Integer> withQuotes = new HashSet<Integer>();
    private static Set<Integer> withoutQuotes = new HashSet<Integer>();    
    static
    {
    	/* Tipos que requieren comillas */
    	withQuotes.add(String);
    	withQuotes.add(Text);
    	withQuotes.add(Date);
    	withQuotes.add(DateTime);
    	withQuotes.add(List);
    	withQuotes.add(YesNo);
    	withQuotes.add(Binary);
    	withQuotes.add(Time);
    	withQuotes.add(Color);
    	withQuotes.add(Button);
    	withQuotes.add(Image);
    	withQuotes.add(Memo);
    	withQuotes.add(TextLong);
    	
    	/* Tipos que no requieren comillas */
    	withoutQuotes.add(Integer);
    	withoutQuotes.add(Amount);
    	withoutQuotes.add(ID);
    	withoutQuotes.add(Table);
    	withoutQuotes.add(TableDir);
    	withoutQuotes.add(Location);
    	withoutQuotes.add(Number);
    	withoutQuotes.add(Account);
    	withoutQuotes.add(RowID);
    	withoutQuotes.add(Quantity);
    	withoutQuotes.add(Search);
    	withoutQuotes.add(Locator);
    	withoutQuotes.add(Assignment);
    	withoutQuotes.add(PAttribute);
    	withoutQuotes.add(CostPrice);
    }

    /**
	 * 	Get Description
	 *	@param displayType display Type
	 *	@return display type description
	 */
	public static String getDescription (int displayType)
	{
		if (displayType == String)
			return "String";
		if (displayType == Integer)
			return "Integer";
		if (displayType == Amount)
			return "Amount";
		if (displayType == ID)
			return "ID";
		if (displayType == Text)
			return "Text";
		if (displayType == Date)
			return "Date";
		if (displayType == DateTime)
			return "DateTime";
		if (displayType == List)
			return "List";
		if (displayType == Table)
			return "Table";
		if (displayType == TableDir)
			return "TableDir";
		if (displayType == YesNo)
			return "YesNo";
		if (displayType == Location)
			return "Location";
		if (displayType == Number)
			return "Number";
		if (displayType == Binary)
			return "Binary";
		if (displayType == Time)
			return "Time";
		if (displayType == Account)
			return "Account";
		if (displayType == RowID)
			return "RowID";
		if (displayType == Color)
			return "Color";
		if (displayType == Button)
			return "Button";
		if (displayType == Quantity)
			return "Quantity";
		if (displayType == Search)
			return "Search";
		if (displayType == Locator)
			return "Locator";
		if (displayType == Image)
			return "Image";
		if (displayType == Assignment)
			return "Assignment";
		if (displayType == Memo)
			return "Memo";
		if (displayType == PAttribute)
			return "PAttribute";
		if (displayType == TextLong)
			return "TextLong";
		if (displayType == CostPrice)
			return "CostPrice";
		if (displayType == FilePath)
			return "FilePath";
		if (displayType == FileName)
			return "FileName";
		if (displayType == URL)
			return "URL";
		if (displayType == PrinterName)
			return "PrinterName";
		//
		return "UNKNOWN DisplayType=" + displayType;
	}	//	getDescription

	/**
	 *	JDBC Timestamp Format yyyy-mm-dd hh:mm:ss
	 *  @return timestamp format
	 */
	static public SimpleDateFormat getTimestampFormat_Default()
	{
		return new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");
	}   //  getTimestampFormat_JDBC

    
}    // DisplayType



/*
 *  @(#)DisplayType.java   25.03.06
 * 
 *  Fin del fichero DisplayType.java
 *  
 *  Versión 2.2
 *
 */
