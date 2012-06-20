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
import java.util.Properties;

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

public class MDistributionLine extends X_GL_DistributionLine {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param GL_DistributionLine_ID
     * @param trxName
     */

    public MDistributionLine( Properties ctx,int GL_DistributionLine_ID,String trxName ) {
        super( ctx,GL_DistributionLine_ID,trxName );

        if( GL_DistributionLine_ID == 0 ) {

            // setGL_Distribution_ID (0);              //      Parent
            // setLine (0);
            //

            setOverwriteAcct( false );
            setOverwriteActivity( false );
            setOverwriteBPartner( false );
            setOverwriteCampaign( false );
            setOverwriteLocFrom( false );
            setOverwriteLocTo( false );
            setOverwriteOrg( false );
            setOverwriteOrgTrx( false );
            setOverwriteProduct( false );
            setOverwriteProject( false );
            setOverwriteSalesRegion( false );
            setOverwriteUser1( false );
            setOverwriteUser2( false );

            //

            setPercent( Env.ZERO );
        }
    }    // MDistributionLine

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MDistributionLine( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MDistributionLine

    /** Descripción de Campos */

    private MDistribution m_parent = null;

    /** Descripción de Campos */

    private BigDecimal m_amt = null;

    /** Descripción de Campos */

    private MAccount m_account = null;

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MDistribution getParent() {
        if( m_parent == null ) {
            m_parent = new MDistribution( getCtx(),getGL_Distribution_ID(),get_TrxName());
        }

        return m_parent;
    }    // getParent

    /**
     * Descripción de Método
     *
     *
     * @param parent
     */

    public void setParent( MDistribution parent ) {
        m_parent = parent;
    }    // setParent

    /**
     * Descripción de Método
     *
     *
     * @param acct
     */

    public void setAccount( MAccount acct ) {
        m_account = acct;
    }    // setAccount

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MAccount getAccount() {
        MAccount acct = MAccount.get( getCtx(),m_account.getAD_Client_ID(),(isOverwriteOrg() && (getOrg_ID() != 0))
                ?getOrg_ID()
                :m_account.getAD_Org_ID(),m_account.getC_AcctSchema_ID(),(isOverwriteAcct() && (getAccount_ID() != 0))
                ?getAccount_ID()
                :m_account.getAccount_ID(),

        //

        isOverwriteProduct()
        ?getM_Product_ID()
        :m_account.getM_Product_ID(),isOverwriteBPartner()
                                     ?getC_BPartner_ID()
                                     :m_account.getC_BPartner_ID(),isOverwriteOrgTrx()
                ?getAD_OrgTrx_ID()
                :m_account.getAD_OrgTrx_ID(),isOverwriteLocFrom()
                ?getC_LocFrom_ID()
                :m_account.getC_LocFrom_ID(),isOverwriteLocTo()
                ?getC_LocTo_ID()
                :m_account.getC_LocTo_ID(),isOverwriteSalesRegion()
                                           ?getC_SalesRegion_ID()
                                           :m_account.getC_SalesRegion_ID(),isOverwriteProject()
                ?getC_Project_ID()
                :m_account.getC_Project_ID(),isOverwriteCampaign()
                ?getC_Campaign_ID()
                :m_account.getC_Campaign_ID(),isOverwriteActivity()
                ?getC_Activity_ID()
                :m_account.getC_Activity_ID(),isOverwriteUser1()
                ?getUser1_ID()
                :m_account.getUser1_ID(),isOverwriteUser2()
                                         ?getUser2_ID()
                                         :m_account.getUser2_ID());

        return acct;
    }    // setAccount

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public BigDecimal getAmt() {
        return m_amt;
    }    // getAmt

    /**
     * Descripción de Método
     *
     *
     * @param amt
     */

    public void setAmt( BigDecimal amt ) {
        m_amt = amt;
    }    // setAmt

    /**
     * Descripción de Método
     *
     *
     * @param amt
     * @param precision
     */

    public void calculateAmt( BigDecimal amt,int precision ) {
        m_amt = amt.multiply( getPercent());
        m_amt = m_amt.divide( Env.ONEHUNDRED,precision,BigDecimal.ROUND_HALF_UP );
    }    // setAmt

    /**
     * Descripción de Método
     *
     *
     * @param newRecord
     *
     * @return
     */

    protected boolean beforeSave( boolean newRecord ) {
        if( getLine() == 0 ) {
            String sql = "SELECT COALESCE(MAX(Line),0)+10 FROM GL_DistributionLine WHERE GL_Distribution_ID=?";
            int ii = DB.getSQLValue( get_TrxName(),sql,getGL_Distribution_ID());

            setLine( ii );
        }

        // Reset not selected Overwrite

        if( !isOverwriteAcct() && (getAccount_ID() != 0) ) {
            setAccount_ID( 0 );
        }

        if( !isOverwriteActivity() && (getC_Activity_ID() != 0) ) {
            setC_Activity_ID( 0 );
        }

        if( !isOverwriteBPartner() && (getC_BPartner_ID() != 0) ) {
            setC_BPartner_ID( 0 );
        }

        if( !isOverwriteCampaign() && (getC_Campaign_ID() != 0) ) {
            setC_Campaign_ID( 0 );
        }

        if( !isOverwriteLocFrom() && (getC_LocFrom_ID() != 0) ) {
            setC_LocFrom_ID( 0 );
        }

        if( !isOverwriteLocTo() && (getC_LocTo_ID() != 0) ) {
            setC_LocTo_ID( 0 );
        }

        if( !isOverwriteOrg() && (getOrg_ID() != 0) ) {
            setOrg_ID( 0 );
        }

        if( !isOverwriteOrgTrx() && (getAD_OrgTrx_ID() != 0) ) {
            setAD_OrgTrx_ID( 0 );
        }

        if( !isOverwriteProduct() && (getM_Product_ID() != 0) ) {
            setM_Product_ID( 0 );
        }

        if( !isOverwriteProject() && (getC_Project_ID() != 0) ) {
            setC_Project_ID( 0 );
        }

        if( !isOverwriteSalesRegion() && (getC_SalesRegion_ID() != 0) ) {
            setC_SalesRegion_ID( 0 );
        }

        if( !isOverwriteUser1() && (getUser1_ID() != 0) ) {
            setUser1_ID( 0 );
        }

        if( !isOverwriteUser2() && (getUser2_ID() != 0) ) {
            setUser2_ID( 0 );
        }

        // Account Overwrite cannot be 0

        if( isOverwriteAcct() && (getAccount_ID() == 0) ) {
            log.saveError( "Error",Msg.parseTranslation( getCtx(),"@Account_ID@ = 0" ));

            return false;
        }

        // Org Overwrite cannot be 0

        if( isOverwriteOrg() && (getOrg_ID() == 0) ) {
            log.saveError( "Error",Msg.parseTranslation( getCtx(),"@Org_ID@ = 0" ));

            return false;
        }

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
        getParent();
        m_parent.validate();
        m_parent.save();

        return success;
    }    // afterSave
}    // MDistributionLine



/*
 *  @(#)MDistributionLine.java   02.07.07
 * 
 *  Fin del fichero MDistributionLine.java
 *  
 *  Versión 2.2
 *
 */
