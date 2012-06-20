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

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.Comparator;
import java.util.Properties;

import org.openXpertya.util.Env;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MRfQResponseLineQty extends X_C_RfQResponseLineQty implements Comparator {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param C_RfQResponseLineQty_ID
     * @param trxName
     */

    public MRfQResponseLineQty( Properties ctx,int C_RfQResponseLineQty_ID,String trxName ) {
        super( ctx,C_RfQResponseLineQty_ID,trxName );

        if( C_RfQResponseLineQty_ID == 0 ) {

            // setC_RfQResponseLineQty_ID (0);         //      PK
            // setC_RfQLineQty_ID (0);
            // setC_RfQResponseLine_ID (0);
            //

            setPrice( Env.ZERO );
            setDiscount( Env.ZERO );
        }
    }    // MRfQResponseLineQty

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MRfQResponseLineQty( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MRfQResponseLineQty

    /**
     * Constructor de la clase ...
     *
     *
     * @param line
     * @param qty
     */

    public MRfQResponseLineQty( MRfQResponseLine line,MRfQLineQty qty ) {
        this( line.getCtx(),0,line.get_TrxName());
        setClientOrg( line );
        setC_RfQResponseLine_ID( line.getC_RfQResponseLine_ID());
        setC_RfQLineQty_ID( qty.getC_RfQLineQty_ID());
    }    // MRfQResponseLineQty

    /** Descripción de Campos */

    private MRfQLineQty m_rfqQty = null;

    /** Descripción de Campos */

    private static BigDecimal ONEHUNDRED = new BigDecimal( 100 );

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MRfQLineQty getRfQLineQty() {
        if( m_rfqQty == null ) {
            m_rfqQty = MRfQLineQty.get( getCtx(),getC_RfQLineQty_ID(),get_TrxName());
        }

        return m_rfqQty;
    }    // getRfQLineQty

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isValidAmt() {
        BigDecimal price = getPrice();

        if( (price == null) || (Env.ZERO.compareTo( price ) == 0) ) {
            log.warning( "No Price - " + price );

            return false;
        }

        BigDecimal discount = getDiscount();

        if( discount != null ) {
            if( discount.abs().compareTo( ONEHUNDRED ) > 0 ) {
                log.warning( "Discount > 100 - " + discount );

                return false;
            }
        }

        BigDecimal net = getNetAmt();

        if( net == null ) {
            log.warning( "Net is null" );

            return false;
        }

        if( net.compareTo( Env.ZERO ) <= 0 ) {
            log.warning( "Net <= 0 - " + net );

            return false;
        }

        return true;
    }    // isValidAmt

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public BigDecimal getNetAmt() {
        BigDecimal price = getPrice();

        if( (price == null) || (Env.ZERO.compareTo( price ) == 0) ) {
            return null;
        }

        //

        BigDecimal discount = getDiscount();

        if( (discount == null) || (Env.ZERO.compareTo( discount ) == 0) ) {
            return price;
        }

        // Calculate
        // double result = price.doubleValue() * (100.0 - discount.doubleValue()) / 100.0;

        BigDecimal factor = ONEHUNDRED.subtract( discount );

        return price.multiply( factor ).divide( ONEHUNDRED,2,BigDecimal.ROUND_HALF_UP );
    }    // getNetAmt

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer( "MRfQResponseLineQty[" );

        sb.append( getID()).append( ",Rank=" ).append( getRanking()).append( ",Price=" ).append( getPrice()).append( ",Discount=" ).append( getDiscount()).append( ",Net=" ).append( getNetAmt()).append( "]" );

        return sb.toString();
    }    // toString

    /**
     * Descripción de Método
     *
     *
     * @param o1
     * @param o2
     *
     * @return
     */

    public int compare( Object o1,Object o2 ) {
        if( o1 == null ) {
            throw new IllegalArgumentException( "o1 = null" );
        }

        if( o2 == null ) {
            throw new IllegalArgumentException( "o2 = null" );
        }

        MRfQResponseLineQty q1 = null;
        MRfQResponseLineQty q2 = null;

        if( o1 instanceof MRfQResponseLineQty ) {
            q1 = ( MRfQResponseLineQty )o1;
        } else {
            throw new ClassCastException( "o1" );
        }

        if( o2 instanceof MRfQResponseLineQty ) {
            q2 = ( MRfQResponseLineQty )o2;
        } else {
            throw new ClassCastException( "o2" );
        }

        //

        if( !q1.isValidAmt()) {
            return -99;
        }

        if( !q2.isValidAmt()) {
            return +99;
        }

        BigDecimal net1 = q1.getNetAmt();

        if( net1 == null ) {
            return -9;
        }

        BigDecimal net2 = q2.getNetAmt();

        if( net2 == null ) {
            return +9;
        }

        return net1.compareTo( net2 );
    }    // compare

    /**
     * Descripción de Método
     *
     *
     * @param obj
     *
     * @return
     */

    public boolean equals( Object obj ) {
        if( obj instanceof MRfQResponseLineQty ) {
            MRfQResponseLineQty cmp = ( MRfQResponseLineQty )obj;

            if( !cmp.isValidAmt() ||!isValidAmt()) {
                return false;
            }

            BigDecimal cmpNet = cmp.getNetAmt();

            if( cmpNet == null ) {
                return false;
            }

            BigDecimal net = cmp.getNetAmt();

            if( net == null ) {
                return false;
            }

            return cmpNet.compareTo( net ) == 0;
        }

        return false;
    }    // equals

    /**
     * Descripción de Método
     *
     *
     * @param newRecord
     *
     * @return
     */

    protected boolean beforeSave( boolean newRecord ) {
        if( !isActive()) {
            setRanking( 999 );
        }

        return true;
    }    // beforeSave
}    // MRfQResponseLineQty



/*
 *  @(#)MRfQResponseLineQty.java   02.07.07
 * 
 *  Fin del fichero MRfQResponseLineQty.java
 *  
 *  Versión 2.2
 *
 */
