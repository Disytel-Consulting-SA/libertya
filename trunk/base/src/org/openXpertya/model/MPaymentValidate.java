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

import java.util.Calendar;
import java.util.StringTokenizer;

import org.openXpertya.util.CLogger;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MPaymentValidate {

    /** Descripción de Campos */

    private static CLogger s_log = CLogger.getCLogger( MPaymentValidate.class );

    /**
     * Descripción de Método
     *
     *
     * @param mmyy
     *
     * @return
     */

    public static String validateCreditCardExp( String mmyy ) {
        String exp = checkNumeric( mmyy );

        if( exp.length() != 4 ) {
            return "CreditCardExpFormat";
        }

        //

        String mmStr = exp.substring( 0,2 );
        String yyStr = exp.substring( 2,4 );

        //

        int mm = 0;
        int yy = 0;

        try {
            mm = Integer.parseInt( mmStr );
            yy = Integer.parseInt( yyStr );
        } catch( Exception e ) {
            return "CreditCardExpFormat";
        }

        return validateCreditCardExp( mm,yy );
    }    // validateCreditCardExp

    /**
     * Descripción de Método
     *
     *
     * @param mmyy
     *
     * @return
     */

    public static int getCreditCardExpMM( String mmyy ) {
        String mmStr = mmyy.substring( 0,2 );
        int    mm    = 0;

        try {
            mm = Integer.parseInt( mmStr );
        } catch( Exception e ) {
        }

        return mm;
    }    // getCreditCardExpMM

    /**
     * Descripción de Método
     *
     *
     * @param mmyy
     *
     * @return
     */

    public static int getCreditCardExpYY( String mmyy ) {
        String yyStr = mmyy.substring( 2 );
        int    yy    = 0;

        try {
            yy = Integer.parseInt( yyStr );
        } catch( Exception e ) {
        }

        return yy;
    }    // getCreditCardExpYY

    /**
     * Descripción de Método
     *
     *
     * @param mm
     * @param yy
     *
     * @return
     */

    public static String validateCreditCardExp( int mm,int yy ) {
        if( (mm < 1) || (mm > 12) ) {
            return "CreditCardExpMonth";
        }

        // if (yy < 0 || yy > EXP_YEAR)
        // return "CreditCardExpYear";

        // Today's date

        Calendar cal   = Calendar.getInstance();
        int      year  = cal.get( Calendar.YEAR ) - 2000;    // two digits
        int      month = cal.get( Calendar.MONTH ) + 1;      // zero based

        //

        if( yy < year ) {
            return "CreditCardExpired";
        } else if( (yy == year) && (mm < month) ) {
            return "CreditCardExpired";
        }

        return "";
    }    // validateCreditCardExp

    /**
     * Descripción de Método
     *
     *
     * @param creditCardNumber
     *
     * @return
     */

    public static String validateCreditCardNumber( String creditCardNumber ) {
        if( (creditCardNumber == null) || (creditCardNumber.length() == 0) ) {
            return "CreditCardNumberError";
        }

        // Clean up number

        String ccNumber1 = checkNumeric( creditCardNumber );
        int    ccLength  = ccNumber1.length();

        // Reverse string

        StringBuffer buf = new StringBuffer();

        for( int i = ccLength;i != 0;i-- ) {
            buf.append( ccNumber1.charAt( i - 1 ));
        }

        String ccNumber = buf.toString();
        int    sum      = 0;

        for( int i = 0;i < ccLength;i++ ) {
            int digit = Character.getNumericValue( ccNumber.charAt( i ));

            if( i % 2 == 1 ) {
                digit *= 2;

                if( digit > 9 ) {
                    digit -= 9;
                }
            }

            sum += digit;
        }

        if( sum % 10 == 0 ) {
            return "";
        }

        s_log.fine( "validateCreditCardNumber - " + creditCardNumber + " -> " + ccNumber + ", Luhn=" + sum );

        return "CreditCardNumberError";
    }    // validateCreditCardNumber

    /**
     * Descripción de Método
     *
     *
     * @param creditCardNumber
     * @param creditCardType
     *
     * @return
     */

    public static String validateCreditCardNumber( String creditCardNumber,String creditCardType ) {
        if( (creditCardNumber == null) || (creditCardType == null) ) {
            return "CreditCardNumberError";
        }

        // http://www.beachnet.com/~hstiles/cardtype.html
        // http://staff.semel.fi/~kribe/document/luhn.htm

        String ccStartList  = "";    // comma separated list of starting numbers
        String ccLengthList = "";    // comma separated list of lengths

        //

        if( creditCardType.equals( MPayment.CREDITCARDTYPE_MasterCard )) {
            ccStartList  = "51,52,53,54,55";
            ccLengthList = "16";
        } else if( creditCardType.equals( MPayment.CREDITCARDTYPE_Visa )) {
            ccStartList  = "4";
            ccLengthList = "13,16";
        } else if( creditCardType.equals( MPayment.CREDITCARDTYPE_Amex )) {
            ccStartList  = "34,37";
            ccLengthList = "15";
        } else if( creditCardType.equals( MPayment.CREDITCARDTYPE_Discover )) {
            ccStartList  = "6011";
            ccLengthList = "16";
        } else if( creditCardType.equals( MPayment.CREDITCARDTYPE_Diners )) {
            ccStartList  = "300,301,302,303,304,305,36,38";
            ccLengthList = "14";
        } else {

            // enRouteCard

            ccStartList  = "2014,2149";
            ccLengthList = "15";

            // JCBCard

            ccStartList  += ",3088,3096,3112,3158,3337,3528";
            ccLengthList += ",16";

            // JCBCard

            ccStartList  += ",2131,1800";
            ccLengthList += ",15";
        }

        // Clean up number

        String          ccNumber   = checkNumeric( creditCardNumber );
        int             ccLength   = ccNumber.length();
        boolean         ccLengthOK = false;
        StringTokenizer st         = new StringTokenizer( ccLengthList,",",false );

        while( st.hasMoreTokens() &&!ccLengthOK ) {
            int l = Integer.parseInt( st.nextToken());

            if( ccLength == l ) {
                ccLengthOK = true;
            }
        }

        if( !ccLengthOK ) {
            s_log.fine( "validateCreditCardNumber Length=" + ccLength + " <> " + ccLengthList );

            return "CreditCardNumberError";
        }

        boolean ccIdentified = false;

        st = new StringTokenizer( ccStartList,",",false );

        while( st.hasMoreTokens() &&!ccIdentified ) {
            if( ccNumber.startsWith( st.nextToken())) {
                ccIdentified = true;
            }
        }

        if( !ccIdentified ) {
            s_log.fine( "validateCreditCardNumber Type=" + creditCardType + " <> " + ccStartList );
        }

        //

        String check = validateCreditCardNumber( ccNumber );

        if( check.length() != 0 ) {
            return check;
        }

        if( !ccIdentified ) {
            return "CreditCardNumberProblem?";
        }

        return "";
    }    // validateCreditCardNumber

    /**
     * Descripción de Método
     *
     *
     * @param creditCardVV
     *
     * @return
     */

    public static String validateCreditCardVV( String creditCardVV ) {
        if( creditCardVV == null ) {
            return "";
        }

        int length = checkNumeric( creditCardVV ).length();

        if( (length == 3) || (length == 4) ) {
            return "";
        }

        try {
            Integer.parseInt( creditCardVV );

            return "";
        } catch( NumberFormatException ex ) {
            s_log.fine( "validateCreditCardVV - " + ex );
        }

        s_log.fine( "validateCreditCardVV - length=" + length );

        return "CreditCardVVError";
    }    // validateCreditCardVV

    /**
     * Descripción de Método
     *
     *
     * @param creditCardVV
     * @param creditCardType
     *
     * @return
     */

    public static String validateCreditCardVV( String creditCardVV,String creditCardType ) {

        // no data

        if( (creditCardVV == null) || (creditCardVV.length() == 0) || (creditCardType == null) || (creditCardType.length() == 0) ) {
            return "";
        }

        int length = checkNumeric( creditCardVV ).length();

        // Amex = 4 digits

        if( creditCardType.equals( MPayment.CREDITCARDTYPE_Amex )) {
            if( length == 4 ) {
                try {
                    Integer.parseInt( creditCardVV );

                    return "";
                } catch( NumberFormatException ex ) {
                    s_log.fine( "validateCreditCardVV - " + ex );
                }
            }

            s_log.fine( "validateCreditCardVV(4) CC=" + creditCardType + ", length=" + length );

            return "CreditCardVVError";
        }

        // Visa & MasterCard - 3 digits

        if( creditCardType.equals( MPayment.CREDITCARDTYPE_Visa ) || creditCardType.equals( MPayment.CREDITCARDTYPE_MasterCard )) {
            if( length == 3 ) {
                try {
                    Integer.parseInt( creditCardVV );

                    return "";
                } catch( NumberFormatException ex ) {
                    s_log.fine( "validateCreditCardVV - " + ex );
                }
            }

            s_log.fine( "validateCreditCardVV(3) CC=" + creditCardType + ", length=" + length );

            return "CreditCardVVError";
        }

        // Other

        return "";
    }    // validateCreditCardVV

    /**
     * Descripción de Método
     *
     *
     * @param routingNo
     *
     * @return
     */

    public static String validateRoutingNo( String routingNo ) {
        int length = checkNumeric( routingNo ).length();

        // US - length 9
        // Germany - length 8

        if( (length == 8) || (length == 9) ) {
            return "";
        }

        return "PaymentBankRoutingNotValid";
    }    // validateBankRoutingNo

    /**
     * Descripción de Método
     *
     *
     * @param AccountNo
     *
     * @return
     */

    public static String validateAccountNo( String AccountNo ) {
        int length = checkNumeric( AccountNo ).length();

        if( length > 0 ) {
            return "";
        }

        return "PaymentBankAccountNotValid";
    }    // validateBankAccountNo

    /**
     * Descripción de Método
     *
     *
     * @param CheckNo
     *
     * @return
     */

    public static String validateCheckNo( String CheckNo ) {
        int length = checkNumeric( CheckNo ).length();

        if( length > 0 ) {
            return "";
        }

        return "PaymentBankCheckNotValid";
    }    // validateBankCheckNo

    /**
     * Descripción de Método
     *
     *
     * @param data
     *
     * @return
     */

    public static String checkNumeric( String data ) {
        if( (data == null) || (data.length() == 0) ) {
            return "";
        }

        // Remove all non Digits

        StringBuffer sb = new StringBuffer();

        for( int i = 0;i < data.length();i++ ) {
            if( Character.isDigit( data.charAt( i ))) {
                sb.append( data.charAt( i ));
            }
        }

        return sb.toString();
    }    // checkNumeric
}    // MPaymentValidate



/*
 *  @(#)MPaymentValidate.java   02.07.07
 * 
 *  Fin del fichero MPaymentValidate.java
 *  
 *  Versión 2.2
 *
 */
