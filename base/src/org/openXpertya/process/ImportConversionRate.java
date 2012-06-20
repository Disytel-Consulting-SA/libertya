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



package org.openXpertya.process;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.logging.Level;

import org.openXpertya.model.MConversionRate;
import org.openXpertya.model.X_I_Conversion_Rate;
import org.openXpertya.util.DB;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class ImportConversionRate extends SvrProcess {

    /** Descripción de Campos */

    private int p_AD_Client_ID = 0;

    /** Descripción de Campos */

    private int p_AD_Org_ID = 0;

    /** Descripción de Campos */

    private int p_C_ConversionType_ID = 0;

    /** Descripción de Campos */

    private Timestamp p_ValidFrom = null;

    /** Descripción de Campos */

    private boolean p_CreateReciprocalRate = false;

    /** Descripción de Campos */

    private boolean p_DeleteOldImported = false;

    /**
     * Descripción de Método
     *
     */

    protected void prepare() {
        ProcessInfoParameter[] para = getParameter();

        for( int i = 0;i < para.length;i++ ) {
            String name = para[ i ].getParameterName();

            if( para[ i ].getParameter() == null ) {
                ;
            } else if( name.equals( "AD_Client_ID" )) {
                p_AD_Client_ID = (( BigDecimal )para[ i ].getParameter()).intValue();
            } else if( name.equals( "AD_Org_ID" )) {
                p_AD_Org_ID = (( BigDecimal )para[ i ].getParameter()).intValue();
            } else if( name.equals( "C_ConversionType_ID" )) {
                p_C_ConversionType_ID = (( BigDecimal )para[ i ].getParameter()).intValue();
            } else if( name.equals( "ValidFrom" )) {
                p_ValidFrom = ( Timestamp )para[ i ].getParameter();
            } else if( name.equals( "CreateReciprocalRate" )) {
                p_CreateReciprocalRate = "Y".equals( para[ i ].getParameter());
            } else if( name.equals( "DeleteOldImported" )) {
                p_DeleteOldImported = "Y".equals( para[ i ].getParameter());
            } else {
                log.log( Level.SEVERE,"prepare - Unknown Parameter: " + name );
            }
        }
    }    // prepare

    /**
     * Descripción de Método
     *
     *
     * @return
     *
     * @throws Exception
     */

    protected String doIt() throws Exception {
        log.info( "doIt - AD_Client_ID=" + p_AD_Client_ID + ",AD_Org_ID=" + p_AD_Org_ID + ",C_ConversionType_ID=" + p_C_ConversionType_ID + ",ValidFrom=" + p_ValidFrom + ",CreateReciprocalRate=" + p_CreateReciprocalRate );

        //

        StringBuffer sql         = null;
        int          no          = 0;
        String       clientCheck = " AND AD_Client_ID=" + p_AD_Client_ID;

        // ****    Prepare ****

        // Delete Old Imported

        if( p_DeleteOldImported ) {
            sql = new StringBuffer( "DELETE I_Conversion_Rate " + "WHERE I_IsImported='Y'" ).append( clientCheck );
            no = DB.executeUpdate( sql.toString());
            log.fine( "doIt - Delete Old Impored =" + no );
        }

        // Set Client, Org, Location, IsActive, Created/Updated

        sql = new StringBuffer( "UPDATE I_Conversion_Rate " + "SET AD_Client_ID = COALESCE (AD_Client_ID," ).append( p_AD_Client_ID ).append( ")," + " AD_Org_ID = COALESCE (AD_Org_ID," ).append( p_AD_Org_ID ).append( ")," );

        if( p_C_ConversionType_ID != 0 ) {
            sql.append( " C_ConversionType_ID = COALESCE (C_ConversionType_ID," ).append( p_C_ConversionType_ID ).append( ")," );
        }

        if( p_ValidFrom != null ) {
            sql.append( " ValidFrom = COALESCE (ValidFrom," ).append( DB.TO_DATE( p_ValidFrom )).append( ")," );
        } else {
            sql.append( " ValidFrom = COALESCE (ValidFrom,SysDate)," );
        }

        sql.append( " CreateReciprocalRate = COALESCE (CreateReciprocalRate,'" ).append( p_CreateReciprocalRate
                ?"Y"
                :"N" ).append( "')," + " IsActive = COALESCE (IsActive, 'Y')," + " Created = COALESCE (Created, SysDate)," + " CreatedBy = COALESCE (CreatedBy, 0)," + " Updated = COALESCE (Updated, SysDate)," + " UpdatedBy = " ).append( getAD_User_ID()).append( "," + " I_ErrorMsg = NULL," + " Processed = 'N'," + " I_IsImported = 'N' " + "WHERE I_IsImported<>'Y' OR I_IsImported IS NULL" );
        no = DB.executeUpdate( sql.toString());
        log.info( "doIt - Reset =" + no );

        // Org

        sql = new StringBuffer( "UPDATE I_Conversion_Rate o " + "SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg||'ERR=Invalid Org, '" + "WHERE (AD_Org_ID IS NULL" + " OR EXISTS (SELECT * FROM AD_Org oo WHERE o.AD_Org_ID=oo.AD_Org_ID AND (oo.IsSummary='Y' OR oo.IsActive='N')))" + " AND I_IsImported<>'Y'" ).append( clientCheck );
        no = DB.executeUpdate( sql.toString());

        if( no != 0 ) {
            log.warning( "doIt - Invalid Org =" + no );
        }

        // Conversion Type

        sql = new StringBuffer( "UPDATE I_Conversion_Rate i " + "SET C_ConversionType_ID = (SELECT C_ConversionType_ID FROM C_ConversionType c" + " WHERE c.Value=i.ConversionTypeValue AND c.AD_Client_ID IN (0,i.AD_Client_ID) AND c.IsActive='Y') " + "WHERE C_ConversionType_ID IS NULL AND ConversionTypeValue IS NOT NULL" + " AND I_IsImported<>'Y'" ).append( clientCheck );
        no = DB.executeUpdate( sql.toString());

        if( no > 0 ) {
            log.fine( "doIt - Set ConversionType =" + no );
        }

        sql = new StringBuffer( "UPDATE I_Conversion_Rate i " + "SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg||'ERR=Invalid ConversionType, ' " + "WHERE (C_ConversionType_ID IS NULL" + " OR NOT EXISTS (SELECT * FROM C_ConversionType c " + "WHERE i.C_ConversionType_ID=c.C_ConversionType_ID AND c.IsActive='Y'" + " AND c.AD_Client_ID IN (0,i.AD_Client_ID)))" + " AND I_IsImported<>'Y'" ).append( clientCheck );
        no = DB.executeUpdate( sql.toString());

        if( no != 0 ) {
            log.warning( "doIt - Invalid ConversionType =" + no );
        }

        // Currency

        sql = new StringBuffer( "UPDATE I_Conversion_Rate i " + "SET C_Currency_ID = (SELECT C_Currency_ID FROM C_Currency c" + "     WHERE c.ISO_Code=i.ISO_Code AND c.AD_Client_ID IN (0,i.AD_Client_ID) AND c.IsActive='Y') " + "WHERE C_Currency_ID IS NULL AND ISO_Code IS NOT NULL" + " AND I_IsImported<>'Y'" ).append( clientCheck );
        no = DB.executeUpdate( sql.toString());

        if( no > 0 ) {
            log.fine( "doIt - Set Currency =" + no );
        }

        sql = new StringBuffer( "UPDATE I_Conversion_Rate i " + "SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg||'ERR=Invalid Currency, ' " + "WHERE (C_Currency_ID IS NULL" + " OR NOT EXISTS (SELECT * FROM C_Currency c " + "WHERE i.C_Currency_ID=c.C_Currency_ID AND c.IsActive='Y'" + " AND c.AD_Client_ID IN (0,i.AD_Client_ID)))" + " AND I_IsImported<>'Y'" ).append( clientCheck );
        no = DB.executeUpdate( sql.toString());

        if( no != 0 ) {
            log.warning( "doIt - Invalid Currency =" + no );
        }

        // Currency To

        sql = new StringBuffer( "UPDATE I_Conversion_Rate i " + "SET C_Currency_ID_To = (SELECT C_Currency_ID FROM C_Currency c" + "     WHERE c.ISO_Code=i.ISO_Code_To AND c.AD_Client_ID IN (0,i.AD_Client_ID) AND c.IsActive='Y') " + "WHERE C_Currency_ID_To IS NULL AND ISO_Code_To IS NOT NULL" + " AND I_IsImported<>'Y'" ).append( clientCheck );
        no = DB.executeUpdate( sql.toString());

        if( no > 0 ) {
            log.fine( "doIt - Set Currency To =" + no );
        }

        sql = new StringBuffer( "UPDATE I_Conversion_Rate i " + "SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg||'ERR=Invalid Currency To, ' " + "WHERE (C_Currency_ID_To IS NULL" + " OR NOT EXISTS (SELECT * FROM C_Currency c " + "WHERE i.C_Currency_ID_To=c.C_Currency_ID AND c.IsActive='Y'" + " AND c.AD_Client_ID IN (0,i.AD_Client_ID)))" + " AND I_IsImported<>'Y'" ).append( clientCheck );
        no = DB.executeUpdate( sql.toString());

        if( no != 0 ) {
            log.warning( "doIt - Invalid Currency To =" + no );
        }

        // Rates

        sql = new StringBuffer( "UPDATE I_Conversion_Rate i " + "SET MultiplyRate = 1 / DivideRate " + "WHERE (MultiplyRate IS NULL OR MultiplyRate = 0) AND DivideRate IS NOT NULL AND DivideRate<>0" + " AND I_IsImported<>'Y'" ).append( clientCheck );
        no = DB.executeUpdate( sql.toString());

        if( no > 0 ) {
            log.fine( "doIt - Set MultiplyRate =" + no );
        }

        sql = new StringBuffer( "UPDATE I_Conversion_Rate i " + "SET DivideRate = 1 / MultiplyRate " + "WHERE (DivideRate IS NULL OR DivideRate = 0) AND MultiplyRate IS NOT NULL AND MultiplyRate<>0" + " AND I_IsImported<>'Y'" ).append( clientCheck );
        no = DB.executeUpdate( sql.toString());

        if( no > 0 ) {
            log.fine( "doIt - Set DivideRate =" + no );
        }

        sql = new StringBuffer( "UPDATE I_Conversion_Rate i " + "SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg||'ERR=Invalid Rates, ' " + "WHERE (MultiplyRate IS NULL OR MultiplyRate = 0 OR DivideRate IS NULL OR DivideRate = 0)" + " AND I_IsImported<>'Y'" ).append( clientCheck );
        no = DB.executeUpdate( sql.toString());

        if( no != 0 ) {
            log.warning( "doIt - Invalid Rates =" + no );
        }

        // sql = new StringBuffer ("UPDATE I_Conversion_Rate i "   //      Rate diff > 10%
        // + "SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg||'ERR=Inconsistent Rates='||(MultiplyRate - (1/DivideRate)) "
        // + "WHERE ((MultiplyRate - (1/DivideRate)) > (MultiplyRate * .1))"
        // + " AND I_IsImported<>'Y'").append (clientCheck);
        // no = DB.executeUpdate (sql.toString ());
        // if (no != 0)
        // log.warn ("doIt - Inconsistent Rates =" + no);

        int noInsert = 0;

        sql = new StringBuffer( "SELECT * FROM I_Conversion_Rate " + "WHERE I_IsImported='N'" ).append( clientCheck ).append( " ORDER BY C_Currency_ID, C_Currency_ID_To, ValidFrom" );

        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql.toString());

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                X_I_Conversion_Rate imp = new X_I_Conversion_Rate( getCtx(),rs,get_TrxName());
                MConversionRate rate = new MConversionRate( imp,imp.getC_ConversionType_ID(),imp.getC_Currency_ID(),imp.getC_Currency_ID_To(),imp.getMultiplyRate(),imp.getValidFrom());

                if( imp.getValidTo() != null ) {
                    rate.setValidTo( imp.getValidTo());
                }

                if( rate.save()) {
                    imp.setC_Conversion_Rate_ID( rate.getC_Conversion_Rate_ID());
                    imp.setI_IsImported( true );
                    imp.setProcessed( true );
                    imp.save();
                    noInsert++;

                    //

                    if( imp.isCreateReciprocalRate()) {
                        rate = new MConversionRate( imp,imp.getC_ConversionType_ID(),imp.getC_Currency_ID_To(),imp.getC_Currency_ID(),imp.getDivideRate(),imp.getValidFrom());

                        if( imp.getValidTo() != null ) {
                            rate.setValidTo( imp.getValidTo());
                        }

                        if( rate.save()) {
                            noInsert++;
                        }
                    }
                }
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"doIt",e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        // Set Error to indicator to not imported

        sql = new StringBuffer( "UPDATE I_Conversion_Rate " + "SET I_IsImported='N', Updated=SysDate " + "WHERE I_IsImported<>'Y'" ).append( clientCheck );
        no = DB.executeUpdate( sql.toString());
        addLog( 0,null,new BigDecimal( no ),"@Errors@" );

        //

        addLog( 0,null,new BigDecimal( noInsert ),"@C_Conversion_Rate_ID@: @Inserted@" );

        return "";
    }    // doIt
}    // ImportConversionRate



/*
 *  @(#)ImportConversionRate.java   02.07.07
 * 
 *  Fin del fichero ImportConversionRate.java
 *  
 *  Versión 2.2
 *
 */
