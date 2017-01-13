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
import java.sql.Timestamp;
import java.util.Properties;

import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MJournalLine extends X_GL_JournalLine {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param GL_JournalLine_ID
     * @param trxName
     */

    public MJournalLine( Properties ctx,int GL_JournalLine_ID,String trxName ) {
        super( ctx,GL_JournalLine_ID,trxName );

        if( GL_JournalLine_ID == 0 ) {

            // setGL_JournalLine_ID (0);               //      PK
            // setGL_Journal_ID (0);                   //      Parent
            // setC_Currency_ID (0);
            // setC_ValidCombination_ID (0);

            setLine( 0 );
            setAmtAcctCr( Env.ZERO );
            setAmtAcctDr( Env.ZERO );
            setAmtSourceCr( Env.ZERO );
            setAmtSourceDr( Env.ZERO );
            setCurrencyRate( Env.ONE );

            // setC_ConversionType_ID (0);

            setDateAcct( new Timestamp( System.currentTimeMillis()));
            setIsGenerated( true );
        }
    }    // MJournalLine

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MJournalLine( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MJournalLine

    /**
     * Constructor de la clase ...
     *
     *
     * @param parent
     */

    public MJournalLine( MJournal parent ) {
        this( parent.getCtx(),0,parent.get_TrxName());
        setClientOrg( parent );
        setGL_Journal_ID( parent.getGL_Journal_ID());
    }    // MJournalLine

    /** Descripción de Campos */

    private int m_precision = 2;

    /** Descripción de Campos */

    private MAccount m_validCombination = null;

    /** Descripción de Campos */

    private MElementValue m_account = null;

	private String m_processMsg;

    /**
     * Descripción de Método
     *
     *
     * @param C_Currency_ID
     * @param C_ConversionType_ID
     * @param CurrencyRate
     */

    public void setCurrency( int C_Currency_ID,int C_ConversionType_ID,BigDecimal CurrencyRate ) {
        setC_Currency_ID( C_Currency_ID );

        if( C_ConversionType_ID != 0 ) {
            setC_ConversionType_ID( C_ConversionType_ID );
        }

        if( (CurrencyRate != null) && (CurrencyRate.compareTo( Env.ZERO ) == 0) ) {
            setCurrencyRate( CurrencyRate );
        }
    }    // setCurrency

    /**
     * Descripción de Método
     *
     *
     * @param C_Currency_ID
     */

    public void setC_Currency_ID( int C_Currency_ID ) {
        if( C_Currency_ID == 0 ) {
            return;
        }

        super.setC_Currency_ID( C_Currency_ID );
        m_precision = MCurrency.getStdPrecision( getCtx(),C_Currency_ID );
    }    // setC_Currency_ID

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getPrecision() {
        return m_precision;
    }    // getPrecision

    /**
     * Descripción de Método
     *
     *
     * @param CurrencyRate
     */

    public void setCurrencyRate( BigDecimal CurrencyRate ) {
        if( CurrencyRate == null ) {
            log.warning( "setCurrencyRate - was NULL - set to 1" );
            super.setCurrencyRate( Env.ONE );
        } else if( CurrencyRate.compareTo( Env.ZERO ) < 0 ) {
            log.warning( "setCurrencyRate - negative - " + CurrencyRate + " - set to 1" );
            super.setCurrencyRate( Env.ONE );
        } else {
            super.setCurrencyRate( CurrencyRate );
        }
    }    // setCurrencyRate

    /**
     * Descripción de Método
     *
     *
     * @param AmtAcctDr
     * @param AmtAcctCr
     */

    public void setAmtAcct( BigDecimal AmtAcctDr,BigDecimal AmtAcctCr ) {

        // setConversion

        double rateDR = 0;

        if( (AmtAcctDr != null) && (AmtAcctDr.compareTo( Env.ZERO ) != 0) ) {
            rateDR = AmtAcctDr.doubleValue() / getAmtSourceDr().doubleValue();
            super.setAmtAcctDr( AmtAcctDr );
        }

        double rateCR = 0;

        if( (AmtAcctCr != null) && (AmtAcctCr.compareTo( Env.ZERO ) != 0) ) {
            rateCR = AmtAcctCr.doubleValue() / getAmtSourceCr().doubleValue();
            super.setAmtAcctCr( AmtAcctCr );
        }

        if( (rateDR != 0) && (rateCR != 0) && (rateDR != rateCR) ) {
            log.warning( "setAmtAcct - Rates Different DR=" + rateDR + "(used) <> CR=" + rateCR + "(ignored)" );
            rateCR = 0;
        }

        if( rateDR < 0 ) {
            log.warning( "setAmtAcct - DR Rate negatine - ignored - " + rateDR );

            return;
        }

        if( rateCR < 0 ) {
            log.warning( "setAmtAcct - CR Rate negatine - ignored - " + rateCR );

            return;
        }

        if( rateDR != 0 ) {
            setCurrencyRate( new BigDecimal( rateDR ));
        }

        if( rateCR != 0 ) {
            setCurrencyRate( new BigDecimal( rateCR ));
        }
    }    // setAmtAcct

    /**
     * Descripción de Método
     *
     *
     * @param C_ValidCombination_ID
     */

    public void setC_ValidCombination_ID( int C_ValidCombination_ID ) {
        super.setC_ValidCombination_ID( C_ValidCombination_ID );
        m_validCombination = null;
        m_account          = null;
    }    // setC_ValidCombination_ID

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MAccount getValidCombination() {
        if( (m_validCombination == null) && (getC_ValidCombination_ID() != 0) ) {
            m_validCombination = new MAccount( getCtx(),getC_ValidCombination_ID(),get_TrxName());
        }

        return m_validCombination;
    }    // getValidCombination

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MElementValue getAccountElementValue() {
        if( m_account == null ) {
            MAccount vc = getValidCombination();

            if( (vc != null) && (vc.getAccount_ID() != 0) ) {
                m_account = new MElementValue( getCtx(),vc.getAccount_ID(),get_TrxName());
            }
        }

        return m_account;
    }    // getAccount

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isDocControlled() {
        MElementValue acct = getAccountElementValue();

        if( acct == null ) {
            log.warning( "isControlAcct - Account not found for C_ValidCombination_ID=" + getC_ValidCombination_ID());

            return false;
        }

        return acct.isDocControlled();
    }    // isDocControlled

    /**
     * Descripción de Método
     *
     *
     * @param newRecord
     *
     * @return
     */

    protected boolean beforeSave( boolean newRecord ) {

		if (getAmtSourceCr().compareTo(BigDecimal.ZERO) != 0
				&& getAmtSourceDr().compareTo(BigDecimal.ZERO) != 0) {
			log.saveError("SaveError", "Al menos una de las columnas debe ser 0");
			return false;
		}
		
		if(getAmtSourceCr().compareTo(BigDecimal.ZERO) == 0 
				&& getAmtSourceDr().compareTo(BigDecimal.ZERO) == 0) {
			log.saveError("SaveError", "Al menos una de las columnas debe ser distinta a 0");
			return false;
		}
		
        BigDecimal rate = getCurrencyRate();
        BigDecimal amt  = rate.multiply( getAmtSourceDr());

        if( amt.scale() > getPrecision()) {
            amt = amt.setScale( getPrecision(),BigDecimal.ROUND_HALF_UP );
        }

        setAmtAcctDr( amt );
        amt = rate.multiply( getAmtSourceCr());

        if( amt.scale() > getPrecision()) {
            amt = amt.setScale( getPrecision(),BigDecimal.ROUND_HALF_UP );
        }

        setAmtAcctCr( amt );

        if( getLine() == 0 ) {
			String sql = "SELECT COALESCE(MAX(Line),0)+10 FROM " + Table_Name + " WHERE gl_journal_id=?";
            int ii = DB.getSQLValue( get_TrxName(),sql,getGL_Journal_ID());

            setLine( ii );
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
        if( !success ) {
            return success;
        }

        return updateJournalTotal();
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
        if( !success ) {
            return success;
        }

        return updateJournalTotal();
    }    // afterDelete

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private boolean updateJournalTotal() {

        // Update Journal Total
    	// begin DMA - Dataware - BugNo: 242
    	String sql;
    	
    	if(DB.isPostgreSQL()) {
    		sql = "UPDATE GL_Journal " +
    		      "SET TotalDr = (SELECT COALESCE(SUM(AmtSourceDr),0) FROM GL_JournalLine jl WHERE jl.IsActive='Y' AND GL_Journal.GL_Journal_ID=jl.GL_Journal_ID)," +
    		      "    TotalCr = (SELECT COALESCE(SUM(AmtSourceCr),0) FROM GL_JournalLine jl WHERE jl.IsActive='Y' AND GL_Journal.GL_Journal_ID=jl.GL_Journal_ID) " +
    		      "WHERE GL_Journal_ID=" + getGL_Journal_ID();
    	} else {
	        sql = "UPDATE GL_Journal j" + 
    			  " SET (TotalDr, TotalCr) = (SELECT COALESCE(SUM(AmtSourceDr),0), COALESCE(SUM(AmtSourceCr),0)" + 
    			  " FROM GL_JournalLine jl WHERE jl.IsActive='Y' AND j.GL_Journal_ID=jl.GL_Journal_ID) " + 
    			  "WHERE GL_Journal_ID=" + getGL_Journal_ID();
    	}
    	// end DMA - Dataware - BugNo: 242
        int no = DB.executeUpdate( sql,get_TrxName());

        if( no != 1 ) {
            log.warning( "afterSave - Update Journal #" + no );
        }

        // Update Batch Total
    	// begin DMA - Dataware - BugNo: 242
        if(DB.isPostgreSQL()) {
        	sql = "UPDATE GL_JournalBatch " +
			      "SET TotalDr=(SELECT COALESCE(SUM(j.TotalDr),0) FROM GL_Journal j WHERE j.IsActive='Y' AND GL_JournalBatch.GL_JournalBatch_ID=j.GL_JournalBatch_ID), " +
			      "    TotalCr=(SELECT COALESCE(SUM(j.TotalCr),0) FROM GL_Journal j WHERE j.IsActive='Y' AND GL_JournalBatch.GL_JournalBatch_ID=j.GL_JournalBatch_ID) " +
			      "WHERE GL_JournalBatch_ID=(SELECT DISTINCT GL_JournalBatch_ID FROM GL_Journal WHERE GL_Journal_ID=" + getGL_Journal_ID() + ")";
        } else {
	        sql = "UPDATE GL_JournalBatch jb" + 
	        	  " SET (TotalDr, TotalCr) = (SELECT COALESCE(SUM(TotalDr),0), COALESCE(SUM(TotalCr),0)" + 
	        	  " FROM GL_Journal j WHERE jb.GL_JournalBatch_ID=j.GL_JournalBatch_ID) " + 
	        	  "WHERE GL_JournalBatch_ID=" + 
	        	  "(SELECT DISTINCT GL_JournalBatch_ID FROM GL_Journal WHERE GL_Journal_ID=" + getGL_Journal_ID() + ")";
        }
    	// end DMA - Dataware - BugNo: 242

        no = DB.executeUpdate( sql,get_TrxName());

        if( no != 1 ) {
            log.warning( "afterSave - Update Batch #" + no );
        }

        return no == 1;
    }    // updateJournalTotal

	/**
	 * @return Returns the m_processMsg.
	 */
	public String getM_processMsg() {
		return m_processMsg;
	}
}    // MJournalLine



/*
 *  @(#)MJournalLine.java   02.07.07
 * 
 *  Fin del fichero MJournalLine.java
 *  
 *  Versión 2.2
 *
 */
