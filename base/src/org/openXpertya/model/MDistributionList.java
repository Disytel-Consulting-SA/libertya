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
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MDistributionList extends X_M_DistributionList {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param M_DistributionList_ID
     * @param trxName
     */

    public MDistributionList( Properties ctx,int M_DistributionList_ID,String trxName ) {
        super( ctx,M_DistributionList_ID,trxName );
    }    // MDistributionList

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MDistributionList( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MDistributionList

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MDistributionListLine[] getLines() {
        ArrayList  list       = new ArrayList();
        BigDecimal ratioTotal = Env.ZERO;

        //

        String sql = "SELECT * FROM M_DistributionListLine WHERE M_DistributionList_ID=?";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql,get_TrxName());
            pstmt.setInt( 1,getM_DistributionList_ID());

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                MDistributionListLine line = new MDistributionListLine( getCtx(),rs,get_TrxName());

                list.add( line );

                BigDecimal ratio = line.getRatio();

                if( ratio != null ) {
                    ratioTotal = ratioTotal.add( ratio );
                }
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"getLines",e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        // Update Ratio

        if( ratioTotal.compareTo( getRatioTotal()) != 0 ) {
            log.info( "getLines - Set RatioTotal from " + getRatioTotal() + " to " + ratioTotal );
            setRatioTotal( ratioTotal );
            save();
        }

        MDistributionListLine[] retValue = new MDistributionListLine[ list.size()];

        list.toArray( retValue );

        return retValue;
    }    // getLines
}    // MDistributionList



/*
 *  @(#)MDistributionList.java   02.07.07
 * 
 *  Fin del fichero MDistributionList.java
 *  
 *  Versión 2.2
 *
 */
