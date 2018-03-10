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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MOrderTax extends X_C_OrderTax {

    /**
     * Descripción de Método
     *
     *
     * @param line
     * @param precision
     * @param oldTax
     * @param trxName
     *
     * @return
     */

    public static MOrderTax get( MOrderLine line,int precision,boolean oldTax,String trxName ) {
        MOrderTax retValue = null;

        if( (line == null) || (line.getC_Order_ID() == 0) ) {
            s_log.fine( "get - No Order" );

            return null;
        }

        int C_Tax_ID = line.getC_Tax_ID();

        if( oldTax && line.is_ValueChanged( "C_Tax_ID" )) {
            Object old = line.get_ValueOld( "C_Tax_ID" );

            if( old == null ) {
                s_log.fine( "get - No Old Tax" );

                return null;
            }

            C_Tax_ID = (( Integer )old ).intValue();
        }

        if( C_Tax_ID == 0 ) {
            s_log.fine( "get - No Tax" );

            return null;
        }

        String sql = "SELECT * FROM C_OrderTax WHERE C_Order_ID=? AND C_Tax_ID=?";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql,trxName );
            pstmt.setInt( 1,line.getC_Order_ID());
            pstmt.setInt( 2,C_Tax_ID );

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                retValue = new MOrderTax( line.getCtx(),rs,trxName );
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            s_log.log( Level.SEVERE,"get",e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        if( retValue != null ) {
            retValue.setPrecision( precision );
            retValue.set_TrxName( trxName );
            s_log.fine( "get (old=" + oldTax + ") " + retValue );

            return retValue;
        }

        // Create New

        retValue = new MOrderTax( line.getCtx(),0,trxName );
        retValue.set_TrxName( trxName );
        retValue.setClientOrg( line );
        retValue.setC_Order_ID( line.getC_Order_ID());
        retValue.setC_Tax_ID( line.getC_Tax_ID());
        retValue.setPrecision( precision );
        retValue.setIsTaxIncluded( line.isTaxIncluded());
        s_log.fine( "get (new) " + retValue );

        return retValue;
    }    // get

    /** Descripción de Campos */

    private static CLogger s_log = CLogger.getCLogger( MOrderTax.class );

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param ignored
     * @param trxName
     */

    public MOrderTax( Properties ctx,int ignored,String trxName ) {
        super( ctx,0,trxName );

        if( ignored != 0 ) {
            throw new IllegalArgumentException( "Multi-Key" );
        }

        setTaxAmt( Env.ZERO );
        setTaxBaseAmt( Env.ZERO );
        setIsTaxIncluded( false );
    }    // MOrderTax

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MOrderTax( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MOrderTax

    /** Descripción de Campos */

    private MTax m_tax = null;

    /** Descripción de Campos */

    private Integer m_precision = null;

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private int getPrecision() {
        if( m_precision == null ) {
            return 2;
        }

        return m_precision.intValue();
    }    // getPrecision

    /**
     * Descripción de Método
     *
     *
     * @param precision
     */

    protected void setPrecision( int precision ) {
        m_precision = new Integer( precision );
    }    // setPrecision

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    protected MTax getTax() {
        if( m_tax == null ) {
            m_tax = MTax.get( getCtx(),getC_Tax_ID(),get_TrxName());
        }

        return m_tax;
    }    // getTax

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean calculateTaxFromLines() {
        BigDecimal taxBaseAmt = Env.ZERO;
        BigDecimal taxAmt     = Env.ZERO;

        //

        boolean documentLevel = getTax().isDocumentLevel();
        MTax    tax           = getTax();

        //

        String sql = "SELECT COALESCE(SUM("+getSqlOrderLineCalcForTaxBaseAmt()+"),0.0) FROM C_OrderLine WHERE C_Order_ID=? AND C_Tax_ID=?";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql,get_TrxName());
            pstmt.setInt( 1,getC_Order_ID());
            pstmt.setInt( 2,getC_Tax_ID());

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                taxBaseAmt = rs.getBigDecimal( 1 );

                //taxBaseAmt = taxBaseAmt.add( baseAmt );

                //

                if( !documentLevel ) {    // calculate line tax
                    taxAmt = tax.calculateTax( taxBaseAmt,isTaxIncluded(),getPrecision());
                }
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,get_TrxName(),e );
            taxBaseAmt = null;
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        //

        if( taxBaseAmt == null ) {
            return false;
        }

        // Calculate Tax

        if( documentLevel ) {    // document level
            taxAmt = tax.calculateTax( taxBaseAmt,isTaxIncluded(),getPrecision());
        }

        setTaxAmt( taxAmt );

        // Set Base

        if( isTaxIncluded()) {
            setTaxBaseAmt( taxBaseAmt.subtract( taxAmt ));
        } else {
            setTaxBaseAmt( taxBaseAmt );
        }

        log.fine( toString());

        return true;
    }    // calculateTaxFromLines

    
    /**
	 * Obtiene el cálculo del importe base de impuestos aplicado a las líneas
	 * del pedido al realizar consultas SQL
	 * 
	 * @return
	 */
    public String getSqlOrderLineCalcForTaxBaseAmt(){
    	return "LineNetAmt-DocumentDiscountAmt";
    }
    
    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer( "MOrderTax[" );

        sb.append( "C_Order_ID=" ).append( getC_Order_ID()).append( ",C_Tax_ID=" ).append( getC_Tax_ID()).append( ", Base=" ).append( getTaxBaseAmt()).append( ",Tax=" ).append( getTaxAmt()).append( "]" );

        return sb.toString();
    }    // toString
}    // MOrderTax



/*
 *  @(#)MOrderTax.java   02.07.07
 * 
 *  Fin del fichero MOrderTax.java
 *  
 *  Versión 2.2
 *
 */
