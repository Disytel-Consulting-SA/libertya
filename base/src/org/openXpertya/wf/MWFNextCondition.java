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



package org.openXpertya.wf;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.Properties;

import org.openXpertya.model.PO;
import org.openXpertya.model.X_AD_WF_NextCondition;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MWFNextCondition extends X_AD_WF_NextCondition {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param id
     * @param trxName
     */

    public MWFNextCondition( Properties ctx,int id,String trxName ) {
        super( ctx,id,trxName );
    }    // MWFNextCondition

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MWFNextCondition( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MWFNextCondition

    /** Descripción de Campos */

    private boolean m_numeric = true;

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isOr() {
        return ANDOR_Or.equals( getAndOr());
    }    // isOr

    /**
     * Descripción de Método
     *
     *
     * @param activity
     *
     * @return
     */

    public boolean evaluate( MWFActivity activity ) {
        if( getAD_Column_ID() == 0 ) {
            throw new IllegalStateException( "No Column defined - " + this );
        }

        PO po = activity.getPO();

        if( (po == null) || (po.getID() == 0) ) {
            throw new IllegalStateException( "Could not evaluate " + po + " - " + this );
        }

        //

        Object valueObj = po.get_ValueOfColumn( getAD_Column_ID());

        if( valueObj == null ) {
            valueObj = "";
        }

        String value1 = getValue();

        if( value1 == null ) {
            value1 = "";
        }

        String value2 = getValue2();

        if( value2 == null ) {
            value2 = "";
        }

        String resultStr = "{" + valueObj + "}" + getOperation() + "{" + value1 + "}";

        if( getOperation().equals( OPERATION_Sql )) {
            throw new IllegalArgumentException( "SQL Operator not implemented yet: " + resultStr );
        }

        if( getOperation().equals( OPERATION_X )) {
            resultStr += "{" + value2 + "}";
        }

        boolean result = false;

        if( valueObj instanceof Number ) {
            result = compareNumber(( Number )valueObj,value1,value2 );
        } else {
            result = compareString( valueObj,value1,value2 );
        }

        //

        log.fine( "evaluate " + resultStr + " -> " + result + ( m_numeric
                ?" (#)"
                :" ($)" ));

        return result;
    }    // evaluate

    /**
     * Descripción de Método
     *
     *
     * @param valueObj
     * @param value1
     * @param value2
     *
     * @return
     */

    private boolean compareNumber( Number valueObj,String value1,String value2 ) {
        BigDecimal valueObjB = null;
        BigDecimal value1B   = null;
        BigDecimal value2B   = null;

        try {
            if( valueObj instanceof BigDecimal ) {
                valueObjB = ( BigDecimal )valueObj;
            } else if( valueObj instanceof Integer ) {
                valueObjB = new BigDecimal((( Integer )valueObj ).intValue());
            } else {
                valueObjB = new BigDecimal( String.valueOf( valueObj ));
            }
        } catch( Exception e ) {
            log.fine( "compareNumber - valueObj=" + valueObj + " - " + e.toString());

            return compareString( valueObj,value1,value2 );
        }

        try {
            value1B = new BigDecimal( value1 );
        } catch( Exception e ) {
            log.fine( "compareNumber - value1=" + value1 + " - " + e.toString());

            return compareString( valueObj,value1,value2 );
        }

        String op = getOperation();

        if( OPERATION_Eq.equals( op )) {
            return valueObjB.compareTo( value1B ) == 0;
        } else if( OPERATION_Gt.equals( op )) {
            return valueObjB.compareTo( value1B ) > 0;
        } else if( OPERATION_GtEq.equals( op )) {
            return valueObjB.compareTo( value1B ) >= 0;
        } else if( OPERATION_Le.equals( op )) {
            return valueObjB.compareTo( value1B ) < 0;
        } else if( OPERATION_LeEq.equals( op )) {
            return valueObjB.compareTo( value1B ) <= 0;
        } else if( OPERATION_Like.equals( op )) {
            return valueObjB.compareTo( value1B ) == 0;
        } else if( OPERATION_NotEq.equals( op )) {
            return valueObjB.compareTo( value1B ) != 0;

            //

        } else if( OPERATION_Sql.equals( op )) {
            throw new IllegalArgumentException( "SQL not Implemented" );

            //

        } else if( OPERATION_X.equals( op )) {
            if( valueObjB.compareTo( value1B ) < 0 ) {
                return false;
            }

            // To

            try {
                value2B = new BigDecimal( String.valueOf( value2 ));

                return valueObjB.compareTo( value2B ) <= 0;
            } catch( Exception e ) {
                log.fine( "compareNumber - value2=" + value2 + " - " + e.toString());

                return false;
            }
        }

        //

        throw new IllegalArgumentException( "Unknown Operation=" + op );
    }    // compareNumber

    /**
     * Descripción de Método
     *
     *
     * @param valueObj
     * @param value1S
     * @param value2S
     *
     * @return
     */

    private boolean compareString( Object valueObj,String value1S,String value2S ) {
        m_numeric = false;

        String valueObjS = String.valueOf( valueObj );

        //

        String op = getOperation();

        if( OPERATION_Eq.equals( op )) {
            return valueObjS.compareTo( value1S ) == 0;
        } else if( OPERATION_Gt.equals( op )) {
            return valueObjS.compareTo( value1S ) > 0;
        } else if( OPERATION_GtEq.equals( op )) {
            return valueObjS.compareTo( value1S ) >= 0;
        } else if( OPERATION_Le.equals( op )) {
            return valueObjS.compareTo( value1S ) < 0;
        } else if( OPERATION_LeEq.equals( op )) {
            return valueObjS.compareTo( value1S ) <= 0;
        } else if( OPERATION_Like.equals( op )) {
            return valueObjS.compareTo( value1S ) == 0;
        } else if( OPERATION_NotEq.equals( op )) {
            return valueObjS.compareTo( value1S ) != 0;

            //

        } else if( OPERATION_Sql.equals( op )) {
            throw new IllegalArgumentException( "SQL not Implemented" );

            //

        } else if( OPERATION_X.equals( op )) {
            if( valueObjS.compareTo( value1S ) < 0 ) {
                return false;
            }

            // To

            return valueObjS.compareTo( value2S ) <= 0;
        }

        //

        throw new IllegalArgumentException( "Unknown Operation=" + op );
    }    // compareString

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer( "MWFNextCondition[" );

        sb.append( getID()).append( ",SeqNo=" ).append( getSeqNo()).append( "]" );

        return sb.toString();
    }    // toString
}    // MWFNextCondition



/*
 *  @(#)MWFNextCondition.java   02.07.07
 * 
 *  Fin del fichero MWFNextCondition.java
 *  
 *  Versión 2.2
 *
 */
