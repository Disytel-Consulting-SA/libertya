/*
 *    El contenido de este fichero está sujeto a la  Licencia Pública openXpertya versión 1.1 (LPO)
 * en tanto en cuanto forme parte íntegra del total del producto denominado:  openXpertya, solución 
 * empresarial global , y siempre según los términos de dicha licencia LPO.
 *    Una copia  íntegra de dicha  licencia está incluida con todas  las fuentes del producto.
 *    Partes del código son CopyRight (c) 2002-2005 de Ingeniería Informática Integrada S.L., otras 
 * partes son  CopyRight (c)  2003-2005 de  Consultoría y  Soporte en  Redes y  Tecnologías  de  la
 * Información S.L.,  otras partes son  adaptadas, ampliadas,  traducidas, revisadas  y/o mejoradas
 * a partir de código original de  terceros, recogidos en el  ADDENDUM  A, sección 3 (A.3) de dicha
 * licencia  LPO,  y si dicho código es extraido como parte del total del producto, estará sujeto a
 * su respectiva licencia original.  
 *     Más información en http://www.openxpertya.org/ayuda/Licencia.html
 */



package org.openXpertya.report;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Properties;

import org.openXpertya.model.MAcctSchemaElement;
import org.openXpertya.model.X_PA_ReportLine;
import org.openXpertya.util.DB;

/**
 * Descripción de Clase
 *
 *
 * @version    2.0, 22.03.06
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MReportLine extends X_PA_ReportLine {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param PA_ReportLine_ID
     * @param trxName
     */

    public MReportLine( Properties ctx,int PA_ReportLine_ID,String trxName ) {
        super( ctx,PA_ReportLine_ID, trxName );

        if( PA_ReportLine_ID == 0 ) {
            setSeqNo( 0 );

            // setIsSummary (false);           //      not active in DD

            setIsPrinted( false );
        } else {
            loadSources();
        }
    }    // MReportLine

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MReportLine( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs, trxName );
        loadSources();
    }    // MReportLine

    /** Descripción de Campos */

    private MReportSource[] m_sources = null;

    /** Descripción de Campos */

    private String m_whereClause = null;

    /**
     * Descripción de Método
     *
     */

    private void loadSources() {
        ArrayList list = new ArrayList();
        String    sql  = "SELECT * FROM PA_ReportSource WHERE PA_ReportLine_ID=? AND IsActive='Y'";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql );
            pstmt.setInt( 1,getPA_ReportLine_ID());

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                list.add( new MReportSource( getCtx(),rs,null ));
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.severe("MReportLine.loadSources: " + e );
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

        m_sources = new MReportSource[ list.size()];
        list.toArray( m_sources );
        log.info( "ID=" + getPA_ReportLine_ID() + " - Size=" + list.size());
    }    // loadSources

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MReportSource[] getSources() {
        return m_sources;
    }    // getSources

    /**
     * Descripción de Método
     *
     */

    public void list() {
        System.out.println( "- " + toString());

        if( m_sources == null ) {
            return;
        }

        for( int i = 0;i < m_sources.length;i++ ) {
            System.out.println( "  - " + m_sources[ i ].toString());
        }
    }    // list

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getSourceColumnName() {
        String ColumnName = null;

        for( int i = 0;i < m_sources.length;i++ ) {
            String col = MAcctSchemaElement.getColumnName( m_sources[ i ].getElementType());

            if( (ColumnName == null) || (ColumnName.length() == 0) ) {
                ColumnName = col;
            } else if( !ColumnName.equals( col )) {
                log.info( "MReportLine.getSourceColumnName - more than one: " + ColumnName + " - " + col );

                return null;
            }
        }

        return ColumnName;
    }    // getColumnName

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getSourceValueQuery() {
        if( (m_sources != null) && (m_sources.length > 0) ) {
            return MAcctSchemaElement.getValueQuery( m_sources[ 0 ].getElementType());
        }

        return null;
    }    //

    /**
     * Descripción de Método
     *
     * Tipo de importe
     * @param withSum
     *
     * @return
     */

    public String getSelectClause( boolean withSum ) {
        String       at = getAmountType().substring( 0,1 );    // first letter
        StringBuffer sb = new StringBuffer();

        if( withSum ) {
            sb.append( "SUM(" );
        }

        if( AmountType_Balance.equals( at )) {

            // sb.append("AmtAcctDr-AmtAcctCr");

        	
            sb.append( "acctbalance(Account_ID,AmtAcctDr,AmtAcctCr)" );
        } else if( AmountType_CR.equals( at )) {
            sb.append( "AmtAcctCr" );
        } else if( AmountType_DR.equals( at )) {
            sb.append( "AmtAcctDr" );
        } else if( AmountType_Qty.equals( at )) {
            sb.append( "Qty" );
        } else {
            log.severe("MReportLine.getSelectClause - AmountType=" + getAmountType() + ", at=" + at );

            return "NULL";
        }

        if( withSum ) {
            sb.append( ")" );
        }

        return sb.toString();
    }    // getSelectClause

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isPeriod() {
        String at = getAmountType();

        if( at == null ) {
            return false;
        }

        return AMOUNTTYPE_PeriodBalance.equals( at ) || AMOUNTTYPE_PeriodCreditOnly.equals( at ) || AMOUNTTYPE_PeriodDebitOnly.equals( at ) || AMOUNTTYPE_PeriodQuantity.equals( at );
    }    // isPeriod

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isYear() {
        String at = getAmountType();

        if( at == null ) {
            return false;
        }

        return AMOUNTTYPE_YearBalance.equals( at ) || AMOUNTTYPE_YearCreditOnly.equals( at ) || AMOUNTTYPE_YearDebitOnly.equals( at ) || AMOUNTTYPE_YearQuantity.equals( at );
    }    // isYear

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isTotal() {
        String at = getAmountType();

        if( at == null ) {
            return false;
        }

        return AMOUNTTYPE_TotalBalance.equals( at ) || AMOUNTTYPE_TotalCreditOnly.equals( at ) || AMOUNTTYPE_TotalDebitOnly.equals( at ) || AMOUNTTYPE_TotalQuantity.equals( at );
    }    // isTotal

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getWhereClause() {
        if( m_sources == null ) {
            return "";
        }

        if( m_whereClause == null ) {

            // Only one

            if( m_sources.length == 0 ) {
                m_whereClause = "";
            } else if( m_sources.length == 1 ) {
                m_whereClause = m_sources[ 0 ].getWhereClause();
                if (m_whereClause == null)	{
                	log.severe("No se ha podido obtener la clausula de la consulta.");
                	return null;
                }
            } else {

                // Multiple

                StringBuffer sb = new StringBuffer( "(" );

                for( int i = 0;i < m_sources.length;i++ ) {
                    if( i > 0 ) {
                        sb.append( " OR " );
                    }

                    sb.append( m_sources[ i ].getWhereClause());
                }

                sb.append( ")" );
                m_whereClause = sb.toString();
            }

            // Posting Type

            String PostingType = getPostingType();

            if( (PostingType != null) && (PostingType.length() > 0) ) {
                if( m_whereClause.length() > 0 ) {
                    m_whereClause += " AND ";
                }

                m_whereClause += "PostingType='" + PostingType + "'";
            }

            log.info( m_whereClause );
        }

        return m_whereClause;
    }    // getWhereClause

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isPostingType() {
        String PostingType = getPostingType();

        return( (PostingType != null) && (PostingType.length() > 0) );
    }    // isPostingType

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer( "MReportLine[" ).append( getID()).append( " - " ).append( getName()).append( " - " ).append( getDescription()).append( ", SeqNo=" ).append( getSeqNo()).append( ", AmountType=" ).append( getAmountType()).append( " - LineType=" ).append( getLineType());

        if( isLineTypeCalculation()) {
            sb.append( " - Calculation=" ).append( getCalculationType()).append( " - " ).append( getOper_1_ID()).append( " - " ).append( getOper_2_ID());
        } else {    // SegmentValue
            sb.append( " - SegmentValue - PostingType=" ).append( getPostingType()).append( ", AmountType=" ).append( getAmountType());
        }

        sb.append( "]" );

        return sb.toString();
    }    // toString

    // First Letter

    /** Descripción de Campos */

    public static final String AmountType_Balance = "B";

    /** Descripción de Campos */

    public static final String AmountType_CR = "C";

    /** Descripción de Campos */

    public static final String AmountType_DR = "D";

    /** Descripción de Campos */

    public static final String AmountType_Qty = "Q";

    // Second Letter

    /** Descripción de Campos */

    public static final String AmountType_Period = "P";

    /** Descripción de Campos */

    public static final String AmountType_Year = "Y";

    /** Descripción de Campos */

    public static final String AmountType_Total = "T";

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isLineTypeCalculation() {
        return LINETYPE_Calculation.equals( getLineType());
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isLineTypeSegmentValue() {
        return LINETYPE_SegmentValue.equals( getLineType());
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isCalculationTypeRange() {
        return CALCULATIONTYPE_AddRangeOp1ToOp2.equals( getCalculationType());
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isCalculationTypeAdd() {
        return CALCULATIONTYPE_AddOp1PlusOp2.equals( getCalculationType());
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isCalculationTypeSubtract() {
        return CALCULATIONTYPE_SubtractOp1_Op2.equals( getCalculationType());
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isCalculationTypePercent() {
        return CALCULATIONTYPE_PercentageOp1OfOp2.equals( getCalculationType());
    }

    /**
     * Descripción de Método
     *
     *
     * @param newRecord
     *
     * @return
     */

    protected boolean beforeSave( boolean newRecord ) {
        if( LINETYPE_SegmentValue.equals( getLineType())) {
            if( getCalculationType() != null ) {
                setCalculationType( null );
            }

            if( getOper_1_ID() != 0 ) {
                setOper_1_ID( 0 );
            }

            if( getOper_2_ID() != 0 ) {
                setOper_2_ID( 0 );
            }
        }

        return true;
    }    // beforeSave

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param AD_Client_ID
     * @param AD_Org_ID
     * @param PA_ReportLineSet_ID
     * @param source
     * @param trxName
     *
     * @return
     */

    public static MReportLine copy( Properties ctx,int AD_Client_ID,int AD_Org_ID,int PA_ReportLineSet_ID,MReportLine source,String trxName ) {
        MReportLine retValue = new MReportLine( ctx,0,trxName );

        MReportLine.copyValues( source,retValue,AD_Client_ID,AD_Org_ID );

        //

        retValue.setPA_ReportLineSet_ID( PA_ReportLineSet_ID );
        retValue.setOper_1_ID( 0 );
        retValue.setOper_2_ID( 0 );

        return retValue;
    }    // copy
}    // MReportLine



/*
 *  @(#)MReportLine.java   22.03.06
 * 
 *  Fin del fichero MReportLine.java
 *  
 *  Versión 2.0
 *
 */
