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

import org.openXpertya.util.DB;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class CalloutQuarterCategory extends CalloutEngine {

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param WindowNo
     * @param mTab
     * @param mField
     * @param value
     *
     * @return
     */

    public String distribution( Properties ctx,int WindowNo,MTab mTab,MField mField,Object value ) {
        if( isCalloutActive()) {
            return "";
        }

        setCalloutActive( true );

        double           total                   = 100;
        double           dist                    = 0;
        int              m_C_Quarter_Category_ID = mTab.getRecord_ID();
        MQuarterCategory to_MQC                  = new MQuarterCategory( ctx,m_C_Quarter_Category_ID,null );
        MQuarterCategory mqc = null;
        String           sql = "SELECT * FROM C_Quarter_Category cqc WHERE cqc.M_Product_Category_ID=? ";

        sql += " AND cqc.StockTime=? AND cqc.AD_Org_ID=? ORDER BY cqc.SeqNo ASC";

        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql );
            pstmt.setInt( 1,to_MQC.getM_Product_Category_ID());
            pstmt.setInt( 2,to_MQC.getStockTime());
            pstmt.setInt( 3,to_MQC.getAD_Org_ID());

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                mqc  = new MQuarterCategory( ctx,rs,null );
                dist = mqc.getDistribution().doubleValue();

                if( mqc.getID() == m_C_Quarter_Category_ID ) {
                    dist = (( BigDecimal )value ).doubleValue();
                }

                // si se pasa con lo que tiene se ajusta a 100

                if((( total > 0 ) && ( dist > total )) || ( total == 0 )) {
                    if( mqc.getID() != m_C_Quarter_Category_ID ) {
                        sql = "UPDATE C_Quarter_Category SET distribution=" + total + " WHERE C_Quarter_Category_ID=" + mqc.getC_Quarter_Category_ID();
                        DB.executeUpdate( sql );
                    } else {
                        mTab.setValue( mField,new BigDecimal( total ));
                    }
                }

                total = total - dist;

                if( total < 0 ) {
                    total = 0;
                }
            }

            if(( total > 0 ) && ( mqc != null ) && ( mqc.getID() != m_C_Quarter_Category_ID )) {
                sql = "UPDATE C_Quarter_Category SET distribution=" + ( total + dist ) + " WHERE C_Quarter_Category_ID=" + mqc.getC_Quarter_Category_ID();
                DB.executeUpdate( sql );
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"Callout QuarterCategory distribution",e );
        }

        //

        setCalloutActive( false );

        return "";
    }    // product
}    // CalloutOrder



/*
 *  @(#)CalloutQuarterCategory.java   02.07.07
 * 
 *  Fin del fichero CalloutQuarterCategory.java
 *  
 *  Versión 2.2
 *
 */
