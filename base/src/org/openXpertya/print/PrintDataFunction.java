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

import org.openXpertya.util.DisplayType;
import org.openXpertya.util.Env;

/**
 * Descripción de Clase
 *
 *
 * @versión    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class PrintDataFunction {

    /**
     * Constructor de la clase ...
     *
     */

    public PrintDataFunction() {}    // PrintDataFunction

    /** Descripción de Campos */

    private BigDecimal m_sum = Env.ZERO;

    /** Descripción de Campos */

    private int m_count = 0;

    /** Descripción de Campos */

    private int m_totalCount = 0;

    /** Descripción de Campos */

    private BigDecimal m_min = null;

    /** Descripción de Campos */

    private BigDecimal m_max = null;

    /** Descripción de Campos */

    private BigDecimal m_sumSquare = Env.ZERO;

    /** Descripción de Campos */

    static public final char F_SUM = 'S';

    /** Descripción de Campos */

    static public final char F_MEAN = 'A';    // Average mu

    /** Descripción de Campos */

    static public final char F_COUNT = 'C';

    /** Descripción de Campos */

    static public final char F_MIN = 'm';

    /** Descripción de Campos */

    static public final char F_MAX = 'M';

    /** Descripción de Campos */

    static public final char F_VARIANCE = 'V';    // sigma square

    /** Descripción de Campos */

    static public final char F_DEVIATION = 'D';    // sigma

    /** Descripción de Campos */

    static private final char[] FUNCTIONS = new char[] {
        F_SUM,F_MEAN,F_COUNT,F_MIN,F_MAX,F_VARIANCE,F_DEVIATION
    };

    /** Descripción de Campos */

    static private final String[] FUNCTION_SYMBOLS = new String[] {
        " \u03A3"," \u03BC"," \u2116"," \u2193"," \u2191"," \u03C3\u00B2"," \u03C3"
    };

    /** Descripción de Campos */

    static private final String[] FUNCTION_NAMES = new String[] {
        "Sum","Mean","Count","Min","Max","Variance","Deviation"
    };

    /**
     * Descripción de Método
     *
     *
     * @param bd
     */

    public void addValue( BigDecimal bd ) {
        if( bd != null ) {

            // Sum

            m_sum = m_sum.add( bd );

            // Count

            m_count++;

            // Min

            if( m_min == null ) {
                m_min = bd;
            }

            m_min = m_min.min( bd );

            // Max

            if( m_max == null ) {
                m_max = bd;
            }

            m_max = m_max.max( bd );

            // Sum of Squares

            m_sumSquare = m_sumSquare.add( bd.multiply( bd ));
        }

        m_totalCount++;
    }    // addValue

    /**
     * Descripción de Método
     *
     *
     * @param function
     *
     * @return
     */

    public BigDecimal getValue( char function ) {

        // Sum

        if( function == F_SUM ) {
            return m_sum;
        }

        // Min/Max

        if( function == F_MIN ) {
            return m_min;
        }

        if( function == F_MAX ) {
            return m_max;
        }

        // Count

        BigDecimal count = new BigDecimal( m_count );

        if( function == F_COUNT ) {
            return count;
        }

        // All other functions require count > 0

        if( m_count == 0 ) {
            return Env.ZERO;
        }

        // Mean = sum/count - round to 4 digits

        if( function == F_MEAN ) {
            BigDecimal mean = m_sum.divide( count,BigDecimal.ROUND_HALF_UP );

            if( mean.scale() > 4 ) {
                mean = mean.setScale( 4,BigDecimal.ROUND_HALF_UP );
            }

            return mean;
        }

        // Variance = sum of squares - (square of sum / count)

        BigDecimal ss = m_sum.multiply( m_sum );

        ss = ss.divide( count,BigDecimal.ROUND_HALF_UP );

        BigDecimal variance = m_sumSquare.subtract( ss );

        if( function == F_VARIANCE ) {
            if( variance.scale() > 4 ) {
                variance = variance.setScale( 4,BigDecimal.ROUND_HALF_UP );
            }

            return variance;
        }

        // Standard Deviation

        BigDecimal deviation = new BigDecimal( Math.sqrt( variance.doubleValue()));

        if( deviation.scale() > 4 ) {
            deviation = deviation.setScale( 4,BigDecimal.ROUND_HALF_UP );
        }

        return deviation;
    }    // getValue

    /**
     * Descripción de Método
     *
     */

    public void reset() {
        m_count      = 0;
        m_totalCount = 0;
        m_sum        = Env.ZERO;
        m_sumSquare  = Env.ZERO;
        m_min        = null;
        m_max        = null;
    }    // reset

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer( "[" ).append( "Count=" ).append( m_count ).append( "," ).append( m_totalCount ).append( ",Sum=" ).append( m_sum ).append( ",SumSquare=" ).append( m_sumSquare ).append( ",Min=" ).append( m_min ).append( ",Max=" ).append( m_max );

        sb.append( "]" );

        return sb.toString();
    }    // toString

    /**
     * Descripción de Método
     *
     *
     * @param function
     *
     * @return
     */

    static public String getFunctionSymbol( char function ) {
        for( int i = 0;i < FUNCTIONS.length;i++ ) {
            if( FUNCTIONS[ i ] == function ) {
                return FUNCTION_SYMBOLS[ i ];
            }
        }

        return "UnknownFunction=" + function;
    }    // getFunctionSymbol

    /**
     * Descripción de Método
     *
     *
     * @param function
     *
     * @return
     */

    static public String getFunctionName( char function ) {
        for( int i = 0;i < FUNCTIONS.length;i++ ) {
            if( FUNCTIONS[ i ] == function ) {
                return FUNCTION_NAMES[ i ];
            }
        }

        return "UnknownFunction=" + function;
    }    // getFunctionName

    /**
     * Descripción de Método
     *
     *
     * @param function
     *
     * @return
     */

    static public int getFunctionDisplayType( char function ) {
        if( (function == F_SUM) || (function == F_MIN) || (function == F_MAX) ) {
            return DisplayType.Amount;
        }

        if( function == F_COUNT ) {
            return DisplayType.Integer;
        }

        // Mean, Variance, Std. Deviation

        return DisplayType.Number;
    }    // getFunctionName
}    // PrintDataFunction



/*
 *  @(#)PrintDataFunction.java   23.03.06
 * 
 *  Fin del fichero PrintDataFunction.java
 *  
 *  Versión 2.2
 *
 */
