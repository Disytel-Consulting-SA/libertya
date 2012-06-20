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
import java.sql.SQLException;
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

public class CalloutAssignment extends CalloutEngine {

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

    public String product( Properties ctx,int WindowNo,MTab mTab,MField mField,Object value ) {
        if( isCalloutActive() || (value == null) ) {
            return "";
        }

        // get value

        int S_ResourceAssignment_ID = (( Integer )value ).intValue();

        if( S_ResourceAssignment_ID == 0 ) {
            return "";
        }

        setCalloutActive( true );

        int        M_Product_ID = 0;
        String     Name         = null;
        String     Description  = null;
        BigDecimal Qty          = null;
        String     sql          = "SELECT p.M_Product_ID, ra.Name, ra.Description, ra.Qty " + "FROM S_ResourceAssignment ra" + " INNER JOIN M_Product p ON (p.S_Resource_ID=ra.S_Resource_ID) " + "WHERE ra.S_ResourceAssignment_ID=?";

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql );

            pstmt.setInt( 1,S_ResourceAssignment_ID );

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                M_Product_ID = rs.getInt( 1 );
                Name         = rs.getString( 2 );
                Description  = rs.getString( 3 );
                Qty          = rs.getBigDecimal( 4 );
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"product",e );
        }

        log.fine( "S_ResourceAssignment_ID=" + S_ResourceAssignment_ID + " - M_Product_ID=" + M_Product_ID );

        if( M_Product_ID != 0 ) {
            mTab.setValue( "M_Product_ID",new Integer( M_Product_ID ));

            if( Description != null ) {
                Name += " (" + Description + ")";
            }

            if( !".".equals( Name )) {
                mTab.setValue( "Description",Name );
            }

            //

            String variable = "Qty";    // TimeExpenseLine

            if( mTab.getTableName().startsWith( "C_Order" )) {
                variable = "QtyOrdered";
            } else if( mTab.getTableName().startsWith( "C_Invoice" )) {
                variable = "QtyInvoiced";
            }

            if( Qty != null ) {
                mTab.setValue( variable,Qty );
            }
        }

        setCalloutActive( false );

        return "";
    }    // product
}    // CalloutAssignment



/*
 *  @(#)CalloutAssignment.java   02.07.07
 * 
 *  Fin del fichero CalloutAssignment.java
 *  
 *  Versión 2.2
 *
 */
