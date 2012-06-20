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

public class MQuarterCategory extends X_C_Quarter_Category {

    /**
     * Descripción de Método
     *
     *
     * @param from
     */

    public void copyMQuarterCategoryFrom( MQuarterCategory from ) {
        PO.copyValues( from,this,from.getAD_Client_ID(),from.getAD_Org_ID());

        MQuarter[] qs = from.getQuarters( null );

        for( int i = 0;i < qs.length;i++ ) {
            MQuarter mq = new MQuarter( Env.getCtx(),0,null );

            mq.copyMQuarterFrom( qs[ i ],this );
            mq.save();
        }
    }    // copyMQuarterCategoryFrom

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param C_Quarter_Category_ID
     * @param trxName
     */

    public MQuarterCategory( Properties ctx,int C_Quarter_Category_ID,String trxName ) {
        super( ctx,C_Quarter_Category_ID,trxName );

        if( C_Quarter_Category_ID == 0 ) {
            setSeqNo( 0 );

            //

            setDistribution( Env.ZERO );
            setStockTime( 0 );
            setIsInitial( false );
            setIsFinal( false );
        }
    }    // MQuarterCategory

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MQuarterCategory( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MQuarterCategory

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MQuarterCategory getNextBySeq() {
        MQuarterCategory next = null;
        String           sql  = "SELECT * FROM C_Quarter_Category qc WHERE M_Product_Category_ID=? ";

        sql += " AND qc.SeqNo>? AND qc.StockTime=? AND qc.AD_Org_ID=?";
        sql += " ORDER BY SeqNo ASC";

        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql,get_TrxName());
            pstmt.setInt( 1,getM_Product_Category_ID());
            pstmt.setInt( 2,getSeqNo());
            pstmt.setInt( 3,getStockTime());
            pstmt.setInt( 4,getAD_Org_ID());

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                next = new MQuarterCategory( getCtx(),rs,null );
            } else {
                sql = "SELECT * FROM C_Quarter_Category qc WHERE M_Product_Category_ID=? ";
                sql   += " AND qc.StockTime=? AND qc.AD_Org_ID=?";
                sql   += " ORDER BY SeqNo ASC";
                pstmt = DB.prepareStatement( sql,get_TrxName());
                pstmt.setInt( 1,getM_Product_Category_ID());
                pstmt.setInt( 2,getStockTime());
                pstmt.setInt( 3,getAD_Org_ID());
                rs = pstmt.executeQuery();

                if( rs.next()) {
                    next = new MQuarterCategory( getCtx(),rs,null );
                }
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"getNext",e );
        }

        //

        return next;
    }    // getNext

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MQuarterCategory getPreviousBySeq() {
        MQuarterCategory previous = null;
        String           sql      = "SELECT * FROM C_Quarter_Category qc WHERE M_Product_Category_ID=? ";

        sql += " AND qc.SeqNo<? AND qc.StockTime=? AND qc.AD_Org_ID=?";
        sql += " ORDER BY SeqNo DESC";

        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql,get_TrxName());
            pstmt.setInt( 1,getM_Product_Category_ID());
            pstmt.setInt( 2,getSeqNo());
            pstmt.setInt( 3,getStockTime());
            pstmt.setInt( 4,getAD_Org_ID());

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                previous = new MQuarterCategory( getCtx(),rs,null );
            } else {
                sql = "SELECT * FROM C_Quarter_Category qc WHERE M_Product_Category_ID=? ";
                sql   += " AND qc.StockTime=? AND qc.AD_Org_ID=?";
                sql   += " ORDER BY SeqNo DESC";
                pstmt = DB.prepareStatement( sql,get_TrxName());
                pstmt.setInt( 1,getM_Product_Category_ID());
                pstmt.setInt( 2,getStockTime());
                pstmt.setInt( 3,getAD_Org_ID());
                rs = pstmt.executeQuery();

                if( rs.next()) {
                    previous = new MQuarterCategory( getCtx(),rs,null );
                }
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"getPrevious",e );
        }

        //

        return previous;
    }    // getPrevious

    /**
     * Descripción de Método
     *
     *
     * @param number
     *
     * @return
     */

    public MQuarterCategory[] getPreviousBySeq( int number ) {
        ArrayList previous = new ArrayList();
        String    sql      = "SELECT * FROM C_Quarter_Category qc WHERE qc.M_Product_Category_ID=? ";

        sql += " AND qc.SeqNo<=? AND qc.StockTime=? AND qc.AD_Org_ID=?";
        sql += " ORDER BY qc.SeqNo DESC";

        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql,get_TrxName());
            pstmt.setInt( 1,getM_Product_Category_ID());
            pstmt.setInt( 2,getSeqNo());
            pstmt.setInt( 3,getStockTime());
            pstmt.setInt( 4,getAD_Org_ID());

            ResultSet rs     = pstmt.executeQuery();
            int       finals = 0;

            for( int i = 1;i <= number;i++ ) {
                if( rs.next()) {
                    previous.add( new MQuarterCategory( getCtx(),rs,null ));
                } else {
                    finals++;
                }
            }

            if( finals > 0 ) {
                sql = "SELECT * FROM C_Quarter_Category qc WHERE qc.M_Product_Category_ID=? " + " AND qc.StockTime=? AND qc.AD_Org_ID=?" + " ORDER BY qc.SeqNo DESC";
                pstmt = DB.prepareStatement( sql,get_TrxName());
                pstmt.setInt( 1,getM_Product_Category_ID());
                pstmt.setInt( 2,getStockTime());
                pstmt.setInt( 3,getAD_Org_ID());
                rs = pstmt.executeQuery();

                for( int i = 0;i < finals;i++ ) {
                    if( rs.next()) {
                        previous.add( new MQuarterCategory( getCtx(),rs,null ));
                    }
                }
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"getPrevious " + number,e );
        }

        //

        MQuarterCategory[] mqc = new MQuarterCategory[ number ];

        previous.toArray( mqc );

        return mqc;
    }    // getPrevious

    /**
     * Descripción de Método
     *
     *
     * @param whereClause
     *
     * @return
     */

    public MQuarter[] getQuarters( String whereClause ) {
        ArrayList list = new ArrayList();
        String    sql  = "SELECT * FROM C_Quarter WHERE C_Quarter_Category_ID=? ";

        if( whereClause != null ) {
            sql += whereClause;
        }

        sql += " ORDER BY Year ASC";

        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql,get_TrxName());
            pstmt.setInt( 1,getID());

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                list.add( new MQuarter( getCtx(),rs,null ));
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"getQuarters",e );
        } finally {
            try {
                if( pstmt != null ) {
                    pstmt.close();
                }
            } catch( Exception e ) {
            }

            pstmt = null;
        }

        //

        MQuarter[] quarters = new MQuarter[ list.size()];

        list.toArray( quarters );

        return quarters;
    }    // getQuarters

    /**
     * Descripción de Método
     *
     *
     * @param newRecord
     *
     * @return
     */

    protected boolean beforeSave( boolean newRecord ) {
       // log.log( Level.SEVERE,"beforeSave" );

        return true;
    }    // beforeSave

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer( "MQuarterCategory[" ).append( getID()).append( "," ).append( getID()).append( ",Name=" ).append( getName()).append( ",Product Category=" ).append( getM_Product_Category_ID()).append( ",StockTime=" ).append( getStockTime()).append( ",SeqNo=" ).append( getSeqNo()).append( ",BeginDate=" ).append( getBeginDate()).append( ",EndDate=" ).append( getEndDate()).append( "]" );

        return sb.toString();
    }
}    // MQuarterCategory



/*
 *  @(#)MQuarterCategory.java   02.07.07
 * 
 *  Fin del fichero MQuarterCategory.java
 *  
 *  Versión 2.2
 *
 */
