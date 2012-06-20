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

import org.openXpertya.util.CCache;
import org.openXpertya.util.CLogMgt;
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

public class MDistribution extends X_GL_Distribution {

    /**
     * Descripción de Método
     *
     *
     * @param acct
     * @param PostingType
     * @param C_DocType_ID
     *
     * @return
     */

    public static MDistribution[] get( MAccount acct,String PostingType,int C_DocType_ID ) {
        return get( acct.getCtx(),acct.getC_AcctSchema_ID(),PostingType,C_DocType_ID,acct.getAD_Org_ID(),acct.getAccount_ID(),acct.getM_Product_ID(),acct.getC_BPartner_ID(),acct.getC_Project_ID(),acct.getC_Campaign_ID(),acct.getC_Activity_ID(),acct.getAD_OrgTrx_ID(),acct.getC_SalesRegion_ID(),acct.getC_LocTo_ID(),acct.getC_LocFrom_ID(),acct.getUser1_ID(),acct.getUser2_ID());
    }    // get

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param C_AcctSchema_ID
     * @param PostingType
     * @param C_DocType_ID
     * @param AD_Org_ID
     * @param Account_ID
     * @param M_Product_ID
     * @param C_BPartner_ID
     * @param C_Project_ID
     * @param C_Campaign_ID
     * @param C_Activity_ID
     * @param AD_OrgTrx_ID
     * @param C_SalesRegion_ID
     * @param C_LocTo_ID
     * @param C_LocFrom_ID
     * @param User1_ID
     * @param User2_ID
     *
     * @return
     */

    public static MDistribution[] get( Properties ctx,int C_AcctSchema_ID,String PostingType,int C_DocType_ID,int AD_Org_ID,int Account_ID,int M_Product_ID,int C_BPartner_ID,int C_Project_ID,int C_Campaign_ID,int C_Activity_ID,int AD_OrgTrx_ID,int C_SalesRegion_ID,int C_LocTo_ID,int C_LocFrom_ID,int User1_ID,int User2_ID ) {
        MDistribution[] acctList = get( ctx,Account_ID );

        if( (acctList == null) || (acctList.length == 0) ) {
            return null;
        }

        //

        ArrayList list = new ArrayList();

        for( int i = 0;i < acctList.length;i++ ) {
            MDistribution distribution = acctList[ i ];

            if( !distribution.isActive() ||!distribution.isValid()) {
                continue;
            }

            // Mandatory Acct Schema

            if( distribution.getC_AcctSchema_ID() != C_AcctSchema_ID ) {
                continue;
            }

            // Only Posting Type / DocType

            if( (distribution.getPostingType() != null) &&!distribution.getPostingType().equals( PostingType )) {
                continue;
            }

            if( (distribution.getC_DocType_ID() != 0) && (distribution.getC_DocType_ID() != C_DocType_ID) ) {
                continue;
            }

            // Optional Elements - "non-Any"

            if( !distribution.isAnyOrg() && (distribution.getAD_Org_ID() != AD_Org_ID) ) {
                continue;
            }

            if( !distribution.isAnyAcct() && (distribution.getAccount_ID() != Account_ID) ) {
                continue;
            }

            if( !distribution.isAnyProduct() && (distribution.getM_Product_ID() != M_Product_ID) ) {
                continue;
            }

            if( !distribution.isAnyBPartner() && (distribution.getC_BPartner_ID() != C_BPartner_ID) ) {
                continue;
            }

            if( !distribution.isAnyProject() && (distribution.getC_Project_ID() != C_Project_ID) ) {
                continue;
            }

            if( !distribution.isAnyCampaign() && (distribution.getC_Campaign_ID() != C_Campaign_ID) ) {
                continue;
            }

            if( !distribution.isAnyActivity() && (distribution.getC_Activity_ID() != C_Activity_ID) ) {
                continue;
            }

            if( !distribution.isAnyOrgTrx() && (distribution.getAD_OrgTrx_ID() != AD_OrgTrx_ID) ) {
                continue;
            }

            if( !distribution.isAnySalesRegion() && (distribution.getC_SalesRegion_ID() != C_SalesRegion_ID) ) {
                continue;
            }

            if( !distribution.isAnyLocTo() && (distribution.getC_LocTo_ID() != C_LocTo_ID) ) {
                continue;
            }

            if( !distribution.isAnyLocFrom() && (distribution.getC_LocFrom_ID() != C_LocFrom_ID) ) {
                continue;
            }

            if( !distribution.isAnyUser1() && (distribution.getUser1_ID() != User1_ID) ) {
                continue;
            }

            if( !distribution.isAnyUser2() && (distribution.getUser2_ID() != User2_ID) ) {
                continue;
            }

            //

            list.add( distribution );
        }    // for all distributions with acct

        //

        MDistribution[] retValue = new MDistribution[ list.size()];

        list.toArray( retValue );

        return retValue;
    }    // get

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param Account_ID
     *
     * @return
     */

    public static MDistribution[] get( Properties ctx,int Account_ID ) {
        Integer         key      = new Integer( Account_ID );
        MDistribution[] retValue = ( MDistribution[] )s_accounts.get( key );

        if( retValue != null ) {
            return retValue;
        }

        String            sql   = "SELECT * FROM GL_Distribution " + "WHERE Account_ID=?";
        ArrayList         list  = new ArrayList();
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql,null );
            pstmt.setInt( 1,Account_ID );

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                list.add( new MDistribution( ctx,rs,null ));
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

        //

        retValue = new MDistribution[ list.size()];
        list.toArray( retValue );
        s_accounts.put( key,retValue );

        return retValue;
    }    // get

    /** Descripción de Campos */

    private static CLogger s_log = CLogger.getCLogger( MDistribution.class );

    /** Descripción de Campos */

    private static CCache s_accounts = new CCache( "GL_Distribution",100 );

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param GL_Distribution_ID
     * @param trxName
     */

    public MDistribution( Properties ctx,int GL_Distribution_ID,String trxName ) {
        super( ctx,GL_Distribution_ID,trxName );

        if( GL_Distribution_ID == 0 ) {

            // setC_AcctSchema_ID (0);
            // setName (null);
            //

            setAnyAcct( true );           // Y
            setAnyActivity( true );       // Y
            setAnyBPartner( true );       // Y
            setAnyCampaign( true );       // Y
            setAnyLocFrom( true );        // Y
            setAnyLocTo( true );          // Y
            setAnyOrg( true );            // Y
            setAnyOrgTrx( true );         // Y
            setAnyProduct( true );        // Y
            setAnyProject( true );        // Y
            setAnySalesRegion( true );    // Y
            setAnyUser1( true );          // Y
            setAnyUser2( true );          // Y

            //

            setIsValid( false );    // N
            setPercentTotal( Env.ZERO );
        }
    }                               // MDistribution

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MDistribution( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MDistribution

    /** Descripción de Campos */

    private MDistributionLine[] m_lines = null;

    /**
     * Descripción de Método
     *
     *
     * @param reload
     *
     * @return
     */

    public MDistributionLine[] getLines( boolean reload ) {
        if( (m_lines != null) &&!reload ) {
            return m_lines;
        }

        BigDecimal PercentTotal = Env.ZERO;
        ArrayList  list         = new ArrayList();
        String     sql          = "SELECT * FROM GL_DistributionLine " + "WHERE GL_Distribution_ID=? ORDER BY Line";
        boolean           hasNullRemainder = false;
        PreparedStatement pstmt            = null;

        try {
            pstmt = DB.prepareStatement( sql,get_TrxName());
            pstmt.setInt( 1,getGL_Distribution_ID());

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                MDistributionLine dl = new MDistributionLine( getCtx(),rs,get_TrxName());

                if( dl.isActive()) {
                    PercentTotal     = PercentTotal.add( dl.getPercent());
                    hasNullRemainder = Env.ZERO.compareTo( dl.getPercent()) == 0;
                }

                dl.setParent( this );
                list.add( dl );
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

        // Update Ratio when saved and difference

        if( hasNullRemainder ) {
            PercentTotal = Env.ONEHUNDRED;
        }

        if( (getID() != 0) && (PercentTotal.compareTo( getPercentTotal()) != 0) ) {
            setPercentTotal( PercentTotal );
            save();
        }

        // return

        m_lines = new MDistributionLine[ list.size()];
        list.toArray( m_lines );

        return m_lines;
    }    // getLines

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String validate() {
        String retValue = null;

        getLines( true );

        if( m_lines.length == 0 ) {
            retValue = "@NoLines@";
        } else if( getPercentTotal().compareTo( Env.ONEHUNDRED ) != 0 ) {
            retValue = "@PercentTotal@ <> 100";
        } else {

            // More then one line with 0

            int lineFound = -1;

            for( int i = 0;i < m_lines.length;i++ ) {
                if( m_lines[ i ].getPercent().compareTo( Env.ZERO ) == 0 ) {
                    if( (lineFound >= 0) && (m_lines[ i ].getPercent().compareTo( Env.ZERO ) == 0) ) {
                        retValue = "@Line@ " + lineFound + " + " + m_lines[ i ].getLine() + ": == 0";

                        break;
                    }

                    lineFound = m_lines[ i ].getLine();
                }
            }    // for all lines
        }

        setIsValid( retValue == null );

        return retValue;
    }    // validate

    /**
     * Descripción de Método
     *
     *
     * @param acct
     * @param Amt
     * @param C_Currency_ID
     */

    public void distribute( MAccount acct,BigDecimal Amt,int C_Currency_ID ) {
        log.info( "distribute - Amt=" + Amt + " - " + acct );
        getLines( false );

        int precision = MCurrency.getStdPrecision( getCtx(),C_Currency_ID );

        // First Round

        BigDecimal total            = Env.ZERO;
        int        indexBiggest     = -1;
        int        indexZeroPercent = -1;

        for( int i = 0;i < m_lines.length;i++ ) {
            MDistributionLine dl = m_lines[ i ];

            if( !dl.isActive()) {
                continue;
            }

            dl.setAccount( acct );

            // Calculate Amount

            dl.calculateAmt( Amt,precision );
            total = total.add( dl.getAmt());

            // log.fine("distribute - Line=" + dl.getLine() + " - " + dl.getPercent() + "% " + dl.getAmt() + " - Total=" + total);
            // Remainder

            if( dl.getPercent().compareTo( Env.ZERO ) == 0 ) {
                indexZeroPercent = i;
            }

            if( indexZeroPercent == -1 ) {
                if( indexBiggest == -1 ) {
                    indexBiggest = i;
                } else if( dl.getAmt().compareTo( m_lines[ indexBiggest ].getAmt()) > 0 ) {
                    indexBiggest = i;
                }
            }
        }

        // Adjust Remainder

        BigDecimal difference = Amt.subtract( total );

        if( difference.compareTo( Env.ZERO ) != 0 ) {
            if( indexZeroPercent != -1 ) {

                // log.fine("distribute - Difference=" + difference + " - 0%Line=" + m_lines[indexZeroPercent]);

                m_lines[ indexZeroPercent ].setAmt( difference );
            } else if( indexBiggest != -1 ) {

                // log.fine("distribute - Difference=" + difference + " - MaxLine=" + m_lines[indexBiggest] + " - " + m_lines[indexBiggest].getAmt());

                m_lines[ indexBiggest ].setAmt( m_lines[ indexBiggest ].getAmt().add( difference ));
            } else {
                log.warning( "distribute - Remaining Difference=" + difference );
            }
        }

        //

        if( CLogMgt.isLevelFinest()) {
            for( int i = 0;i < m_lines.length;i++ ) {
                if( m_lines[ i ].isActive()) {
                    log.fine( "distribute = Amt=" + m_lines[ i ].getAmt() + " - " + m_lines[ i ].getAccount());
                }
            }
        }
    }    // distribute

    /**
     * Descripción de Método
     *
     *
     * @param newRecord
     *
     * @return
     */

    protected boolean beforeSave( boolean newRecord ) {

        // Reset not selected Any

        if( isAnyAcct() && (getAccount_ID() != 0) ) {
            setAccount_ID( 0 );
        }

        if( isAnyActivity() && (getC_Activity_ID() != 0) ) {
            setC_Activity_ID( 0 );
        }

        if( isAnyBPartner() && (getC_BPartner_ID() != 0) ) {
            setC_BPartner_ID( 0 );
        }

        if( isAnyCampaign() && (getC_Campaign_ID() != 0) ) {
            setC_Campaign_ID( 0 );
        }

        if( isAnyLocFrom() && (getC_LocFrom_ID() != 0) ) {
            setC_LocFrom_ID( 0 );
        }

        if( isAnyLocTo() && (getC_LocTo_ID() != 0) ) {
            setC_LocTo_ID( 0 );
        }

        if( isAnyOrg() && (getOrg_ID() != 0) ) {
            setOrg_ID( 0 );
        }

        if( isAnyOrgTrx() && (getAD_OrgTrx_ID() != 0) ) {
            setAD_OrgTrx_ID( 0 );
        }

        if( isAnyProduct() && (getM_Product_ID() != 0) ) {
            setM_Product_ID( 0 );
        }

        if( isAnyProject() && (getC_Project_ID() != 0) ) {
            setC_Project_ID( 0 );
        }

        if( isAnySalesRegion() && (getC_SalesRegion_ID() != 0) ) {
            setC_SalesRegion_ID( 0 );
        }

        if( isAnyUser1() && (getUser1_ID() != 0) ) {
            setUser1_ID( 0 );
        }

        if( isAnyUser2() && (getUser2_ID() != 0) ) {
            setUser2_ID( 0 );
        }

        return true;
    }    // beforeSave
}    // MDistribution



/*
 *  @(#)MDistribution.java   02.07.07
 * 
 *  Fin del fichero MDistribution.java
 *  
 *  Versión 2.2
 *
 */
