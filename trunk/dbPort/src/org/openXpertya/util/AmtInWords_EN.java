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

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class AmtInWords_EN implements AmtInWords {

    /**
     * Constructor de la clase ...
     *
     */

    public AmtInWords_EN() {
        super();
    }    // AmtInWords_EN

    /** Descripción de Campos */

    private static final String[] majorNames = {
        "","Thousand-","Million-","Billion-","Trillion-","Quadrillion-","Quintillion-"
    };

    /** Descripción de Campos */

    private static final String[] tensNames = {
        "","Ten","Twenty","Thirty","Fourty","Fifty","Sixty","Seventy","Eighty","Ninety"
    };

    /** Descripción de Campos */

    private static final String[] numNames = {
        "","One","Two","Three","Four","Five","Six","Seven","Eight","Nine","Ten","Eleven","Twelve","Thirteen","Fourteen","Fifteen","Sixteen","Seventeen","Eighteen","Nineteen"
    };

    /**
     * Descripción de Método
     *
     *
     * @param number
     *
     * @return
     */

    private String convertLessThanOneThousand( int number ) {
        String soFar;

        // Esta dentro de los 1os. diecinueve?? ISCAP

        if( number % 100 < 20 ) {
            soFar  = numNames[ number % 100 ];
            number /= 100;
        } else {
            soFar  = numNames[ number % 10 ];
            number /= 10;
            soFar  = tensNames[ number % 10 ] + soFar;
            number /= 10;
        }

        if( number == 0 ) {
            return soFar;
        }

        return numNames[ number ] + "Hundred-" + soFar;
    }    // convertLessThanOneThousand

    /**
     * Descripción de Método
     *
     *
     * @param number
     *
     * @return
     */

    private String convert( int number ) {

        /* special case */

        if( number == 0 ) {
            return "Zero";
        }

        String prefix = "";

        if( number < 0 ) {
            number = -number;
            prefix = "Negative ";
        }

        String soFar = "";
        int    place = 0;

        do {
            int n = number % 1000;

            if( n != 0 ) {
                String s = convertLessThanOneThousand( n );

                soFar = s + majorNames[ place ] + soFar;
            }

            place++;
            number /= 1000;
        } while( number > 0 );

        return( prefix + soFar ).trim();
    }    // convert

    /**
     * Descripción de Método
     *
     *
     * @param amount
     *
     * @return
     *
     * @throws Exception
     */

    public String getAmtInWords( String amount ) throws Exception {
        if( amount == null ) {
            return amount;
        }

        //

        StringBuffer sb   = new StringBuffer();
        int          pos  = amount.lastIndexOf( '.' );
        int          pos2 = amount.lastIndexOf( ',' );

        if( pos2 > pos ) {
            pos = pos2;
        }

        String oldamt = amount;

        amount = amount.replaceAll( ",","" );

        int newpos = amount.lastIndexOf( '.' );
        int pesos  = Integer.parseInt( amount.substring( 0,newpos ));

        sb.append( convert( pesos ));

        for( int i = 0;i < oldamt.length();i++ ) {
            if( pos == i )    // we are done
            {
                String cents = oldamt.substring( i + 1 );

                sb.append( ' ' ).append( cents ).append( "/100" );

                break;
            }
        }

        return sb.toString();
    }    // getAmtInWords

    /**
     * Descripción de Método
     *
     *
     * @param amt
     */

    private void print( String amt ) {
        try {
            System.out.println( amt + " = " + getAmtInWords( amt ));
        } catch( Exception e ) {
            e.printStackTrace();
        }
    }    // print

    /**
     * Descripción de Método
     *
     *
     * @param args
     */

    public static void main( String[] args ) {
        AmtInWords_EN aiw = new AmtInWords_EN();

        // aiw.print (".23");      Error

        aiw.print( "0.23" );
        aiw.print( "1.23" );
        aiw.print( "12.345" );
        aiw.print( "123.45" );
        aiw.print( "1234.56" );
        aiw.print( "12345.78" );
        aiw.print( "123457.89" );
        aiw.print( "1,234,578.90" );
    }    // main
}    // AmtInWords_EN



/*
 *  @(#)AmtInWords_EN.java   25.03.06
 * 
 *  Fin del fichero AmtInWords_EN.java
 *  
 *  Versión 2.2
 *
 */
