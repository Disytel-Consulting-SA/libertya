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

public class AmtInWords_FR implements AmtInWords {

    /**
     * Constructor de la clase ...
     *
     */

    public AmtInWords_FR() {
        super();
    }    // AmtInWords_FR

    /** Descripción de Campos */

    private static final String[] majorNames = {
        ""," mille"," million"," milliard"," trillion"," quadrillion"," quintillion"
    };

    /** Descripción de Campos */

    private static final String[] tensNames = {
        ""," dix"," vingt"," trente"," quarante"," cinquante"," soixante"," soixante-dix"," quatre-vingt"," quatre-vingt-dix"
    };

    /** Descripción de Campos */

    private static final String[] numNames = {
        ""," un"," deux"," trois"," quatre"," cinq"," six"," sept"," huit"," neuf"," dix"," onze"," douze"," treize"," quatorze"," quinze"," seize"," dix-sept"," dix-huit"," dix-neuf"
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

        if( number % 100 < 20 ) {

            // 19 et moins

            soFar  = numNames[ number % 100 ];
            number /= 100;
        } else {

            // 9 et moins

            soFar  = numNames[ number % 10 ];
            number /= 10;

            // 90, 80, ... 20

            soFar  = tensNames[ number % 10 ] + soFar;
            number /= 10;
        }

        // reste les centaines
        // y'en a pas

        if( number == 0 ) {
            return soFar;
        }

        if( number == 1 ) {

            // on ne retourne "un cent xxxx" mais "cent xxxx"

            return " cent" + soFar;
        } else {
            return numNames[ number ] + " cent" + soFar;
        }
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
        if( number == 0 ) {
            return "zero";
        }

        String prefix = "";

        if( number < 0 ) {
            number = -number;
            prefix = "moins";
        }

        String  soFar          = "";
        int     place          = 0;
        boolean pluralPossible = true;
        boolean pluralForm     = false;

        do {
            int n = number % 1000;

            // par tranche de 1000

            if( n != 0 ) {
                String s = convertLessThanOneThousand( n );

                if( s.trim().equals( "un" ) && (place == 1) ) {

                    // on donne pas le un pour mille

                    soFar = majorNames[ place ] + soFar;
                } else {
                    if( place == 0 ) {
                        if( s.trim().endsWith( "cent" ) &&!s.trim().startsWith( "cent" )) {

                            // nnn200 ... nnn900 avec "s"

                            pluralForm = true;
                        } else {

                            // pas de "s" jamais

                            pluralPossible = false;
                        }
                    }

                    if( (place > 0) && pluralPossible ) {
                        if( !s.trim().startsWith( "un" )) {

                            // avec "s"

                            pluralForm = true;
                        } else {

                            // jamis de "s"

                            pluralPossible = false;
                        }
                    }

                    soFar = s + majorNames[ place ] + soFar;
                }
            }

            place++;
            number /= 1000;
        } while( number > 0 );

        String result = ( prefix + soFar ).trim();

        return( pluralForm
                ?result + "s"
                :result );
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
}    // AmtInWords_FR



/*
 *  @(#)AmtInWords_FR.java   25.03.06
 * 
 *  Fin del fichero AmtInWords_FR.java
 *  
 *  Versión 2.2
 *
 */
