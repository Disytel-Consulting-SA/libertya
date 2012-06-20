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

import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MQuarter extends X_C_Quarter {

    /** Descripción de Campos */

    private static CLogger s_log = CLogger.getCLogger( MQuarter.class );

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param C_Quarter_ID
     * @param trxName
     */

    public MQuarter( Properties ctx,int C_Quarter_ID,String trxName ) {
        super( ctx,C_Quarter_ID,trxName );

        if( C_Quarter_ID == 0 ) {
            setIncomings( 0 );
            setOutGoings( Env.ZERO );
            setStock( 0 );
            setTam12( Env.ZERO );
            setTam3( Env.ZERO );
        }
    }    // MQuarter

    /**
     * Descripción de Método
     *
     *
     * @param from
     * @param mqc
     */

    public void copyMQuarterFrom( MQuarter from,MQuarterCategory mqc ) {
        PO.copyValues( from,this,from.getAD_Client_ID(),from.getAD_Org_ID());
        this.setMQuarterCategory( mqc );
    }

    /**
     * Constructor de la clase ...
     *
     *
     * @param m_quarter_category
     * @param trxName
     */

    public MQuarter( MQuarterCategory m_quarter_category,String trxName ) {
        this( m_quarter_category.getCtx(),0,trxName );

        if( m_quarter_category.getID() == 0 ) {
            throw new IllegalArgumentException( "Header not saved" );
        }

        setMQuarterCategory( m_quarter_category );
    }    // MQuarter

    /**
     * Descripción de Método
     *
     *
     * @param m_quarter_category
     */

    public void setMQuarterCategory( MQuarterCategory m_quarter_category ) {
        if( m_quarter_category.getID() == 0 ) {
            throw new IllegalArgumentException( "Header not saved" );
        }

        setC_Quarter_Category_ID( m_quarter_category.getC_Quarter_Category_ID());
    }    // MQuarter

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MQuarter( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MQuarter

    /** Descripción de Campos */

    private String m_name = null;

    /** Descripción de Campos */

    private Integer m_precision = null;

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer( "MQuarter[" ).append( getID()).append( "," ).append( getID()).append( ",Name=" ).append( getName()).append( ",Product=" ).append( getM_Product_ID()).append( ",Stock=" ).append( getStock()).append( ",Tam12=" ).append( getTam12()).append( ",Tam3=" ).append( getTam3()).append( ",Incomings=" ).append( getIncomings()).append( ",Outgoings=" ).append( getOutGoings()).append( "]" );

        return sb.toString();
    }    // toString

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MQuarter getNext() {
        MQuarterCategory category = new MQuarterCategory( Env.getCtx(),getC_Quarter_Category_ID(),null );
        MQuarterCategory nextCategory = category.getNextBySeq();
        MQuarter         next         = null;
        String           sql          = "SELECT * FROM C_Quarter cq WHERE cq.C_Quarter_Category_ID=? " + " AND cq.M_Product_ID=? AND cp.Year=?";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql,get_TrxName());
            pstmt.setInt( 1,nextCategory.getC_Quarter_Category_ID());
            pstmt.setInt( 2,getM_Product_ID());

            if( category.getSeqNo() < nextCategory.getSeqNo()) {
                pstmt.setInt( 3,getYear());
            } else {
                pstmt.setInt( 3,( getYear() + 1 ));
            }

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                next = new MQuarter( getCtx(),rs,null );
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"getNext",e );
        }

        return next;
    }    // getNext

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MQuarter getPrevious() {
        MQuarterCategory category = new MQuarterCategory( Env.getCtx(),getC_Quarter_Category_ID(),null );
        MQuarterCategory prevCategory = category.getPreviousBySeq();
        MQuarter         previous     = null;

        if( prevCategory != null ) {
            String sql = "SELECT * FROM C_Quarter cq WHERE cq.C_Quarter_Category_ID=? ";

            sql += " AND cq.M_Product_ID=? AND cq.Year=?";

            PreparedStatement pstmt = null;

            try {
                pstmt = DB.prepareStatement( sql,get_TrxName());
                pstmt.setInt( 1,prevCategory.getC_Quarter_Category_ID());
                pstmt.setInt( 2,getM_Product_ID());

                if( category.getSeqNo() > prevCategory.getSeqNo()) {
                    pstmt.setInt( 3,getYear());
                } else {
                    pstmt.setInt( 3,( getYear() - 1 ));
                }

                ResultSet rs = pstmt.executeQuery();

                if( rs.next()) {
                    previous = new MQuarter( getCtx(),rs,null );
                }

                rs.close();
                pstmt.close();
                pstmt = null;
            } catch( Exception e ) {
                log.log( Level.SEVERE,"getPrvious",e );
            }
        } else {
            log.log( Level.SEVERE,Msg.translate( Env.getCtx(),"It's necessary a previous quarter category" ));
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

    public MQuarter[] getPrevious( int number ) {
        ArrayList        previous  = new ArrayList();
        MQuarterCategory actualMQC = new MQuarterCategory( Env.getCtx(),getC_Quarter_Category_ID(),null );
        MQuarterCategory[] prevMQC = actualMQC.getPreviousBySeq( number );

        // Anteriores en secuencia e iguales en a�o

        int          addeds = 0;
        StringBuffer sql    = new StringBuffer( "SELECT * FROM C_Quarter cq WHERE cq.C_Quarter_Category_ID IN(" );

        for( int i = 0;i < prevMQC.length;i++ ) {
            if(( prevMQC[ i ] != null ) && ( prevMQC[ i ].getSeqNo() <= actualMQC.getSeqNo())) {
                sql.append( prevMQC[ i ].getC_Quarter_Category_ID());

                if( i < ( prevMQC.length - 1 )) {
                    if((( i + 1 ) < prevMQC.length ) && ( prevMQC[ i + 1 ] != null ) && ( prevMQC[ i + 1 ].getSeqNo() <= actualMQC.getSeqNo())) {
                        sql.append( "," );
                    }
                }

                addeds++;
            }
        }

        sql.append( ") AND cq.M_Product_ID=? AND cq.Year=?" );

        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql.toString(),get_TrxName());
            pstmt.setInt( 1,getM_Product_ID());
            pstmt.setInt( 2,getYear());

            ResultSet rs = pstmt.executeQuery();

            for( int i = 0;i < addeds;i++ ) {
                if( rs.next()) {
                    previous.add( new MQuarter( getCtx(),rs,null ));
                } else {
                    previous.add( null );
                }
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"getPrevious before " + number,e );
        }

        //
        // Posteriores en secuencia pero anteriores en a�o

        int afters = 0;

        sql = new StringBuffer( "SELECT * FROM C_Quarter cq WHERE cq.C_Quarter_Category_ID IN(" );

        for( int i = 0;i < prevMQC.length;i++ ) {
            if(( prevMQC[ i ] != null ) && ( prevMQC[ i ].getSeqNo() > actualMQC.getSeqNo())) {
                sql.append( prevMQC[ i ].getC_Quarter_Category_ID());

                if( i < ( prevMQC.length - 1 )) {
                    if((( i + 1 ) < prevMQC.length ) && ( prevMQC[ i + 1 ] != null ) && ( prevMQC[ i + 1 ].getSeqNo() > actualMQC.getSeqNo())) {
                        sql.append( "," );
                    }
                }

                afters++;
            }
        }

        sql.append( ") AND cq.M_Product_ID=? AND cq.Year=?" );
        pstmt = null;

        if( afters > 0 ) {
            try {
                pstmt = DB.prepareStatement( sql.toString(),get_TrxName());
                pstmt.setInt( 1,getM_Product_ID());
                pstmt.setInt( 2,( getYear() - 1 ));

                ResultSet rs = pstmt.executeQuery();

                for( int i = addeds;i < prevMQC.length;i++ ) {
                    if( rs.next()) {
                        previous.add( new MQuarter( getCtx(),rs,null ));
                    } else {
                        previous.add( null );
                    }
                }

                rs.close();
                pstmt.close();
                pstmt = null;
            } catch( Exception e ) {
                log.log( Level.SEVERE,"getPrevious after " + number,e );
            }
        }

        MQuarter[] mq = new MQuarter[ number ];

        previous.toArray( mq );

        return mq;
    }    // getPrevious

    /**
     * Descripción de Método
     *
     *
     * @param newRecord
     *
     * @return
     */

    protected boolean beforeSave( boolean newRecord ) {
        log.fine( "beforeSave" );

        //

        return true;
    }    // beforeSave

    /**
     * Descripción de Método
     *
     *
     * @param newRecord
     * @param success
     *
     * @return
     */

    protected boolean afterSave( boolean newRecord,boolean success ) {
        return success;
    }    // afterSave

    /**
     * Descripción de Método
     *
     *
     * @param success
     *
     * @return
     */

    protected boolean afterDelete( boolean success ) {
        return success;
    }    // afterDelete
}    // MInvoiceLine



/*
 *  @(#)MQuarter.java   02.07.07
 * 
 *  Fin del fichero MQuarter.java
 *  
 *  Versión 2.2
 *
 */
