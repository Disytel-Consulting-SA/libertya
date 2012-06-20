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



package org.openXpertya.report;

import java.sql.ResultSet;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.model.X_PA_ReportColumn;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MReportColumn extends X_PA_ReportColumn {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param PA_ReportColumn_ID
     * @param trxName
     */

    public MReportColumn( Properties ctx,int PA_ReportColumn_ID,String trxName ) {
        super( ctx,PA_ReportColumn_ID,trxName );

        if( PA_ReportColumn_ID == 0 ) {
            setIsPrinted( true );
            setSeqNo( 0 );
        }
    }    // MReportColumn

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MReportColumn( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MReportColumn

    /**
     * Descripción de Método
     *
     *
     * @param withSum
     *
     * @return
     */

    public String getSelectClause( boolean withSum ) {

        // Amount Type = Period Balance, Period Credit

        String       amountType = getAmountType().substring( 0,1 );    // first character
        StringBuffer sb         = new StringBuffer();

        if( withSum ) {
            sb.append( "SUM(" );
        }

        if( AmountType_Balance.equals( amountType )) {

            // sb.append("AmtAcctDr-AmtAcctCr");

            sb.append( "acctBalance(Account_ID,AmtAcctDr,AmtAcctCr)" );
        } else if( AmountType_CR.equals( amountType )) {
            sb.append( "AmtAcctCr" );
        } else if( AmountType_DR.equals( amountType )) {
            sb.append( "AmtAcctDr" );
        } else if( AmountType_Qty.equals( amountType )) {
            sb.append( "Qty" );
        } else {
            log.log( Level.SEVERE,"AmountType=" + getAmountType() + ", at=" + amountType );

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
    }    // isTotalBalance

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer( "MReportColumn[" ).append( getID()).append( " - " ).append( getName()).append( " - " ).append( getDescription()).append( ", SeqNo=" ).append( getSeqNo()).append( ", AmountType=" ).append( getAmountType()).append( ", CurrencyType=" ).append( getCurrencyType()).append( "/" ).append( getC_Currency_ID()).append( " - ColumnType=" ).append( getColumnType());

        if( isColumnTypeCalculation()) {
            sb.append( " - Calculation=" ).append( getCalculationType()).append( " - " ).append( getOper_1_ID()).append( " - " ).append( getOper_2_ID());
        } else if( isColumnTypeRelativePeriod()) {
            sb.append( " - Period=" ).append( getRelativePeriod());
        } else {
            sb.append( " - SegmentValue ElementType=" ).append( getElementType());
        }

        sb.append( "]" );

        return sb.toString();
    }    // toString

    /** Descripción de Campos */

    public static final String AmountType_Balance = "B";

    /** Descripción de Campos */

    public static final String AmountType_CR = "C";

    /** Descripción de Campos */

    public static final String AmountType_DR = "D";

    /** Descripción de Campos */

    public static final String AmountType_Qty = "Q";

    //

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
     * @return
     */

    public boolean isColumnTypeCalculation() {
        return COLUMNTYPE_Calculation.equals( getColumnType());
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isColumnTypeRelativePeriod() {
        return COLUMNTYPE_RelativePeriod.equals( getColumnType());
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isColumnTypeSegmentValue() {
        return COLUMNTYPE_SegmentValue.equals( getColumnType());
    }

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param AD_Client_ID
     * @param AD_Org_ID
     * @param PA_ReportColumnSet_ID
     * @param source
     * @param trxName
     *
     * @return
     */

    public static MReportColumn copy( Properties ctx,int AD_Client_ID,int AD_Org_ID,int PA_ReportColumnSet_ID,MReportColumn source,String trxName ) {
        MReportColumn retValue = new MReportColumn( ctx,0,trxName );

        MReportColumn.copyValues( source,retValue,AD_Client_ID,AD_Org_ID );

        //

        retValue.setPA_ReportColumnSet_ID( PA_ReportColumnSet_ID );    // parent
        retValue.setOper_1_ID( 0 );
        retValue.setOper_2_ID( 0 );

        return retValue;
    }    // copy
}    // MReportColumn



/*
 *  @(#)MReportColumn.java   02.07.07
 * 
 *  Fin del fichero MReportColumn.java
 *  
 *  Versión 2.2
 *
 */
