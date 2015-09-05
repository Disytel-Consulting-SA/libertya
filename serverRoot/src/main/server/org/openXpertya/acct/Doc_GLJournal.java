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



package org.openXpertya.acct;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;

import org.openXpertya.model.MAccount;
import org.openXpertya.model.MAcctSchema;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

/**
 * Descripción de Clase
 *
 *
 * @version 2.2, 24.03.06
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class Doc_GLJournal extends Doc {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ass
     * @param AD_Table_ID
     * @param TableName
     */

    protected Doc_GLJournal( MAcctSchema[] ass,int AD_Table_ID,String TableName ) {
        super( ass );
        p_AD_Table_ID = AD_Table_ID;
        p_TableName   = TableName;
    }

    /** Descripción de Campos */

    private String m_PostingType = Fact.POST_Actual;

    /**
     * Descripción de Método
     *
     *
     * @param rs
     *
     * @return
     */

    protected boolean loadDocumentDetails( ResultSet rs ) {
        loadDocumentType();    // lines require doc type

        try {
            m_PostingType     = rs.getString( "PostingType" );
            p_vo.GL_Budget_ID = rs.getInt( "GL_Budget_ID" );
        } catch( SQLException ex ) {
            log.log( Level.SEVERE,"loadDocumentDetails - load PostingType",ex );
        }

        // Contained Objects

        p_lines = loadLines();
        log.fine( "Lines=" + p_lines.length );

        return true;
    }    // loadDocumentDetails

    /**
     * Descripción de Método
     *
     * @return
     */
	/**
	 * @author: Horacio Alvarez
	 * @modificado: 2008-08-11
	 */
	private DocLine[] loadLines()
	{
		
		MAcctSchema[] schema = null;

        ArrayList list = new ArrayList();
        String    sql  = "SELECT jl.GL_JournalLine_ID,jl.AD_Client_ID,jl.AD_Org_ID,"    // 1..3
                         + " jl.Line,jl.Description,"                            // 4..5
                         + " jl.C_Currency_ID,jl.AmtSourceDr,jl.AmtSourceCr,"    // 6..8
                         + " jl.C_ConversionType_ID,jl.CurrencyRate,"            // 9..10
			             + " j.DateAcct,jl.AmtAcctDr,jl.AmtAcctCr,"                     //  11..13
                         + " jl.C_UOM_ID,jl.Qty,"                                // 14..15
                         + " vc.C_AcctSchema_ID,vc.C_ValidCombination_ID,"       // 16..17
                         + " vc.Account_ID,vc.M_Product_ID,vc.C_BPartner_ID,"    // 18..20
                         + " vc.AD_OrgTrx_ID,vc.C_LocFrom_ID,vc.C_LocTo_ID," 
                         + " vc.C_SalesRegion_ID,vc.C_Project_ID,vc.C_Campaign_ID," 
                         + " vc.C_Activity_ID,vc.User1_ID,vc.User2_ID "
			             + " , jl.c_elementvalue_id "
			//
				//			+ "FROM C_ValidCombination vc, GL_JournalLine jl "
				//			+ "WHERE vc.C_ValidCombination_ID=jl.C_ValidCombination_ID"
				//			+ " AND jl.GL_Journal_ID=?"
			
						+ "FROM GL_JournalLine jl "
						+ "INNER JOIN GL_Journal j on j.GL_Journal_ID = jl.GL_Journal_ID "
						
						// ***********************
						// Este JOIN se utiliza para obtener una combinación de cuentas a partir del ElementValue y la
						// Organización en el asiento.
						// PROBLEMA: si en la tabla C_ValidCombination existe mas de una Tupla con el
						// ElmentValue y Organización, el resultado del JOIN será tuplas de asientos 
						// repetidas, con lo cual se estarán contabilizando erróneamente los asientos
						// manuales. (multiplicando el nro de asientos)
						//+ "LEFT JOIN C_ValidCombination vc ON ( vc.account_id = jl.c_elementvalue_id and vc.ad_org_id = jl.ad_org_id ) "
						// ***********************
						
						+ "INNER JOIN C_ValidCombination vc ON (jl.C_ValidCombination_ID = vc.C_ValidCombination_ID) "
						+ "WHERE  jl.GL_Journal_ID = ? "
						+ " AND jl.IsActive='Y' "
			//			+ " AND jl.IsActive='Y' AND vc.IsFullyQualified='Y' "
						+ "ORDER BY jl.Line";
		try
		{
			PreparedStatement pstmt = DB.prepareStatement(sql);
			pstmt.setInt(1, getRecord_ID());
            ResultSet rs = pstmt.executeQuery();

            //
			while (rs.next())
			{
				int Line_ID = rs.getInt(1);
				DocLine docLine = new DocLine (p_vo.DocumentType, 
					getRecord_ID(), Line_ID, getTrxName());
				docLine.loadAttributes (rs, p_vo);
                // --  Source Amounts
				BigDecimal AmtSourceDr = rs.getBigDecimal(7);
				BigDecimal AmtSourceCr = rs.getBigDecimal(8);
				docLine.setAmount (AmtSourceDr, AmtSourceCr);
                // --  Converted Amounts
//				int C_AcctSchema_ID = rs.getInt(16);
				schema = MAcctSchema.getClientAcctSchema(getCtx(), rs.getInt(2));
				int C_AcctSchema_ID = schema[0].getC_AcctSchema_ID();
				BigDecimal AmtAcctDr = rs.getBigDecimal(12);
				BigDecimal AmtAcctCr = rs.getBigDecimal(13);
				docLine.setConvertedAmt (C_AcctSchema_ID, AmtAcctDr, AmtAcctCr);
                // --  Account
				int C_ValidCombination_ID = rs.getInt(17);
				MAccount acct = null;
				if(C_ValidCombination_ID != 0)
				   acct = new MAccount(getCtx(), C_ValidCombination_ID, getTrxName());
				else
				   acct = MAccount.get(getCtx(),rs.getInt(2),rs.getInt(3),C_AcctSchema_ID,
						   rs.getInt("C_ElementValue_ID"),0,0,0,0,0,0,0,0,0,0,0);
				docLine.setAccount (acct);
				docLine.setDateAcct(rs.getTimestamp(11));
                // --      Set Org from account    (x-org)
				docLine.setAD_Org_ID (acct.getAD_Org_ID());

//				docLine.setAD_Org_ID (rs.getInt(3));
                //
				list.add(docLine);
            }

            //

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"loadLines - SQL=" + sql,e );
        }

        // Return Array

        int       size = list.size();
        DocLine[] dl   = new DocLine[ size ];

        list.toArray( dl );

        return dl;
    }    // loadLines

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public BigDecimal getBalance() {
        BigDecimal   retValue = Env.ZERO;
        StringBuffer sb       = new StringBuffer( " [" );

        // Lines

        for( int i = 0;i < p_lines.length;i++ ) {
            retValue = retValue.add( p_lines[ i ].getAmount());
            sb.append( "+" ).append( p_lines[ i ].getAmount());
        }

        sb.append( "]" );

        //

        log.fine( toString() + " Balance=" + retValue + sb.toString());

        return retValue;
    }    // getBalance

    /**
     * Descripción de Método
     *
     *
     * @param as
     *
     * @return
     */

    public Fact createFact( MAcctSchema as ) {

        // create Fact Header
        Fact fact = new Fact( this,as,m_PostingType );

        // GLJ

        if( p_vo.DocumentType.equals( DOCTYPE_GLJournal )) {

            // account     DR      CR
			for (int i = 0; i < p_lines.length; i++)
			{
				if (p_lines[i].getC_AcctSchema_ID () == as.getC_AcctSchema_ID ())
				{
					FactLine line = fact.createLine (p_lines[i],
									p_lines[i].getAccount (),
									p_vo.C_Currency_ID,
									p_lines[i].getAmtSourceDr (),
									p_lines[i].getAmtSourceCr ());
                }
            }    // for all lines
		}
		else
		{
            p_vo.Error = "DocumentType unknown: " + p_vo.DocumentType;
            log.log( Level.SEVERE,"createFact - " + p_vo.Error );
            fact = null;
        }

        return fact;
    }    // createFact

	@Override
	public String applyCustomSettings( Fact fact, int index ) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String loadDocumentDetails() {
		// TODO Auto-generated method stub
		return null;
	}
}    // Doc_GLJournal



/*
 *  @(#)Doc_GLJournal.java   24.03.06
 * 
 *  Fin del fichero Doc_GLJournal.java
 *  
 *  Versión 2.2
 *
 */
